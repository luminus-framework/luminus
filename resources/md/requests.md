## Requests

By default the request parameters, such as those from a form POST, will be automatically parsed
and set as a `:params` key on the request.

However, if you're passing in parameters as a request body then you will need to enable the appropriate
middleware to handle them. Luminus uses [ring-middleware-format](https://github.com/ngrunwald/ring-middleware-format)
for encoding and decoding the body parameters.

The request parameters will be available under the `:params` key
of the request. Note that the middleware will also handle encoding the response bodies when you set the appropriate MIME
type on the response. Please see the [response types](/docs/responses.html) section for more information on generating responses.

Below is a list of valid formats:

* :json - JSON with string keys in :params and :body-params
* :json-kw - JSON with keywordized keys in :params and :body-params
* :edn - native Clojure format.
* :yaml - YAML format
* :yaml-kw - YAML format with keywordized keys in :params and :body-params
* :yaml-in-html - yaml in a html page
* :transit-json Transit over JSON format
* :transit-msgpack Transit over Msgpack format
