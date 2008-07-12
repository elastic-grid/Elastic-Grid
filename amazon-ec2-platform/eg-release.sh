mvn -o clean install
export COPYFILE_DISABLE=true
tar --exclude .svn -cvzf target/eg-0.1.0.tar.gz eg-0.1.0
