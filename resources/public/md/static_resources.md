## Static resources

There are several helpers for serving static resources found under the `noir.io`
namespace.

You can get the path to public folder of the application by calling `resource-path`.

### Handling file uploads

Uploading files is handled via `upload-file` in `noir.io` namespace which accepts a
path relative to the public folder and the file map, eg:

```clojure
(ns myapp.upload
  ...
  (:require [upload-test.views.layout :as layout]
            [noir.response :as response]
            [noir.io :as io]))

(defn upload-page []
  (layout/common
    [:h2 "Upload a file"]
    (form-to {:enctype "multipart/form-data"}
             [:post "/upload"]
             (file-upload :file)
             (submit-button "upload"))))

(defn handle-upload [file]
  (io/upload-file "/" file)
  (response/redirect
    (str "/" (:filename file))))

(defroutes upload-routes
  (GET "/upload" [] (upload-page))
  (POST "/upload" [file] (handle-upload file)))
```

### Serving static resources

You can load a static resource relative to the public folder using `get-resource`
with the relative path supplied as individual strings, eg:

```clojure
(get-resource "/md/outline.md")
```

The above will return clojure.java.io/resource for `screen.css` located at public/md/outline.md path.

Finally, there's `slurp-resource` which will read the contents of the file and
return a string, eg:

```clojure
(slurp-resource "/md/outline.md")
```










