(ns luminus.util
  (:require [noir.io :as io]
            [markdown.core :as md]
            [clj-http.client :as client]
            [crouton.html :as html]))

(defn required-number
  "Returns 0 instead of nil if x is not a number."
  [x]
  (if-not (number? x) 0 x))

(defn required-string
  "Returns blank (not nil) string if x is not a string
   if convert? apply str to x"
  ([x] (required-string x false))
  ([x ^java.lang.Boolean convert?]
  (if convert?
    (.toString x)
    (if-not (string? x) " " x))))
  
(defmulti str-limit
  (fn [x y]))

(defmethod str-limit
  java.lang.String
  [x ^java.lang.Long max]
  (when-not (empty? x)
    (if (> (count x)  
           max)
      (subs x 0 max)
      x)))

(defmethod str-limit
  java.lang.Long
  [max ^java.lang.String x]
  (when-not (empty? x)
    (if (> (count x)  
           max)
      (subs x 0 max)
      x)))

(defn format-time
  "formats the time using SimpleDateFormat, the default format is
   \"dd MMM, yyyy\" and a custom one can be passed in as the second argument"
  ([time] (format-time time "dd MMM, yyyy"))
  ([time fmt]
    (.format (new java.text.SimpleDateFormat fmt) time)))

(defn md->html
  "reads a markdown file from public/md and returns an HTML string"
  [filename]
  (->>
    (io/slurp-resource (str "/md/" filename))
    (md/md-to-html-string)))

(defn fetch-doc-pages []
  (with-open
    [r (->> "https://raw.github.com/yogthos/luminus/master/resources/public/docpages.clj"         
         client/get
         :body
         java.io.StringReader.
         java.io.PushbackReader.)]
      (binding [*read-eval* false]
        (read r))))

(defn fetch-doc [name]
  (md/md-to-html-string
    (->> name
      (str "https://raw.github.com/yogthos/luminus/master/resources/public/md/")
      (client/get)
      :body)
    :heading-anchors true))

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
  (into [:ol.contents]
    (for [{[{{name :name} :attrs} title] :content} headings]
      [:li [:a {:href (str "#" name)} title]])))

(defn generate-toc [html]
  (-> html
    (.getBytes)
    (java.io.ByteArrayInputStream.)
    (html/parse)
    :content
    (get-headings)
    (make-links)))
