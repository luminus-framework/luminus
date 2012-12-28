(ns luminus.docs
  (:use compojure.core hiccup.element noir.util.cache)
  (:require [luminus.common :as common]
            [luminus.util :as util]))

#_(def doc-pages 
  [["Your first application" "/docs/tutorial" "guestbook.md"]
   ["Application profiles" "/docs/profiles" "profiles.md"]
   ["Generating HTML" "/docs/html" "generating_html.md"]
   ["Defining routes" "/docs/routes" "routes.md"]
   ["Static resources" "/docs/static" "static_resources.md"]
   ["Custom middleware" "/docs/middleware" "middleware.md"]
   ["Sessions and cookies" "/docs/sessions" "sessions_cookies.md"]
   ["Logging" "/docs/logging" "logging.md"]
   ["Security" "/docs/security" "security.md"]
   ["Deployment" "/docs/deployment" "deployment.md"]])



(def doc-titles 
  {"guestbook.md"        [1 "Your first application"]
   "profiles.md"         [2 "Application profiles"]
   "generating_html.md"  [3 "Generating HTML"]
   "static_resources.md" [4 "Static resources"]
   "routes.md"           [5 "Defining routes"]   
   "middleware.md"       [6 "Custom middleware"]
   "sessions_cookies.md" [7 "Sessions and cookies"]
   "security.md"         [8 "Security"]
   "logging.md"          [9 "Logging"]   
   "deployment.md"       [10 "Deployment"]})

(defn doc-link [route selected? title]
  [:li.nav-link 
    (link-to {:class (if selected? "selected" "unselected")} route title)])

(defn doc-page-links [doc]  
  (let [selected-title (get doc-titles doc)] 
    (into 
      [:ul.docs] 
      (for [[doc [_ page-title]] (sort-by #(first (second %)) doc-titles)]
        (doc-link (str "/docs/" doc) 
                  (= page-title selected-title) 
                  page-title)))))

(defn doc-page [& [doc]]  
  (let [doc (or doc "guestbook.md")]
    (common/layout 
        "Documentation"
        [:div
         [:div.sidebar 
          [:div.docs [:h2 "Topics"]]
          (doc-page-links doc)]
         [:section.main 
          (util/md->html doc)]])    
    #_ (cache
      doc
      (common/layout 
        "Documentation"
        [:div
         [:div.sidebar 
          [:div.docs [:h2 "Topics"]]
          (doc-page-links doc)]
         [:section.main 
          (util/md->html doc)]]))))

(defroutes doc-routes 
  (GET "/docs/:doc" [doc] (doc-page doc)))

#_(defmacro functionize [macro]
  `(fn [& args#] (eval (cons '~macro args#))))

#_(defmacro apply-macro [macro args]
   `(apply (functionize ~macro) ~args))
     
#_(apply-macro defroutes
  (cons 'doc-routes
    (for [[title# route# doc-id#] doc-pages]
      (GET route# [] (doc-page doc-id# route# title#)))))
    