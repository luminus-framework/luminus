## Profiles

Running `lein new luminus myapp` will create an application using the default profile template.
However, if you would like to attach further functionality to your template you can append
profile hints for the extended functionality.

### alternative web servers

* +aleph - adds [Aleph](https://github.com/ztellman/aleph) server support to the project
* +http-kit - adds [HTTP Kit](http://http-kit.org/) support to the project
* +immutant - adds the [Immutant](http://immutant.org/) web server to the project

### databases

* +h2 - adds `db.core` namespace and H2 db dependencies
* +postgres - adds `db.core` namespace and add PostreSQL dependencies
* +mysql - adds `db.core` namespace and add MySQL dependencies
* +mongodb - adds `db.core` namespace and MongoDB dependencies

 
### miscellaneous 

* +auth - adds [Buddy](https://funcool.github.io/buddy/latest/) dependency and authentication middleware
* +cljs - adds ClojureScript support to the project along with an example
* +cucumber - a profile for cucumber with clj-webdriver
* +swagger - adds support for [Swagger-UI](https://github.com/swagger-api/swagger-ui) using the [compojure-api](https://github.com/metosin/compojure-api) library
* +sassc - adds support for [SASS/SCSS](http://sass-lang.com/) files using [SassC](https://github.com/sass/sassc) command line compiler

 
To add a profile simply pass it as an argument after your application name, eg:

```
lein new luminus myapp +cljs
```

You can also mix multiple profiles when creating the application, eg:

```
lein new luminus myapp +cljs +swagger +postgres
```

