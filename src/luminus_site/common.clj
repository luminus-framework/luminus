(ns luminus-site.common
  (:use hiccup.element
        hiccup.page))

(defn head []
  [:head
   [:title "Welcome to luminus"]
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
               "/js/site.js")])

(defn nav-link [page-id url name]
  [:li {:class (str
                 "header-link "
                 (if (= name page-id) "selected" "unselected"))}
   (link-to url name)])

(defn footer []
  [:footer            
      [:div#license
       [:p "Luminus framework is released under the " 
        (link-to "http://www.eclipse.org/legal/epl-v10.html" "Eclipse Public License")]
       [:a.yogthos {:href "http://yogthos.net"} #_(image "img/logo.jpg")]]
      [:div#poweredbydiv [:a {:href "http://clojure.org"}
             (image {:id "clojure-logo" :width "20" :height "20"} "http://clojure.org/space/showimage/clojure-icon.gif")
             [:span#poweredby " powered by Clojure"]]]])

(defn layout [page-id & body]
  (html5
    (head)    
    [:body     
     [:header
      [:h1 [:a.yogthos {:href "/"}  "Luminus" #_(image "img/logo.jpg")]]
      [:p#subtitle "- a Clojure web framework optimized for productivity"]]
     
     [:ul.header-menu 
      (nav-link page-id "/" "Home")      
      (nav-link page-id "/docs/tutorial" "Documentation")
      (nav-link page-id "/api" "API")
      (nav-link page-id "/download" "Download")
      (nav-link page-id "/contribute" "Get involved")      
      (nav-link page-id "/about" "About")]     
     
     (into [:content] body)     
     (footer)]))
