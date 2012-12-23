## Sessions

Session management is provided by the `noir.session` namespace from `lib-noir`.
The sessions are provided via middleware handlers both `app-handler` 
and `war-handler` functions in `lib-luminus.middleware` default to memory session
store. This can be overriden by passing in a second argument which specifies a 
specific store you'd like to use.

This following creates an in-memory session store.
  
```clojure
(def app (middleware/app-handler all-routes))
(def war-handler (middleware/war-handler all-routes))
```

Here we specify the `monger-store` as our session store instead.
Note that the store is specified independently for both the `app=handler` 
and the `war-handler`.
 
```clojure
(def app (middleware/app-handler all-routes (monger-store "sessions")))
(def war-handler (middleware/war-handler all-routes (monger-store "sessions")))
```

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
(session/flash-put! "User added!")
(session/flash-get)
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
              :expires 1})  
```