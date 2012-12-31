## Generating HTML

[Hiccup](https://github.com/weavejester/hiccup) is used as the default templating engine in Luminus.
The advantage of using Hiccup is that we can use the full power of Clojure to generate and manipulate our markup.
This means that you don't have to learn a separate DSL for generating your HTML with its own rules and quirks.

In Hiccup, HTML elements are represented by Clojure vectors and the structure of the element looks as following:

```clojure
[:tag-name {:attribute-key "attribute value"} tag-body]
```

For example, if we wanted to create a div with a paragraph in it, we could write:

```clojure
[:div {:id "hello", :class "content"} [:p "Hello world!"]]
```

which corresponds to the following HTML:

```xml
<div id="hello" class="content"><p>Hello world!</p></div>
```

Hiccup provides shortcuts for setting the id and class of the element, so instead of what we wrote above we could simply write:

```clojure
[:div#hello.content [:p "Hello world!"]]
```

Hiccup also provides a number of helper functions for defining common elements such as forms, links, images, etc. All of these functions simply output vectors in the format described above. This means that if a function doesn't do what you need, you can either write out the literal form for the element by hand or take its output and modify it to fit your needs. Each function which describes an HTML element can also take optional map of attributes as its first parameter:

```clojure
(image {:align "left"} "foo.png")
```

This would result in the following HTML:

```xml
<img align=\"left\" src=\"foo.png\">
```

However, it is best practice to use CSS for the actual styling of the elements in order to keep the structure separate from the representation. 

## Forms and Input

Hiccup also provides helpers for creating HTML forms, here's an example:

```clojure
(form-to [:post "/login"] 
  (text-field {:placeholder "screen name"} "id")
  (password-field {:placeholder "password"} "pass")
  (submit-button "login"))
```

The helper takes a vector with the type of the HTTP request specified as a keyword followed by the URL string, the rest of the arguments should be HTML elements.
The above will generate the following HTML:

```xml
<form action="/login" method="POST">
  <input id="id" name="id" placeholder="screen name" type="text" />
  <input id="pass" name="pass" placeholder="password" type="password" />
  <input type="submit" value="login" />
</form>
```

Finally, Luminus template provides a helper function under the `<yourapp>.util` namespace called `md->html`, 
this function will read a markdown file located in `resources/public/md` folder and return an HTML string. This can
be used in conjunction with Hiccup functions, eg:

```clojure
(:require [<yourapp>.util :as util])

...

(html [:div.contenr [:p (util/md->html "paragraph.md")]])

```
The markdown generation is done by markdown-clj, please see the [Github page](https://github.com/yogthos/markdown-clj) for
details on supported syntax.

## Content caching

lib-noir provides some very basic in-memory content caching via the `cache` macro located in `noir.util.cache`.
To cache a page you can simply do the following:

```clojure
(use 'noir.util.cache)
 
(defn slow-loading-page []
  (cache
   :slow-page
   (common/layout
    [:div "I load slowly"] 
     (parse-lots-of-files))))
```

The cache can be invalidated by calling `invalidate-cache!` and specifying the
key to invalidate.

```clojure
(invalidate-cache! :slow-page)
```

Use `clear-cache!` to clear all items which are currently cached.

```clojure
(clear-cache!)
```

Use `set-cache-timeout!` to set the timeout for items in seconds,
if an item has a lifetime longer than the timeout it will be reloaded.

```clojure
(set-cache-timeout! 10)
```

Finally, you can limit the total size of the cache, when the cache
grows past the specified size the oldest items will be removed to 
make room for new items.

```clojure 
(set-cache-size! 10)
```
