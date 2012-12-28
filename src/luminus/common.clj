(ns luminus.common
  (:use hiccup.element
        hiccup.page))

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
               "/js/site.js")
   [:script {:type "text/javascript"}
    "var _gaq = _gaq || [];
     _gaq.push(['_setAccount', 'UA-37258749-1']);
     _gaq.push(['_trackPageview']);

     (function() {
      var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
      ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
      var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
     })();"]])

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

(defn layout [page-id & body]
  (html5
    (head)    
    [:body     
     [:header
      [:h1 [:a.yogthos {:href "/"}  "Luminus" #_(image "img/logo.jpg")]]
      [:p#subtitle "- a friendly Clojure web framework"]]
     
     [:ul.header-menu 
      (nav-link page-id "/" "Home")      
      (nav-link page-id "/docs/tutorial" "Documentation")
      ;(nav-link page-id "/api" "API")      
      (nav-link page-id "/contribute" "Get involved")      
      (nav-link page-id "/about" "About")]     
     
     (into [:content] body)     
     (footer)]))
