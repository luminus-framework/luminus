## Managing Component Lifecycle

The management of stateful components, such as database connections, is handled by the [mount](https://github.com/tolitius/mount) library.
The library handles the lifecycle of such resources within the application ensuring that any such resources are started
and stopped as necessary.

Luminus encourages keeping related domain logic close together. Therefore, in cases where we have functions that
reply on an external resource the management of the state for that resource should be handled in the same namespace
where the functions using it are defined.
 
Stateful components belong to the namespace they're declared in. To create a component we need to reference
the `mount.core/defstate` macro in the namespace definition and then use it as follows:

```clojure
(ns myapp.resource
  (:require [mount.core :refer [defstate]]))

(defn connect []
  ;;open-a-remote-connection should return the connection instance
  {:state :connected})
  
(defn disconnect [conn]
  (assoc conn :state :disconnected))

(defstate conn
  :start (connect)
  :stop (disconnect conn))
```

When the component is started the function bound to the `:start` key will be called. It's result will be used as the value
for the state. In the example above, the `conn` will contain the map returned by the `connect` function.

When the component is shut down then the function bound to the `:stop` key is called. This function must accept the
current state of the var. The function is expected to clean up any external resources before the component is
shutdown.

The component dependencies are inferred from the namespace hierarchy. If namespace `a` references namespace `b` then
the component specified using `defstate` in namespace `b` will be started before the one specified in namespace `a`.
When the system is shutdown the `:stop` functions for each state are called in the reverse order.

For example, we may have one namespace that loads the configuration and another that used the configuration to connect
to a database. This could be expressed as follows:

```clojure
(ns app.config
  (:require [mount.core :refer [defstate]]))

(defstate app-config
  :start (load-config "config.edn"))
```

The `app.config` namespace loads the config into the `app-config` state var. We can now access this config in a different
namespace:

```clojure
(ns app.db
  (:require [mount.core :refer [defstate]]
            [app.config :refer [app-config]]))

(defn connect! [] ...)

(defn disconnect! [conn] ...)

(defstate conn :start (connect! app-config)
               :stop (disconnect! conn))
```

The component hierarchy is initialized by calling `mount.core/start` and stopped with `mount.core/stop`. This is done by the
 `<app>.handler/init` and `<app>.handler/destroy` functions respectively.

These functions can also be used from the REPL to reload the system in a clean state without having to restart the REPL
as seen below:

```clojure
(ns repl
  (:require [clojure.tools.namespace.repl :as tn]
            [mount.core :as mount]))

(defn go
  "starts all states defined by defstate"
  []
  (mount/start)
  :ready)

(defn reset
  "stops all states defined by defstate, reloads modified source files, and restarts the states"
  []
  (mount/stop)
  (tn/refresh :after 'repl/go))
```

The states can be started selectively by explicitly providing the namespaces to be started and stopped to the `start`
and `stop` functions:

```clojure
(mount.core/start #'app.config #'app.db)

(mount.core/stop #'app.config #'app.db)
```

Alternatively, it's possible to specify the namespaces that should be omitted from the lifecycle using the
`start-without` function:

```clojure
(start-without #'app.db)
```

Finally, the states can be replaced by alternate ones such as mock states for testing using the `start-with` function:

```clojure
(start-with #'app.db #'app.test.mock-db)
```

In the above example, the `app.db` will be replaced by the `app.test.mock-db` when the the components are loaded.


