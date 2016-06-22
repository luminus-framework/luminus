## Running Standalone

To create a standalone executable for your application simply run

```bash
lein ring uberjar
```

The resulting `jar` can be found in the `target` folder. It can be run as follows:

```bash
java -jar myapp.jar
```

The standalone application uses an embedded Jetty server to run the application.
To specify a custom port you need to set the `$PORT` environment variable, eg:

```
export PORT=8080
java -jar target/myapp.jar
```
## Deploying on Immutant

A Luminus app created using lein new luminus myapp should deploy to [Immutant](http://immutant.org/) out of the box. 
Simply run the following from the root folder of the applicaiton:

```
lein immutant deploy
```

More information available on the [official site](http://immutant.org/tutorials/deploying/index.html).

## Deploying to Tomcat

A WAR archive needs to be generated in order to deploy the application to a container such as Apache Tomcat. In order to create a WAR you can package the application by running:
```bash
lein ring uberwar
```

Next, simply copy the resulting `myapp.war` to the `webapps` folder on Tomcat, eg:

```bash
cp target/myapp.war ~/tomcat/webapps/
```

Your app will now be avaliable at the context `/myapp` when Tomcat starts. To deploy the app
at root context, simply copy it to `webapp` as `ROOT.war`.

### JNDI database configuration

Tomcat may have database configuration specified as a [JNDI resource](https://tomcat.apache.org/tomcat-7.0-doc/jndi-resources-howto.html) (check JDBC Data Sources
section). In this case you need to fetch this data from the Tomcat configuration and not from clojure profiles. Just change this lines in `src/clj/<app>/db/core.clj`:

```clojure
(defstate ^:dynamic *db*
           :start (conman/connect!
                   {:init-size  1
                    :min-idle   1
                    :max-idle   4
                    :max-active 32
                    :jdbc-url   (env :database-url)})
           :stop (conman/disconnect! *db*))
```

to:

```clojure
(def ^:dynamic *db* {:name "java:comp/env/jdbc/EmployeeDB"})
```

(in this example `jdbc/EmployeeDB` is the same name as specified in `context.xml` and `web.xml` in the [JNDI HowTo page](https://tomcat.apache.org/tomcat-7.0-doc/jndi-resources-howto.html). Note that this name is case sensitive.)

## VPS Deployment

Virtual Private Servers (VPS) such as [DigitalOcean](https://www.digitalocean.com/) provide a cheap hosting option for Clojure applications. 

Follow [this guide](https://www.digitalocean.com/community/tutorials/how-to-create-your-first-digitalocean-droplet-virtual-server) in order to setup your DigitalOcean server. Once the server is created you can install Ubuntu [as described here](https://www.digitalocean.com/community/tutorials/initial-server-setup-with-ubuntu-12-04). Finally, install Java on your Ubuntu instance by following [these instructions](https://help.ubuntu.com/community/Java).

Create a `deploy` user for running the Nginx server:

```
adduser deploy
passwd -l deploy
visudo
```

add the `sudo` priviliges for the user:

```
deploy  ALL=NOPASSWD: /etc/init.d/nginx
```

You're now ready to deploy your application to the server. The most common approach is to run the `uberjar` and front it using [Nginx](http://wiki.nginx.org/Main).


Create a directory for your application on the server such as `/var/myapp` then upload your application to the server using `scp`:

```
$ scp myapp.jar user@<domain>:/var/myapp/
```

You should now test that you're able to run the application. Connect to the server using `ssh` and run the application:

```
java -jar /var/myapp/myapp.jar
```

If everything went well then your application should now be accessible on the server at `http://<domain>:3000`. If your application is not accessible make sure that the firewall is configured to allow access to the port.

First, you'll need to create a user for deployment and run `visudo` to set the user `sudo` permissions:

```
adduser deploy
visudo
```

then add the following line to the `sudo` config:

```
deploy  ALL=NOPASSWD: /etc/init.d/nginx
```

Now, let's stop the application instance and create a an `upstart` configuration to manage its lifecycle. To do this you will need to create a file called `/etc/init/myapp.conf` and put the following settings there:

```
description "Run my app"

setuid deploy
setgid deploy

start on runlevel startup
stop on runlevel shutdown

respawn

chdir /var/myapp

script
        exec java -jar /var/myapp/myapp.jar
end script        
```

You should now be able to start the application by running:

```
$ start myapp
```

Test that the application starts up correctly by navigating to its URL `http://<domain>:3000`. You're now ready to setup Nginx to front the application on port `80`.

Install Nginx using the following command:

```
$ sudo apt-get install nginx
```

Next, make a backup of the default configuration in `/etc/nginx/sites-available/default` and replace it with a custom configuration file for the application such as:

```
server{
  listen 80 default_server;
  listen [::]:80 default_server ipv6only=on;
  server_name localhost mydomain.com www.mydomain.com;

  access_log /var/log/myapp_access.log;
  error_log /var/log/myapp_error.log;

  location / {
    proxy_pass http://localhost:3000/;
    proxy_set_header Host $http_host;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_redirect  off;
  }
}
```

Restart Nginx by running:

```
sudo service nginx restart
```

Then test that the application is available at `http://<domain>`.

Optionally, you can configure Nginx to serve static resources for the application. In order to do that you will need to ensure that all static resources are served using a common prefix such as `static`. Next, upload the `resources/public/static` folder from your application to the server to a location such as `/var/myapp/static` by running the following command from the project folder:

```
scp -r resources/public/static user@<domain>:/var/myapp/static
```

Now add the following additional configuration option under the `server` section of the Ngnix configuration above:

```
location /static/ {
    alias /var/myapp/static/;
  }
```

This will cause Nginx to bypass your application for any requests to `http://<domain>/static` and serve them directly instead.

To enable compression make sure the following settings are present in your `/etc/nginx/nginx.conf`:

```
gzip on;
gzip_disable "msie6";

gzip_vary on;
gzip_proxied any;
gzip_comp_level 6;
gzip_buffers 16 8k;
gzip_http_version 1.1;
gzip_types text/plain text/css application/json application/x-javascript text/xml application/xml application/xml+rss text/javascript;">
```

Finally, configure your firewall to only allow access to specified ports by running the following commands:

```
$ sudo ufw allow ssh
$ sudo ufw allow http
$ sudo ufw allow https
$ sudo ufw enable
```

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

optionally, create a database for the application
```
heroku addons:add heroku-postgresql
```

The connection settings can be found at your
[Heroku dashboard](https://dashboard.heroku.com/apps/) under
the add-ons for the app.

deploy the application

```
git push heroku master
```

Your application should now be deployed to Heroku!

For further instructions see the [official documentation](https://devcenter.heroku.com/articles/clojure).
