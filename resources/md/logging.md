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


The default logger configuration is found in the `resources/log4j.xml` file and looks as follows:

```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration name="XmlConfig">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="[%d][%p][%c] %m%n"/>
        </Console>
        <RollingFile name="File"
                     fileName="./log/myapp.log"
                     filePattern="./log/myapp-%d{MM-dd-yyyy}-%i.log.gz">
            <PatternLayout pattern="[%d][%p][%c] %m%n"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="10 MB"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
    </Appenders>
    <Loggers>
        <Logger name="org.xnio.nio" level="warn">
            <AppenderRef ref="Console"/>
        </Logger>
        <Logger name="com.zaxxer.hikari" level="warn">
            <AppenderRef ref="Console"/>
        </Logger>
        <Root level="info">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="File"/>
        </Root>
    </Loggers>
</Configuration>
```

An external logging configuration can be provided by setting the `log4j.configurationFile` Java system property
that points to the path of the log configuration file. For example, we could create a production configuration
called `log4j-prod.xml` and have it log to the `/var/log/myapp.log` location.

```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration name="XmlConfig">
    <Appenders>
        <RollingFile name="File"
                     fileName="/var/log/myapp.log"
                     filePattern="/var/log/myapp-%d{MM-dd-yyyy}-%i.log.gz">
            <PatternLayout pattern="[%d][%p][%c] %m%n"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="10 MB"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
    </Appenders>
    <Loggers>
        <Logger name="org.xnio.nio" level="warn">
            <AppenderRef ref="Console"/>
        </Logger>
        <Logger name="com.zaxxer.hikari" level="warn">
            <AppenderRef ref="Console"/>
        </Logger>
        <Root level="info">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="File"/>
        </Root>
    </Loggers>
</Configuration>
```

Then we can start the app with the following flag to have it use this logging configuration:

```
java -Dlog4j.configurationFile=log4j-prod.xml -jar myapp.jar
```

Please refer to the [official documentation](https://logging.apache.org/log4j/2.x/manual/configuration.html) for further information on configuring `log4j`.
