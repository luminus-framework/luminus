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

Simply follow the steps in the [official documentation](https://devcenter.heroku.com/articles/clojure)