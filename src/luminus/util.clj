(ns luminus.util
  (:require [markdown.core :as md]
            [clj-http.client :as client]
            [crouton.html :as html]
            [hiccup.core :as hiccup]
            [clojure.java.io :refer [resource]]))

(def docs (atom {}))

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
  (with-open
    [r (->> "https://raw.github.com/luminus-framework/luminus/master/resources/docpages.edn"
            client/get
            :body
            java.io.StringReader.
            java.io.PushbackReader.)]
    (binding [*read-eval* false]
      (read r))))

(defn fetch-doc [name]
  (md/md-to-html-string
    (->> name
         (str "https://raw.github.com/luminus-framework/luminus/master/resources/md/")
         (client/get)
         :body)
    :heading-anchors true
    :code-style #(str "class=\"" % "\"")))

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
      (into [:ol.contents]
        (for [{[{{name :name} :attrs} title] :content} headings]
          [:li [:a {:href (str "#" name)} title]])))))

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
    (reset! docs {:topics pages :docs-by-topic (into {} pages)})
    (doseq [id (map first pages)]
      (when-let [doc (try (fetch-doc id) (catch Exception _))]
        (swap! docs assoc id {:toc (generate-toc doc) :content doc}))
      (Thread/sleep 1000))))


