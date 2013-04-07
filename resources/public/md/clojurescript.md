## Using ClojureScript in Your Project

### Adding ClojureScript

The easiest way to add ClojureScript support is by using the `+cljs` flag when making a new project. However, it's quite easy to add it to an existing project as well.

This is done by adding the following sections to your `project.clj` file.

```clojure
:plugins [...
          [lein-cljsbuild "0.3.0"]]  

:hooks [... leiningen.cljsbuild]

:cljsbuild
{:builds [{:id "dev"
           :source-paths ["src-cljs"] 
           :compiler {:pretty-print true 
                      :output-to "resources/public/cljs/site.js"
                      :output-dir "resources/public/cljs/"                         
                      :optimizations :none}}             
          {:id "prod"
           :source-paths ["src-cljs"] 
           :compiler {:output-to "resources/public/cljs/site.js"
                      :optimizations :advanced}}]}   
```

The above will add the `cljsbuild` plugin and hook for your project as well as the build configuration.

All the namespaces should live in the `src-cljs` directory under the root of your project. Note that ClojureScript files **must** end with the `.cljs` extension. If the file ends with `.clj` it will still compile, but it will not have access to the JavaScript namespace.

### Interacting with JavaScript

All the global JavaScript functions and variables are available via the `js` namespace. For example, if we wanted to log something to the console we could write the following:

```clojure
(.log js/console "hello world!")
```

As you can see this is very similar to using the Java interop in Clojure.

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

If we use a Js library in our code we must protect the names of any functions we call from it as well. For example, if we wanted to use the [AlbumColors](https://github.com/chengyin/albumcolors) library, we would write the following:

```clojure
(defn set-background [style [r g b]]  
  (set! (.-backgroundColor style)
        (str "rgb(" r "," g "," b ")")))

(defn ^:export init []
  (.getColors (js/AlbumColors. "/img/foo.jpg") 
    (fn [[background]]
     (set-background (.-style div) background))))
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
      :output-to "resources/public/cljs/site.js"
      ;;specify the externs file to protect function names
      :externs ["resources/externs.js"]
      :optimizations :advanced}}
```

### Working With the DOM

There are several libraries available for accessing and modifying DOM elements. We'll take a look at how to use [Domina](https://github.com/levand/domina) and [Dommy](https://github.com/Prismatic/dommy) to accomplish some basic tasks.

#### Domina

Domina is a lightweight library for selecting and manipulating DOM elements as well as handling events. 

##### Selecting Elements

The elements can either be selected using Xpath or CSS style selectors.

##### Modifying Elements

Domina provides several helpers for 

##### Events

#### Dommy

##### Templating

### Ajax

A simple way to work with Ajax is by using the `XhrIo` provided by the Google Closure library that ClojureScript depends on. Let's take a look at an example:

```clojure
(ns ajax
  (:require [goog.net.XhrIo :as xhr]
            [goog.Uri :as uri]))

(defn default-handler [handler] 
  (fn [response]
    (if handler 
      (let [result (js->clj 
                     (.getResponseText (.-target response))
                     :keywordize-keys true)]
        (handler result)))))

(defn params-to-str [params]
  (let [query-data (uri/QueryData.)] 
    (doseq [[k v] params] 
      (if (coll? v)
        (.setValues query-data (str k "[]") (apply array v))
        (.setValues query-data k v)))
    (.toString query-data)))
                        
(defn ajax-request [rel-url method handler params]
  (xhr/send ;prepend the servlet context to the URL
            (str js/context rel-url) 
            (default-handler handler) 
            method 
            (params-to-str params)))
            
(defn GET
  ([rel-url] (GET rel-url nil))
  ([rel-url handler & params]
    (ajax-request rel-url "GET" handler params)))

(defn POST
  ([rel-url] (POST rel-url nil))
  ([rel-url handler & params]
    (ajax-request rel-url "POST" handler params)))
```

Above, we define a function to make the request called `ajax-request`. This function takes the url, the method, the handler, and the params. The params are then converted to a string using `goog.Uri`. The method is a string specifying the HTTP method we'd like to use to make the request. Finally, the handler is the function which will be called when we receive a response from the server.

Let's say we'd like to call the server and get a list of messages to display on our page. We could do this with the `ajax` namespace as follows:

```clojure
(ns main
  (:require ajax
            [domina :as dom]
            [dommy.template :as template]))

(defn render-message [{:keys [message user]}]
  [:li [:p message "-" user]])
  
(defn render-messages [messages]
  (->> messages
       (map render-message)         
       (into [:ul])
       template/node
       .-outerHTML
       (append! (dom/by-id "messages"))))
        
(defn ^:export init []
  (ajax/GET "/messages" render-messages))  
```

On the server we would have:

```clojure
(def messages
  [{:message "Hello world"
    :user    "Foo"}
   {:message "Ajax is fun"
    :user    "Bar"}])

(defroutes fetch-routes  
  (GET "/messages" [] (response/json messages)))
```

#### Using EDN

EDN is an alternative data notation to JSON that uses native Clojure data types. When using EDN we don't have to worry about converting between Clojure data structures and JSON notation when making our Ajax calls. Switching to using EDN is very simple:

```clojure
(ns ajax
  (:require ... [cljs.reader :as reader]))

(defn default-handler [handler] 
  (fn [response]
    (if handler 
      (let [result (reader/read-string ;parse the EDN data structure
                     (.getResponseText (.-target response)))]
        (handler result)))))

```

Now we simply change our response type to EDN in our service route:

```clojure
(defroutes fetch-routes  
  (GET "/all-sketches" [] (response/json (get-sketches))))
```


