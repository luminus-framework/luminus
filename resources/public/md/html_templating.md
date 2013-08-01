## Templating Options

Luminus comes with the [Hiccup](https://github.com/weavejester/hiccup) dependency. If you're familiar with Hiccup then
you can start using it out of the box.

Hiccup uses standard Clojure data structures to represent its templates. On top of that, Hiccup provides a rich API of 
helper functions for generating common HTML elements.

Luminus also packages [Selmer](https://github.com/yogthos/Selmer) as it provides a more familiar templating expereince
for newcomers. 

You can choose to use either templating engine or combine them. Alternatively, you can choose to use a different templating
engine alltogether. A couple of popular options are [Enlive](https://github.com/cgrand/enlive) and [Stencil](https://github.com/davidsantiago/stencil).

## HTML Templating Using Selmer

Selmer is a Clojure implementation of the [Django template language](https://docs.djangoproject.com/en/1.4/topics/templates/).
If you're familiar with Django or similar templating languages such as [Smarty](http://www.smarty.net/) or [CheetahTemplate](http://www.cheetahtemplate.org/), you should feel right at home.

### Creating Templates

By design, Selmer separates the presentation logic from the program logic. The templates
are simply HTML files with additional template tags. Let's take a look at a an example 
template below:

```xml
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>My First Template</title>
    </head>
    <body>
        <h2>Hello {{name}}</h2>
    </body>
</html>
``` 

The templates are rendered using a context represented by a map of key/value pairs. The context contains
the variables that we'd like to render in our template at runtime. Above, we have a template representing 
a page that renders a single variable called `name`. 

There are two functions for rendering templates called `render` and `render-file`. 
The `render` function accepts a string representing the template.
The `render-file` function accepts a string representing the path to the file containing the template. 


If we saved the template defined above in a file called `index.html` then we could render it as follows:

```clojure
(ns example.routes.home
  (:use [selmer.parser :only [render-file]]))
  
(defn index [request]
  (render-file "example/views/templates/index.html" 
               {:name "John"}))  
``` 

The `render-file` function expects the templates to be found at a path relative 
to the `src` folder of the application.

Above, we passed in a string as the value for the variable `name`. 
However, we're not restricted to strings and can pass in any type we like. 
For example, if we pass in a collection we can iterate it using the `for` tag:

```xml
<ul>
{% for item in items %}
<li> {{item}} </li>
{% endfor %}
</ul>
```

```clojure
(render-file "/example/views/templates/items.html 
             {:items (range 10)})
```

If an item happens to be a map, we can access the keys by their name as follows:


```clojure
(render "<p>Hello {{user.first}} {{user.last}}</p>" 
        {:user {:first "John" :last "Doe"}})
```

When no special processing is specified in the template then the `.toString` value
of the paramter will be used.

By default Selmer caches the compiled template. A recompile will be triggered if the last modified timestamp of the files changes.
 Alternatively you can turn caching on and off calling `(selmer.parser/cache-on!)` and `(selmer.parser/cache-off!)` respectively. 

### Filters

Filters allow post processing the variables before they are rendered. For example, you
can use a filter to convert the variable to upper case, compute a hash, or count the length.
Filters are specified by using a `|` after the variable name, as can be seen below:

```clojure
{{name|upper}}
```

The following filters are currently available:

*upper* - converts a string to upper case 

`{{name|upper}}`

*date* - formats an instance of java.util.Date 

`{{my-date|date:"yyyy-MM-dd_HH:mm:ss"}}`

*hash* - computes a hash of the variable (md5, sha, sha256, sha384, sha512) 

`{{name|hash:md5}}`

*count* - counts the length of the variable 

`{{name|count}}`

*pluralize* - returns pluralization of the word 

`{{items|count}} item{{items|pluralize}}`

*json* - renders a Clojure data structure as JSON 

`{{data|to-json}}`

*block.super* - will be replaced with the content from the parent block

`{{block.super}}` - used inside a block to insert the content from the parent block in its place

`{% block foo %} {{block.super}} some content{% endblock %}`

### Defining Custom Filters

You can easily add your own filters using the `selmer.filters/add-filter!` function.
The filter function should accept the element and return a value that will replace the
original value.

```clojure
(use 'selmer.filters)
 
(add-filter! :embiginate #(.toUpperCase %))
 (render "{{shout|embiginate}}" {:shout "hello"})

(add-filter! :count count)
(render "{{foo|count}}" {:foo (range 3)})
```

Filters can also be chained together as needed:

```clojure
(add-filter! :empty? empty?)
(render "{{foo|upper|empty?}}" {:foo "Hello"})
```

### Tags

Selmer provides two types of tags. The frist kind are inline tags such as the `extends` 
and `include` tags. These tags are self contained statements and do not require an end tag.
The other type is the block tags. These tags have a start and an end tag, and operate on a
block of text. An example of this would be the `if` ... `endif` block.

Let's take a look at the default tags:

*extends* - used to indicate that the template extends another template

`{% extends "base.html" %}`

*include* - used to include a block from a different template file

`{% include "form.html" %}`

*block/endbock* - used to specify a block that can be overriden by a block from different template with the same name

`{% block form %}{% endblock %}`

*if/endif* - used to test a condition, the if portion is rendered only if the condition is true

`{% if error %} <p>An error occurred: {{error}}</p>{% endif %}`

*ifequal/endequal* - same as if tag except that it checks that its arguments are equal

`{% ifequal foo bar %}yes!{% endifequal %}`

*else* - can be used inside the `if` and `ifequal` tags to specify the content for the negative case 

`{% if condition %}yes!{% else %}no!{% endif %}`

*for/endfor* - used to iterate over collections of items

`{% for story in stories %} <h2>{{story.name}}</h2>{% endfor %}`

### Defining Custom Tags

In addition to tags already provides you can easily define custom tags of your own. This
is done by using the `add-tag!` macro. Let's take a look at a couple of examples to
see how it works:

```clojure
(use 'selmer.parser)
 
(add-tag! :foo
  (fn [args context-map]
    (str "foo " (first args))))
 
(render "{% foo quux %} {% foo baz %}" {}) 


(add-tag! :bar
  (fn [args context-map content]
    (str content))
  :baz :endbar)
 
(render "{% bar %} some text {% baz %} some more text {% endbar %}" {})
```

As can be seen above, the tag is defined by providing a keyword specifying the tag name followed
by the handler and any closing tags.

When there are no closing tags the tag will not have any content. The handler for such tags accepts
the arguments defined in the tag and the context map.

When closing tags are present then the content for each block will be keyed on the opening tags.
The content will be a map containing the `:args` and `:content` keys associated with each block.

### Template inheritance

Selmer templates can refer to other templates using the `block` tag. There are two ways
to refer to a template. We can either use the `extends` tag or the `include` tag for this.


#### Extending Templates

When we use the `extends` tag, the current template will use the template it's extending
as the base. Any blocks in the base template with the names matching the current template will 
be overwritten.

The content of the child template **must** be encapsulated in blocks. Any content outside the
blocks present in the parent templates will be ignored.

Let's take a look at a concrete example. First, we'll define our base template and call it
`base.html`:

```xml
<!DOCTYPE html>
<head>
    <link rel="stylesheet" href="style.css" />
    <title>{% block title %}My amazing site{% endblock %}</title>
</head>

<body>
    <div id="content">
        {% block content %}{% endblock %}
    </div>
</body>
</html>
```

Then we'll create a new template called `home.html` that will extend `base.html` as follows:

```xml
{% extends "base.html" %}

{% block content %}
    {% for entry in entries %}
        <h2>{{ entry.title }}</h2>
        <p>{{ entry.body }}</p>
    {% endfor %}
{% endblock %}
``` 

When the `home.html` is rendered the `content` block will display the entries. However, since
we did not define a block for the title, the one from `base.html` will be used.

Note that you can chain extended templates together. In this case the latest occurrence of a block tag
will be the one that's rendered. 

#### Including Templates

The `include` tag allows including blocks from other templates in the current template. Let's take a look
at an example. Let's say we have a `base.html` template that includes templates named `register.html` and 
`home.html`, then defines blocks called `register` and `home`:

```xml



<!DOCTYPE html>
<head>
    <link rel="stylesheet" href="style.css" />
    <title>{% block title %}My amazing site{% endblock %}</title>
</head>

<body>
    <div id="content">
        {% if user %}
        {% include "templates_path/home.html" %}        
        {% else %}
        {% include "templates_path/register.html" %}
        {% endif %}
    </div>
</body>
</html>
```

We can now define the content for these blocks in separate template files called `register.html`:

```xml
{% block register %}
<form action="/register" method="POST">
    <label for="id">user id</label>
    <input id="id" name="id" type="text"></input>
    <input pass="pass" name="pass" type="text"></input>
    <input type="submit" value="register">
</form>
{% endblock %}
```

and `home.html`:

```xml
{% block home %}
<h1>Hello {{user}}</h1>
{% endblock %}
```

When the `base.html` is rendered it will replace the `register` and `home` include tags with the content
from the templates they are referencing.

For more details please see the [official documentation](https://github.com/yogthos/Selmer).

## HTML Templating Using Hiccup

[Hiccup](https://github.com/weavejester/hiccup) is a popular HTML templating engine for Clojure.
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

### Forms and Input

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
this function will read a markdown file relative to `resources/public/` folder and return an HTML string. This can
be used in conjunction with Hiccup functions, eg:

```clojure
(:require [<yourapp>.util :as util])
...
(html [:div.contenr [:p (util/md->html "/md/paragraph.md")]])
```

The markdown generation is done by markdown-clj, please see the [Github page](https://github.com/yogthos/markdown-clj) for
details on supported syntax.

## Content caching

lib-noir provides basic in-memory content caching via the `cache!` macro located in `noir.util.cache`.
To cache a page you can simply do the following:

```clojure
(require '[noir.util.cache :as cache])

(defn slow-loading-page []
  (cache/cache!
   :slow-page
   (common/layout
    [:div "I load slowly"]
     (parse-lots-of-files))))
```

The cache can be invalidated by calling `invalidate!` and specifying the
key to invalidate.

```clojure
(cache/invalidate! :slow-page)
```

Use `clear!` to clear all items which are currently cached.

```clojure
(cache/clear!)
```

Use `set-timeout!` to set the timeout for items in seconds,
if an item has a lifetime longer than the timeout it will be reloaded.

```clojure
(cache/set-timeout! 10)
```

Finally, you can limit the total size of the cache using `set-size!`, when the cache
grows past the specified size the least recently used items will be removed to
make room for new items.

```clojure
(cache/set-size! 10)
```

Note that cache checks if the operation is successful when reloading. This means that if
the operation, such as fetching a remote file, fails then the current cached value is kept.

