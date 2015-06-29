## Migrations

By default Luminus uses the [Migratus](https://github.com/yogthos/migratus) library for database migration and schema management. When you select the `+mysql`, or `+postgres` profiles a migrations configuration will be added to the `profiles.clj` file in your application.

### Migrations with Migratus

The development database configuration should placed in the `profiles.clj` file. This file specifies your local configuration and should **not** be checked into the source repository.

```clojure
{:provided {:env {:database-url "jdbc:h2:./myapp_dev.db"}}}
```

For production, the configuration is expected to be present in the environment. An example would be to
have a shell variable called `DATABASE_URL` that points to the URL. See the [official Environ documentation](https://github.com/weavejester/environ) for the complete list of configuration options.

Migratus is invoked from the `-main` function in the `<app>.core` namespace of the application.

By default, the SQL migration scripts are expected to be found in the `resources/migrations` directory in the root of the project. A custom directory can be set in the `<app>.db.migrations` namespace. With the directory created we can start adding our migrations SQL scripts there.

Migration ids are not assumed to be incremented integers and are considered for completion independently. The recommended way to keep migrations ordered is by prefixing the current date to the name of the script. 

Let's create two scripts, one for the migration and the other for the rollback:

`resources/migrations/201506120401-add-users-table.up.sql`

```
CREATE TABLE users (id INT, name VARCHAR(25));
```

`resources/migrations/201506120401-add-users-table.down.sql`

```
DROP TABLE users;
```

The migrations can now be invoked by running:

```
lein run migrate
```

Rolling back is done by running:

```
lein run rollback
```

### Popular Migrations Alternatives

Below is a list of all the popular migration libraries that are currently available:

* [Drift](https://github.com/macourtney/drift) - Drift is a migration library written in Clojure. Drift works much like Rails migrations where a directory in your project contains all of the migration files. Drift will detect which migration files need to be run and run them as appropriate.
* [Lobos](https://github.com/budu/lobos) - Lobos is a SQL database schema manipulation and migration library written in Clojure. It currently support supports H2, MySQL, PostgreSQL, SQLite and SQL Server.
* [Ragtime](https://github.com/weavejester/ragtime) - a general migration framework, with an implementation for database migrations.
* [Joplin](https://github.com/juxt/joplin) - Joplin is a library for flexible datastore migration and seeding

