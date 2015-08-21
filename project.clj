(defproject luminus "0.1"
  :description "Documentation site for the Luminus framework"
  :url "http://www.luminusweb.net/"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.cache "0.6.4"]
                 [compojure "1.3.4"]
                 [ring-server "0.3.1"]
                 [markdown-clj "0.9.69"]
                 [clj-http "1.1.2"]
                 [crouton "0.1.2"]
                 [selmer "0.8.2"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.9.6"]]
  :profiles {:production
             {:ring
              {:open-browser? false
               :stacktraces? false
               :auto-reload? false}}}
  :ring {:handler luminus.handler/app
         :init luminus.handler/init})
