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
  [:div [:h3 title] [:p description]])

(defn home []
  (cache
    :home
    (common/base
      "Home"
      [:div#featured.clear
       #_[:div.image-block (image "img/img-featured.png" "featured")]
       [:div [:h2 "What is Luminus?"]
        [:p [:strong "Luminus"] " is a micro-framework based on a set of lightweight libraries. It aims to provide a robust, scalable, and easy to use platform.
With Luminus you can focus on developing your app the way you want without any distractions."]

        [:h3 "Why develop web applications with Clojure?"]

        [:div#footer-wrap
          [:div.col-a
           (feature-item "Rapid development" "No boilerplate, no nonsense, get your work done framework")
           (feature-item "Productivity" "The performance of the JVM combined with the power of Clojure")]
          [:div.col-a
           (feature-item "Interactivity" "Interactive development with full REPL integration")
           (feature-item "Flexibility" "Choose the components which make sense for you")]
          [:div.col-b
           (feature-item "Mature ecosystem" "Access to the plethora of existing Clojure and Java libraries")
           (feature-item "Powerful tools" "Excellent build tools and deployment options")]]

        [:p (link-to {:class "more-link"} "/about" "Read More")]]]

      [:div#content-outer.clear
       [:div#footer-outer.clear]
       [:div#content-wrap (util/md->html "intro.md")]])))

(defroutes app-routes
  (GET "/" [] (home))
  (GET "/api" [] (common/layout "API" [:section (util/md->html "api.md")]))
  (GET "/contribute" [] (common/layout "Get involved" [:section (util/md->html "contributing.md")]))
  (GET "/about" [] (common/layout "About" [:section (util/md->html "about.md")]))
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init []
  (set-cache-timeout! 60))

(def all-routes [doc-routes app-routes])
(def app (middleware/app-handler all-routes))
(def war-handler (middleware/war-handler app))
