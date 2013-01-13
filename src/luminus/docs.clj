(ns luminus.docs
  (:use compojure.core hiccup.element)
  (:require [luminus.common :as common]
            [noir.util.cache :as cache]
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
   ["database.md"         "Database access"]
   ["logging.md"          "Logging"]
   ["deployment.md"       "Deployment"]])

(defn doc-link [route selected? title]
  [:li (link-to {:class (if selected? "selected" "unselected")} route title)])

(defn doc-page-links [doc]
  (let [selected-title (get (into {} doc-titles) doc)]
    (into
      [:ul ]
      (for [[doc page-title] doc-titles]
        (doc-link (str "/docs/" doc)
          (= page-title selected-title)
          page-title)))))

(defn doc-page [doc]
  (cache/cache!
    doc
    (common/layout "Documentation"
      (let [doc-content (util/fetch-doc doc)]
        [:div [:div {:id "left"}
         [:div.entry [:h1 (get (into {} doc-titles) doc)]
          [:h2 "Contents"]
          (util/generate-toc doc-content)
          [:h2]
          doc-content
          ]]
        [:div {:id "right"}
         [:div.sidemenu [:h3 "Topics"]
          (doc-page-links doc)]]]))))

(defroutes doc-routes
  (GET "/docs" [] (doc-page "guestbook.md"))
  (GET "/docs/:doc" [doc] (doc-page doc)))
