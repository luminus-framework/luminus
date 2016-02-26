## Responses

Ring responses are generated using the [ring-http-response](https://github.com/metosin/ring-http-response) library.
The library provides a number of helpers for producing responses with their respective HTTP Status codes.

For example, the `ring.util.http-response/ok` helper is used to generate a response with the status `200`. The following code will produce a valid response map with the content set as its `:body` key.

```clojure
(ok {:foo "bar"})

;;result of calling response
{:status  200
 :headers {}
 :body    {:foo "bar"}}
```

The response body can be one of a string, a sequence, a file, or an input stream. The body must correspond appropriately with the response's status code.

A string, it will be sent back to the client as is. For a sequence, a string representing each element is sent to the client. Finally, if the response is a file or an input stream, then the server sends its contents to the client.

### Response encoding

By default [ring-middleware-format](https://github.com/ngrunwald/ring-middleware-format) middleware is used to infer the response type when a route returns a map containing the`:body` key:

```clojure
(GET "/json" [] {:body {:foo "bar"}})
```

The middleware is found in the `<app-name>.middleware` namespace of your application. The middleware function is called `wrap-formats`, and it enables support for `ring-middleware-format` using the JSON and Transit encodings.

```clojure
  (defn wrap-base [handler]
  (-> handler
      wrap-dev
      wrap-formats ;; enables JSON/Transit serialization and deserialization
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (assoc-in  [:session :store] (ttl-memory-store (* 60 30)))))
      wrap-servlet-context
      wrap-internal-error))
```

The formats are controlled by the `:formats` key and can be selected as follows:


```clojure
(defn wrap-formats [handler]
  (wrap-restful-format handler {:formats [:json-kw :transit-json :transit-msgpack]}))

```

The available formats are:

* :json - JSON with string keys in :params and :body-params
* :json-kw - JSON with keywodized keys in :params and :body-params
* :edn - native Clojure format.
* :yaml - YAML format
* :yaml-kw - YAML format with keywodized keys in :params and :body-params
* :yaml-in-html - yaml in a html page

When no format is supplied in the `Accept` header or the format specified is unknown, the first format from the `:formats` vector in the handler will be used (JSON by default).

### Setting headers

Setting additional response headers is done by calling `ring.util.http-response/header`, and
passing it a map of HTTP headers. Note that the keys **must** be strings.

```clojure
(-> "hello world" response (header "x-csrf" "csrf"))
```

### Setting content type

You can set a custom response type by using the `ring.util.http-response/content-type` function, eg:

```clojure
(GET "/project" []
  (-> (clojure.java.io/input-stream "report.pdf")
      response
      (content-type "application/pdf")))
```

### Setting custom status

Setting a custom status is accomplished by passing the content to the `ring.util.http-response/status` function:

```clojure
(GET "/missing-page" []
  (-> "your page could not be found"
      response
      (status 404)))
```

### Redirects

Redirects are handled by the `ring.util.http-response/found` function. The function will set a `302` redirect status on the response.

```clojure
(GET "/old-location" []
  (found "/new-location"))
```

Please refer to the [ring-http-response](https://github.com/metosin/ring-http-response/blob/master/src/ring/util/http_response.clj) to see other available helpers.
