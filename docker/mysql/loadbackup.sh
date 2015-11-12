# This script is meant to be run after pull.sh and run.sh were run.
# The play container does not need to be linked or running for this to work.
# Running this while the play server is active is safe, but your admin login will invalidate.
# Also note you will lose all local db changes, its a hard reset to the backup file.
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
schedulerpath="$DIR"/../../
echo "schedulerpath: $schedulerpath"
echo ""
cat "$schedulerpath"play/conf/evolutions/default/1.sql | sed '/!Downs/q' > evolution.sql
docker exec -i mysql bash -c 'cat > /tmp/evolution.sql' < evolution.sql
echo "copied evolution.sql over to the mysql docker at /tmp/evolution.sql"
echo ""
rm evolution.sql
unset -v latest
for file in "$schedulerpath"backup/*; do
  [[ $file -nt $latest ]] && latest=$file
done
echo "selected $latest as newest backup file"
echo ""
docker exec -i mysql bash -c 'cat > /tmp/content.sql' < "$latest"
echo "copied "$latest" over to the mysql docker at /tmp/content.sql"
echo ""
echo "now deleting the previous infoscreen db, press a button to continue past the password warning"
docker exec -i mysql bash -c 'mysql -u infoscreen -pphael4hievah7hiech0E -e "drop database infoscreen"'
echo "now creating a fresh infoscreen db (good thing the permissions stay), press a button to continue past the password warning"
docker exec -i mysql bash -c 'mysql -u infoscreen -pphael4hievah7hiech0E -e "create database infoscreen"'
echo "Now attempting to run evolution inside the containers mysql, press a button to continue past the password warning"
docker exec -i mysql bash -c 'mysql -u infoscreen -pphael4hievah7hiech0E infoscreen < /tmp/evolution.sql'
echo "Now attempting to restore the latest backup file to the db, press a button to continue past the password warning"
docker exec -i mysql bash -c 'mysql -u infoscreen -pphael4hievah7hiech0E infoscreen < /tmp/content.sql'
echo ""
echo "If there were errors, you can enter the container bash manually and execute these commands by using:"
echo "docker exec -ti mysql bash"
echo "mysql -u infoscreen -pphael4hievah7hiech0E infoscreen < /tmp/evolution.sql"
echo "mysql -u infoscreen -pphael4hievah7hiech0E infoscreen < /tmp/content.sql"