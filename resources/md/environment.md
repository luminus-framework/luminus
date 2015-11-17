Luminus aims to facilitate developing [12 factor](http://12factor.net/) style applications.
The 12 factor approach states that the configuration should be kept separate from the code. The application
should not have to be packaged differently for each environment that it's deployed in.

Luminus projects use the following environment variables by default:

* `PORT` - HTTP port that the application will attempt to bind to, defaults to 3000
* `NREPL_PORT` - when set the application will run the nREPL server on the specified port, defaults to 7000 for development
* `DATABASE_URL` - the URL for the database connection
* `APP_CONTEXT` - used to specify an optional context for the routes in the application
* `LOG_PATH` - the path to the application log file, defaults to `<app-name>.log`
* `LOG_LEVEL` - level for logging, defaults to `trace` in dev mode and to `info` in production

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

## Environment Specific Code

Some code, such as development middleware for showing stacktraces in the browser, is dependent on the mode the application
runs in. For example, we'd only want to run the above middleware during development and not show stacktraces to the client
in production.

Luminus uses `env/env/clj` and `env/prod/clj` source paths for this purpose. By default the source path will contain the
`<app>.config` namespace that has the environment specific configuration. The `dev` config looks as follows:

```clojure
(ns myapp.config
  (:require [selmer.parser :as parser]
            [taoensso.timbre :as timbre]
            [<app>.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (timbre/info "\n-=[myapp started successfully using the development profile]=-"))
   :middleware wrap-dev})
```

The config references the `<app>.dev-middleware` namespace found in the same source path. Any development specific middleware
should be placed there.

Meanwhile, the `prod` config will not 
 
```clojure
(ns myapp.config
  (:require [taoensso.timbre :as timbre]))
 
(def defaults
  {:init
   (fn []
     (timbre/info "\n-=[myapp started successfully]=-"))
   :middleware identity})
```

Only the middleware defined in the `<app>.middleware` namespace is run during production.
