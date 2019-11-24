(ns luminus.core
  (:require
    [me.raynes.fs :as fs]
    [clojure.java.io :as io]
    [clojure.string :as string]
    [luminus.util :as util]
    [selmer.parser :as parser]
    [selmer.filters :refer [add-filter!]]
    [markdown.core :refer [md-to-html-string]]))

(parser/set-resource-path! (clojure.java.io/resource "templates"))

(add-filter! :markdown (fn [content] [:safe (md-to-html-string content)]))

(defn render-page [[url template params]]
  (let [path (str "static" (string/replace url #"/" (java.io.File/separator)))]
    (io/make-parents path)
    (spit path (parser/render-file (str template) (assoc params :page template)))))

(defn translated-topics [docs]
  (mapv
    (fn [[doc-id title]]
      [(string/replace doc-id #".md$" ".html") title])
    (:topics docs)))

(defn render-doc [docs doc-id]
  (merge
    {:title  (get-in docs [:docs-by-topic doc-id])
     :topics (translated-topics docs)}
    (get docs doc-id)))

(defn doc-page [docs doc-id]
  [(str "/docs/" (string/replace doc-id #".md$" ".html"))
   "docs.html"
   (render-doc docs doc-id)])

(defn pages [docs]
  (into
    [["/index.html" "home.html"]
     ["/404.html" "404.html"]
     ["/contribute.html" "contribute.html" {:content (util/slurp-resource "md/contributing.md")}]]
    (map (partial doc-page docs) (keys (dissoc docs :docs-by-topic :topics)))))

(defn -main []
  (fs/delete-dir "static")
  (fs/copy-dir "resources/static" "static")
  (doseq [page (pages (util/generate-docs))]
    (render-page page)))

(comment
  (-main)

  )
