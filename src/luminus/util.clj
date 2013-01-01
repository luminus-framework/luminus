(ns luminus.util
  (:require [noir.io :as io]
            [markdown.core :as md]
            [clj-http.client :as client]))

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

(defn fetch-doc [name]
  (->> name 
    (str "https://raw.github.com/yogthos/luminus/master/resources/public/md/")
    (client/get)
    :body
    (md/md-to-html-string)))
