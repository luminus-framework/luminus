(defproject luminus "0.1.0-SNAPSHOT"
  :description "Documentation site for the Luminus framework"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [lib-noir "0.3.2"]
                 [compojure "1.1.3"]
                 [hiccup "1.0.2"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [bultitude "0.1.7"]                                  
                 [environ "0.3.0"]
                 [markdown-clj "0.9.13"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.7.5"]
            [environ/environ.lein "0.3.0"]]
  :hooks [environ.leiningen.hooks]
  :profiles {:production {:env {:production true}}
             :dev {:dependencies [[ring-mock "0.1.3"]
                                  [ring/ring-devel "1.1.0"]]}}
  :ring {:handler luminus.handler/war-handler}
  :main luminus.server)
