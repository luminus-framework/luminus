(defproject luminus "0.1.0-SNAPSHOT"
  :description "Documentation site for the Luminus framework"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [lib-luminus "0.1.5"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [bultitude "0.1.7"]                                  
                 [environ "0.3.0"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.7.5"]
            [environ/environ.lein "0.3.0"]]
  :hooks [environ.leiningen.hooks]
  :profiles {:production {:env {:production true}}
             :dev {:dependencies [[ring-mock "0.1.3"]
                                  [ring/ring-devel "1.1.0"]]}}
  :ring {:handler luminus-site.handler/war-handler}
  :main luminus-site.server)
