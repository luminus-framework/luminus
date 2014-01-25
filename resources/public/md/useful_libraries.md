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

* [Stefon](https://github.com/circleci/stefon) - Asset pipeline ring middleware

## Async HTTP

* [Aleph](https://github.com/ztellman/aleph) - Asynchronous communication
* [http-kit](https://github.com/http-kit/http-kit) - High-performance event-driven HTTP client/server

## Authentication

* [Friend](https://github.com/cemerick/friend) - An extensible authentication and authorization library
* [Sandbar](https://github.com/brentonashworth/sandbar) - A web application library with higher level abstractions for Compojure, Ring
* [ring-basic-authentication](https://github.com/remvee/ring-basic-authentication) - Ring middleware to enforce basic authentication

## Caching

* [Spyglass](https://github.com/clojurewerkz/spyglass) - A Memcached client (also: Couchbase, Kestrel)
* [core.cache](https://github.com/clojure/core.cache) - A caching library implementing various cache strategies

## ClojureScript

* [cljs-ajax](https://github.com/yogthos/cljs-ajax) - a simple Ajax client for ClojureScript
* [markdown-clj](https://github.com/yogthos/markdown-clj) - Clojure/Script Markdown parser
* [Domina](https://github.com/levand/domina) - A jQuery inspired DOM manipulation library
* [Dommy](https://github.com/Prismatic/dommy) - A no-nonsense templating and (soon) dom manipulation library
* [Enfocus](https://github.com/ckirkendall/enfocus) - DOM manipulation and templating library inspired by Enlive
* [Fetch](https://github.com/ibdknox/fetch) - A library for Client/Server interaction
* [Shoreleave](https://github.com/shoreleave) - A smarter client-side with ClojureScript
* [Webfui](https://github.com/drcode/webfui) - Client-Side Web Framework
* [Widje](https://github.com/Flamefork/widje) - Templating for ClojureScript
* [Cloact](http://holmsand.github.io/cloact/) - a minimalistic interface between ClojureScript and React
* [Om](https://github.com/swannodette/om) - A ClojureScript interface to Facebook's React

## Database clients

* [CongoMongo](https://github.com/aboekhoff/congomongo) - Wrapper for the mongo-db java api
* [Monger](http://clojuremongodb.info/) - Monger, a client for MongoDB
* [Clutch](https://github.com/clojure-clutch/clutch) - A library for Apache CouchDB
* [Neocons](https://github.com/michaelklishin/neocons) - A feature rich idiomatic client for the Neo4J REST API
* [Welle](http://clojureriak.info/) - An expressive client for Riak
* [Cassaforte](https://github.com/clojurewerkz/cassaforte) - A young client for Apache Cassandra 1.2+
* [Rotary](https://github.com/weavejester/rotary) - DynamoDB API
* [Rummage](https://github.com/cemerick/rummage) - A client library for Amazon's SimpleDB (SDB)
* [Carmine](https://github.com/ptaoussanis/carmine) - Clojure Redis client & message queue

## Database Migrations

* [Drift](https://github.com/macourtney/drift) - A migration library
* [Lobos](http://budu.github.com/lobos/) - Lobos is a library to help you create and modify database schemas
* [Migratus](https://github.com/pjstadig/migratus) - A general migration framework
* [Ragtime](https://github.com/weavejester/ragtime) - Database-independent migration library

## SQL Libraries

* [Yesql](https://github.com/krisajenkins/yesql) - a library for loading SQL queries from files
* [Honey SQL](https://github.com/jkk/honeysql) - a Korma alternative DSL for building SQL queries
* [clojure.java.jdbc](https://github.com/clojure/java.jdbc) - a low level wrapper for Java JDBC

## Email Sending

* [Mailer](https://github.com/clojurewerkz/mailer) - An ActionMailer-inspired mailer library
* [Postal](https://github.com/drewr/postal) - Clojure email support
* [clj-mail](https://github.com/MayDaniel/clj-mail) - Send and receive emails from Clojure
 
## SVG

* [Analemma](http://liebke.github.com/analemma/) - a Clojure-based SVG DSL and charting library

## Template Languages

* [Basil](https://github.com/kumarshantanu/basil) - A general purpose template library
* [Clostache](https://github.com/fhd/clostache) - {{ mustache }} for Clojure
* [Enlive](https://github.com/cgrand/enlive) - A selector-based (à la CSS) templating and transformation system
* [Fleet](https://github.com/Flamefork/fleet) - Templating System for Clojure
* [Laser](https://github.com/Raynes/laser) - HTML transformation/templating
* [Stencil](https://github.com/davidsantiago/stencil) - A fast, compliant implementation of Mustache
* [Tinsel](https://github.com/davidsantiago/tinsel) - Selector-based templates with Hiccup

## Web Services 

* [Liberator](http://clojure-liberator.github.com/) - a library for creating REST services
* [necessary-evil](https://github.com/brehaut/necessary-evil) - XML RPC library for Clojure

## Miscellaneous

* [Urly](https://github.com/michaelklishin/urly) - a library that unifies parsing of URIs, URLs and URL-like values like relative href values
* [ring-anti-forgery](https://github.com/weavejester/ring-anti-forgery) - CSRF attack prevention using randomly-generated anti-forgery tokens
* [clj-rss](https://github.com/yogthos/clj-rss) - a library for generating RSS feeds
* [clj-pdf](https://github.com/yogthos/clj-pdf) - PDF report generation library
* [Validateur](http://clojurevalidations.info/articles/getting_started.html) - a validation library inspired by Ruby's ActiveModel
* [ring-rewrite](https://github.com/ebaxt/ring-rewrite) - Ring middleware for defining and applying rewrite rules
* [Pantomime](https://github.com/michaelklishin/pantomime) - a Library For Working With MIME Types
 

It's just few categories, more libraries related to web development
 for testing, data validation, text search, random data generation,
 JSON parsing, exception handling, SQL abstractions and other can be found on
[The Clojure Toolbox](http://www.clojure-toolbox.com/),
[ClojureSphere](http://www.clojuresphere.com/) and
[ClojureWerkz](http://clojurewerkz.org/) websites.
