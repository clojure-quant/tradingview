# run this as root user (db dumping requires this??)

# parameter
d=20-04-16
dbname=seasonality
tarname=${d}backup_seasonality.tar

# download the backup
echo "Downloading Database via rclone: " ${tarname}
rclone copy flogoogle:rclone/${dbname}/${tarname} /var/backups/mongobackups/

# uncompress (and strip 3 levels of the directory hierarchy)
echo "Uncompressing"
tar -C /var/backups/mongobackups -xvf /var/backups/mongobackups/${tarname} --strip=3

# drop old database, as the restore will only add new entries
echo "dropping old database"
mongo ${dbname} --eval "db.dropDatabase();"

# restore database
echo "restoring new database"
mongorestore --db ${dbname} /var/backups/mongobackups/${dbname}_${d}/${dbname}

echo "done"


