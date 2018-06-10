(ns luminus.util
  (:require [markdown.core :as md]
            [clj-http.client :as client]
            [clojure.edn :as edn] ;;TODO remove again
            [crouton.html :as html]
            [hiccup.core :as hiccup]
            [clojure.edn :as edn]
            [clojure.java.io :refer [resource]]
            [clojure.string :as s]))

(defonce docs (agent {}))

(defn remove-div-spans [text state]
  (let [opener #"&lt;(boot|lein)-div&gt;"
        closer #"&lt;/(boot|lein)-div&gt;"]
    (if (or (:codeblock state)
            (:code state))
      [(-> text
           (s/replace opener "<div class=\"$1\">")
           (s/replace closer "</div>"))
       state]
      [text state])))

(defn format-time
  "formats the time using SimpleDateFormat, the default format is
   \"dd MMM, yyyy\" and a custom one can be passed in as the second argument"
  ([time] (format-time time "dd MMM, yyyy"))
  ([time fmt]
   (.format (new java.text.SimpleDateFormat fmt) time)))

(defn slurp-resource
  "reads a markdown file from resources/md and returns an HTML string"
  [filename]
  (->> filename resource slurp))

(defn fetch-doc-pages []
  (->> "https://raw.github.com/luminus-framework/luminus/master/resources/docpages.edn"
       client/get
       :body
       (edn/read-string)))

(defn fetch-doc-pages-local []
  (edn/read-string (slurp (resource "docpages.edn"))))

(defn fetch-doc [name]
  (md/md-to-html-string
    (->> name
         (str "https://raw.github.com/luminus-framework/luminus/master/resources/md/")
         (client/get)
         :body)
    :heading-anchors true
    :code-style #(str "class=\"" % "\"")))

(defn fetch-doc-local [name]
  (md/md-to-html-string
   (slurp-resource (str "md/" name))
   :heading-anchors true
   :code-style #(str "class=\"" % "\"")
   :replacement-transformers (conj markdown.transformers/transformer-vector
                                   remove-div-spans)))

(defn get-headings [content]
  (reduce
    (fn [headings {:keys [tag attrs content] :as elm}]
      (if (some #{tag} [:h1 :h2 :h3])
        (conj headings elm)
        (if-let [more-headings (get-headings content)]
          (into headings more-headings)
          headings)))
    [] content))

(defn make-links [headings]
  (when (not-empty headings)
    (hiccup/html
      [:ol.contents
       (for [{[{{name :name} :attrs} title] :content} headings]
         [:li [:a {:href (str "#" name)} title]])])))

(defn generate-toc [content]
  (when content
    (-> content
        (.getBytes)
        (java.io.ByteArrayInputStream.)
        html/parse
        :content
        get-headings
        make-links)))

(defn refresh-docs! []
  (when-let [pages (try (fetch-doc-pages) (catch Exception _))]
    (send docs
          (fn [_]
            (reduce
              (fn [docs id]
                (if-let [doc (try (Thread/sleep 1000) (fetch-doc id) (catch Exception _))]
                  (assoc docs id {:toc (generate-toc doc) :content doc})
                  docs))
              {:topics pages :docs-by-topic (into {} pages)}
              (map first pages))))))


