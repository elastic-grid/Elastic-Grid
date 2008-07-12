groupadd mysql
useradd -g mysql mysql
chown -R mysql .
chgrp -R mysql .
scripts/mysql_install_db --user=mysql
chown -R root .
chown -R mysql data
#bin/mysqld_safe --user=mysql &
bin/mysqld --basedir=. --datadir=data --user=mysql --pid-file=mysql.pid --skip-external-locking