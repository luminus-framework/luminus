## Sessions

Luminus defaults to using in-memory sessions and the the session store atom is located in the `<app>.session` namespace.


```clojure
(ns myapp.session)

(defonce mem (atom {}))
(def half-hour 1800000)

(defn- current-time []
  (quot (System/currentTimeMillis) 1000))

(defn- expired? [[id session]]
  (pos? (- (:ring.middleware.session-timeout/idle-timeout session) (current-time))))

(defn clear-expired-sessions []
  (clojure.core/swap! mem #(->> % (filter expired?) (into {}))))

(defn start-cleanup-job! []
  (future
    (loop []
      (clear-expired-sessions)
      (Thread/sleep half-hour)
      (recur))))

```

The namespace also sets up session expiry in order to clean out timed out sessions. The
`start-cleanup-job!` function creates a thread that will remove expired session. It is
started in the `<app>.handler/init` function when the application loads.

The session middleware is initialized in the `<app>.middleware` namespace by the `production-middleware`
function seen below.

Session timeout is controlled by the `wrap-idle-session-timeout` middleware.
Default sessions timeout is set to 30 minutes of inactivity, and
timed out sessions will be redirected to the `/` URI.

The session store is initialized using the `wrap-defaults` middleware.

```clojure
(defn production-middleware [handler]
  (-> handler
      wrap-restful-format
      (wrap-idle-session-timeout
        {:timeout (* 60 30)
         :timeout-response (redirect "/")})
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:session :store] (memory-store session/mem))))
      wrap-servlet-context
      wrap-internal-error))
```

We can easily swap the default memory store for a different one, such as a cookie store. Note that
we'll also need to update the `clear-expired-sessions` function seen above accordingly to work with the new store.

Below, we explicitly specify the `ring.middleware.session.cookie/cookie-store` with the name `example-app-session` as our session store:

```clojure
(wrap-defaults
  (-> site-defaults
      (assoc-in [:session :store] (cookie-store))
      (assoc-in [:session :cookie-name] "example-app-sessions")))
```

We can also specify the maximum age for our session cookies using the `:max-age` key:

```clojure
(wrap-defaults
  (-> site-defaults
      (assoc-in [:session :store] (cookie-store))
      (assoc-in [:session :cookie-attrs] {:max-age 10})))
```

When using cookie store it is also important to specify a secret key (16 characters) for cookie encryption. Otherwise a random one will be generated each time application is started and sessions created before will be lost.

```clojure
(wrap-defaults
  (-> site-defaults
      (assoc-in [:session :store] (cookie-store {:key "BuD3KgdAXhDHrJXu"}))
      (assoc-in [:session :cookie-name] "example-app-sessions")))
```

You may also wish to take a look at [Redis](http://redis.io/) for your session store. Creating Redis sessions is easy thanks to [Carmine](https://github.com/ptaoussanis/carmine). You would simply need to define a connection and use the `taoensso.carmine.ring/carmine-store` with it:

```clojure

(def redis-conn {:pool {<opts>} :spec {<opts>}})


(wrap-defaults
  (-> site-defaults
      (assoc-in [:session :store] (carmine-store redis-conn))))
```

For further information, please see the [official API documentation](http://ptaoussanis.github.io/carmine/taoensso.carmine.ring.html).

### Accessing the session

Ring tracks sessions using the request map and the current session will be found under the `:session` key.
Below we have a simple example of interaction with the session.

```clojure
(ns myapp.home
  (:require [compojure.core :refer [defroutes GET]]
            [ring.util.response :refer [response]]))

(defn set-user! [id {session :session}]
  (-> (str "User set to: " id)
      response
      (assoc :session (assoc session :user id))))

(defn remove-user! [{session :session}]
  (-> (response "")
      (assoc :session (dissoc session :user))))

(defn clear-session! []
  (dissoc (response "") :session))

(defroutes app-routes
  (GET "/login/:id" [id :as req] (set-user! id req))
  (GET "/remove" req (remove-user req))
  (GET "/logout" req (clear-session!)))
```

Note that the the default `<app>.layout/render` function does not allow setting the session.
The function is intended to render the page and this should not be conflated with any controller actions.
In a scenario where you wish to set the session and render a page a redirect is the recommended approach.

### Flash sessions

Flash sessions have a lifespan of a single request, these can be accessed using the `:flash` key instead of the `:session` key used for regular sessions.

## Cookies

Cookies are found under the `:cookies` key of the request, eg:

```clojure
{:cookies {"username" {:value "Bob"}}}

```

Conversely, to set a cookie on the response we simply update the response map with the desired cookie value:

```clojure
(-> "cookie set" response (update-in [:cookies "username" :value] "Alice"))
```

Cookies cn contain the following additional attributes in addition to the `:value` key:

* :domain - restrict the cookie to a specific domain
* :path - restrict the cookie to a specific path
* :secure - restrict the cookie to HTTPS URLs if true
* :http-only - restrict the cookie to HTTP if true (not accessible via e.g. JavaScript)
* :max-age - the number of seconds until the cookie expires
* :expires - a specific date and time the cookie expires
