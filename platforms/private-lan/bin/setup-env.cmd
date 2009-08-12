if [ -f /tmp/user-data ] ; then
    source /tmp/user-data
fi
if [ -f /tmp/public-hostname ] ; then
    export NET_ADDR=`cat /tmp/public-hostname`
fi

export EG_OPTS="-Dorg.rioproject.groups=$CLUSTER_NAME -Dcom.sun.management.jmxremote"
