#/bin/bash

# Remove backup older than 7 days.

find /var/backups/mongobackups/ -type f -mtime +7 -name '*.tar' -execdir rm -- '{}' \;

# Explanation:
# find: the unix command for finding files/directories/links and etc.
# /path/to/: the directory to start your search in.
# -type f: only find files.
# -name '*.gz': list files that ends with .gz.
# -mtime +7: only consider the ones with modification time older than 7 days.
# -execdir ... \;: for each such result found, do the following command in ....
# rm -- '{}': remove the file; the {} part is where the find result gets substituted 
# into from the previous part. -- means end of command parameters 
# avoid prompting error for those files starting with hyphen.

cd /var/backups/mongobackups/
#find /var/backups/mongobackups/ -type d -mtime +7 -name 'cititrip_*' -execdir rm  -- ' {}' \;
find /var/backups/mongobackups/cititrip_* -mtime +7 -exec rm -rf {} \;
