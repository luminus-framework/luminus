## Sessions

Session management is provided by the `noir.session` namespace from [lib-noir](https://github.com/noir-clojure/lib-noir).
The `noir.util.middleware/app-handler` function default will default to using a memory session
store. This can be overriden by passing in a second argument which specifies a specific store you'd like to use.

This following creates an in-memory session store.

```clojure
(def app (middleware/app-handler [home-routes app-routes]))
```

Additional session options can be passed in via the `:session-options` key. For example, a default session timeout can be set by adding the `:timeout` and `:timeout-response` keys as follows.

```clojure
(def app
  (middleware/app-handler
    [home-routes app-routes]
    :session-options {:timeout (* 30 60)
                      :timeout-response (response/redirect "/")}))
```

The above will force sessions to timeout after 30 minutes of inactivity. The timed out sessions will be redirected to the "/" URI.


Below, we explicitly specify the `ring.middleware.session.cookie/cookie-store` with the name `example-app-session` as our session store using the `:session-options` key instead:

```clojure
(def app
  (middleware/app-handler
    [home-routes app-routes]
    :session-options {:cookie-name "example-app-session"
                      :store (cookie-store)}))
```

We can also specify the maximum age for our session cookies using the `:max-age` key:

```clojure
(def app
  (middleware/app-handler
    [home-routes app-routes]
    :session-options {:cookie-attrs {:max-age 10}
                      :store (cookie-store)}))
```

When using cookie store it is also important to specify a secret key (16 characters) for cookie encryption. Otherwise a random one will be generated each time application is started and sessions created before will be lost.

```clojure
(def app
  (middleware/app-handler
    [home-routes app-routes]
    :session-options {:cookie-name "example-app-session"
                      :store (cookie-store {:key "BuD3KgdAXhDHrJXu")}))
```

You may also wish to take a look at [Redis](http://redis.io/) for your session store. Creating Redis sessions is easy thanks to [Carmine](https://github.com/ptaoussanis/carmine). You would simply need to define a connection and use the `taoensso.carmine.ring/carmine-store` with it:

```clojure

(def redis-conn {:pool {<opts>} :spec {<opts>}}) 

(def app
  (middleware/app-handler
    [home-routes app-routes]
    :session-options {:store (carmine-store redis-conn)}))
```

For further information, please see the [official API documentation](http://ptaoussanis.github.io/carmine/taoensso.carmine.ring.html).

### Accessing the session

The sessions can be accessed from within any function within the scope of the request,
and provide functions to put, get, remove, and clear the session keys, eg:

```clojure
(require '[noir.session :as session])

(defn set-user [id]
  (session/put! :user id)
  (session/get :user))

(defn remove-user []
  (session/remove! :user)
  (session/get :user))

(defn set-user-if-nil [id]
  (session/get :user id))


(defn clear-session []
  (session/clear!))

(defroutes app-routes
  (GET "/login/:id" [id] (set-user id))
  (GET "/remove" [] (remove-user))
  (GET "/set-if-nil/:id" [id] (set-user-if-nil id))
  (GET "/logout" [] (clear-session)))
```

It's also possible to create flash variables by using `noir.session/flash-put!`
and `noir.session/flash-get`, these variables have a lifespan of a single request.

```clojure
(session/flash-put! :message "User added!")
(session/flash-get :message)
```

## Cookies

Cookie handling is provided by the `noir.cookies` namespace. Setting and getting
cookies works exactly the same as with session variables described above:

```clojure
(require '[noir.cookies :as cookies])

(defn set-user-cookie [id]
  (cookies/put! :user id)
  (cookies/get :user))

(defn set-user-if-nil [id]
  (cookies/get :user id))

(cookies/put! :track
              {:value (str (java.util.UUID/randomUUID))
              :path "/"
              :expires "1"})
```
