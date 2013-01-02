(ns luminus.server
  (:use luminus.handler
        ring.adapter.jetty
        [ring.middleware file-info file]
        bultitude.core)
  ;;needed to force compile so that 
  ;;the application is runnable
  (:gen-class))

(defn wrap-reload-if-available [handler]
  (if (some #{'ring.middleware.reload} (namespaces-on-classpath))
    (do       
      (use 'ring.middleware.reload)
      ((resolve 'wrap-reload) handler))
    handler))

(defn get-handler []
  ;we call #'app, which creates a var, so that when we reload our code, 
  ;the server is using the Var rather than having its own copy. 
  ;When the root binding changes, the server picks it up without 
  ;having to restart 
  (-> #'app                    
    (wrap-file "resources")
    (wrap-file-info)
    (wrap-reload-if-available)))

(defn -main [& [port]]  
  (init)  
  (let [port    (if port (Integer/parseInt port) 8080)]    
    (run-jetty (get-handler) {:join? false :port port})
    (println "Server started on port [" port "].")
    (println (str "You can view the site at http://localhost:" port))))
