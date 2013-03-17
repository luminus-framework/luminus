## Migrations

As you can see Luminus doesn't provide you a migration or a database's scheme management tool.
Nevertheless in Clojure world exist a bunch of options and you can choose whatever you want
depending on your requirements.

Here is list of them with some description:

* [Drift](https://github.com/macourtney/drift) - Drift is a migration library written in Clojure. Drift works much like Rails migrations where a directory in your project contains all of the migration files. Drift will detect which migration files need to be run and run them as appropriate.
* [Migratus](https://github.com/pjstadig/migratus) - A general migration framework, with an implementation for database migrations.
* [Ragtime](https://github.com/weavejester/ragtime) - Ragtime is a Clojure library for migrating structured data. It defines a common interface for expressing migrations, much like Ring defines a common interface for expression web applications.
* [Lobos](https://github.com/budu/lobos) - Lobos is a SQL database schema manipulation and migration library written in Clojure. It currently support supports H2, MySQL, PostgreSQL, SQLite and SQL Server.

If you haven't found you favorite yet you can read following info how to integrate Lobos to an existing Luminus project.
Lobos contains neat DSL to write migrations which is good addition to the Korma SQL syntax.
Other options sush as Ragtime and Migratus use plain SQL by default for writiting
migrations and try to provide more general interface which can be used not only for database
migrations.

Lets' generate sample luminus project.

    lein new luminus ltest +site

First of all add lobos dependency.
Just add this line to the project.clj file:

    [lobos "1.0.0-beta1"]

Then we need to create a `lobos` directory inside our `src` directory.
There we create two files `config.clj` and `migrations.clj` with the following contents:

    (ns lobos.config
      (:use lobos.connectivity)
      (:require [ltest.models.schema :as schema]))

    (open-global schema/db-spec)

and

    (ns lobos.migrations
      (:refer-clojure :exclude
                      [alter drop bigint boolean char double float time])
      (:use (lobos [migration :only [defmigration]] core schema config)))

respectively.

Let's create our first migration, basically convert the `create-users-table` function from
clojure jdbc to lobos.
Just add this migrations definition to the `migations.clj` file and remove `create-users-table`
from `schema.clj`.

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

To add a migration in future just add a `defmigration` definition below.

Next step is fix the `create-table` function which uses `create-users-table` which does't exist anymore
and change the `initialised?` function to use info from Lobos about current state of schema.
We have to change dependencies in `schema.clj` file like that:

    (ns ltest.models.schema
      (:use [lobos.core :only (defcommand migrate)])
      (:require [noir.io :as io]
                [lobos.migration :as lm]))

Then we will define a command which will give us a list of pending migrations.

    (defcommand pending-migrations []
      (lm/pending-migrations db-spec sname))

Using this info we can figure out state of the database.
Let's replace `initialised?` method with `actualized?` function which will return `true`
if there are no pending migrations"

    (defn actualized?
      "checks if there are no pending migrations"
      []
      (empty? (pending-migrations)))

also replace `create-tables` with `actualize` function which will perform a migration process.

    (def actualize migrate)

basically it's just an alias for an existing `migrate` function from the `lobos.core` namespace.

And the last thing it's update the `handler.clj` file to use our new functions:

    (defn init
      "runs when the application starts and checks if the database
       schema exists, calls schema/create-tables if not."
      []
      (if-not (schema/actualized?)
        (schema/actualize)))

Now just run your server and your application will automatically run all pending migrations.

Hope it helps you start using migrations in Luminus if you got stuck you can find all code in
the [repository](https://github.com/edtsech/ltest)
