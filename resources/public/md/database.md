## Database access

The [clojure.java.jdbc](https://github.com/clojure/java.jdbc) library provides easy access to SQL databases.
To use it you will need to include it as a dependency in your `project.clj` file.

```clojure
[org.clojure/java.jdbc "0.2.3"]
```

Once included you can create a new namespace for your model, coventionally this namespace would be called `models.db`.
There you will need to add a dependency for `clojure.java.jdbc` and import the driver for the database you will be using.
The driver also has to be present on the classpath, which means you should include it as a dependency in `project.clj` as well.

For example, if you were connecting to PostreSQL, you would have to include the following dependency in your `project.clj`:

```clojure
[postgresql/postgresql "9.1-901.jdbc4"]
```

In your `db` namespace, you will need to include `clojure.java.jdbc` as a dependency:

```clojure
(:require [clojure.java.jdbc :as sql])
```

### Setting up the database connection

The first thing we'll need to do is to define our database connection, this can be done by providing a map of connection parameters:

```clojure
(def db {:subprotocol "postgresql"
         :subname "//localhost/my_website"
         :user "admin"
         :password "admin"})
```

Another approach is to specify the JNDI name for a connection managed by the application server:

```clojure
(def db {:name "jdbc/myDatasource"})
```

This can be useful if you have multiple environments in which the application runs in. For example,
if you have dev/staging/production servers, you can point the JNDI connection to their respective databases. 
The application will pick up the connection from the environment when it loads, which means that you can
safely deploy the same code in each environment.

Finally, you can provide a JDBC data source, which you configure manually:

```clojure
(def db
  {:datasource
    (doto (new PGPoolingDataSource)
     (.setServerName   "localhost")
     (.setDatabaseName "my_website")
     (.setUser         "admin")
     (.setPassword     "admin")
     (.setMaxConnections 10))})
```

This option is useful if you wish to specify any driver specific parameters directly.


### Creating tables

You can use the `create-table` function to create the database tables from within the application.

```clojure
(defn create-users-table []
  (sql/with-connection db
    (sql/create-table 
      :users
      [:id "varchar(32)"]      
      [:pass "varchar(100)"])))
```

The `create-table` call must be wrapped inside `with-connection`, which ensures that the connection
is cleaned up after the function exists.

### Selecting records

To select records from the database you can call `with-query-results` 

```clojure
(defn get-user [id]
  (sql/with-connection db
    (sql/with-query-results 
      res ["select * from users where id = ?" id] (first res))))
``` 

When `with-query-results` runs it will bind `res` to the result set. The result wil be
a lazy sequence, which means that you have to force evaluation on it before returning.
This can be done by either calling `doall` or a function such as `first` as is done above.

If you try to return `res` directly, you will get a nil exception because the connection
will be closed when the function returns.


The result will be in a form of a sequence of maps, each map will contain keys matching 
the names of the selected columns.

### Inserting records

Inserting records is accomplished by calling `insert-record` with the keyword representing the
table name and a map representing the record to be inserted. The keys in the map must match
the column names in the table. 

```clojure
(defn create-user [user]
  (with-connection db     
    (sql/insert-record :users user)))
``` 

### Transactions

It's also possible to call statements inside a transaction, which will cause all the queries
to be executed atomically, where if any query fails the rest will be rolled back.

```clojure
(defn write-all []
  (sql/with-connection db
    (sql/transaction
      (get-user "foo")
      (create-user {:id "bar" :pass "baz"}))))
```

***

Further documentation is available on the official [github page](https://github.com/clojure/java.jdbc/tree/master/doc/clojure/java/jdbc).