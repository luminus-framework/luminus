## Guestbook Application

This tutorial will guide you through building a simple guestbook application using Luminus.
The guestbook allows users to leave a message and to view a list of messages left by others.
The application will demonstrate the basics of HTML templating, database access, and
project architecture.

If you don't have a preferred Clojure editor already, then it's recommended that you use [Light Table](http://www.lighttable.com/) to follow along with this tutorial.

### Installing Leiningen

You need to have [Leiningen](http://leiningen.org/) installed in
order to work with Luminus. Installing Leiningen is accomplished by
following the step below.

1. Download the script.
3. Set it to be executable. (eg: chmod +x lein)
2. Place it on your $PATH. (eg: ~/bin)
4. Run `lein` and wait for the self-installation to complete.

```
wget https://raw.github.com/technomancy/leiningen/stable/bin/lein
chmod +x lein
mv lein ~/bin
lein
```

### Creating a new application

Once you have Leiningen installed you can run the following command in your terminal to
initialize your application:

```
lein new luminus guestbook +h2
cd guestbook
```

The above will create a new template project with the support for [H2 embedded database](http://www.h2database.com/html/main.html) engine.

### Anatomy of a Luminus application

The newly created application has the following structure:

```
guestbook
|____.gitignore
|____Procfile
|____project.clj
|____README.md
|
|____src
| |____guestbook
|   |____handler.clj
|   |____layout.clj
|   |____middleware.clj
|   |____session.clj
|   |____routes
|   | |____home.clj
|   |____db
|     |____core.clj
|
|____test
| |____guestbook
|   |____test
|     |____handler.clj
|
|____resources
| |____templates
| | |____about.html
| | |____base.html
| | |____home.html
| |____public
| | |____css
| | | |____screen.css
| | |____img
| | |____js
| |____docs
| | |____docs.md
| |____sql
|   |____queries.sql
|
|____migrations
  |____201501155317-add-users-table.down.sql
  |____201501155317-add-users-table.up.sql
```

Let's take a look at what the files in the root folder of the application do:

* `Procfile` - used to facilitate Heroku deployments
* `README.md` - where documentation for the application is conventionally put
* `project.clj` - used to manage the project configuration and dependencies by Leiningen
* `.gitignore` - a list of assets, such as build generated files, to exclude from Git

### The Source Directory

All our code lives under the `src` folder. Since our application is called guestbook, this
is the root namespace for project. Let's take a look at all the namespaces that have been created for us.

#### guestbook

* `handler.clj` - defines the base routes for the application, this is the entry point into the application
* `layout.clj` - a namespace for the layout helpers used to render the content for our pages
* `middleware.clj` - a namespace that contains custom middleware for the application
* `session.clj` - a namespace for creating managing in-memory sessions

#### guestbook.db

The `db` namespace is used to define the model for the application and handle the persistence layer.

* `core.clj` - used to house the functions for interacting with the database

#### guestbook.routes

The `routes` namespace is where the routes and controllers for our home and about pages are located. When you add more routes, such as authentication, or specific workflows you should create namespaces for them here.

* `home.clj` - a namespace that defines the home and about pages of the application

### The Test Directory

Here is where we put tests for our application, a couple of sample tests have already been defined for us.

### The Resources Directory

This is where we put all the static resources for our application. Content in the `public` directory under `resources` will be served to the clients by the server. We can see that some CSS resources have already been created for us.

#### HTML templates

The templates directory is reserved for the [Selmer](https://github.com/yogthos/Selmer) templates
that represent the application pages.

* `about.html` - the about page
* `base.html` - the base layout for the site
* `home.html` - the home page

#### SQL Queries

The SQL queries are found in the `resources/sql` folder.

* `queries.sql` - defines the SQL queries and their associated function names

### The Migrations Directory

Luminus uses [Ragtime](https://github.com/weavejester/ragtime) for managing migrations. Migrations are managed using up and down SQL files. The files are conventionally versioned using the date and will be applied in order.

* `201501155317-add-users-table.up.sql` - migrations file to create the tables
* `201501155317-add-users-table.down.sql` - migrations file to drop the tables


### The Project File

As was noted above, all the dependencies are managed via updating the `project.clj` file.
The project file of the application we've created is found in its root folder and should look as follows:

```clojure
(defproject guestbook "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring-server "0.3.1"]
                 [selmer "0.8.0"]
                 [com.taoensso/timbre "3.3.1"]
                 [com.taoensso/tower "3.0.2"]
                 [markdown-clj "0.9.62"]
                 [environ "1.0.0"]
                 [im.chit/cronj "1.4.3"]
                 [ring/ring-defaults "0.1.3"]
                 [ring/ring-session-timeout "0.1.0"]
                 [ring-middleware-format "0.4.0"]
                 [noir-exception "0.2.3"]
                 [bouncer "0.3.2"]
                 [prone "0.8.0"]
                 [ragtime "0.3.8"]
                 [yesql "0.5.0-rc1"]
                 [com.h2database/h2 "1.4.182"]]

  :min-lein-version "2.0.0"
  :uberjar-name "guestbook.jar"
  :repl-options {:init-ns guestbook.handler}
  :jvm-opts ["-server"]

  :plugins [[lein-ring "0.9.0"]
            [lein-environ "1.0.0"]
            [lein-ancient "0.5.5"]
            [ragtime/ragtime.lein "0.3.8"]]

  :ring {:handler guestbook.handler/app
         :init    guestbook.handler/init
         :destroy guestbook.handler/destroy
         :uberwar-name "guestbook.war"}

  :ragtime
  {:migrations ragtime.sql.files/migrations
   :database "jdbc:h2:./site.db"}

  :profiles
  {:uberjar {:omit-source true
             :env {:production true}
             :aot :all}
   :production {:ring {:open-browser? false
                       :stacktraces?  false
                       :auto-reload?  false}}
   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.3.2"]
                        [pjstadig/humane-test-output "0.6.0"]]
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]
         :env {:dev true}}})
```

As you can see the project.clj is simply a Clojure list containing key/value pairs describing different aspects of the application.

The most common task is adding new libraries to the project. These libraries are specified using the `:dependencies` vector. In order to use a new library in our project we simply have to add its dependency here.

The items in the `:plugins` vector can be used to provide additional functionality such as migrations support via Ragtime plugin.

The `:profiles` contain a map of different project configurations that are used to initialize it for either development or production builds.

Please refer to the official Leiningen documentation for further details on structuring the `project.clj` build file.


### Creating the Database

First, we will create a model for our application, to do that we'll open up the `<date>-add-users-table.up.sql` file located under the `migrations` folder. The file has the following contents:

```sql
CREATE TABLE users
(id VARCHAR(20) PRIMARY KEY,
 first_name VARCHAR(30),
 last_name VARCHAR(30),
 email VARCHAR(30),
 admin BOOLEAN,
 last_login TIME,
 is_active BOOLEAN,
 pass VARCHAR(100));
```

We'll replace the `users` table with one that's more appropriate for our application:

```sql
CREATE TABLE guestbook
(id INTEGER PRIMARY KEY AUTO_INCREMENT,
 name VARCHAR(30),
 message VARCHAR(200),
 timestamp TIMESTAMP);
```

The guestbook table will store all the fields describing the message, such as the name of the
commenter, the content of the message and a timestamp. We'll save the file and run the following
command from the root of our project:

```
lein ragtime migrate
```

If everything went well we should now have our database initialized.

### Accessing The Database

Next, we'll take a look at the `src/guestbook/db/core.clj` file.
Here, we can see that we already have the definition for our database connection.

```clojure
(ns guestbook.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [clojure.java.io :as io]))

(def db-store (str (.getName (io/file ".")) "/site.db"))

(def db-spec
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname     db-store
   :make-pool?  true
   :naming      {:keys   clojure.string/lower-case
                 :fields clojure.string/upper-case}})

(defqueries "sql/queries.sql" {:connection db-spec})
```

The database is expected to be stored in a file called `site.db` relative to the folder from where the application is run.

The functions that map to database queries are generated when `defqueries` is called. As we can see it references the `sql/queries.sql` file. This location is found under the `resources` folder. Let's open up this file and take a look inside.

```sql
--name: create-user!
-- creates a new user record
INSERT INTO users
(id, first_name, last_name, email, pass)
VALUES (:id, :first_name, :last_name, :email, :pass)

--name: update-user!
-- update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- name: get-user
-- retrieve a used given the id.
SELECT * FROM users
WHERE id = :id
```

As we can see each function is defined using the comment that starts with `--name:` followed by the name of the function. The next comment provides the doc string for the function and finally we have the body that's plain SQL. The parameters are denoted using `:` notation. Let's replace the existing queries with some of our own:


```sql
--name:save-message!
-- creates a new message
INSERT INTO guestbook
(name, message, timestamp)
VALUES (:name, :message, :timestamp)

--name:get-messages
-- selects all available messages
SELECT * from guestbook
```

### Running the Application

We can run our application in development mode using the [lein-ring](https://github.com/weavejester/lein-ring) plugin that's already bundled with our project. The plugin is invoked as seen below.

```
>lein ring server
guestbook started successfully...
2013-03-01 19:05:30.389:INFO:oejs.Server:jetty-7.6.1.v20120215
Started server on port 3000
2013-03-01 19:05:30.459:INFO:oejs.AbstractConnector:Started SelectChannelConnector@0.0.0.0:3000
```

Once server starts, a new browser window will pop up and you should see your application running.
Note that if you didn't want to open a new browser you could run:

```
lein ring server-headless
```

You can also pass in a custom port as follows:

```
lein ring server-headless 8000
```

Note that the page is prompting us to run the migrations in order to initialize the database. However, since we've already done that earlier and we won't need to do that again.

### Creating Pages and Handling Form Input

Our routes are defined in the `guestbook.routes.home` namespace. Let's open it up and add the logic for
rendering the messages from the database. We'll first need to add a reference to our `db` namespace along with
references for [Bouncer](https://github.com/leonardoborges/bouncer) validators and [ring.util.response](http://ring-clojure.github.io/ring/ring.util.response.html)

```clojure
(ns guestbook.routes.home
  (:require
    ...
    [guestbook.db.core :as db]
    [bouncer.core :as b]
    [bouncer.validators :as v]
    [ring.util.response :refer [redirect]]))
```

Next, we'll create a function to validate the form parameters.

```clojure
(defn validate-message [params]
  (first
    (b/validate
      params
      :name v/required
      :message [v/required [v/min-count 10]])))
```

The function uses the Bouncer `validate` function to check that the `:name` and the `:message` keys
conform to the rules we specified. Specifically, the name is required and the message must contain at least
10 characters. Buddy uses vector syntax to pass multiple rules to a validator as is the case with the message validator.

When a validator takes additional parameters as is the case with `min-count` the vector syntax is used as well. The value will be passed in implicitly as the first parameter to the validator.

We'll now add a function to validate and save messages:

```clojure
(defn save-message! [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (-> (redirect "/")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/save-message! (assoc params :timestamp (java.util.Date.)))
      (redirect "/"))))
```

The function will grab the `:params` key from the request that contains the form parameters. When the `validate-message` functions returns errors we'll redirect back to `/`, we'll associate a `:flash` key with the response where we'll put the supplied parameters along with the errors. Otherwise, we'll save the message in our database and redirect.

Finally, we'll change the `home-page` handler function to look as follows:

```clojure
(defn home-page [{:keys [flash]}]
  (layout/render
   "home.html"
   (merge {:messages (db/get-messages)}
          (select-keys flash [:name :message :errors]))))
```

The function renders the home page template and passes it the currently stored messages along with any parameters from the `:flash` session, such as validation errors.

Our routes will now have to pass the request to both the `home-page` and the `save-message!` handlers:

```clojure
(defroutes home-routes
  (GET "/" request (home-page request))
  (POST "/" request (save-message! request))
  (GET "/about" [] (about-page)))
```

Don't forget to refer `POST` from `compojure.core`

```
(ns guestbook.routes.home
  (:require ...
            [compojure.core :refer [defroutes GET POST]]
            ...))
```

Now that we have our controllers setup, let's open the `home.html` template located under the `resources/templates` directory. Currently, it simply renders the contents of the `content` variable inside the content block:

```xml
{% extends "base.html" %}
{% block content %}
  <div class="jumbotron">
    <h1>Welcome to guestbook</h1>
    <p>Time to start building your site!</p>
    <p><a class="btn btn-primary btn-lg" href="http://luminusweb.net">Learn more &raquo;</a></p>
  </div>

  <div class="row">
    <div class="span12">
    {{docs|markdown}}
    </div>
  </div>
{% endblock %}
```

We'll update our `content` block to iterate over the messages and print each one in a list:

```xml
{% extends "base.html" %}
{% block content %}
  <div class="jumbotron">
    <h1>Welcome to guestbook</h1>
    <p>Time to start building your site!</p>
    <p><a class="btn btn-primary btn-lg" href="http://luminusweb.net">Learn more &raquo;</a></p>
  </div>

  <div class="row">
    <div class="span12">
        <ul class="messages">
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

As you can see above, we use a `for` iterator to walk the messages.
Since each message is a map with the message, name, and timestamp keys, we can access them by name.
Also, notice the use of the `date` filter to format the timestamps into a human readable form.

Finally, we'll create a form to allow users to submit their messages. We'll populate the name and message values if they're supplied and render any errors associated with them. Note that the forms also uses the `csrf-field` tag that's required for cross-site scripting protection.

```xml
<div class="row">
    <div class="span12">
        <form method="POST" action="/">
                {% csrf-field %}
                <p>
                    Name:
                    <input class="form-control"
                           type="text"
                           name="name"
                           value="{{name}}" />
                </p>
                {% if errors.name %}
                <div class="alert alert-danger">{{errors.name|join}}</div>
                {% endif %}
                <p>
                    Message:
                <textarea class="form-control"
                          rows="4"
                          cols="50"
                          name="message">{{message}}</textarea>
                </p>
                {% if errors.message %}
                <div class="alert alert-danger">{{errors.message|join}}</div>
                {% endif %}
                <input type="submit" class="btn btn-primary" value="comment" />
        </form>
    </div>
</div>
```

Our final `home.html` template should look as follows:

```xml
{% extends "base.html" %}
{% block content %}
<div class="row">
    <div class="span12">
        <ul class="messages">
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
<div class="row">
    <div class="span12">
        <form method="POST" action="/">
                {% csrf-field %}
                <p>
                    Name:
                    <input class="form-control"
                           type="text"
                           name="name"
                           value="{{name}}" />
                </p>
                {% if errors.name %}
                <div class="alert alert-danger">{{errors.name|join}}</div>
                {% endif %}
                <p>
                    Message:
                <textarea class="form-control"
                          rows="4"
                          cols="50"
                          name="message">{{message}}</textarea>
                </p>
                {% if errors.message %}
                <div class="alert alert-danger">{{errors.message|join}}</div>
                {% endif %}
                <input type="submit" class="btn btn-primary" value="comment" />
        </form>
    </div>
</div>
{% endblock %}
```

Finally, we can update the `screen.css` file located in the `resources/public/css` folder to format our form nicer:

```
body {
	height: 100%;
	padding-top: 70px;
	font: 14px 'Helvetica Neue', Helvetica, Arial, sans-serif;
	line-height: 1.4em;
	background: #eaeaea;
	width: 550px;
	margin: 0 auto;
}

.messages {
  background: white;
  width: 520px;
}
ul {
	list-style: none;
}

li {
	position: relative;
	font-size: 16px;
	padding: 5px;
	border-bottom: 1px dotted #ccc;
}

li:last-child {
	border-bottom: none;
}

li time {
	font-size: 12px;
	padding-bottom: 20px;
}

form, .error {
	width: 520px;
	padding: 30px;
	margin-bottom: 50px;
	position: relative;
  background: white;
}

```

When we reload the page in the browser we should be greeted by the guestbook page.
We can test that everything is working as expected by adding a comment in our comment form.


## Packaging the application

The application can be packaged for standalone deployment by running the following command:

```
lein ring uberjar
```

This will create a runnable jar that can be run as seen below:

```
java -jar target/guestbook.jar
```

If we wanted to deploy our application to an app server such as Apache Tomcat, we would package it as `uberwar` instead:

```
lein ring uberwar
```

The resulting archive can be deployed to any Servlet container.

***

Complete source listing for the tutorial is available [here](https://github.com/yogthos/guestbook).
For a more complete example you can see the source for this site on [Github](https://github.com/yogthos/luminus).
