## Adding custom middleware

Since Luminus uses Ring for routing the application handler
is a standard Ring handler and can be wrapped in middleware
just like you would in any other Ring based application.

The middleware allows wrapping the handlers in functions which can modify the the way the request is processed. Middleware functions are often used to extend the base functionality of Ring handlers to match the needs of the particular application.

A handler is simply a function which accepts an existing handler with some optional parameters and returns a new handler with some added behaviour. An example of a middleware function would be:

```clojure
(defn wrap-nocache [handler]
  (fn [request]
     (let [response (handler request)]
        (assoc-in response [:headers  "Pragma"] "no-cache"))))

(def app (wrap-nocache handler))
```

As you can see the wrapper accepts the handler and returns a function which in turn accepts the request. Since the returned function was defined in the scope where the handler exists, it can use it internally. When called, it will call the handler with the request and add Pragma: no-cache to the response map.

You can add custom middleware to the `app` defined in the `handler` namespace.
Alternatively, you can define your own middleware instead of using `app-handler`
and `war-handler` helpers.


For detailed information please refer to the official [Ring documentation](https://github.com/ring-clojure/ring).
