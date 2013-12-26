## Guestbook Application

This tutorial will guide you through building a simple guestbook application using Luminus.
The guestbook allows users to leave a message and to view a list of messages left by others. 
The application will demonstrate the basics of HTML templating, database access, and 
project architecture. 

### Installing Leiningen

You need to have [Leiningen](http://leiningen.org/) installed in
order to work with Luminus. Installing Leiningen is a simple process which involves 
the following steps.

1. Download the script.
3. Set it to be executable. (eg: chmod +x lein)
2. Place it on your $PATH. (eg: ~/bin)
4. Run `lein self-install` and wait for the installation to complete.

```
wget https://raw.github.com/technomancy/leiningen/stable/bin/lein
chmod +x lein
mv lein ~/bin
lein self-install
```

### Creating a new application

Once you have Leiningen installed you can run the following command in your terminal to 
initialize your application:


```
lein new luminus guestbook +h2
cd guestbook
```

The above will create a new template project with the support for [H2 embedded database](http://www.h2database.com/html/main.html) engine.

We can now run the project as follows:

```
>lein ring server
guestbook started successfully...
2013-03-01 19:05:30.389:INFO:oejs.Server:jetty-7.6.1.v20120215
Started server on port 3000
2013-03-01 19:05:30.459:INFO:oejs.AbstractConnector:Started SelectChannelConnector@0.0.0.0:3000
```

A new browser window will pop up and you should see your application running.
Note that if you didn't want to pop up a new browser you could run:

```
lein ring server-headless
```

You can also pass in a custom port as follows:

```
lein ring server-headless 8000
```

### Anatomy of a Luminus application

The newly created application has the following structure

```
Procfile
README.md
project.clj
src
  └ log4j.xml
    guestbook
       └ handler.clj
         util.clj
         repl.clj
         models
           └ db.clj
             schema.clj
          routes
           └ home.clj
          views
           └ layout.clj
           └ templates
              └ about.html
                base.html
                home.html
test
  └ guestbook
       └ test
           └ handler.clj
resources
  └ public
       └ css
           └ bootstrap-theme.min.css
             bootstrap.min.css
             screen.css     
         fonts
          └ glyphicons-halflings-regular.eot 
            glyphicons-halflings-regular.svg 
            glyphicons-halflings-regular.ttf
            glyphicons-halflings-regular.woff
         img
         js
          └ bootstrap.min.css
         md
          └ docs.md
```

Let's take a look at what the files in the root folder of the application do:

* `Procfile` - used to facilitate Heroku deployments.
* `README.md` - where documentation for the application is conventionally put.
* `project.clj` - used to manage the project configuration and dependencies by Leiningen.

### The Source Directory

All our code lives under the `src` folder. Since our application is called guestbook, this
is the root namespace for project. Let's take a look at all the namespaces that have been created for us.

#### guestbook

* `handler.clj` - defines the base routes for the application, this is the entry point into the application 
and any pages we define will have to have their routes added here
* `repl.clj` - provides functions to start and stop the application from the REPL
* `util.clj` - used for general helper functions, it comes prepopulated with the `md->html` helper
* `log4j.xml` - logging configuration for [Korma](http://sqlkorma.com/)

#### guestbook.models

The `models` namespace is used to define the model for the application and handle the persistence layer.

* `db.clj` - used to house the functions for interacting with the database
* `schema.clj` - used to define the connection parameters and the database tables

#### guestbook.routes

The `routes` namespace is where the routes and controllers for our homepage are located. When you add more routes,
such as authentication, or specific workflows you should create namespaces for them here.

* `home.clj` - a namespace that defines the home and about pages of the application

#### guestbook.views

The `views` namespace defines the visual layout of the application.

* `layout.clj` - a namespace for the layout helpers

#### guestbook.views.templates

This namespace is reserved for the [Selmer](https://github.com/yogthos/Selmer) templates
that represent the application pages.

* `about.html` - the about page
* `base.html` - the base layout for the site
* `home.html` - the home page

### The Test Directory

Here is where we put tests for our application, a couple of sample tests have already been defined for us.

### The Resources Directory

This is where we put all the static resoruces for our application. We have folders for CSS, JavaScript, images, and markdown already defined.

### Adding Dependencies

As was noted above, all the dependencies are managed via updating the `project.clj` file.
The project file of the application we've created is found in its root folder and should look as follows:

```clojure
(defproject
  guestbook
  "0.1.0-SNAPSHOT"
  :dependencies
  [[com.h2database/h2 "1.3.174"]
   [ring-server "0.3.1"]
   [environ "0.4.0"]
   [com.taoensso/timbre "2.7.1"]
   [markdown-clj "0.9.35"]
   [korma "0.3.0-RC6"]
   [com.taoensso/tower "2.0.0"]
   [selmer "0.5.3"]
   [org.clojure/clojure "1.5.1"]
   [log4j
    "1.2.17"
    :exclusions
    [javax.mail/mail
     javax.jms/jms
     com.sun.jdmk/jmxtools
     com.sun.jmx/jmxri]]
   [compojure "1.1.6"]
   [lib-noir "0.7.6"]
   [com.postspectacular/rotor "0.1.0"]]
  :ring
  {:handler guestbook.handler/app,
   :init guestbook.handler/init,
   :destroy guestbook.handler/destroy}
  :profiles
  {:production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}},
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.2.1"]],
    :env {:selmer-dev true}}}
  :url
  "http://example.com/FIXME"
  :aot
  :all
  :plugins
  [[lein-ring "0.8.7"] [lein-environ "0.4.0"]]
  :description
  "FIXME: write description"
  :min-lein-version "2.0.0")
```

As you can see the project.clj is simply a Clojure list containing key/value pairs describing different aspects of the application.
If you need to add any custom dependencies simply append them to the `:dependencies` vector.


### Accessing the Database

First, we will create a model for our application, to do that we'll open up the `schema.clj` file located
under the `src/guestbook/models` folder.


Here, we can see that we already have the definition for our database connection.
The definition is simply a map containing the class for the JDBC driver, the protocol,
user, password, and the name of the database file used by the H2 database.

```clojure
(def db-spec {:classname "org.h2.Driver"
              :subprotocol "h2"
              :subname (str (io/resource-path) db-store)
              :user "sa"
              :password ""
              :naming {:keys clojure.string/upper-case
                       :fields clojure.string/upper-case}})
```

Next, we have a function called `create-users-table` with a definition for a table called `users`.
We'll replace this function with a `create-guestbook-table` function instead:

```clojure
(defn create-guestbook-table []
  (sql/with-connection
    db-spec
    (sql/create-table
      :guestbook
      [:id "INTEGER PRIMARY KEY AUTO_INCREMENT"]
      [:timestamp :timestamp]
      [:name "varchar(30)"]
      [:message "varchar(200)"])
    (sql/do-commands
      "CREATE INDEX timestamp_index ON guestbook (timestamp)")))
```

The guestbook table will store all the fields describing the message, such as the name of the
commenter, the content of the message and a timestamp.

We'll also update the `create-tables` function to call it:

```clojure
(defn create-tables
  "creates the database tables used by the application"
  []
  (create-guestbook-table))
```

With the table created we can write functions to read and write the messages in our guestbook.
Let's open the `db.clj` file and add them there. Again, we see that there's already some code
here to work with the `users` table. We'll replace it with the following code instead:

```clojure
(ns guestbook.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [guestbook.models.schema :as schema]))

(defdb db schema/db-spec)

(defentity guestbook)

(defn save-message
  [name message]
  (insert guestbook
          (values {:name name
                   :message message
                   :timestamp (new java.util.Date)})))

(defn get-messages []
  (select guestbook))
```

Above we create an entity to represent the guestbook table we created in the `schema` namespace.
Then we add a functions called `save-message` and `get-messages` to interact with it.

### Running Code on Startup

The `handler` namespace contains a function called `init`. This function will be called
once when the application starts. Let's add the code to check if the database has been initialized
and initialize it if necessary.

We'll first need to reference the `schema` namespace in order to use the `initialized?` and `create-tables` functions
from there.

```clojure
(ns guestbook.handler
  (:use ...)
  (:require ...
            [guestbook.models.schema :as schema]))
```

Next, we can update the `init` function as follows:

```clojure
(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info
     :enabled? true
     :async? false ; should be always false for rotor
     :max-message-per-msecs nil
     :fn rotor/append})

  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "guestbook.log" :max-size (* 512 1024) :backlog 10})

  (if (env :selmer-dev) (parser/cache-off!))
  
  ;;initialize the database if needed
  (if-not (schema/initialized?) (schema/create-tables))
  
  (timbre/info "guestbook started successfully"))
```

Since we changed the `init` function of our application,
let's restart it by hitting `CTRL+C` and running `lein ring server` again.

### Creating Pages and Handling Form Input

Our routes are defined in the `guestbook.routes.home` namespace. Let's open it up and add the logic for
rendering the messages from the database. We'll first need to add a reference to our `db` namespace:

```clojure
(ns guestbook.routes.home
  (:use ...)
  (:require ...
            [guestbook.models.db :as db]))
```

Then we'll change the `home-page` controller to look as follows:

```clojure
(defn home-page [& [name message error]]
  (layout/render "home.html"
                 {:error    error
                  :name     name
                  :message  message
                  :messages (db/get-messages)}))
```

All we did here was update it to send some extra parameters to the template, one of them being
a list of messages from the database.

Since we'd like the users to be able to post new messages, we'll add a controller to handle
the form posts:

```clojure
(defn save-message [name message]
  (cond

    (empty? name)
    (home-page name message "Somebody forgot to leave a name")

    (empty? message)
    (home-page name message "Don't you have something to say?")

    :else
    (do
      (db/save-message name message)
      (home-page))))
```

Finally, we'll add a route for this controller to our `home-routes` definition:

````clojure
(defroutes home-routes
  (GET "/" [] (home-page))
  (POST "/" [name message] (save-message name message))
  (GET "/about" [] (about-page)))
```

Now that we have our controllers setup, let's open the `home.html` template located under the `guestbook.views.templates` namespace.
Currenlty, simply renders the contents of the `content` variable inside the content block:

```xml
{% extends "guestbook/views/templates/base.html" %}
{% block content %}
 <div class="jumbotron">
    <h1>Welcome to guestbook</h1>
    <p>Time to start building your site!</p>
    <p><a class="btn btn-primary btn-lg" href="http://luminusweb.net">Learn more &raquo;</a></p>
 </div>

 <div class="row-fluid">
    <div class="span8">
    {{content|safe}}
    </div>
 </div>
{% endblock %}
```

We'll update our `content` block to iterate over the messages and print each one in a list:

```xml
{% extends "guestbook/views/templates/base.html" %}
{% block content %}
 <div class="jumbotron">
    <h1>Welcome to guestbook</h1> 
 </div>

 <div class="row-fluid">
    <div class="span8">    
      <ul>
      {% for item in messages %}
        <li>
          <time>{{item.timestamp|date:"yyyy-MM-dd HH:mm"}}</time> 
          <p>{{item.message}}</p>
          <p> - {{item.name}}</p>      
        </li>
      {% endfor %}
      </ul>
    </div>
 </div>
{% endblock %}

```

As you can see above, we use a for iterator to walk the messages.
Since each message is a map with the message, name, and timestamp keys, we can access them by name.
Also, notice the use of the `date` filter to format the timestamps into a human readable form.

Next, we'll add an error block for displaying errors that might be populated by the controller:

```xml
{% if error %}
<p class="error">{{error}}</p>
{% endif %}
```

Here we simply check if the error field was populated and display it.
Finally, we'll create a form to allow users to submit their messages:

```xml
<form action="/" method="POST">
    <p>
       Name: 
       <input type="text" name="name" value={{name}}>
    </p>
    <p>
       Message: 
       <textarea rows="4" cols="50" name="message">
           {{message}}
       </textarea>
    </p>
    <input type="submit" value="comment">
</form>
```

Our final `home.html` template should look as follows:

```xml
{% extends "guestbook/views/templates/base.html" %}
{% block content %}
 <div class="jumbotron">
    <h1>Welcome to guestbook</h1> 
 </div>

 <div class="row-fluid">
    <div class="span8">    
      <ul>
      {% for item in messages %}
        <li>
          <time>{{item.timestamp|date:"yyyy-MM-dd HH:mm"}}</time> 
          <p>{{item.message}}</p>
          <p> - {{item.name}}</p>      
        </li>
      {% endfor %}
      </ul>
        
      {% if error %}
        <p class="error">{{error}}</p>
      {% endif %}
    </div>
    <div class="span8">
      <form action="/" method="POST">
        <p>
         Name: 
         <input type="text" name="name" value="{{name}}" />
        </p>
        <p>
         Message: 
         <textarea rows="4" cols="50" name="message" />{{message}}</textarea>
        </p>
        <input type="submit" value="comment" />
      </form>        
    </div>    
 </div>
{% endblock %}
```

Finally, we can update the `screen.css` file located in the `resources/public/css` folder to format our form nicer:

```
form {
    width: 200px;
    clear: both;
}
form input {
    width: 50%;
    clear: both;
}
```

Now, if you reload the page in the browser you should be greeted by the guestbook page.
Try adding a comment in the guestbook to see that it's working correctly.


## Packaging the application

To package our application we simply run

```
lein ring uberjar
```

This will create a runnable jar which can be run with:

```
java -jar target/guestbook-0.1.0-SNAPSHOT-standalone.jar
```

If we wanted to deploy our application to an app server such as Apache Tomcat, we could run

```
lein ring uberwar
```

The resulting war archive can now be deployed to the server of your choosing.

***

Complete source listing for the tutorial is available [here](https://github.com/yogthos/guestbook). 
For a more complete example you can see the source for this site on [Github](https://github.com/yogthos/luminus).
