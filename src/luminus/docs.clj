(ns luminus.docs
  (:use compojure.core hiccup.element noir.util.cache
        noir.response)
  (:require [luminus.common :as common]
            [luminus.util :as util]))

(def doc-titles 
  [["guestbook.md"        "Your first application"]
   ["profiles.md"         "Application profiles"]
   ["generating_html.md"  "Generating HTML"]
   ["static_resources.md" "Static resources"]
   ["responses.md"        "Response types"]
   ["routes.md"           "Defining routes"]   
   ["middleware.md"       "Custom middleware"]
   ["sessions_cookies.md" "Sessions and cookies"]
   ["security.md"         "Security"]
   ["logging.md"          "Logging"]   
   ["deployment.md"       "Deployment"]])

(defn doc-link [route selected? title]
  [:li.nav-link 
   (link-to {:class (if selected? "selected" "unselected")} route title)])

(defn doc-page-links [doc]  
  (let [selected-title (get (into {} doc-titles) doc)] 
    (into 
      [:ul.docs] 
      (for [[doc page-title] doc-titles]
        (doc-link (str "/docs/" doc) 
                  (= page-title selected-title) 
                  page-title)))))

(defn doc-page [doc]  
  (cache
    doc
    (common/layout 
      "Documentation"
      [:div
       [:div.sidebar 
        [:div.docs [:h2 "Topics"]]
        (doc-page-links doc)]
       [:section.main 
        (util/md->html doc)]])))

(defroutes doc-routes 
  (GET "/docs" [] (doc-page "guestbook.md"))
  (GET "/docs/:doc" [doc] (doc-page doc)))
