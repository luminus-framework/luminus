## Migrations

By default Luminus doesn't provide any tools for database migration or scheme management.
Nevertheless, there is a number of data base migration options available for Clojure and
they can be easily integrated into a Luminus project.

Below is a list of all the popular migration libraries currently available:

* [Drift](https://github.com/macourtney/drift) - Drift is a migration library written in Clojure. Drift works much like Rails migrations where a directory in your project contains all of the migration files. Drift will detect which migration files need to be run and run them as appropriate.
* [Migratus](https://github.com/pjstadig/migratus) - A general migration framework, with an implementation for database migrations.
* [Ragtime](https://github.com/weavejester/ragtime) - Ragtime is a Clojure library for migrating structured data. It defines a common interface for expressing migrations, much like Ring defines a common interface for expression web applications.
* [Lobos](https://github.com/budu/lobos) - Lobos is a SQL database schema manipulation and migration library written in Clojure. It currently support supports H2, MySQL, PostgreSQL, SQLite and SQL Server.

In this guide we'll see how to integrate the Lobos migration library with Luminus. Lobos provides 
a powerful DSL for writing migrations and makes a nice addition to the Korma SQL syntax.

First, let's generate sample Luminus project.

    lein new luminus ltest +site

We'll then add the Lobos dependency to the `project.clj` file:

```clojure
[lobos "1.0.0-beta1"]
```

Then we need to create a `lobos` directory under the `src` directory of the project.
There we'll create two files `config.clj` and `migrations.clj` with the following contents:

```clojure
(ns lobos.config
  (:use lobos.connectivity)
  (:require [ltest.models.schema :as schema]))
    (open-global schema/db-spec)
```

and

```clojure
(ns lobos.migrations
  (:refer-clojure 
   :exclude [alter drop bigint boolean char double float time])
  (:use (lobos [migration :only [defmigration]] core schema config)))
```

respectively.

Let's create our first migration to convert the `create-users-table` function from
clojure jdbc to lobos. This is done by simply removing the `create-users-table` from
`schema.clj` and adding the following definition to `migations.clj`:

```clojure
(defmigration add-users-table
  (up [] (create
          (table :users
                 (varchar :id 20 :primary-key)
                 (varchar :first_name 30)
                 (varchar :last_name 30)
                 (varchar :email 30)
                 (boolean :admin)
                 (time    :last_login)
                 (boolean :is_active)
                 (varchar :pass 100))))
  (down [] (drop (table :users))))
```

To add other migrations in future just add another `defmigration` definition such as the one above.


Now that we have a migration defined, let's take a look at the `schema` namespace and update it
to use it. First, we'll add the `lobos.migration` dependency to the namespace declaration:

```clojure
(ns ltest.models.schema
  (:use [lobos.core :only (defcommand migrate)])
  (:require [noir.io :as io]
            [lobos.migration :as lm]))
```

Then we will define a command to give us a list of pending migrations:

```clojure
(defcommand pending-migrations []
  (lm/pending-migrations db-spec sname))
```

Using this info we can figure out state of the database.
Let's rename the `initialised?` function to `actualized?` and have it return `true`
if there are no pending migrations:

```clojure
(defn actualized?
  "checks if there are no pending migrations"
  []
  (empty? (pending-migrations)))
```

We'll also replace the `create-tables` function with the `actualize` function which will perform a migration process:

```clojure
(def actualize migrate)
```

Basically it's just an alias for an existing `migrate` function from the `lobos.core` namespace.

The last thing we need to do is to update the `handler.clj` file to use our new functions:

```clojure
(defn init
  "runs when the application starts and checks if the database
   schema exists, calls schema/create-tables if not."
  []
  (if-not (schema/actualized?)
    (schema/actualize)))
```

Now just run your server and your application will automatically run all the pending migrations.

Hopefully this guide will help you get started using migrations in Luminus. A complete code listing is
available [in this repository](https://github.com/edtsech/ltest).
