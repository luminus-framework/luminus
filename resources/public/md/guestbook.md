## Installing Leiningen

Installing [Leiningen](https://github.com/technomancy/leiningen) is a simple process which
involves the following steps.

1. Download the script.
3. Set it to be executable. (eg: chmod +x lein)
2. Place it on your $PATH. (eg: ~/bin)
4. Run `lein self-install` and wait for the installation to complete.

```
wget https://raw.github.com/technomancy/leiningen/preview/bin/lein
chmod +x lein
mv lein ~/bin
lein self-install
```

## Creating a new application

In this tutorial we will create a simple guestbook application using Luminus. 
This will provide you with an overview of the basic project structure and development
process.

### Create a new application using Leiningen

```
lein new luminus guestbook
cd guestbook
```

## Anatomy of a Luminus application

The newly created application has the following structure

```
README.md
project.clj
src
  └ guestbook
            └ views
                  └ layout.clj
              routes 
                   └ home.clj
              handler.clj
              util.clj
              server.clj
test
   └ guestbook
             └ test
                 └ handler.clj
resources
        └ public
               └ css
                  └ screen.css
                 img
                 js
                 md
```

First, we have the README.md file, this is where documentation for the application is conventionally put.  
The `project.clj` file is used to manage the project configuration and dependencies by Leiningen.

### The src directory

Our code lives under the `src` folder. A `guestbook` folder has been created for us already.
In this folder we have a `models` folder where we would put the namespaces for the model of our application. 
We also have several namespaces already defined for us as well. 

First, we have `views/layout.clj`, this is where all the common page layout helpers are located. For example,
it's where you would put headers, footers, and the common layout for the pages. 

Next, we have `routes/home.clj`, this is where the routes for our homepage are located. When you add more routes,
such as authentication, or specific workflows you should create namespaces for them here.

The `handler` namespace defines the base routes for the application, this is the entry point into the application 
and any pages we define will have to have their routes added here.

The `util` namespace is used for general helper functions, it comes prepopulated with the `md->html` helper.

Finally, we have the `server` namespace, this is used for running the application in standalone mode.

### The test directory

Here is where we put tests for our application, a couple of sample tests have already been defined for us. 

### The resources directory

This is where we put all the static resoruces for our application. We have folders for CSS, JavaScript, images, and markdown already defined.

## Adding dependencies

As was noted above, all the dependencies are managed via updating the `project.clj` file. 
The project file of the application we've created is found in its root folder and should look as follows:

```clojure
(defproject guestbook "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [lib-noir "0.3.0"]
                 [compojure "1.1.3"]
                 [hiccup "1.0.2"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [bultitude "0.1.7"]
                 [com.taoensso/timbre "1.1.0"]
                 [com.taoensso/tower "1.0.0"]
                 [markdown-clj "0.9.13"]]
  :plugins [[lein-ring "0.7.5"]]
  :ring {:handler guestbook.handler/war-handler
         :init guestbook.handler/init}
  :main guestbook.server
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]
                        [ring/ring-devel "1.1.0"]]}})
```

As you can see the project.clj is simply a Clojure file containing key/value pairs describing different aspects of the application.
Since our application will need to store the comments posted by visitors, we'll add JDBC dependencies to the `:dependencies` vector:

```clojure
[org.clojure/java.jdbc "0.2.3"]
[org.xerial/sqlite-jdbc "3.7.2"]
```

We can now run the project as follows:

```
lein run
Server started on port [ 8080 ].
You can view the site at http://localhost:8080
```

If you browse to [localhost:8080](http://localhost:8080), you should see your application running.

## Accessing the databse with JDBC

First, we will create a model for our application, to do that we'll create a new namespace under 
the `src/guestbook/models` folder. We will call this namespace `db`.
The namespace will live in a file called `db.clj` under `src/guestbook/models` directory.

```clojure
(ns guestbook.models.db
  (:require [clojure.java.jdbc :as sql])
  (:import java.sql.DriverManager))
```

Next, we will create the definition for our database connection. 
The definition is simply a map containing the class for the JDBC driver, 
the protocol and the name of the database file used by SQLite.

```clojure
(def db {:classname  "org.sqlite.JDBC",
         :subprotocol   "sqlite",
         :subname       "db.sq3"})
```

Now that we have a database connection declared, let's write a function which will create the table for storing the guest messages.

```clojure
(defn create-guestbook-table []
  (sql/with-connection
    db
    (sql/create-table
      :guestbook
      [:id "INTEGER PRIMARY KEY AUTOINCREMENT"]
      [:timestamp "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"]
      [:name "TEXT"]
      [:message "TEXT"])
    (sql/do-commands 
      "CREATE INDEX timestamp_index ON guestbook (timestamp)")))
```
With the table created we can write a function to read the messages from the database.

```clojure
(defn read-guests []
  (sql/with-connection
    db
    (sql/with-query-results res 
      ["SELECT * FROM guestbook ORDER BY timestamp DESC"] 
      (doall res))))
```

We'll also need to create a function to add a new row to our guestbook table. 
To do that we'll call `insert-values` and pass it the name and the message to be stored.

```clojure
(defn save-message [name message]
  (sql/with-connection 
    db
    (sql/insert-values
      :guestbook
      [:name :message :timestamp]
      [name message (new java.util.Date)])))
```

### Running code on startup

Since we need to have the database table created in order to access it, 
we'll add the following code to our `server` namespace.

First, we will reference our `db` namespace in the namespace declaration of our handler.

```clojure
(ns guestbook.handler
  (:use guestbook.routes.home
        compojure.core)
  (:require [noir.util.middleware :as middleware]
            [compojure.route :as route]
            [guestbook.models.db :as db]))
```

Then we will update our `init` function to check if our database exists and 
create it if needed.

```clojure
(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (if-not (.exists (new java.io.File "db.sq3"))
    (db/create-guestbook-table))
  (println "guestbook started successfully..."))
```

Since we changed the `init` function of our application, let's restart it by hitting `CTRL+C` and
running `lein run` again.

## Creating pages and handling input from forms

We'll now open up our home namespace located under `routes/home.clj` and add the references for `db`
and `hiccup.form`.

```clojure
(ns guestbook.routes.home
  (:use compojure.core hiccup.form)
  (:require [guestbook.views.layout :as layout]
            [guestbook.models.db :as db]))
```

We'll first write a function to read the guests from the database by calling `db/read-guests` and 
display thm as a list.

```clojure            
(defn show-guests []
  (into [:ul.guests]
        (for [{:keys [message name timestamp]} (db/read-guests)]
          [:li
           [:blockquote message]
           [:p "-" [:cite name]]
           [:time timestamp]])))            
```
Next, we'll add a function to render the home page. Here we create a form 
with text fields named `name` and `message`, these will be sent when the 
form posts to the server as keywords of the same name.

The function accepts optional parameters called name, message, and error. If
these are passed in then they will be rendered by the page. This allows us
to retain the form input in case of an error.

```clojure
(defn home-page [& [name message error]] 
  (layout/common
    [:h1 "Guestbook"]
    [:p "Welcome to my guestbook"]
    [:p error]
    
    ;here we call our show-guests function 
    ;to generate the list of existing comments
    (show-guests)

    [:hr]
   
    (form-to [:post "/"]
      [:p "Name:" (text-field "name" name)]
      [:p "Message:" (text-area {:rows 10 :cols 40} "message" message)]
      (submit-button "comment"))))
```

We'll also need to add a function to save messages to the database. We'll
check if name or message are empty and display the home page with an error.
Otherwise we will save the message to the database and display a fresh page.

```clojure      
(defn save-message [name message]
  (cond 
    
    (empty? name) 
    (home-page name message "Some dummy who forgot to leave a name")
    
    (empty? message) 
    (home-page name message "Don't you have something to say?")
    
    :else 
    (do 
      (db/save-message name message)
      (home-page))))
```

Finally, we need to update our route to pass the form parameters to the `home-page` function,
and create a new route for handling HTTP POST from our form.

```clojure
(defroutes home-routes
  (GET "/"  [name message error] (home-page name message error))
  (POST "/" [name message] (save-message name message)))
``` 

We now have to add these routes to `all-routes` vector in our `handler` namespace for them to be accessible:

```clojure
(def all-routes [home-routes app-routes])
```

Now, if you reload the page in the browser you should be greeted by the guestbook page. 
Try adding a comment in the guestbook to see that it's working correctly.

## Packaging the application

To package our application we simply run

```
lein uberjar
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

For a more complete example you can see the source for
this site on [Github](https://github.com/yogthos/luminus).  