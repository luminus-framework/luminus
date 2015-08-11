Luminus aims to facilitate developing [12 factor](http://12factor.net/) style applications.
The 12 factor approach states that the configuration should be kept separate from the code. The application
should not have to be packaged differently for each environment that it's deployed in.

Luminus projects use the following environment variables by default:

* `PORT` - HTTP port that the application will attempt to bind to, defaults to 3000
* `NREPL_PORT` - when set the application will run the nREPL server on the specified port, defaults to 7000 for development
* `DATABASE_URL` - the URL for the database connection
* `APP_CONTEXT` - used to specify an optional context for the routes in the application

The environment variables are managed by the [Environ](https://github.com/weavejester/environ) library. The library
supports using shell variables as well as Java system properties.

A shell variable could be declared as follows:

```
export DATABASE_URL=jdbc:postgresql://localhost/app?user=app_user&password=secret"
```

A system property can be passed in as a command line argument to `java` using the `-D` flag:

```
java -Ddatabase_url="jdbc:postgresql://localhost/app?user=app_user&password=secret" -jar app.jar
```

Environ takes care of converting the variable names into Clojure style keywords. The variables are lowercased and any `_` and `.`
characters are converted to `-` characters. The following keywords correspond to the above environment variables:

* `:port`
* `:nrepl-port`
* `:database-url`
* `:app-context`

The variables are populated in the `environ.core/env` map and can be accessed as seen in the example below:

```clojure
(ns <app>.db.core
  (:require [environ.core :refer [env]]))

(def database-url
  (env :database-url))
```

