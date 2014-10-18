## Requests

By default the request parameters, such as those from a form POST, will be automatically parsed
and set as a `:params` key on the request.

However, if you're passing in parameters as a request body then you will need to enable the appropriate
middleware to handle them. Luminus uses [ring-middleware-format](https://github.com/ngrunwald/ring-middleware-format)
for encoding and decoding the body parameters.

The middleware can be enabled by specifying the `:formats` key on the `noir.util.middleware/app-handler` as seen
below:

```clojure
(def app (middleware/app-handler
          all-routes
          :formats [:json :edn]))
```

This will cause the body of the requests with MIME type `application/json` and `application/edn` to be encoded
by the `ring-middleware-format`. The request parameters will be available under the `:params` key
of the request. Note that this will also handle encoding the response bodies when you set the appropriate MIME
type on the response. Please see the [response types](/docs/responses.md) section for more information on generating responses.

Below is a list of valid formats:

* :json - JSON with string keys in :params and :body-params
* :json-kw - JSON with keywodized keys in :params and :body-params
* :edn - native Clojure format.
* :yaml - YAML format
* :yaml-kw - YAML format with keywodized keys in :params and :body-params
* :yaml-in-html - yaml in a html page
* :transit-json Transit over JSON format
* :transit-msgpack Transit over Msgpack format
