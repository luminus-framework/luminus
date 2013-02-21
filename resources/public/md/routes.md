Luminus uses Compojure to define application routes.
A route is defined by its HTTP request method and accepts the URI, parameters, and the handler.
Compojure defines routes for all the standard HTTP requests such as
ANY, DELETE, GET, HEAD, OPTIONS, PATCH, POST, and PUT.

For example, if we wanted to define an application with a single route pointing to / which
says "Hello World!" we could write the following:

```clojure
(defroutes app-routes
  (GET "/" [] "Hello World!"))
```

If we want to make a route which responds to POST and accepts some form parameters we'd write:

```clojure
(POST "/hello" [id] (str "Welcome " id))
```

For some routes we'll need to access the request map, this is done by simply declaring it as the second argument to the route.

```clojure
(GET "/foo" request (interpose ", " (keys request)))
```

The above route reads out all the keys from the request map and displays them. The output will look like the following:

```clojure
:ssl-client-cert, :remote-addr, :scheme, :query-params, :session, :form-params,
:multipart-params, :request-method, :query-string, :route-params, :content-type,
:cookies, :uri, :server-name, :params, :headers, :content-length, :server-port,
:character-encoding, :body, :flash
```

Compojure also provides some useful functionality for handling the request maps and the form parameters.
For example, in the guestbook application we created in the last chapter we saw the following route defined:

```clojure
(POST "/"  [name message] (save-message name message))
```

This route extracts the name and message form parameters and binds them to variables of the same name.
We can now use them as any other declared variable. It's also possible to use the regular Clojure destructuring
inside the route.

```clojure
(GET "/:foo" {{foo "foo"} :params}
  (str "Foo = " foo))
```

Furthermore, Compojure also allows destructuring a subset of form parameters and creating a map from the rest.

```clojure
[x y & z]
x -> "foo"
y -> "bar"
z -> {:v "baz", :w "qux"}
```

Above, parameters x and y have been bound to variables, while parameters v and w remain in a map called z.
Finally, if we need to get at the complete request along with the parameters we can do the following:

```clojure
(GET "/" [x y :as r] (str x y r))
```

Here we bind the form parameters x an y, and bind the complete request map to the variable r.

## Organizing application routes

It's a good practice to organize your application routes together by functionality. Compojure provides
a `defroutes` macro which can group several routes together and bind them to a symbol.

```clojure
(defroutes auth-routes
  (POST "/login" [id pass] (login id pass))
  (POST "/logout" [] (logout)))

(defroutes app-routes
  (GET "/" [] (home))
  (route/resources "/")
  (route/not-found "Not Found"))
```

Compojure provides the `routes` function to group multiple route definitions together.
There's an `noir.util.middleware/app-handler` function in `lib-noir` which will wrap all
the common routes for you.

The `app-handler` accepts a vector of routes followed by optional session store. If
the store is not specified then in-memory store will be used.

You'll notice that the template already defined an `all-routes` vector in the `handler`.
All you have to do is add your new routes there. The `noir.util.middleware/war-handler`
function adds additional middleware used needed for running on an application server
such as Tomcat.

```clojure
(def all-routes [auth-routes app-routes])
(def app (middleware/app-handler all-routes))
(def war-handler (middleware/war-handler app))
```

Further documentation is available on the [official Compojure wiki](https://github.com/weavejester/compojure/wiki)

## Restricting access

Some pages should only be accessible if specific conditions are met. For example,
you may wish to define admin pages only visible to the administrator, or a user profile
page which is only visible if there is a user in the session.

Using the `noir.util.route` namespace from `lib-noir`, we can define rules for restricting 
access to specific pages. Let's take a look at how to create a private page which is only 
viewable if the `:user` key in the session matches the name of the page. First, we'll need 
to reference `noir.util.route` and `noir.session` in the handler.

```clojure
(ns myapp.handler
  (:use ... 
        noir.util.route)
  (:require ...             
            [noir.session :as session]))
```

Next, we'll write the function which implements the rule we described above. This function 
must accept three argument which are the method, the url, and the params. The function must 
return a boolean indicating whether the page matches the specified rule.

Here's a function which checks that the URI is of the format "/private/:id" and that the id 
matches the user in the session.

```clojure
(defn user-page [method url params]
  (and (= url "/private/:id")
       (= (first params) (session/get :user))))
```

Once you've got your rules defined, you need to wrap the handler with the
`noir.util.middleware/wrap-access-rules` and pass in the rules as parameters.
In our case we have a single rule, which is the function `user-page`.

```clojure
(def app (-> all-routes
             (middleware/app-handler)
             (middleware/wrap-access-rules user-page)))
```

By default `wrap-access-rules` will redirect to the "/" URI if none of the rules return true.
To set a custom redirect URI simply pass in a map with a `:redirect` key set to the URI string:

```clojure
(def app (-> all-routes
             (middleware/app-handler)
             (middleware/wrap-access-rules 
               {:redirect "/unauthorized"} user-page)))

```

Finally, when we want to restrict page access to a page, we simply mark 
our route with `noir.util.route/restricted`:

```clojure
(restricted GET "/private/:id" [id] "private!")
```

All restricted routes will be checked to see if they match at least one of access rules
passed into `wrap-access-rules`.


