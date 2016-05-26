## Configuring the Database

### SQL Korma

Luminus uses [SQL Korma](https://github.com/korma/Korma) when you select a database profile such as `+postgres`.

>[Korma is a domain specific language for Clojure that takes the pain out of working with your favorite RDBMS. Built for speed and designed for flexibility, Korma provides a simple and intuitive interface to your data that won't leave a bad taste in your mouth.](http://sqlkorma.com/)


Adding Korma support to an existing project is rather simple as well. You will first need to add the Korma dependency
to you `project.clj`:

```clojure
[korma "0.4.0"]
```

The driver has to be present on the classpath, which means you should include it as a dependency in `project.clj` as well.
For example, if you were connecting to PostgreSQL, you would have to include the following dependency in your `project.clj`:

```clojure
[postgresql/postgresql "9.3-1102-jdbc41"]
```


Once the dependencies are included you can create a new namespace for your model, coventionally this namespace would be called `db.core`.
There you will need to add a reference to `korma.db`.

```clojure
(ns myapp.db.core
  (:use korma.core
        [korma.db :only (defdb)]))
```

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
     (.setServerName     "localhost")
     (.setDatabaseName   "my_website")
     (.setUser           "admin")
     (.setPassword       "admin")
     (.setMaxConnections 10))})
```

This option is useful if you wish to specify any driver specific parameters directly.


#### Creating tables

By default tables are managed using the SQL migration files found in the `migrations` directory of the application.
Please refer to the [migrations section](/docs/migrations.md) for further documentation on this topic.

It's also possible to use the [clojure.java.jdbc](https://github.com/clojure/java.jdbc) to manipulate tables directly with DDL. This library is already included in Luminus via the Korma dependency.

To create new tables simply use the `create-table-ddl` function from within the application.

```clojure
(defn create-users-table []
  (sql/db-do-commands db-spec
    (sql/create-table-ddl
      :users
      [:id "varchar(32)"]
      [:pass "varchar(100)"])))
```

The `create-table-ddl` call must be wrapped inside `db-do-commands`, which ensures that the connection
is cleaned up after the function exists.

#### Accessing the Database

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

### Yesql

While Korma provides a Clojure DSL for working with SQL, [Yesql](https://github.com/krisajenkins/yesql) is a great alternative that allows to work with SQL directly.

To use Yesql, you'll first have to add the following dependency to your project:

```clojure
[yesql "0.4.0"]
```

Then you simply create a SQL file with the queries that's located somewhere on the `classpath`, such as `resources/queries.sql`. The format for the file is `(<name tag> [docstring comments] <the query>)*` as seen below:

```clojure
-- name: find-users
-- Find the users with the given ID(s).
SELECT *
FROM user
WHERE user_id IN (:id)
AND age > :min_age

-- name: user-count
-- Counts all the users.
SELECT count(*) AS count
FROM user
```

With the file created, you will have to require `yesql.core/defqueries` in your namespace and call it:

```clojure
(ns myapp.db.core
  (:require [yesql.core :refer [defqueries]]))

(defqueries "resources/queries.sql")
```

Each query can now be called by its name like a regular function:

```clojure
(find-users db-spec [1001 1003 1005] 18)
```

See the [official documentation](https://github.com/krisajenkins/yesql) for more details.

