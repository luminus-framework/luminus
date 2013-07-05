## Database access

Luminus uses [SQL Korma](http://sqlkorma.com/) when you select a database profile such as `+postgres`. 

>[Korma is a domain specific language for Clojure that takes the pain out of working with your favorite RDBMS. Built for speed and designed for flexibility, Korma provides a simple and intuitive interface to your data that won't leave a bad taste in your mouth.](http://sqlkorma.com/)

Adding database support to an existing project is rather simple as well. You will first need to add the Korma dependency
to you `project.clj`:

```clojure
[korma "0.3.0-RC5"]
```
The driver has to be present on the classpath, which means you should include it as a dependency in `project.clj` as well.
For example, if you were connecting to PostreSQL, you would have to include the following dependency in your `project.clj`:

```clojure
[postgresql/postgresql "9.1-901.jdbc4"]
```


Once the dependencies are included you can create a new namespace for your model, coventionally this namespace would be called `models.db`.
There you will need to add a reference to `korma.db`.


#### Setting up the database connection

The first thing we'll need to do is to define our database connection, this can be done by providing a map of connection parameters:

```clojure
(def db-spec {:subprotocol "postgresql"
         :subname "//localhost/my_website"
         :user "admin"
         :password "admin"})
```

Another approach is to specify the JNDI name for a connection managed by the application server:

```clojure
(def db-spec {:name "jdbc/myDatasource"})
```

This can be useful if you have multiple environments in which the application runs in. For example,
if you have dev/staging/production servers, you can point the JNDI connection to their respective databases.
The application will pick up the connection from the environment when it loads, which means that you can
safely deploy the same code in each environment.

Finally, you can provide a JDBC data source, which you configure manually:

```clojure
(def db-spec
  {:datasource
    (doto (new PGPoolingDataSource)
     (.setServerName   "localhost")
     (.setDatabaseName "my_website")
     (.setUser         "admin")
     (.setPassword     "admin")
     (.setMaxConnections 10))})
```

This option is useful if you wish to specify any driver specific parameters directly.


#### Creating tables

Korma depends on [clojure.java.jdbc](https://github.com/clojure/java.jdbc). This library provides 
the ability to manipulate tables with DDL.

You can use the `create-table` function to create the database tables from within the application.

```clojure
(defn create-users-table []
  (sql/with-connection db-spec
    (sql/create-table
      :users
      [:id "varchar(32)"]
      [:pass "varchar(100)"])))
```

The `create-table` call must be wrapped inside `with-connection`, which ensures that the connection
is cleaned up after the function exists.

### Accessing the Database

When using Korma, we first need to to wrap our `db-spec` using `defdb` as follows:

```clojure
(defdb db schema/db-spec)
```

This will create a connection pool for your db spec using the [c3p0](http://sourceforge.net/projects/c3p0/) library.
Note that the last created pool is set as the default for all queries.

Korma uses entities to represent SQL tables. The entities represent the core building blocks of your queries.
These entities are created by using `defentity` macro:

```clojure
(defentity users)
```

Using the users entity we can rewrite our query to create a user as follows:

```clojure
(defn create-user [user]
  (insert users
          (values user)))
```

The get user query would then be rewritten as:

```clojure
(defn get-user [id]
  (first (select users
                 (where {:id id})
                 (limit 1))))
```

For further documentation on Korma and its features please refer to the [official documentation page](http://sqlkorma.com/docs).

Luminus templates for H2 and Postgres default to using Korma for database access.
