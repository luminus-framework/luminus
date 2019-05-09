## Profiles

Running `lein new luminus myapp` will create an application using the default profile template.
However, if you would like to attach further functionality to your template you can append
profile hints for the extended functionality.

### Routing

* +reitit - adds [Reitit](https://metosin.github.io/reitit/) Clojure/Script router support

### web servers

Luminus defaults to using the [Immutant](http://immutant.org/) webserver, the following
alternative servers are supported:

* +aleph - adds [Aleph](https://github.com/ztellman/aleph) server support to the project
* +jetty - adds [Jetty](https://github.com/mpenet/jet) support to the project
* +http-kit - adds the [HTTP Kit](http://www.http-kit.org/) web server to the project

### databases

* +h2 - adds `db.core` namespace and H2 db dependencies
* +sqlite - adds `db.core` namespace and SQLite db dependencies
* +postgres - adds `db.core` namespace and add PostreSQL dependencies
* +mysql - adds `db.core` namespace and add MySQL dependencies
* +mongodb - adds `db.core` namespace and MongoDB dependencies
* +datomic - adds `db.core` namespace and Datomic dependencies

### ClojureScript

* +cljs - adds [ClojureScript](http://clojurescript.org/) support
* +hoplon adds ClojureScript support with [Hoplon](https://github.com/hoplon/hoplon) to the project
* +reagent - adds ClojureScript support with [Reagent](https://reagent-project.github.io/)
* +re-frame - adds ClojureScript support with [re-frame](https://github.com/Day8/re-frame)
* +kee-frame - adds ClojureScript support with [kee-frame](https://github.com/ingesolvoll/kee-frame)
* +shadow-cljs - adds Clojurescript support with [shadow-cljs](https://github.com/thheller/shadow-cljs), replacing the default cljsbuild and figwheel setup

### service API

* +graphql - adds GraphQL support using [Lacinia](https://github.com/walmartlabs/lacinia)
* +swagger - adds support for [Swagger-UI](https://github.com/swagger-api/swagger-ui)
* +service - create a service application without the front-end boilerplate such as HTML templates

### miscellaneous

* +boot - uses [Boot](https://github.com/boot-clj/boot) as build tool and creates `build.boot` instead of `project.clj`
* +auth - adds [Buddy](https://github.com/funcool/buddy) dependency and authentication middleware
* +auth-jwe - adds [Buddy](https://github.com/funcool/buddy) dependency with the [JWE](https://jwcrypto.readthedocs.io/en/stable/jwe.html) backend
* +oauth - adds OAuth boilerplate for the [clj-oauth](https://github.com/mattrepl/clj-oauth) library
* +cucumber - a profile for cucumber with clj-webdriver
* +sassc - adds support for [SASS/SCSS](http://sass-lang.com/) files using [SassC](https://github.com/sass/sassc) command line compiler
* +war - adds support of building WAR archives for deployment to servers such as Apache Tomcat (should *NOT* be used for [Immutant apps running on WildFly](deployment.html#deploying_to_wildfly))
* +site - creates template for site using the specified database (H2 by default) and ClojureScript
* +kibit - adds [Kibit](https://github.com/jonase/kibit) static code analyzer support
* +servlet - adds middleware to support the servlet context for running inside Java application servers

To add a profile simply pass it as an argument after your application name, eg:

```
lein new luminus myapp +cljs
```

You can also mix multiple profiles when creating the application, eg:

```
lein new luminus myapp +cljs +swagger +postgres
```

Note that some profiles will override others where appropriate. For example, the `+re-frame` profile has precedence over `+reagent`, so
```
lein new luminus myapp +reagent +re-frame
```
is equivalent to
```
lein new luminus myapp +re-frame
```
