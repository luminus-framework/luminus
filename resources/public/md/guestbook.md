## Installing Leiningen

Installing [Leiningen](https://github.com/technomancy/leiningen) is a simple process which
involves the following steps.

1. Download the script.
2. Place it on your $PATH. (eg: ~/bin)
3. Set it to be executable. (eg: chmod +x ~/bin/lein)
4. Run `lein self-install` and wait for the installation to complete.

```
wget https://raw.github.com/technomancy/leiningen/preview/bin/lein
mv lein ~/bin
chmod +x lein
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
└guestbook
         └ models
           common.clj
           handler.clj
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
We also have three namespaces already defined for us as well. 

As the name implies, the `common` namespace is used for functions which will be shared across the pages of our application.

The `handler` namespace defines the routes for the application, this is the entry point into the application and any pages
we define will have to have their routes added here.

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
                 [lib-luminus "0.1.0-SNAPSHOT"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [bultitude "0.1.7"]]
  :plugins [[lein-ring "0.7.5"]]
  :ring {:handler guestbook.handler/war-handler}
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
the src/guestbook/models folder. We will call this namespace `db`.
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

First, we will reference our `db` namespace in the namespace declaration of our server.

```clojure
(ns guestbook.server
  (:use guestbook.handler
        ring.adapter.jetty
        [ring.middleware file-info file]
        bultitude.core
        guestbook.models.db)
  (:gen-class))
```

Then we will update our `-main` function to try and create the guestbook table on load, 
if the table already exists then we'll simply catch the exception and continue. 
Obviously, this is not a best practice, but it'll do the job for our toy app.

```clojure
(defn -main [& [port]]    
  ;initialize the database table if needed
  (try
    (guestbook.models.db/create-guestbook-table)
    (catch Exception ex))
  
  (let [port    (if port (Integer/parseInt port) 8080)]    
    (run-jetty (get-handler) {:join? false :port port})
    (println "Server started on port [" port "].")
    (println (str "You can view the site at http://localhost:" port))))
```

Since we changed the `main` function of our application, let's restart it by hitting `CTRL+C` and
running `lein run` again.

## Creating pages and handling input from forms

We will now add a reference to the `db` namespace to our `handler` and add some helper functions to render the messages.
Since we wish to create an input form to submit messages we'll also have to reference `hiccup.form` as well.

```clojure
(ns guestbook.handler
  (:use compojure.core hiccup.form)
  (:require [lib-luminus.middleware :as middleware]
            [compojure.route :as route]
            [guestbook.common :as common]
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
(defn home [& [name message error]] 
  (common/layout 
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
    (home name message "Some dummy who forgot to leave a name")
    
    (empty? message) 
    (home name message "Don't you have something to say?")
    
    :else 
    (do 
      (db/save-message name message)
      (home))))      
```

Finally, we need to update our route to pass the form parameters to the `home` function,
and create a new route for handling HTTP POST from our form.

```clojure
(defroutes app-routes
  (GET "/"  [name message error] (home name message error))
  (POST "/" [name message] (save-message name message))
  (route/resources "/")
  (route/not-found "Not Found"))
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