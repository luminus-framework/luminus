## Logging

Luminus template comes with [clj-log](https://github.com/yogthos/clj-log) dependency 
and a log4.properties setup for logging to the standard out.

Logging is done using Clojure maps and any Clojure data structure can be logged, eg:

```clojure
(ns example
 (:use clj-log.core))

(log :info "foo")

;output

{:ns "clojure.core",
 :time #inst "2012-06-14T21:46:12.980-00:00",
 :message "foo",
 :level :info}


 ;message can be any clojure data structure
 (log :warn {:foo "bar"})

 ;output 

 {:ns "clojure.core",
 :time #inst "2012-06-15T02:55:17.392-00:00",
 :message {:foo "bar"},
 :level :warn}
``` 

clj-log uses the standard logging levels

```clojure
:trace, :debug, :info, :warn, :error, :fatal
```

You can also provide a format string for the log

```clojure
(logf :info "%s accidentally the whole %s" "I" ".jar file")

;output

{:pattern "%s accidentally the whole %s",
 :ns "example",
 :time #inst "2012-06-15T02:25:42.070-00:00",
 :message "I accidentally the whole .jar file",
 :level :info}
 ```
 
 