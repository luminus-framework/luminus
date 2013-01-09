## Logging

Luminus template comes with [Timbre](https://github.com/ptaoussanis/timbre) dependency which
logs to standard out at the debug level by default. Any Clojure data structure can be logged
directly.

```clojure
(ns example
 (:use [taoensso.timbre :only [trace debug info warn error fatal]]))

(info "Hello")
=>2012-Dec-24 09:03:09 -0500 Helios.local INFO [timbretest] - Hello

(info {:user {:id "Anonymous"}})

=>2012-Dec-24 09:02:44 -0500 Helios.local INFO [timbretest] - {:user {:id "Anonymous"}}
```

More information is available on the [Github](https://github.com/ptaoussanis/timbre) page for the project.
