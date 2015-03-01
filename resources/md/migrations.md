## Migrations

By default Luminus uses the [Ragtime](https://github.com/weavejester/ragtime) library for database migration and schema management. When you select the `+mysql`, or `+postgres` profiles a migrations configuration will be added to the `project.clj` file in your application.

### Migrations with Ragtime

Ragtime is invoked using a Leiningen plugin. It needs to be present in the `:plugins` vector in the `project.clj`.

```clojure
:plugins [... [ragtime/ragtime.lein "0.3.4"]]
```

The actual migrations are done by the `ragtime.sql.files` adapter.
The adapter will need to be present in the dependencies list, and
a configuration needs to be specified using the `:ragtime` key as seen below:

```clojure
:dependencies [... [ragtime/ragtime.sql.files "0.3.4"]]
:ragtime {:migrations ragtime.sql.files/migrations
          :database "jdbc:mysql://localhost:3306/example_db?user=root"}
```

The `ragtime.sql.files` adapter expects the SQL migration scripts to be found in the `migrations` directory in the root of the project.
With the directory created we can start adding our migrations SQL scripts there.

The scripts are applied in alphanumeric order and a simple way to keep them ordered is by simply prefixing the current date to the name of the script.

Let's create two scripts, one for the migration and the other for the rollback:

`migrations/2014-13-57-30-create-tables.up.sql`

```
CREATE TABLE users (id INT, name VARCHAR(25));
```

`migrations/2014-13-57-30-create-tables.down.sql`

```
DROP TABLE users;
```

The migrations can now be invoked by running:

```
lein ragtime migrate
```

Rolling back is done by running:

```
lein ragtime rollback
```

### Popular Migrations Alternatives

Below is a list of all the popular migration libraries that are currently available:

* [Drift](https://github.com/macourtney/drift) - Drift is a migration library written in Clojure. Drift works much like Rails migrations where a directory in your project contains all of the migration files. Drift will detect which migration files need to be run and run them as appropriate.
* [Lobos](https://github.com/budu/lobos) - Lobos is a SQL database schema manipulation and migration library written in Clojure. It currently support supports H2, MySQL, PostgreSQL, SQLite and SQL Server.
* [Migratus](https://github.com/pjstadig/migratus) - A general migration framework, with an implementation for database migrations.

