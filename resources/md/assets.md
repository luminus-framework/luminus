The assets for the application are located under the `resources` folder. Assets include any non-source files that you may
wish to include in the application. These may be database migration files, SQL query files, and so on. The `resources/public`
folder is reserved for assets that will be served directly by the HTTP server. This is where you would place assets such as images and CSS.

Luminus has integrated support for [WebJars](http://www.webjars.org/) and defaults to including external assets, such as
Twitter Bootstrap, via this method. Twitter Bootstrap is included as a dependency in the project `[org.webjars.npm/material-icons "0.3.0"]`.
Its contents are made available using the [ring-webjars](https://github.com/weavejester/ring-webjars) middleware.

Once the library is included, its assets become available on the classpath and can be accessed in the HTML templates
using the `/assets` prefix. as seen below:

```xml
{% style "/assets/bulma/css/bulma.min.css" %}
{% style "/assets/material-icons/css/material-icons.min.css" %}
```

The `ring-webjars` library strips out the version number from the included assets. The asset
referenced as `/assets/bulma/css/bulma.min.css` is found on the classpath as
`/webjars/material-icons/0.3.0/css/material-icons.min.css`.

One common type of static assets are icons. Icons are often times made available by means of a webfont.
If your appliction makes use for example of the icons from [font-awesome](https://fontawesome.com), then you will get, from the web-jar-repository, the font-awesome js and css files. Now when your app is loaded and those files are not cached on the client side you will have to send those files over the network, which depending on the connection quality can slow down your appliction start-up-time considerably. In fact the static file for font-awesome may often times be the single most largest file you are serving to the clients.
Now should you find that the inital load times for your app are of concern to you, then consider the following. How many of the icons do you actually need? Do you need js for layering icons? If you find, that you are using only a small fraction of all the icons you ship with your app, then consider creating your own custom web-font with just the subset of icons that your application needs. This can be done easly for example using [fontello](http://fontello.com/).
