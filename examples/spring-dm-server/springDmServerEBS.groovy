import java.lang.management.ManagementFactory

deployment(name: 'Spring DM Server') {
  groups('rio')

  volumes {
    volume {
	  // how to name the volume to mount/duplicate
	  // how to do that if the first time we create a volume and now we need to mount it somewhere else or duplicate it?
	  restore 'snap-78a54011'
	  duplicate 'snap-78a54011'
	
	
	  size 10           // in GB, defaults to 1, can't be more than 1024
	  zone 'us-east-1a' // the availability zone into which the volume should be create -- will default to the one of the EC2 instance
      format 'xfs'      // which filesystem type should be used when formatting the volume -- defaults to 'xfs'
    }
  }

  serviceExec(name: 'Spring DM Server') {
    software(name: 'Spring DM Server', version: '1.0.1', removeOnDestroy: true) {
      install source: 'http://elastic-grid-examples.s3.amazonaws.com/spring-dm/springsource-dm-server-1.0.1.RELEASE.zip',
              target: 'springsource-dm-server',
              unarchive: true
    }

    // deploy Spring Travel Sample
    data source: 'http://elastic-grid-examples.s3.amazonaws.com/spring-dm/spring-travel-1.2.0.zip', unarchive: true,
         target: 'springsource-dm-server/springsource-dm-server-1.0.1.RELEASE'

    // deploy Form Tags Sample
    data source: 'http://elastic-grid-examples.s3.amazonaws.com/spring-dm/formtags-1.4.0.zip', unarchive: true,
         target: 'springsource-dm-server/springsource-dm-server-1.0.1.RELEASE'

    // monitor number of threads and scale Spring dm Server instances
    sla(id: 'thread-count', low: 80, high: 200) {
      policy type: 'scaling', max: 3
      monitor name: 'Thread Count',
              objectName: ManagementFactory.THREAD_MXBEAN_NAME,
              attribute: 'ThreadCount', period: 5000
    }

    // monitor maximum time spent on a HTTP call
    sla(id: 'http-max-time', high: 5000) {
      policy type: 'notify'
      monitor name: 'HTTP Max Time',
              objectName: 'springsource.server.catalina:type=GlobalRequestProcessor,name=http-8080',
              attribute: 'maxTime', period: 1000
    }

    execute command: 'bin/startup.sh'
    maintain 1                          // we want at least 1 instance to be running in our cluster
    maxPerMachine 1                     // we don't want to host more than 1 instance per machine
  }

}
