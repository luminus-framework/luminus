Luminus aims to provide solid defaults for creating your web application. As such it comes packaged with several
libraries by default. These libraries include 
[Buddy](https://github.com/funcool/buddy) for security, [Bouncer](https://github.com/leonardoborges/bouncer) for validation,
[Selmer](https://github.com/yogthos/Selmer) for HTML templating,
[Tower](https://github.com/ptaoussanis/tower) for internationalization and a few others.
Of course, there are many other Clojure libraires for web development. Here we are going 
to provide a list of Clojure and ClojureScript libraries which can be useful in addition 
to those already included with Luminus.

## Assets

* [Stefon](https://github.com/circleci/stefon) - asset pipeline ring middleware
* [lein-asset-minifier](https://github.com/yogthos/lein-asset-minifier) - a Leiningen plugin to minify CSS and Js assets

## Authentication

* [Friend](https://github.com/cemerick/friend) - an extensible authentication and authorization library
* [clj-ldap](https://github.com/pauldorman/clj-ldap) - a library for talking to LDAP servers
* [ring-basic-authentication](https://github.com/remvee/ring-basic-authentication) - Ring middleware to enforce basic authentication
* [ring-oauth2](https://github.com/weavejester/ring-oauth2) - Ring middleware that acts as a OAuth 2.0 client

## Caching

* [Spyglass](https://github.com/clojurewerkz/spyglass) - a Memcached client (also: Couchbase, Kestrel)
* [core.cache](https://github.com/clojure/core.cache) - a caching library implementing various cache strategies

## Configuration

* [lein-init-script](https://github.com/strongh/lein-init-script) - a plugin for *nix init script generation

## ClojureScript

* [Om](https://github.com/swannodette/om) - ClojureScript interface to Facebook's React
* [Kioo](https://github.com/ckirkendall/kioo) - DOM manipulation and templating library for Reagent/Om
* [Hickory](https://github.com/davidsantiago/hickory) -  parses HTML into Clojure data structures
* [Sente](https://github.com/ptaoussanis/sente) - bidirectional a/sync comms over both WebSockets and Ajax (auto-fallback)
* [Datascript](https://github.com/tonsky/datascript) - central, uniform approach to manage all application state
* [Garden](https://github.com/noprompt/garden) - a library for rendering CSS in Clojure and ClojureScript
* [Dommy](https://github.com/Prismatic/dommy) - a no-nonsense templating and (soon) dom manipulation library
* [json-html](https://github.com/yogthos/json-html) - generates human representation of the JSON/EDN encoded data
* [lein-externs](https://github.com/ejlo/lein-externs) - a plugin to automatically generate externs files for Js libs

## Database clients

* [Carmine](https://github.com/ptaoussanis/carmine) - Clojure Redis client & message queue
* [Cassaforte](https://github.com/clojurewerkz/cassaforte) - A young client for Apache Cassandra 1.2+
* [Clutch](https://github.com/clojure-clutch/clutch) - a library for Apache CouchDB
* [CongoMongo](https://github.com/aboekhoff/congomongo) - Wrapper for the mongo-db java api
* [Monger](http://clojuremongodb.info/) - a client for MongoDB
* [Neocons](https://github.com/michaelklishin/neocons) - a feature rich idiomatic client for the Neo4J REST API
* [pgjdbc-ng](http://impossibl.github.io/pgjdbc-ng/) - JDBC driver with PostgreSQL specific features
* [Rotary](https://github.com/weavejester/rotary) - DynamoDB API
* [Rummage](https://github.com/cemerick/rummage) - a client library for Amazon's SimpleDB (SDB)
* [Welle](http://clojureriak.info/) - an expressive client for Riak

## Database Migrations

* [Drift](https://github.com/macourtney/drift) - a migration library
* [Ragtime](https://github.com/weavejester/ragtime) - database-independent migration library

## SQL Libraries

* [Honey SQL](https://github.com/jkk/honeysql) - a Korma alternative DSL for building SQL queries
* [clojure.java.jdbc](https://github.com/clojure/java.jdbc) - a low level wrapper for Java JDBC
* [blackwater](https://github.com/bitemyapp/blackwater) - a library for logging SQL queries and the time they took for Korma and clojure.java.jdbc
* [walkable](https://github.com/walkable-server/walkable) - Datomic pull syntax for building SQL queries

## Dependency Injection

* [mount](https://github.com/tolitius/mount)
* [yoyo](https://github.com/jarohen/yoyo)

## Email

* [Mailer](https://github.com/clojurewerkz/mailer) - an ActionMailer-inspired mailer library
* [Postal](https://github.com/drewr/postal) - Clojure email support

## Graphics

* [Analemma](http://liebke.github.com/analemma/) - a Clojure-based SVG DSL and charting library
* [Monet](https://github.com/rm-hull/monet) - a small ClojureScript library to make it easier (and performant) to work with canvas 

## Template Languages

* [Cuma](https://github.com/liquidz/cuma) - extensible micro template engine for Clojure.
* [Basil](https://github.com/kumarshantanu/basil) - a general purpose template library
* [Stencil](https://github.com/davidsantiago/stencil) - a fast, compliant implementation of Mustache
* [Enlive](https://github.com/cgrand/enlive) - a selector-based (à la CSS) templating and transformation system

## Miscellaneous

* [clj-pdf](https://github.com/yogthos/clj-pdf) - PDF report generation library
* [clj-rss](https://github.com/yogthos/clj-rss) - a library for generating RSS feeds
* [metrics-clojure](https://github.com/sjl/metrics-clojure/) - a thin Clojure façade around Coda Hale’s wonderful metrics library
* [ring-logger-timbre](https://github.com/nberger/ring-logger-timbre) - log Ring requests & responses using Timbre
* [slf4j-timbre](https://github.com/fzakaria/slf4j-timbre) - SLF4J binding for Clojure's Timbre logging library
* [ring-cors](https://github.com/r0man/ring-cors) CORS middleware for Ring
* [ring-rewrite](https://github.com/ebaxt/ring-rewrite) - Ring middleware for defining and applying rewrite rules
* [Pantomime](https://github.com/michaelklishin/pantomime) - a Library For Working With MIME Types
* [Route One](https://github.com/clojurewerkz/route-one) -  a library that generates HTTP resource routes (as in Ruby on Rails and similar modern Web application frameworks)
* [Schema](https://github.com/prismatic/schema) - a Clojure(Script) library for declarative data description and validation. 
* [Urly](https://github.com/michaelklishin/urly) - a library that unifies parsing of URIs, URLs and URL-like values like relative href values
* [Validateur](http://clojurevalidations.info/articles/getting_started.html) - a validation library inspired by Ruby's ActiveModel
* [aging-session](https://github.com/diligenceengine/aging-session) - a memory based ring session store that has a concept of time
* [Timbre](https://github.com/ptaoussanis/timbre) - a Clojure/Script logging and profiling library
* [Throttler](https://github.com/brunoV/throttler) - token bucket algorithm to control both the overall rate as well as the burst rate for function calls (e.g. incoming requests)
* [Elastisch](https://github.com/clojurewerkz/elastisch) - a minimalistic Clojure client for ElasticSearch, a modern distributed search engine
* [cronj](http://docs.caudate.me/cronj/) - a library for scheduling tasks 
* [ring-async](https://github.com/ninjudd/ring-async) - a Ring adapter for supporting asynchronous responses
* [lein-nvd](https://github.com/rm-hull/lein-nvd) - National Vulnerability Database dependency-checker plugin for Leiningen
## Web Services 

* [sweet-liberty](https://github.com/RJMetrics/sweet-liberty) - a library for building database-backed RESTful services
* [Liberator](http://clojure-liberator.github.com/) - a library for creating REST services
* [necessary-evil](https://github.com/brehaut/necessary-evil) - XML RPC library for Clojure
* [lacinia](https://github.com/walmartlabs/lacinia) - a GraphQL implementation for Clojure

It's just few categories, more libraries related to web development
 for testing, data validation, text search, random data generation,
 JSON parsing, exception handling, SQL abstractions and other can be found on
[The Clojure Toolbox](http://www.clojure-toolbox.com/) and
[ClojureWerkz](http://clojurewerkz.org/) websites.
