## Logging

By default, logging functionality is provided by the [Timbre](https://github.com/ptaoussanis/timbre) library.
The logger is initialized in the `handler/init` function to create a rotating log for the application using the following settings:

```clojure
(timbre/merge-config!
  {:level     ((fnil keyword :info) (env :log-level))
   :appenders {:rotor (rotor/rotor-appender
                        {:path (or (env :log-path) "myapp.log")
                         :max-size (* 512 1024)
                         :backlog 10})}})
```

Timbre can log any Clojure data structures directly.

```clojure
(ns example
 (:use [taoensso.timbre :only [trace debug info warn error fatal]]))

(info "Hello")
=>2012-Dec-24 09:03:09 -0500 Helios.local INFO [timbretest] - Hello

(info {:user {:id "Anonymous"}})

=>2012-Dec-24 09:02:44 -0500 Helios.local INFO [timbretest] - {:user {:id "Anonymous"}}
```

More information is available on the [Github](https://github.com/ptaoussanis/timbre) page for the project.
