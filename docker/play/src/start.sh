#!/bin/bash

#STARTING CRONTAB
#cron -f &

#STARTING PLAY IN PRODUCTION
locationOfScript=$(pwd)
/var/play/infoscreen-server/target/universal/stage/bin/infoscreen -Dconfig.file=/var/play/infoscreen-server/conf/application-prod.conf -Duser.timezone="UTC" -DapplyEvolutions.default=true -DapplyDownEvolutions.default=true
