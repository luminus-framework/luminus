(defproject luminus "0.1.0-SNAPSHOT"
  :description "Documentation site for the Luminus framework"
  :url "http://www.luminusweb.net/"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [lib-noir "0.8.0"]
                 [compojure "1.1.6"]
                 [ring-server "0.3.1"]
                 [markdown-clj "0.9.41"]
                 [clj-http "0.7.9"]
                 [crouton "0.1.1"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.8.10"]]
  :profiles {:production
             {:ring
              {:open-browser? false 
               :stacktraces? false 
               :auto-reload? false}}
             :dev {:dependencies [[ring-mock "0.1.5"]
                                  [ring/ring-devel "1.2.1"]]}}
  :ring {:handler luminus.handler/app
         :init luminus.handler/init})
