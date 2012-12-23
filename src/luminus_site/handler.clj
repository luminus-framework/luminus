(ns luminus-site.handler
  (:use compojure.core
        hiccup.element        
        luminus-site.docs)
  (:require [lib-luminus.middleware :as middleware]
            [compojure.route :as route]
            [lib-luminus.response :as response]
            [luminus-site.common :as common]
            [lib-luminus.util :as util]))

(defn feature-item [title description]
  [:li.feature [:h2 title] [:p description]])

(defn home [] 
  (common/layout  
    "Home"
    [:section#intro
     [:div.wrapper
      [:div.wrapper
       (util/md->html "intro.md")]]]
    
    [:section#features 
     [:h2 "Features"]
     [:ul#feature-list
      (feature-item "Rapid development" "No boilerplate, no nonsense, get your work done framework")
      (feature-item "Productive" "The performance of the JVM combined with the power of Clojure")
      (feature-item "Interactivity" "Interactive development with full REPL integration")
      (feature-item "Flexibility" "Choose the components which make sense for you")      
      (feature-item "Mature ecosystem" "Access to the plethora of existing Clojure and Java libraries")
      (feature-item "Powerful tools" "Excellent build tools and deployment options")]]))

(defn features []
  (common/layout 
    "Features" 
    [:section
     (util/md->html "features.md")]))

(defroutes app-routes
  (GET "/" [] (home))  
  (GET "/api" [] (common/layout "API" [:section "TODO..."]))
  (GET "/download" [] (common/layout "Download" 
                                     [:section 
                                      "Luminus source is available on " 
                                      (link-to "https://github.com/yogthos/lib-luminus" "Github.") 
                                      [:br]
                                      "This site is built with Luminus, its source is available on " 
                                      (link-to "https://github.com/yogthos/luminus-site" "Github") " as well."]))  
  (GET "/contribute" [] (common/layout "Get involved" [:section (util/md->html "contributing.md")]))
  (GET "/about" [] (common/layout "About" [:section (util/md->html "about.md")]))
  (route/resources "/")
  (route/not-found "Not Found"))

;(setup-doc-routes)    
(def all-routes [doc-routes app-routes])
(def app (middleware/app-handler all-routes))
(def war-handler (middleware/war-handler all-routes))
  