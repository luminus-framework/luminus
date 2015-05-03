A typical workflow for developing Clojure applications is to connect the editor to a REPL running an instance
of the application. Luminus provides two ways to connect to the REPL.

## Starting the Application from the REPL

When you run the REPL in the project directory it will switch the `yourapp.core` namespace when it starts. This namespace
contains functions called `start-server` and `stop-server` that are used start and stop the HTTP server respectively.

The `start-server` function accepts a port as its argument:

```clojure
(start-server 3000)
```

The `stop-server` function takes no argments:

```
(stop-server)
```

## Connecting to the nREPL

Luminus also provides an embedded [nREPL](https://github.com/clojure/tools.nrepl) that can be used to connect
the editor to a running instance of the server. In order to enable it simply uncomment the following line in
your `project.clj` file.

```clojure
;:env {:repl-port 7001}
```

When you run your application using `lein run` it will create a network REPL on the port `7001` and you will be
able to connect your editor to it on `localhost:7001`. Note that the nREPL will also be available in production
and can be used to inspect the application the same way you would in development.
