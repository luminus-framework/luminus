## Running Standalone

To create a standalone executable for your application simply run

```bash
lein ring uberjar
```

The resulting `jar` can be found in the `target` folder. It can be run as follows:

```bash
java -jar myapp-0.1.0-SNAPSHOT-standalone.jar
```

The standalone application uses an embedded Jetty server to run the application.
To specify a custom port you need to set the `$PORT` environment variable, eg:

```
export PORT=8080
java -jar target/myapp1-0.1.0-SNAPSHOT-standalone.jar
```

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

First, make sure you have [git](http://git-scm.com/downloads) and [Heroku toolbelt](https://toolbelt.heroku.com/) installed, then simply follow the steps below.

Optionally, test that your application runs locally with `foreman` by running.

```
foreman start
```

Now, you can initialize your git repo and commit your application.

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

Your application should now be deployed to Heroku!

For further instructions see the [official documentation](https://devcenter.heroku.com/articles/clojure).
