## Profiles

Running `lein new luminus myapp` will create an application using the default profile template.
However, if you would like to attach further functionality to your template you can append
profile hints for the extended functionality.

Currently, the following profiles are supported

* +bootstrap - adds bootstrap css/js static resources
* +hiccup - switches to using Hiccup templating instead of Clabango
* +cljs - adds ClojureScript support to the project along with an example
* +h2 - adds `models.db` namespace and H2 db dependencies
* +postgres - adds `models.db` namespace and add PostreSQL dependencies
* +site - creates an application with registration and authentication setup, uses bootstrap and h2 when database is not specified
* +dailycred - it adds support for http://www.dailycred.com, when used together with +site it configures the application to authenticate with dailycred

To add a profile simply pass it as an argument after your application name, eg:

```
lein new luminus myapp +bootstrap
```

You can also mix multiple profiles when creating the application, eg:

```
lein new luminus myapp +site +postgres
```

In case two profiles generate the same files, the latest one will overwrite the files from the preceding profiles.
