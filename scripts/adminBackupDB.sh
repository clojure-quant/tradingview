export LC_ALL="en_US.UTF-8"
bdir=/var/backups/mongobackups/seasonality_`date +"%y-%m-%d"`
btar=/var/backups/mongobackups/`date +"%y-%m-%d"`backup_seasonality.tar
echo "Backing up to directory $bdir"
echo "Tar backup is $btar"

sudo mongodump --db seasonality --out $bdir
tar cvf $btar $bdir

echo "directory /var/backups/mongobackups is:"
ls /var/backups/mongobackups/

echo "uploading via rclone to google drive.."
rclone copy $btar flogoogle:rclone/seasonality

# echo "removing backup files older than one week"
# ./adminBackupRemoveOld.sh
