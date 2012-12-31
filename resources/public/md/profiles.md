## Profiles

Running `lein new luminus myapp` will create an application using the default profile template.
However, if you would like to attach further functionality to your template you can append
profile hints for the extended functionality.

Currently, the following profiles are supported

* +bootstrap - adds bootstrap css/js static resources
* +sqlite - adds `models.db` namespace which initializes a SQLite database
* +site - creates a template application using bootstrap and sqlite templates, adds registration and authentication

To add a profile simply pass it as an argument after your application name, eg:

```
lein new luminus myapp +bootstrap
```
You can also mix multiple profiles when creating the applicaiton, eg:
```
lein new luminus myapp +bootstrap +sqlite
```

In case two profiles generate the same files, the latest one will overwrite the files from the preceding profiles.