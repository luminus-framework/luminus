## Logging

By default, logging functionality is provided by the [Timbre](https://github.com/ptaoussanis/timbre) library.
The logger is initialized in the `handler/init` function to create a rotating log for the application using the following settings:

```clojure
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info
     :enabled? true
     :async? false ; should be always false for rotor
     :max-message-per-msecs nil
     :fn rotor/appender-fn})
  
  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "your-app-name.log" :max-size 10000 :backlog 10})
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
