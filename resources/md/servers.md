While Luminus aims to provide reasonable defaults, you will likely want to tune your server configuration to fit the needs of your specific application.

### Application Middleware

Luminus includes a number of useful wrappers in its default middleware stack. These include [ring-default](https://github.com/ring-clojure/ring-defaults), session middleware, [ring-middleware-format](https://github.com/ngrunwald/ring-middleware-format), and [ring-webjars](https://github.com/weavejester/ring-webjars).

While this middleware is useful in most situations, it will also incur a performance cost. In cases where you wish to optimize for performance, it would make sense to pick the specific middleware that's necessary for your application.

### Immutant Configuration

Luminus defaults to using [Immutant](http://immutant.org/) as the default server. This server provides a number of options that are unique to it.

Immutant allows setting the number of worker and IO threads using the `:worker-threads` and the `:io-threads` keys respectively. For example, we may update the default configuration as follows:

```clojure
(mount/defstate http-server
  :start
  (http/start
    (-> env
        (assoc
          :handler handler/app
          :worker-threads 200
          :io-threads (* 2 (.availableProcessors (Runtime/getRuntime))))
        (update :port #(or (-> env :options :port) %))))
  :stop
  (http/stop http-server))
```

Note that the `env` is used to supply the base configuration, meaning that any initialization parameters can also be supplied
using the environment variables.

Another feature provided by Immutant is the ability to chain multiple independent handlers together. For example, we can use the `wrap-handler` function to append an additional handler for the application.

```clojure
(mount/defstate http-server
                :start
                (->
                   (http/start
                     (-> env
                        (assoc :handler handler/app)
                        (update :port #(or (-> env :options :port) %))))
                  (http/wrap-handler io-routes {:path "/io" :dispatch? false}))
                :stop
                (http/stop http-server))
```

The additional handler uses a `:path` prefix as a context. All the routes served by this handler will be prefixed with the supplied path. Each handler can have its own middleware stack that's independent of other handlers in the application.

