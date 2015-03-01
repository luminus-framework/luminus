Luminus aims to provide you with solid defaults for creating your web application. As such it comes packaged with several
libraries by default. These libraries include 
[lib-noir](https://github.com/noir-clojure/lib-noir) for general purpose utilities,
[Selmer](https://github.com/yogthos/Selmer)/[Hiccup](https://github.com/weavejester/hiccup) for HTML templating,
[Timbre](https://github.com/ptaoussanis/timbre) for logging,
[Tower](https://github.com/ptaoussanis/tower) for internationalization and a few others.
Of course, there are many other Clojure libraires for web development. Here we are going 
to provide a list of Clojure and ClojureScript libraries which can be useful in addition 
to those already included with Luminus.

## Assets

* [Stefon](https://github.com/circleci/stefon) - asset pipeline ring middleware
* [lein-asset-minifier](https://github.com/yogthos/lein-asset-minifier) - a Leiningen plugin to minify CSS and Js assets

## Async HTTP

* [Aleph](https://github.com/ztellman/aleph) - asynchronous communication
* [http-kit](https://github.com/http-kit/http-kit) - high-performance event-driven HTTP client/server

## Authentication

* [buddy](https://github.com/niwibe/buddy) - a complete security library for clojure
* [Friend](https://github.com/cemerick/friend) - an extensible authentication and authorization library
* [clj-ldap](https://github.com/pauldorman/clj-ldap) - a library for talking to LDAP servers
* [ring-basic-authentication](https://github.com/remvee/ring-basic-authentication) - Ring middleware to enforce basic authentication

## Caching

* [Spyglass](https://github.com/clojurewerkz/spyglass) - a Memcached client (also: Couchbase, Kestrel)
* [core.cache](https://github.com/clojure/core.cache) - a caching library implementing various cache strategies

## Configuration

* [edn-config](https://github.com/yogthos/edn-config) - a library for managing environment variables in Clojure using EDN configuration files

## ClojureScript

* [Reagent](https://github.com/holmsand/reagent) - a minimalistic interface between ClojureScript and React
* [Om](https://github.com/swannodette/om) - ClojureScript interface to Facebook's React
* [Kioo](https://github.com/ckirkendall/kioo) - DOM manipulation and templating library for Reagent/Om
* [Hickory](https://github.com/davidsantiago/hickory) -  parses HTML into Clojure data structures
* [cljs-ajax](https://github.com/yogthos/cljs-ajax) - a simple Ajax client for ClojureScript
* [Sente](https://github.com/ptaoussanis/sente) - bidirectional a/sync comms over both WebSockets and Ajax (auto-fallback)
* [Datascript](https://github.com/tonsky/datascript) - central, uniform approach to manage all application state
* [markdown-clj](https://github.com/yogthos/markdown-clj) - Clojure/Script Markdown parser
* [Garden](https://github.com/noprompt/garden) - a library for rendering CSS in Clojure and ClojureScript
* [Dommy](https://github.com/Prismatic/dommy) - a no-nonsense templating and (soon) dom manipulation library
* [json-html](https://github.com/yogthos/json-html) - generates human representation of the JSON/EDN encoded data

## Database clients

* [CongoMongo](https://github.com/aboekhoff/congomongo) - Wrapper for the mongo-db java api
* [Monger](http://clojuremongodb.info/) - a client for MongoDB
* [Clutch](https://github.com/clojure-clutch/clutch) - a library for Apache CouchDB
* [Neocons](https://github.com/michaelklishin/neocons) - a feature rich idiomatic client for the Neo4J REST API
* [Welle](http://clojureriak.info/) - an expressive client for Riak
* [Cassaforte](https://github.com/clojurewerkz/cassaforte) - A young client for Apache Cassandra 1.2+
* [Rotary](https://github.com/weavejester/rotary) - DynamoDB API
* [Rummage](https://github.com/cemerick/rummage) - a client library for Amazon's SimpleDB (SDB)
* [Carmine](https://github.com/ptaoussanis/carmine) - Clojure Redis client & message queue

## Database Migrations

* [Drift](https://github.com/macourtney/drift) - a migration library
* [Lobos](http://budu.github.com/lobos/) - Lobos is a library to help you create and modify database schemas
* [Migratus](https://github.com/pjstadig/migratus) - a general migration framework
* [Ragtime](https://github.com/weavejester/ragtime) - database-independent migration library

## SQL Libraries

* [Yesql](https://github.com/krisajenkins/yesql) - a library for loading SQL queries from files
* [Honey SQL](https://github.com/jkk/honeysql) - a Korma alternative DSL for building SQL queries
* [clojure.java.jdbc](https://github.com/clojure/java.jdbc) - a low level wrapper for Java JDBC
* [blackwater](https://github.com/bitemyapp/blackwater) - a library for logging SQL queries and the time they took for Korma and clojure.java.jdbc

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
* [ring-anti-forgery](https://github.com/ring-clojure/ring-anti-forgery) - CSRF attack prevention using randomly-generated anti-forgery tokens
* [ring-rewrite](https://github.com/ebaxt/ring-rewrite) - Ring middleware for defining and applying rewrite rules
* [Pantomime](https://github.com/michaelklishin/pantomime) - a Library For Working With MIME Types
* [Route One](https://github.com/clojurewerkz/route-one) -  a library that generates HTTP resource routes (as in Ruby on Rails and similar modern Web application frameworks)
* [Schema](https://github.com/prismatic/schema) - a Clojure(Script) library for declarative data description and validation. 
* [Urly](https://github.com/michaelklishin/urly) - a library that unifies parsing of URIs, URLs and URL-like values like relative href values
* [Validateur](http://clojurevalidations.info/articles/getting_started.html) - a validation library inspired by Ruby's ActiveModel
* [aging-session](https://github.com/diligenceengine/aging-session) - a memory based ring session store that has a concept of time
* [Throttler](https://github.com/brunoV/throttler) - token bucket algorithm to control both the overall rate as well as the burst rate for function calls (e.g. incoming requests)
* [Elastisch](https://github.com/clojurewerkz/elastisch) - a minimalistic Clojure client for ElasticSearch, a modern distributed search engine
* [cronj](http://docs.caudate.me/cronj/) - a library for scheduling tasks 

## Web Services 

* [compojure-api](https://github.com/metosin/compojure-api) - a library for writing [Swagger](https://github.com/wordnik/swagger-spec) REST APIs
* [Liberator](http://clojure-liberator.github.com/) - a library for creating REST services
* [necessary-evil](https://github.com/brehaut/necessary-evil) - XML RPC library for Clojure

## Web Sockets
* [ring-jetty9-adapter](https://github.com/sunng87/ring-jetty9-adapter) - a Ring adapter for Jetty 9
* [jet](https://github.com/mpenet/jet) - Jetty 9 client/server

It's just few categories, more libraries related to web development
 for testing, data validation, text search, random data generation,
 JSON parsing, exception handling, SQL abstractions and other can be found on
[The Clojure Toolbox](http://www.clojure-toolbox.com/),
[ClojureSphere](http://www.clojuresphere.com/) and
[ClojureWerkz](http://clojurewerkz.org/) websites.

## Tools

* [Clojure mini-profiler](https://github.com/yeller/clojure-miniprofiler) - a simple but effective profiler for clojure web applications
