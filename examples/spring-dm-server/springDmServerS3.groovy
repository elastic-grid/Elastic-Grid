import java.lang.management.ManagementFactory

deployment(name: 'Spring DM Server') {
  groups('rio')

  serviceExec(name: 'Spring DM Server') {
    software(name: 'Spring DM Server', version: '1.0.1', removeOnDestroy: true) {
	  //install source: 'file://${EG_HOME}/deploy/springsource-dm-server-1.0.1.RELEASE.zip',
      install source: 'http://elastic-grid-examples.s3.amazonaws.com/spring-dm/springsource-dm-server-1.0.1.RELEASE.zip',
              target: 'springsource-dm-server',
              unarchive: true
    }

    // deploy Spring Travel Sample
    //data source: 'http://elastic-grid-examples.s3.amazonaws.com/spring-dm/spring-travel-1.2.0.zip', unarchive: true,
    //     target: 'springsource-dm-server/springsource-dm-server-1.0.1.RELEASE'

    // deploy Form Tags Sample
    //data source: 'http://elastic-grid-examples.s3.amazonaws.com/spring-dm/formtags-1.4.0.zip', unarchive: true,
    //     target: 'springsource-dm-server/springsource-dm-server-1.0.1.RELEASE'

    // monitor number of threads and scale Spring dm Server instances
    sla(id: 'thread-count', low: 80, high: 200) {
      //policy type: 'scaling', max: 3
	  policy handler: 'com.elasticgrid.platforms.ec2.sla.EC2ScalingPolicyHandler', max: 2,
			 lowerDampener: 60000, upperDampener: 60000
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
