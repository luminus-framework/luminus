## Responses

By default `ring-middleware-format` middleware is used to infer the response type when a route returns a map containing the`:body` key:

```clojure
(GET "/json" [] {:body {:foo "bar"}})
```

The formats are controlled by the `:formats` key found under the `app` definition in the `handler` namespace of your application:

```clojure
(def app (middleware/app-handler
           ;; add your application routes here
           [home-routes app-routes]
           ;; add custom middleware here
           :middleware [template-error-page]
           ;; add access rules here
           :access-rules []
           ;; serialize/deserialize the following data formats
           ;; available formats:
           ;; :json :json-kw :yaml :yaml-kw :edn :yaml-in-html
           :formats [:json-kw :edn]))
```           

The available formats are:

* :json - JSON with string keys in :params and :body-params
* :json-kw - JSON with keywodized keys in :params and :body-params
* :edn - native Clojure format.
* :yaml - YAML format
* :yaml-kw - YAML format with keywodized keys in :params and :body-params
* :yaml-in-html - yaml in a html page

When no format is supplied in the `Accept` header or the format specified is unknown, the first format from the `:formats` vector in the handler will be used (JSON by default).

Alternatively, there are a number of helper functions availble in `noir.response` for
returning customized responses to the client.

### Setting headers

Setting additional response headers is done by calling `set-header`, and
passing it a map of HTTP headers. Note that the keys **must** be strings.

```clojure
(set-headers {"x-csrf" csrf}
    (common/layout [:p "hi there"]))
```

### Setting content type

You can set a custom response type by using the `content-type` function, eg:

```clojure
(GET "/project" []
       (noir.response/content-type
       "application/pdf"
       (clojure.java.io/input-stream "report.pdf")))
```

There are helpers available for XML, JSON, and JSONP responses:

* XML - Wraps the response with the content type for xml and sets the body to the content.
* JSON- Wraps the response in the json content type and generates JSON from the content
* JSONP - Generates JSON for the given content and creates a javascript response for calling
  func-name with it.
* edn - Wraps the response in the `application/edn` content-type
   and calls pr-str on the Clojure data stuctures passed in.

```clojure

(GET "/xml" [] (xml "<foo></foo>"))

(GET "/json" [] (json {:response "ok"}))

(GET "/jsonp" [] (jsonp  "showUsers" [{:name "John"} {:name "Jane"}]))

(GET "/edn" [] (edn {:foo 1 :bar 2}))
```

### Setting custom status

Setting a custom status is accomplished by passing the content to the `status` function:

```clojure
(GET "/missing-page" [] (status 404 "your page could not be found"))
```

### Redirects

Redirects are handled by `noir.response/redirect`. It's also possible to
pass a type to the redirect function. Supported types are:

* :permanent
* :found
* :see-other
* :not-modified
* :proxy
* :temporary

The response will default to :found if no type is passed in.

```clojure
(require 'noir.response)

(redirect "/foo")
(redirect "/bar" :see-other)
```
