## Static resources

By default, any resources located under the `resources/public` directory will be available to the clients.

### Serving static resources

Serving these resources is handled by the default routes found in the `handler` namespace of your application:

```clojure
(defroutes base-routes
  (route/resources "/")
  (route/not-found "Not Found"))
```

Any resources found on the classpath of the application can be accessed using `clojure.java.io/resource` function:

```clojure
(slurp (clojure.java.io/resource "myfile.md"))
```

Conventionally, non-source resources should be placed in the `resources` directory of the project.

### Handling file uploads

Given page called `upload.html` with the following form:

```xml
<h2>Upload a file</h2>
<form action="/upload" enctype="multipart/form-data" method="POST">
    {% csrf-field %}
    <input id="file" name="file" type="file" />
    <input type="submit" value="upload" />
</form>
```

we could then render the page and handle the file upload as follows:

```clojure
(ns myapp.upload
  (:use compojure.core)
  (:require [myapp.layout :as layout]
            [ring.util.response :refer [redirect file-response]])
  (:import [java.io File FileInputStream FileOutputStream]))

(def resource-path "/tmp/")

(defn file-path [path & [filename]]
  (java.net.URLDecoder/decode
    (str path File/separator filename)
    "utf-8"))

(defn upload-file
  "uploads a file to the target folder
   when :create-path? flag is set to true then the target path will be created"
  [path {:keys [tempfile size filename]}]
  (try
    (with-open [in (new FileInputStream tempfile)
                out (new FileOutputStream (file-path path filename))]
      (let [source (.getChannel in)
            dest   (.getChannel out)]
        (.transferFrom dest source 0 (.size source))
        (.flush out)))))

(defroutes home-routes
  (GET "/upload" []
       (layout/render "upload.html"))

  (POST "/upload" [file]
       (upload-file resource-path file)
       (redirect (str "/files/" (:filename file))))

  (GET "/files/:filename" [filename]
       (file-response (str resource-path filename))))
```

Th `:file` request form parameter points to a map containing the description of the file that will be uploaded. Our `upload-file` funciton above uses `:tempfile`, `:size` and `:filename` keys from this map to save the file on disk.


If you're fronting with Nginx then you can easily support file upload progress using its [Upload Progress Module](http://wiki.nginx.org/HttpUploadProgressModule).










