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
However, Luminus will handle this for you when you call `app-routes` and `war-routes` functions.
All you have to do is add your routes to the `all-routes` vector defined in the `handler` namespace
of your application.

```clojure
(def all-routes [auth-routes app-routes])
(def app (middleware/app-handler all-routes))
(def war-handler (middleware/war-handler all-routes))
```


Further documentation is available on the [official Compojure wiki](https://github.com/weavejester/compojure/wiki)


