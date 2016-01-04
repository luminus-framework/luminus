## Running Standalone

To create a standalone executable for your application simply run

```bash
lein uberjar
```

The resulting `jar` can be found in the `target` folder. It can be run as follows:

```bash
java -jar myapp.jar
```

By default the standalone application uses an embedded Immutant server to run the application.
However, if you used a profile such as `+jetty` then the alternate server will be used instead.
To specify a custom port you need to set the `$PORT` environment variable, eg:

```
export PORT=8080
java -jar myapp.jar
```
## Deploying on Immutant App Server

Please follow the steps outlined in the [official Immutant documentation](http://immutant.org/documentation/2.0.2/apidoc/guide-installation.html) for Immutant application server deployment.

## Deploying to Tomcat

A WAR archive needs to be generated in order to deploy the application to a container such as Apache Tomcat. This is only supported via the [lein-uberwar](https://github.com/luminus-framework/lein-uberwar) plugin that's included using the `+war` profile.

To enable the `lein-uberwar` plugin manually add the following configuration in the `project.clj` file:

```clojure
:plugins [...
          [lein-uberwar "0.1.0"]]

  :uberwar {:handler <app>.handler/app
            :init <app>.handler/init
            :destroy <app>.handler/destroy
            :name "<app>.war"}
```

In order to create a WAR you can package the application by running:
```bash
lein uberwar
```

Next, simply copy the resulting `<app>.war` to the `webapps` folder on Tomcat, eg:

```bash
cp target/<app>.war ~/tomcat/webapps/
```

Your app will now be avaliable at the context `/<app>` when Tomcat starts. To deploy the app
at root context, simply copy it to `webapp` as `ROOT.war`.

## VPS Deployment

Virtual Private Servers (VPS) such as [DigitalOcean](https://www.digitalocean.com/) provide a cheap hosting option for Clojure applications. 

Follow [this guide](https://www.digitalocean.com/community/tutorials/how-to-create-your-first-digitalocean-droplet-virtual-server) in order to setup your DigitalOcean server. Once the server is created you can install Ubuntu [as described here](https://www.digitalocean.com/community/tutorials/initial-server-setup-with-ubuntu-12-04). Finally, install Java on your Ubuntu instance by following [these instructions](https://help.ubuntu.com/community/Java). The instructions below apply for Ubuntu 15.04 and newer.

The most common approach is to run the `uberjar` and front it using [Nginx](http://wiki.nginx.org/Main).

### Application deployment

In this step, we will deploy your application to the server, and make sure that it is started automatically on boot. We use `systemd` for this.
Create a `deploy` user that will run your application:

```
sudo adduser -m deploy
sudo passwd -l deploy
```

Create a directory for your application on the server such as `/var/myapp` then upload your application to the server using `scp`:

```
$ scp myapp.jar user@<domain>:/var/myapp/
```

You should now test that you're able to run the application. Connect to the server using `ssh` and run the application:

```
java -jar /var/myapp/myapp.jar
```

If everything went well, your application now runs locally. The following command will confirm that the applications runs as expected:
```
curl http://127.0.0.1:3000/
```
Your application should also now be accessible on the server at `http://<domain>:3000`. If your application is not accessible make sure that the firewall is configured to allow access to the port. Depending on your VPS provider, you may need to create an access point for the port 3000.
* [Creating access point on Azure](https://azure.microsoft.com/en-us/documentation/articles/virtual-machines-set-up-endpoints/)
* [Creating access point on Amazon EC2](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-network-security.html#adding-security-group-rule)

Now, let's stop the application instance and create a `systemd` configuration to manage its lifecycle, especially taking care that the application will be launched on server boot.
Create the file `/lib/systemd/system/myapp.service` with the following content:

```
[Unit]
Description=My Application
After=network.target

[Service]
WorkingDirectory=/var/myapp
EnvironmentFile=-/var/myapp/env
Environment="DATABASE_URL=jdbc:postgresql://localhost/app?user=app_user&password=secret"
ExecStart=/usr/bin/java -jar /var/myapp/myapp.jar
User=deploy

[Install]
WantedBy=multi-user.target
```

Tell `systemd` to start the application everytimes the system reboots with the following commands:
```
sudo systemctl daemon-reload
sudo systemctl enable myapp.service
```

When the system reboots your application will now start and will be ready to process requests. You may want to test that. Simply reboot your machine, and check the running processes:
```
 ps -ef | grep java
```
This should return something like this. Pay attention to the `UID`, it should be `deploy`, leaving it to `root` would present a significant security risk.
```
deploy     730     1  1 06:45 ?        00:00:42 /usr/bin/java -jar /var/mysite/mysite.jar
```

### Fronting with Nginx

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

### Setting up SSL

If your site has any user authentication then you will also want to use HTTPS. You will first need to provide a SSL certificate and its key. We'll call these `cert.crt` and `cert.key` respectively.

#### Setting up SSL Certificate using Let's Encrypt

Download the installation tool and generate the certificate using the following commands:

```
git clone https://github.com/letsencrypt/letsencrypt
cd letsencrypt
/letsencrypt-auto certonly --email <you@email.com> -d <yoursite.com> -d <www.yoursite.com> --webroot --webroot-path /var/www/html
```

Optionally, setup a Cron job to automatically update the certificate by updating crontab by running as `root`:

```
su
crontab -e
```
Add the following line:

```
0 0 1,15 * * /path-to-letsencrypt/letsencrypt-auto certonly --keep-until-expiring --email <you@email.com> -d <yoursite.com> -d <www.yoursite.com> --webroot --webroot-path /var/www/html
```


We'll generate a stronger DHE parameter instead of using OpenSSL's defaults, which include a 1024-bit key for the key-exchange:

```
cd /etc/ssl/certs
openssl dhparam -out dhparam.pem 4096
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

    ssl_certificate           /etc/letsencrypt/live/<yoursite.com>/fullchain.pem;
    ssl_certificate_key       /etc/letsencrypt/live/<yoursite.com>/privkey.pem;

    ssl on;
    ssl_prefer_server_ciphers  on;
    ssl_session_timeout        180m;
    ssl_session_cache  builtin:1000  shared:SSL:10m;
    ssl_protocols  TLSv1 TLSv1.1 TLSv1.2;
    ssl_ciphers 'AES256+EECDH:AES256+EDH';
    ssl_dhparam /etc/ssl/certs/dhparam.pem;
    add_header Strict-Transport-Security 'max-age=31536000';

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

You can test the SSL configuration using the [SSL Server Test](https://www.ssllabs.com/ssltest/).

## Heroku Deployment

First, make sure you have [Git](http://git-scm.com/downloads) and [Heroku toolbelt](https://toolbelt.heroku.com/) installed, then simply follow the steps below.

Optionally, test that your application runs locally by running.

```
heroku local
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
heroku addons:create heroku-postgresql
```

The connection settings can be found at your
[Heroku dashboard](https://dashboard.heroku.com/apps/) under
the add-ons for the app.

deploy the application

```
git push heroku master
```

Your application should now be deployed to Heroku!

To initialize or update your database:

```
heroku run lein run migrate
```

For further instructions see the [official documentation](https://devcenter.heroku.com/articles/clojure).

## Enabling nREPL

Luminus comes setup with [nREPL](https://github.com/clojure/tools.nrepl) that allows connecting to a REPL
on a the server. This functionality can useful for debugging as well as hotfixing updates in the running
application. To enable nREPL support set the `NREPL_PORT` envrionment variable to the desired port.

```
export NREPL_PORT=7000
```

To test the REPL connection simply run the following command:

```
lein repl :connect 7001
```

You can also connect your favorite IDE to a remote REPL just as you would connect to a local one.

When running on a remote server it is recommended to forward the REPL port to the local machine using SSH:

```
ssh -L 7001:localhost:7001 remotehost
```

