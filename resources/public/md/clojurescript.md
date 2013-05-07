ClojureScript is an excellent alternative to JavaScript for client side application logic. Some of the advantages of using ClojureScript include:

* use the same language on both the client and the server
* share common code between the front-end and back-end
* cleaner and more consistent language
* dependency management via Leiningen
* immutable data structures
* powerful standard library

### Adding ClojureScript Support

The easiest way to add ClojureScript support is by using the `+cljs` flag when making a new project. However, it's quite easy to add it to an existing project as well. This is done by adding the following sections to your `project.clj` file.

```clojure
:plugins [...
          [lein-cljsbuild "0.3.0"]]  

:hooks [... leiningen.cljsbuild]

:cljsbuild
{:builds [{:source-paths ["src-cljs"] 
           :compiler {:output-to "resources/public/js/site.js"
                      :optimizations :advanced}}]}   
```

The above will add the `cljsbuild` plugin and hook for your project as well as the build configuration.

All the namespaces should live in the `src-cljs` directory under the root of your project. Note that ClojureScript files **must** end with the `.cljs` extension. If the file ends with `.clj` it will still compile, but it will not have access to the `js` namespace.

### Using Libraries

One advantage of using ClojureScript is that it allows managing your client-side libraries using Leiningen. ClojureScript libraries are included under dependencies in the `project.clj` just like any other library.

### Running the Compiler

The easiest way to develop ClojureScript applications is to run the compiler in `auto` mode. This way any changes you make in your namespaces will be recompiled automatically and become immediately available on the page. To start the compiler in this mode simply run:

```
lein cljsbuild auto
```

To compile the application for production use the `once` options. This will compile all the scripts into a single `Js` output file that will be included in your project:

```
lein cljsbuild once
```

### Advanced Compilation and Exports

During advanced compilation variable names will be munged by the compiler to shorten the code. If we wish to expose any functions to JavaScript we have to ensure that their names are protected. This is done by using the `^:export` annotation, eg:

```clojure
(ns main)

(defn ^:export init []
  (js/alert "hello world"))
```

We can now call this function from our page like any other:

```html
<script>
main.init();
</script>
``` 

If we use a Js library in our code we must protect the names of any functions we call from it as well. For example, if we wanted to use the [AlbumColors](https://github.com/chengyin/albumcolors) library, we could write the following:

```clojure
(defn ^:export init []  
  (.getColors (js/AlbumColors. "/img/foo.jpg") 
    (fn [[background]]
     (.log js/console background))))
```

However, when the script is compiled with the `:advanced` flag, the `AlbumColors` and `getColors` will be munged.

To protect them we have to create a Js file with the names we'd like to protect and reference it in our build: 

```javascript
var AlbumColors = {};
AlbumColors.getColors = function() {};
```
If we put the above code in a file called `externs.js` under the `resources` directory then we would reference it in our `cljsbuild` section as follows: 

```clojure
{:source-paths ["src-cljs"]
     :compiler
     {:pretty-print false
      :output-to "resources/public/js/site.js"
      ;;specify the externs file to protect function names
      :externs ["resources/externs.js"]
      :optimizations :advanced}}
```

### Interacting with JavaScript

All the global JavaScript functions and variables are available via the `js` namespace.

#### Method Calls

```clojure
(.method object params)

(.log js/console "hello world!")
```

#### Accessing Properties

```clojure
(.-property object)

(.-style div)
``` 

#### Setting Properties

```clojure
(set! (.-property object))

(set! (.-color (.-style div) "#234567"))
```

For examples of ClojureScript synonyms of common JavaScript operations see the [Himera documentation](http://himera.herokuapp.com/synonym.html).

### Working With the DOM

There are several libraries available for accessing and modifying DOM elements. In particular, you may wish  to take a look at the [Domina](https://github.com/levand/domina) and [Dommy](https://github.com/Prismatic/dommy). Domina is a lightweight library for selecting and manipulating DOM elements as well as handling events. Dommy is a templating library similar to Hiccup.

### Ajax

Luminus uses [cljs-ajax](https://github.com/yogthos/cljs-ajax) for handling Ajax operations. The library provides an easy way to send Ajax queries to the server using `ajax-request`, `GET`, and `POST` functions.

#### ajax-request

The `ajax-request` is the base request function that accepts the following parameters:

* uri - the URI for the request
* method - a string representing the HTTP request type, eg: "PUT", "DELETE", etc.
* format - a keyword indicating the response format, can be either `:json` or `:edn`, defaults to `:edn`
* handler - success handler, a function that accepts the response as a single argument
* error-handler - error handler, a function that accepts a map representing the error with keys `:status and `:status-text`
* params - a map of params to be sent to the server

#### GET/POST helpers

The `GET` and `POST` helpers accept a URI followed by a map of options:

* `:handler` - the handler function for successful operation should accept a single parameter which is the deserialized response
* `:error-handler` - the handler function for errors, should accept a map with keys `:status` and `:status-text`
* `:format` - the format for the response `:edn` or `:json` defaults to `:edn`
* `:params` - a map of parameters that will be sent with the request


```clojure
(ns foo
  (:require [ajax.core :refer [GET POST]]))

(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console 
    (str "something bad happened: " status " " status-text)))

(GET "/hello")

(GET "/hello" {:handler handler})

(POST "/hello")

(POST "/send-message" 
        {:params {:message "Hello World"
                  :user    "Bob"}
         :handler handler
         :error-handler error-handler})
```
