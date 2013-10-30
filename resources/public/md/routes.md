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

```clojure
(def app (middleware/app-handler
           ;;add your application routes here
           [home-routes app-routes]
           ;;add custom middleware here
           :middleware []
           ;;add access rules here
           ;;each rule should be a vector
           :access-rules []))
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

By default restricted routes will be checked to see if they match all the access rules that apply.

### Specifying Access Rules

Let's take a look at how to create a rule to specify that restricted routes should only be 
accessible if the `:user` key is present in the session. 

First, we'll need  to reference `noir.util.route` and `noir.session` in the handler.

```clojure
(ns myapp.handler  
  (:require ... 
            [noir.util.route :refer [restricted]]
            [noir.session :as session]))
```

Next, we'll write the function that implements the rule we described above. This function 
must accept the request map as its argument and return a truthy value indicating whether 
the page satisfies the specified rule.

Here's a function to check if there is a user currently in the session. If the user is 
`nil` then the rule will trigger a redirect. By default rules redirect to the `"/`" URI.

```clojure
(defn user-access [request]
  (session/get :user))

(def app 
 (middleware/app-handler 
   [app-routes]
   :access-rules [user-access]))
```

Now, any restricted handlers will redirect to `"/"` unless there is a `:user` key in the session.

### Access Rule Groups

When specifying rules as a map you can provide further directives using the
following keys:

* `:uri` - the URI pattern to which the rules apply (optional, defaults to any URI)
* `:uris` - alternative to :uri, allows specifying a collection of URIs (optional)
* `:redirect` - the redirect target for the rules (optional defaults to "/")
* `:on-fail` - alternative to redirect, allows specifying a handler function for
               handling the failure, the function must accept the request as a
               parameter (optional)
* `:rule` - a single rule (either :rule or :rules is required)
* `:rules` - alternative to rule, allows specifying a list of rules

The `:rules` can be specified in any of the following ways:

* `:rules [rule1 rule2]`
* `:rules {:any [rule1 rule2]}`
* `:rules {:every [rule1 rule2] :any [rule3 rule4]}`

By default every rule has to pass, the `:any` key specifies that it's sufficient for any of the rules to pass. Here's some examples of access rule combinations:

```clojure
(defn admin-access [req]
 (session/get :admin))

:access-rules [{:redirect "/access-denied"
                :rule user-access}]

:access-rules [{:uris ["/user/*" "/private*"]
                :rule user-access}]

:access-rules [{:uri "/admin/*" :rule admin-access}
               {:uri "/user/*" 
                :rules {:any [user-access admin-access]}]

:access-rules [{:on-fail (fn [req] "access restricted")
                :rule user-access}]
```

### Cross Site Request Forgery Protection

CSRF attack involves a third party performing an action on your site using the credentials of a logged-in user.
This can commonly occur when your site contains malicious a link, a form button, or some JavaScript.

To protect against CSRF attacks use the [Ring-Anti-Forgery](https://github.com/weavejester/ring-anti-forgery).

To do this we will first need to include the `[ring-anti-forgery "0.2.1"]` dependency in your project. Then we'll
reference the required libraries to the handler namespace definition.

```clojure
(ns myapp.handler
  (:require
    ...
    [selmer.parser :refer [add-tag!]]
    [ring.util.anti-forgery :refer [anti-forgery-field]]
    [ring.middleware.anti-forgery :refer [wrap-anti-forgery]])
```

Next, we'll add the `wrap-anti-forgery` middleware to our handler:

```clojure
(def app (middleware/app-handler
           ;;add your application routes here
           [home-routes app-routes]
           ;;add custom middleware here
           :middleware [wrap-anti-forgery]
           ;;add access rules here
           ;;each rule should be a vector
           :access-rules []))
```

Once the middleware is added a randomly-generated string will be assigned to the *anti-forgery-token* var.
Any POST requests coming to the server will have to contain a paremeter called `__anti-forgery-token` with 
this token.

We can then define a new CSRF tag in our `init` function:

```clojure
(defn init
  ...
  (add-tag! :csrf-token (fn [_ _] (anti-forgery-field)))
  ...)
```

and start using it in our templates as follows:

```xml
<form name="input" action="/login" method="POST">
  {% csrf-token %}
  Username: <input type="text" name="user">
  Password: <input type="password" name="pass">
<input type="submit" value="Submit">
</form>
```

Any POST requests that do not contain the token will be rejected by the middleware. The server will
respond with a 403 error saying "Invalid anti-forgery token".
