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

If we want to make a route that responds to POST and accepts some form parameters we'd write:

```clojure
(POST "/hello" [id] (str "Welcome " id))
```

For some routes we'll need to access the request map, this is done by simply declaring it as the 
second argument to the route.

```clojure
(GET "/foo" request (interpose ", " (keys request)))
```

The above route reads out all the keys from the request map and displays them. 
The output will look like the following:

```clojure
:ssl-client-cert, :remote-addr, :scheme, :query-params, :session, :form-params,
:multipart-params, :request-method, :query-string, :route-params, :content-type,
:cookies, :uri, :server-name, :params, :headers, :content-length, :server-port,
:character-encoding, :body, :flash
```

Compojure also provides some useful functionality for handling the request maps and the form parameters.
For example, in the guestbook application example we saw the following route defined:

```clojure
(POST "/"  [name message] (save-message name message))
```

This route extracts the name and the message form parameters and binds them to variables of the same name.
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

It's also possible to group routes by common path elements using `context`. If you had
a set of routes that all shared `/user/:id` path as seen below:

```clojure
(defroutes user-routes
      (GET "/user/:id/profile" [id] ...)
      (GET "/user/:id/settings" [id] ...)
      (GET "/user/:id/change-password [id] ...))
```

You could rewrite that as:

```clojure
(def user-routes
      (context "/user/:id" [id]
        (GET "/profile" [] ...)
        (GET "/settings" [] ...)
        (GET "/change-password" [] ...)))
```


Once all your application routes are defined you can add them to the routes vector in the 
`noir.util.middleware/app-handler` that's found in the `handler` of your application.

You'll notice that the template already defined the `app` in the `handler` namespace of your
application. All you have to do is add your new routes there.

The `noir.util.middleware/war-handler` function adds additional middleware used needed for 
running in a servlet container such as Tomcat.

```clojure
(def app (middleware/app-handler
           ;;add your application routes here
           [home-routes app-routes]
           ;;add custom middleware here
           :middleware []
           ;;add access rules here
           ;;each rule should be a vector
           :access-rules []))

(def war-handler (middleware/war-handler app))
```

Further documentation is available on the [official Compojure wiki](https://github.com/weavejester/compojure/wiki)

## Restricting access

Some pages should only be accessible if specific conditions are met. For example,
you may wish to define admin pages only visible to the administrator, or a user profile
page which is only visible if there is a user in the session.

Using the `noir.util.route` namespace from `lib-noir`, we can define rules for restricting 
access to specific pages.

### Marking Routes as Restricted

The `noir.util.route/restricted` macro is used to indicated that access rules apply to the route:

```clojure
(GET "/private/:id" [id] (restricted "private!"))
```

In case we have multiple routes that we'd like to mark as restricted we can use the 
`def-restricted-routes` macro. This will make all the routes defined inside it restricted:

```clojure
(def-restricted-routes private-pages
  (GET "/profile" [] (show-profile)
  (GET "/my-secret-page" [] (show-secret-page)
  (GET "/another-secret-page" [] (another-secret-page))
```

the above is equivalent to:

```clojure
(defroutes private-pages
  (GET "/profile" [] (restricted (show-profile)))
  (GET "/secret-page1" [] (restricted (show-secret-page)))
  (GET "/secret-page2" [] (restricted (another-secret-page))))
```

All restricted routes will be checked to see if they match at least one of the access rules.

### Specifying Access Rules

Let's take a look at how to create a rule to specify that restricted routes should only be 
accessible if the `:user` key is present in the session. 

First, we'll need  to reference `noir.util.route` and `noir.session` in the handler.

```clojure
(ns myapp.handler
  (:use ... 
        noir.util.route)
  (:require ...             
            [noir.session :as session]))
```

Next, we'll write the function that implements the rule we described above. This function 
must accept the request map as its argument and return a truthy value indicating whether 
the page satisfies the specified rule.

Here's a function to check if there is a user currently in the session. If the user is
`nil` then the rule will trigger a redirect. By default the rules redirect to the `"/`" URI.

```clojure
(defn user-page [request]
  (session/get :user))

(def app 
 (middleware/app-handler 
   [app-routes]
   :access-rules [user-page]))
```

Now, any restricted handlers will redirect to `"/"` unless there is a `:user` key in the session.

### Access Rule Groups

The rules can also be specified a map representing a group of rules. The rule group map contains the following keys:

* `:redirect` - the URI string or a function to specify where requests will be redirected to if rejected (optional defaults to "/")
* `:uri` - the URI for which the rules in the map will be activated (optional if none specified applies to all URIs)
* `:rules` - a vector containing the rule functions associated with the specified `:redirect` and the `:uri`

Let's take a look at an example of how this all works below:

```clojure
(defroutes app-routes
 (GET "/restricted" [] (restricted "this page is restricted"))
 (GET "/restricted1" [] (restricted "this is another restricted page"))
 (GET "/users/:id" [] (restricted "howdy"))
 (GET "/denied1" [] "denied")
 (GET "/denied2" [] "denied differently"))

(def app 
 (middleware/app-handler 
   [app-routes]
   :access-rules 
   [;;global rule tha applies to all restriced routes
    user-page

    ;;rule group for the "/restricted" URI
    ;;redirects to "/denied1" URI
    {:uri "/restricted"
     :redirect "/denied1"
     :rules [(fn [req] false)]}

    ;;rule group for the "/users/*" URI pattern
    ;;redirects to "/denied2" URI
    {:redirect (fn [req] 
                 (log/info 
                   (str "redirecting " (:uri req)))
                 "/denied2")
     :uri "/users/*"
     :rules [(fn [req] false)]}]))
```

The first rule will be activated for any handler that's marked as restricted. This means that all of the restricted 
pages will redirect to *"/"* if there is no user in the session and no other rules succeed.

The second rule will only activate if the request URI matches *"/restricted"* and will be ignored for other URIs. 
The *"/restricted"* route will redirect to the *"/denied1"* URI.

The last rule will match any requests matching the *"/users/"* URI pattern. These requests will be redirected to the 
*"/denied2"* URI and the URI of the request will be logged.

Rules can also be used to create white lists. For example, if we wanted to ensure that pages under the *"/public/\*"*
pattern are always visible we could create the following rule group:

```clojure
:access-rules 
[(fn [req] (session/get :user))                          
 {:uri "/public/*"
  :rules [(fn [req] true)]}]
```
