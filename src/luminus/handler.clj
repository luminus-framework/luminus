(ns luminus.handler
  (:require [compojure.core :refer [GET defroutes routes]]
            [compojure.route :as route]
            [luminus.util :as util]
            [selmer.parser :as parser]
            [selmer.filters :refer [add-filter!]]
            [markdown.core :refer [md-to-html-string]]
            [ring.util.response :refer [content-type response]]
            [compojure.response :refer [Renderable]]))

(parser/cache-off!)

(parser/set-resource-path! (clojure.java.io/resource "templates"))

(add-filter! :markdown (fn [content] [:safe (md-to-html-string content)]))

(deftype RenderableTemplate [template params]
  Renderable
  (render [this request]
    (content-type
      (->> (assoc params
             :page template
             :servlet-context
             (if-let [context (:servlet-context request)]
               (try (.getContextPath context)
                    (catch IllegalArgumentException _ context))))
           (parser/render-file (str template))
           response)
      "text/html; charset=utf-8")))

(defn render [template & [params]]
  (RenderableTemplate. template params))

(defn doc-page [doc]
  (let [doc-content (get @util/docs doc)
        topics (:pages @util/docs)]
    (render "docs.html" {:title   (get (into {} topics) doc)
                         :toc     (util/generate-toc doc-content)
                         :content doc-content
                         :topics  topics})))

(defroutes app-routes
 (GET "/" [] (render "home.html"))
 (GET "/docs" [] (doc-page "guestbook.md"))
 (GET "/docs/:doc" [doc] (doc-page doc))
 (GET "/contribute" [] (render "contribute.html" {:content (util/slurp-resource "md/contributing.md")}))
 (route/resources "/")
 (route/not-found (render "404.html")))

(defn init []
  (.start
    (doto (Thread. #(while true (util/refresh-docs!) (Thread/sleep 600000)))
      (.setDaemon true))))

(defn destroy []
  (println "shutting down!"))

(def app (routes app-routes))
