(ns luminus-site.docs
  (:use compojure.core hiccup.element)
  (:require [luminus-site.common :as common]
            [lib-luminus.util :as util]))

(def doc-pages 
  [["Your first application" "/docs/tutorial" "guestbook.md"]
   ["Generating HTML" "/docs/html" "generating_html.md"]
   ["Defining routes" "/docs/routes" "routes.md"]      
   ["Custom middleware" "/docs/middleware" "middleware.md"]
   ["Sessions and cookies" "/docs/sessions" "sessions_cookies.md"]
   ["Logging" "/docs/logging" "logging.md"]
   ["Security" "/docs/security" "security.md"]
   ["Deployment" "/docs/deployment" "deployment.md"]])

(defn doc-link [route selected? title]
  [:li.nav-link 
    (link-to {:class (if selected? "selected" "unselected")} route title)])

(defn doc-page-links [selected-title]   
  (into [:ul.docs] 
        (for [[page-title route] doc-pages]
          (doc-link
            route
            (= page-title selected-title)
            page-title))))

(defn doc-page [doc-id & [route selected-title]]
  (util/cache
    route
    (common/layout 
      "Documentation"
      [:div
       [:div.sidebar 
        [:div.docs [:h2 "Topics"]]
        (doc-page-links (or selected-title (ffirst doc-pages)))]
       [:section.main 
        (util/md->html doc-id)]])))

(defmacro functionize [macro]
  `(fn [& args#] (eval (cons '~macro args#))))

(defmacro apply-macro [macro args]
   `(apply (functionize ~macro) ~args))
     
(apply-macro defroutes
  (cons 'doc-routes
    (for [[title# route# doc-id#] doc-pages]
      (GET route# [] (doc-page doc-id# route# title#)))))
    