## Logging

By default, logging functionality is provided by the [clojure.tools.logging](https://github.com/clojure/tools.logging)
library. The library provides macros that delegate to a specific logging implementation.
The default implementation used in Luminus is the [log4j](https://logging.apache.org/log4j/2.x/) library.

There are six log levels in `clojure.tools.logging`, and any Clojure data structures can be logged directly.
The log levels are `trace`, `debug`, `info`, `warn`, `error`, and `fatal`.

```clojure
(ns example
 (:require [clojure.tools.logging :as log]))

(log/info "Hello")
=>[2015-12-24 09:04:25,711][INFO][myapp.handler] Hello

(log/debug {:user {:id "Anonymous"}})
=>[2015-12-24 09:04:25,711][INFO][myapp.handler] {:user {:id "Anonymous"}}

(log/error (Exception. "I'm an error") "something bad happened")
=>[2015-12-24 09:43:47,193][ERROR][myapp.handler] something bad happened
  java.lang.Exception: I'm an error
    	at myapp.handler$init.invoke(handler.clj:21)
    	at myapp.core$start_http_server.invoke(core.clj:44)
    	at myapp.core$start_app.invoke(core.clj:61)
    	...
```

### Logging Configuration


The default logger configuration is found in the `resources/log4j.properties` file and looks as follows:

```
### Direct log4j properties to STDOUT ###
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=[%d][%p][%c] %m%n

log4j.appender.R=org.apache.log4j.RollingFileAppender
log4j.appender.R.File=./log/<<name>>.log

log4j.appender.R.MaxFileSize=100KB
log4j.appender.R.MaxBackupIndex=20

log4j.appender.R.layout=org.apache.log4j.PatternLayout
log4j.appender.R.layout.ConversionPattern=[%d][%p][%c] %m%n

log4j.rootLogger=DEBUG, stdout, R
```

An external logging configuration can be provided by setting the `LOG_CONFIG` environment variable
to the path of the log configuration file. For example, we could create a production configuration
called `log4j-prod.properties` and have it log to the `/var/log/myapp.log` location.

```
log4j.appender.R=org.apache.log4j.RollingFileAppender
log4j.appender.R.File=/var/log/myapp.log

log4j.appender.R.MaxFileSize=100KB
log4j.appender.R.MaxBackupIndex=20

log4j.appender.R.layout=org.apache.log4j.PatternLayout
log4j.appender.R.layout.ConversionPattern=[%d][%p][%c] %m%n

log4j.rootLogger=INFO, R
```

Then we can start the app with the following flag to have it use this logging configuration:

```
java -Dlog_config="log4j-prod.properties" -jar myapp.jar
```

