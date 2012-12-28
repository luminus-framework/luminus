(ns luminus.handler
  (:use compojure.core
        hiccup.element        
        luminus.docs
        noir.util.cache)
  (:require [noir.util.middleware :as middleware]
            [compojure.route :as route]
            [noir.response :as response]            
            [luminus.common :as common]
            [luminus.util :as util]))

(defn feature-item [title description]
  [:li.feature [:h2 title] [:p description]])

(defn home []   
  (cache
    :home
    (common/layout  
      "Home"
      [:section#intro
       [:div.wrapper
        [:div.wrapper
         (util/md->html "intro.md")]]]
      
      [:section#features 
       [:h2 "Why Luminus?"]
       [:ul#feature-list
        (feature-item "Rapid development" "No boilerplate, no nonsense, get your work done framework")
        (feature-item "Productive" "The performance of the JVM combined with the power of Clojure")
        (feature-item "Interactivity" "Interactive development with full REPL integration")
        (feature-item "Flexibility" "Choose the components which make sense for you")      
        (feature-item "Mature ecosystem" "Access to the plethora of existing Clojure and Java libraries")
        (feature-item "Powerful tools" "Excellent build tools and deployment options")]])))

(defroutes app-routes
  (GET "/" [] (home))  
  (GET "/api" [] (response/redirect "/autodoc/index.html"))    
  (GET "/contribute" [] (common/layout "Get involved" [:section (util/md->html "contributing.md")]))
  (GET "/about" [] (common/layout "About" [:section (util/md->html "about.md")]))
  (route/resources "/")
  (route/not-found "Not Found"))

(def all-routes [doc-routes app-routes])
(def app (middleware/app-handler all-routes))
(def war-handler (middleware/war-handler app))
