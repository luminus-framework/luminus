(defproject luminus "0.1.0-SNAPSHOT"
  :description "Documentation site for the Luminus framework"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [lib-noir "0.3.3"]
                 [compojure "1.1.3"]
                 [hiccup "1.0.2"]                 
                 [ring-server "0.2.5"]
                 [bultitude "0.1.7"]                 
                 [markdown-clj "0.9.17"]
                 [clj-http "0.6.3"]
                 [crouton "0.1.1"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.8.0-SNAPSHOT"]]
  :hooks [environ.leiningen.hooks]
  :profiles {:production
             {:ring
              {:open-browser? false 
               :stacktraces? false 
               :auto-reload? false}}
             :dev {:dependencies [[ring-mock "0.1.3"]
                                  [ring/ring-devel "1.1.0"]]}}
  :ring {:handler luminus.handler/war-handler
         :init luminus.handler/init})
