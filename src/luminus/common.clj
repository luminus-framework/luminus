(ns luminus.common
  (:use [hiccup.page :only [html5 include-css include-js]]
        [hiccup.element :only [link-to image]]
        [hiccup.util :only [to-uri]]
        [hiccup.def]))

(defn nav-link
  ([page-id url name]
    (nav-link page-id url name {}))
  ([page-id url name options]
    [:li (if (= name page-id) (merge options {:id "current"}) options) (link-to url name)]))

(defn header [page-id]
  [:div#header
   [:h1#logo-text (link-to "/" "Luminus")]
   [:p#slogan "A Clojure web framework"]

   [:div#nav
    [:ul (nav-link page-id "/" "Home" {:class "first"})
     (nav-link page-id "/docs" "Documentation")
     (nav-link page-id "/api" "API")
     (nav-link page-id "/contribute" "Get involved")
     (nav-link page-id "/about" "About")
     ]]
   ;[:div#header-image]
   ])

(defn footer []
  [:div#footer-bottom
   [:div.bottom-left
    [:p "Luminus framework is released under the "
     (link-to "http://www.eclipse.org/legal/epl-v10.html" "Eclipse Public License")]
    [:p "Copyright &copy; 2013 "
     [:strong [:a {:href "http://yogthos.net"} "Dmitri Sotnikov"]] "&nbsp; &nbsp; &nbsp;
   Design by " (link-to "http://www.styleshout.com/" "styleshout") "&nbsp; &nbsp; &nbsp; "
     [:a {:href "http://clojure.org"}
      (image {:id "clojure-logo" :width "20" :height "20"} "http://clojure.org/space/showimage/clojure-icon.gif")
      " powered by Clojure"]]]])


(defn base [page-id & content]
  (html5
    [:head [:title "Welcome to luminus web framework"]
     (include-css "/css/screen.css"
       "/css/shCore.css")
     [:script {:type "text/javascript", :src "//ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"}]
     (include-js "/js/shCore.js"
       "/js/brushes/shBrushClojure.js"
       "/js/brushes/shBrushBash.js"
       "/js/brushes/shBrushCss.js"
       "/js/brushes/shBrushJava.js"
       "/js/brushes/shBrushJScript.js"
       "/js/brushes/shBrushPlain.js"
       "/js/brushes/shBrushXml.js"
       "/js/site.js")]
    [:body
     [:div#wrap
      (header page-id)
      content
      (footer)]]))

(defhtml layout [page-id & content]
  (base page-id
        [:div#content-outer.clear
         [:div#content-wrap
          content]]))

