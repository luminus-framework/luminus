## Profiles

Running `lein new luminus myapp` will create an application using the default profile template.
However, if you would like to attach further functionality to your template you can append
profile hints for the extended functionality.

Currently, the following profiles are supported

* +auth - adds [Buddy](https://funcool.github.io/buddy/latest/) dependency and authentication middleware
* +cljs - adds ClojureScript support to the project along with an example
* +cucumber - a profile for cucumber with clj-webdriver
* +h2 - adds `db.core` namespace and H2 db dependencies
* +postgres - adds `db.core` namespace and add PostreSQL dependencies
* +mysql - adds `db.core` namespace and add MySQL dependencies
* +mongodb - adds `db.core` namespace and MongoDB dependencies
* +http-kit - adds [HTTP Kit](http://http-kit.org/) support to the project
* +immutant - adds the [Immutant](http://immutant.org/) web server to the project
* +sassc - adds support for [SASS/SCSS](http://sass-lang.com/) files using [SassC](https://github.com/sass/sassc) command line compiler

 
To add a profile simply pass it as an argument after your application name, eg:

```
lein new luminus myapp +cljs
```

You can also mix multiple profiles when creating the application, eg:

```
lein new luminus myapp +cljs +postgres
```

### HTTP Kit notes

HTTP Kit is an embedded server that can be used as a drop in replacement for Jetty. Unlike Jetty, HTTP Kit is not supported via `lein-ring`.
This means that you need to run it using:

```
lein run
```

To allow hot code reloading you need to pass the `-dev` option to the server.
You can also pass it a number to specify a custom port. By default the server runs on port `8080`.

Build a runnable HTTP Kit jar for production use:

```
lein uberjar
```

