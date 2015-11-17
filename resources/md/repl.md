A typical workflow for developing Clojure applications is to connect the editor to a REPL running an instance
of the application. Luminus provides two ways to connect to the REPL.

## Starting the Application from the REPL

When you run the REPL in the project directory it will switch the `yourapp.core` namespace when it starts. This namespace
contains functions called `start-http-server` and `stop-http-server` that are used start and stop the HTTP server respectively.

The `start-http-server` function accepts a port as its argument:

```clojure
(start-http-server 3000)
```

The `stop-http-server` function takes no argments:

```
(stop-http-server)
```

## Connecting to the nREPL

Luminus also provides an embedded [nREPL](https://github.com/clojure/tools.nrepl) that can be used to connect
the editor to a running instance of the server. A default nREPL port is set in your `project.clj` file for development
under the `:project/dev` profile:

```clojure
:profiles
{...
 :project/dev {...
               :env {:dev        true
                     :port       3000
                     :nrepl-port 7000
                     :log-level  :trace}}}
```

When you run your application using `lein run` it will create a network REPL on the port `7000` and you will be
able to connect your editor to it on `localhost:7000`. Note that the when the `NREPL_PORT` environment variable is
set then nREPL will also be available in production and can be used to inspect the application the same way you would in development.

Please see the [deployment section](/docs/deployment.md#enabling_nrepl) for the instructions on enabling the nREPL for production.
