## Static resources

There are several helpers for serving static resources found under the `noir.io`
namespace.

You can get the path to public folder of the application by calling `resource-path`.

### Handling file uploads

Uploading files is handled via `upload-file` in `noir.io` namespace which accepts a
file-system path and the file map.

If we had an `upload.html` page with the following form:

```xml
<h2>Upload a file</h2>
<form action="/upload" enctype="multipart/form-data" method="POST">
    <input id="file" name="file" type="file" />
    <input type="submit" value="upload" />
</form>
```

we could then render the page and handle the file upload as follows:

```clojure
(ns myapp.upload
  (:use compojure.core)
  (:require [myapp.layout :as layout]
            [noir.io :as io]
            [noir.response :as response]
            [ring.util.response :refer [file-response]]))

(def resource-path "/tmp/")

(defroutes home-routes
  (GET "/upload" []
       (layout/render "upload.html"))

  (POST "/upload" [file]
       (io/upload-file resource-path file)
       (response/redirect
         (str "/files/" (:filename file))))

  (GET "/files/:filename" [filename]
       (file-response (str resource-path filename))))  
```

If you're fronting with Nginx then you can easily support file upload progress using its [Upload Progress Module](http://wiki.nginx.org/HttpUploadProgressModule).

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










