(ns luminus.common
  (:use hiccup.element
        hiccup.page
        hiccup.def))

(defn head []
  [:head
   [:title "Luminus"]
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
        (link-to "http://www.eclipse.org/legal/epl-v10.html" "Eclipse Public License")]]
      [:div#copyright [:a {:href "http://yogthos.net"} "Copyright © 2012 Dmitri Sotnikov"]]
      [:div#poweredbydiv [:a {:href "http://clojure.org"}
                          (image {:id "clojure-logo" :width "20" :height "20"} "http://clojure.org/space/showimage/clojure-icon.gif")
                          [:span#poweredby " powered by Clojure"]]]])

(defhtml layout [page-id & body]  
  (head)    
  [:body     
   [:header
    [:h1 [:a.yogthos {:href "/"}  "Luminus"]]
    [:p#subtitle "- a friendly Clojure web framework"]]     
   [:ul.header-menu 
    (nav-link page-id "/" "Home")      
    (nav-link page-id "/docs" "Documentation")
    (nav-link page-id "/noir-api/index.html" "API")
    (nav-link page-id "/contribute" "Get involved")      
    (nav-link page-id "/about" "About")]     
   
   (into [:content] body)     
   (footer)])
