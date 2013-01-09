## Running Standalone

To create a standalone executable for your application simply run 

```bash
lein uberjar
```

The resulting `jar` can be found in the `target` folder. It can be run as follows:
```bash
java -jar myapp-0.1.0-SNAPSHOT-standalone.jar
```

The standalone application uses an embedded Jetty server to run the application.

## Deploying to Tomcat

You need to package the application as a WAR archive, to do that run:
```bash
lein ring uberwar
```
then simply copy the resulting `myapp-0.1.0-SNAPSHOT-standalone.war` to the `webapps` folder on Tomcat, eg:
```bash
cp target/myapp-0.1.0-SNAPSHOT-standalone.war ~/tomcat/webapps/myapp.war
```
Your app will now be avaliable at the context `/myapp` when Tomcat starts. To deploy the app
at root context, simply copy it to `webapp` as `ROOT.war`. 

## Heroku Deployment

**Heroku deployment steps assume that you are running Leiningen version 2**

First, make sure you have [Heroku toolbelt](https://toolbelt.heroku.com/) installed.

If you created the project using +heroku flag then simply follow the steps below:

test that your application runs locally with `foreman` 
```
foreman start
```

initialize your git repo

```
git init
git add .
git commit -m "init"
```

create your app on Heroku

```
heroku create
```

deploy the application

```
git push heroku master
```

Your application should now be deployed to Heroku

If you did not use +heroku flag then you need to add the following to your `project.clj`

```clojure
:dependencies [... [environ "0.3.0"]]
:min-lein-version "2.0.0"
:plugins [... [environ/environ.lein "0.3.0"]]
:hooks [environ.leiningen.hooks]
:profiles {:production {:env {:production true}}
           ...}
```

Then create a new file called `Procfile` in the root folder of your application with the following contents:
```
web: lein with-profile production trampoline run -m myapp.server $PORT
```

For further instructions see the [official documentation](https://devcenter.heroku.com/articles/clojure).