## Deployment to heroku

So you've gotten your app running live in heroku, but only have an in memory db setup and want some persistance.

How on earth do you get it working?

Keeping in mind this is still for a smaller app, perhaps running on a single dyno, where you're not expecting much in the way of load or you're still testing out the idea. If you want a larger instance setup or are looking for a more production grade solution, looking at setting it up on [aws](https://docs.datomic.com/on-prem/index.html), [datomic cloud](https://docs.datomic.com/cloud/index.html) or through [ions](https://docs.datomic.com/cloud/ions/ions.html) in the way to go.

So the best way I've found to do it is to pull the db into the app and run it on a single transactor.

It is possible to run a datomic transactor in a heroku 2X standard instance alongside an app and if you are very comfortable in this ecosystem and want to try things out before going any further then welcome.


Also a quick warning before proceeding, this is a bit of a hack, it works but is explicity for smaller instances. The datomic transactor isn't secured by default and is expected to run in a private networked environment, so the expectation is that your app is acting as a gateway to allowing access the datomic db. So access to the database is only as secure as your app. Please don't run this as a production system.

With that said, let's start with creating your heroku stack.

```
heroku create
```

Now as we're wanting to get this persisting data let's connect up a postgres db.

```
heroku addons:create heroku-postgresql:hobby-dev
```

You'll also need to get a [datomic pro starter license](https://my.datomic.com/account), so register for one.

You'll also need to note down your:
1) datomic license user, the email you registered with. eg: `me@example.com`
2) datomic license password, which should be a uuid. eg: `00000000-0000-0000-0000-000000000000`
3) latest datomic version, eg: `0.9.5697`
4) datomic transactor key, this will have been emailed to you.
5) You'll need to decide a database name for your db. eg: `guestbook_datomic_dev`

Now let's add in the nessessary config vars:

```
heroku config:set DATOMIC_LICENSE_USER=me@example.com DATOMIC_LICENSE_PASSWORD=00000000-0000-0000-0000-000000000000 DATOMIC_VERSION=0.9.5697 DATOMIC_TRANSACTOR_KEY=<license key> DATOMICDB_NAME=guestbook_datomic_dev DATOMIC_JAVA_XMX=1g JAVA_OPTS=-XX:+UseCompressedOops
```

Next let's add the [heroku buildpack datomic](https://github.com/opengrail/heroku-buildpack-datomic) to your heroku stack.

```
heroku buildpacks:add https://github.com/opengrail/heroku-buildpack-datomic
```

Now for this buildpack to work we need the line below in our `Procfile`:

```
web: /app/scripts/start-datomic.sh
```

However first we'll want to customise it, we can do this by creating a series of folders at the top level of our app as follows:

```
mkdir -p vendor/datomic/
touch vendor/datomic/start-datomic.sh
```

Now add the contents below to the file you just created:

```
#!/usr/bin/env bash

if [ -z "${SCRIPTS_HOME}" ]
then
    SCRIPTS_HOME=/app/scripts
fi

if [ -z "${DATOMIC_JAVA_XMX}" ]
then
    DATOMIC_JAVA_XMX=2g
fi
echo "Java max heap size set to '${DATOMIC_JAVA_XMX}'"

if [ -z "${DATOMIC_JAVA_XMS}" ]
then
    DATOMIC_JAVA_XMS=256m
fi
echo "Java min heap size set to '${DATOMIC_JAVA_XMS}'"

STORAGE_TYPE=${DATOMIC_STORAGE_TYPE:-"HEROKU_POSTGRES"}

case ${STORAGE_TYPE} in

    DYNAMODB)
        # TODO run a check against DynamoDB
        echo "Skipping check for proper dynamodb setup (not implemented)"
        ;;

    HEROKU_POSTGRES|POSTGRES)
        echo "Establishing whether Postgres is properly setup..."
        ${SCRIPTS_HOME}/datomic-postgres-setup-checker.sh
        if [ $? -ne 0 ]
        then
            echo "Failed to establish whether Postgres is properly setup - aborting dyno"
            exit 1
        fi
        ;;

    *)  echo "Unsupported storage type '${STORAGE_TYPE}'" && return 1
        ;;
esac

PROPERTIES=${SCRIPTS_HOME}/transactor.properties

DYNO_PROPERTIES=${PROPERTIES}.heroku

# Discover the IP that this dyno exposes in the Space

DYNO_IP=$(ip -4 -o addr show dev eth0 | awk '{print $4}' | cut -d/ -f1)

DB_URL=$(cat ${PROPERTIES} | grep sql-url= | sed "s/^sql-url=//")
DB_USER=$(cat ${PROPERTIES} | grep sql-user= | sed "s/^sql-user=//")
DB_PASS=$(cat ${PROPERTIES} | grep sql-password= | sed "s/^sql-password=//")

sed "/^host=localhost/a alt-host=${DYNO_IP}" ${PROPERTIES} > ${DYNO_PROPERTIES}

unset JAVA_OPTS

# Ensure Datomic does not log passwords
# unset or 0 for false, 1 for true
if (( PRINT_CONNECTION ))
then
    PRINT_CONNECTION=true
else
    PRINT_CONNECTION=false
fi

## transactor -Ddatomic.printConnectionInfo=${PRINT_CONNECTION} -Xmx${DATOMIC_JAVA_XMX} -Xms${DATOMIC_JAVA_XMS} ${DYNO_PROPERTIES}

echo "Datomic Transactor Starting"
DATOMIC_URL="datomic:sql://$DATOMICDB_NAME?$DB_URL?user=$DB_USER&password=$DB_PASS&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
if (( PRINT_CONNECTION ))
then
   echo "DISPLAYING DATOMIC_URL:"
   echo "${DATOMIC_URL}"
else
   echo "HIDING DATOMIC_URL:"
fi
transactor -Ddatomic.printConnectionInfo=${PRINT_CONNECTION} -Xmx${DATOMIC_JAVA_XMX} -Xms${DATOMIC_JAVA_XMS} ${DYNO_PROPERTIES} &

```


Now luminus will kindly generate a `Procfile` for your app, such as:

```
web: java -cp target/uberjar/guestbook-datomic.jar clojure.main -m guestbook-datomic.core
```

Now these are just startup commands, so we can join them to produce:

```
web: . /app/vendor/datomic/start-datomic.sh && sleep 15 && echo "STARTING APP" && web: java -cp target/uberjar/guestbook-datomic.jar clojure.main -m guestbook-datomic.core
```

Note the dot at the beginning of the line, I'm sourcing the start-datomic.sh that we created as I want to retain the environmental variables, such as the DATOMIC_URL.

The 15 second sleep delay is to allow the datomic instance to boot, as I notice luminus expects the database to be live, there are issues if it can't find the database when it starts booting up.

Now we should be using `DATOMIC_URL` to connect to datomic, so make sure that's the url you're using in your app.

Now I'm going to assume that you'll commit all this together with a nice git message and push this to heroku.

```
git add vendor/datomic/start-datomic.sh
git commit
git push heroku master
```

And there we go! 🎉
