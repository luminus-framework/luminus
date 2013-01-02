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
  [:div {:id "header"}
   [:h1 {:id "logo-text"} (link-to "index.html" "Luminus")]
   [:p {:id "slogan"} "A Clojure web framework"]

   [:div {:id "nav"}
    [:ul (nav-link page-id "/" "Home" {:class "first"})
     (nav-link page-id "/docs" "Documentation")
     (nav-link page-id "/contribute" "Get involved")
     (nav-link page-id "/about" "About")
     ]]
   [:div {:id "header-image"}]
   ])

(defn footer []
  [:div {:id "footer-bottom"}
   [:div.bottom-left [:p "Luminus framework is released under the "
                      (link-to "http://www.eclipse.org/legal/epl-v10.html" "Eclipse Public License")]
    [:p "Copyright &copy; 2013 " [:strong [:a {:href "http://yogthos.net"} "Dmitri Sotnikov"]] "&nbsp; &nbsp; &nbsp;
   Design by " (link-to "http://www.styleshout.com/" "styleshout") "&nbsp; &nbsp; &nbsp; "[:a {:href "http://clojure.org"}
                       (image {:id "clojure-logo" :width "20" :height "20"} "http://clojure.org/space/showimage/clojure-icon.gif")
                       " powered by Clojure"]]
    ]])


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
    [:body [:div {:id "wrap"}
            (header page-id)
            content
            (footer)]]))

(defhtml layout [page-id & content]
  (base page-id
            [:div.clear {:id "content-outer"}
             [:div {:id "content-wrap"}
              content
              ]]
            ))

(defhtml layout-home [page-id & content]
  (base page-id
    [:div.clear {:id "featured"}
     [:div.image-block (image "images/img-featured.png" "featured")]
     [:div.text-block [:h2 (link-to "index.html" "Read me first")]
      [:p.post-info "Posted by " (link-to "index.html" "erwin") "| Filed under " (link-to "index.html" "templates") ", "
       (link-to "index.html" "internet")]
      [:p [:strong "Luminus"] " is a lightweight web framework written in Clojure. It is based on popular libraries like noir-lib, compojure and aims to provide a robust, scalable and easy to use platform."]
      [:p "Luminus is released under the "
       (link-to "http://www.eclipse.org/legal/epl-v10.html" "Eclipse Public License")]
      [:p [:a.more-link {:href (to-uri "index.html")} "Read More"]]]]

    [:div.clear {:id "content-outer"}
     [:div {:id "content-wrap"}
      content
      ]]
    ))
