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
## Delpoying on Immutant


Please follow the steps outlined in the [official Immutant documentation](http://immutant.org/documentation/2.0.0-beta2/apidoc/guide-installation.html) for Immutant deployment.

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

If your site has any user authentication then you will also want to use HTTPS. You will first need to provide a SSL certificate and its key. We'll call these `cert.crt` and `cert.key` respectively.

You can create a self signed certificate as follows:

```
sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/nginx/cert.key -out /etc/nginx/cert.crt
```

Next, you'll want to update the configuration in `/etc/nginx/sites-available/default` as follows:

```
server {
    listen 80;
    return 301 https://$host$request_uri;
}

server {

    listen 443;
    server_name localhost mydomain.com www.mydomain.com;

    ssl_certificate           /etc/nginx/cert.crt;
    ssl_certificate_key       /etc/nginx/cert.key;

    ssl on;
    ssl_session_cache  builtin:1000  shared:SSL:10m;
    ssl_protocols  TLSv1 TLSv1.1 TLSv1.2;
    ssl_ciphers HIGH:!aNULL:!eNULL:!EXPORT:!CAMELLIA:!DES:!MD5:!PSK:!RC4;
    ssl_prefer_server_ciphers on;

    access_log /var/log/myapp_access.log;
    error_log /var/log/myapp_error.log;

    location / {

      proxy_set_header        Host $host;
      proxy_set_header        X-Real-IP $remote_addr;
      proxy_set_header        X-Forwarded-For $proxy_add_x_forwarded_for;
      proxy_set_header        X-Forwarded-Proto $scheme;

      # Fix the “It appears that your reverse proxy set up is broken" error.
      proxy_pass          http://localhost:3000;
      proxy_read_timeout  90;

      proxy_redirect      http://localhost:3000 https://mydomain.com;
    }
  }
```

The above will cause Nginx to redirect HTTP requests to HTTPS and use the provided certificate to serve them.

Finally, configure your firewall to only allow access to specified ports by running the following commands:

```
$ sudo ufw allow ssh
$ sudo ufw allow http
$ sudo ufw allow https
$ sudo ufw enable
```

## Heroku Deployment

First, make sure you have [Git](http://git-scm.com/downloads) and [Heroku toolbelt](https://toolbelt.heroku.com/) installed, then simply follow the steps below.

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
