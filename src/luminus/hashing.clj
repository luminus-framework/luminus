(ns luminus.hashing
    (:import java.security.MessageDigest))

(defn
  ^{:private true}
  hasher
  "Hashing digest action handler. Common types -> SHA1,SHA-256,MD5"
  [instance-type data salt]
  (let [_ (if-not salt
            (.toString data)
            (let [[s d] (map 
                         (memfn toString)
                         [salt data])]
              (apply str [s d s])))
        sha1-obj (doto (MessageDigest/getInstance instance-type)
                   .reset
                   (.update
                    (.getBytes _)))]
    (apply str
           (map (partial format "%02x")
                (.digest sha1-obj)))))

(defn md5
  [data & salt]
  (let [hash "MD5"]
    (hasher hash data salt)))

(defn sha1 
  [data & salt]
  (hasher "SHA1" data salt))

(defn sha2
  [data & salt]
  (hasher "SHA-256" data salt))
