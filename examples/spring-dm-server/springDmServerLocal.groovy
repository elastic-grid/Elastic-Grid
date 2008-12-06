import java.lang.management.ManagementFactory

deployment(name: 'Spring DM') {
  groups('rio')

  serviceExec(name: 'DM Server') {
    software(name: 'Spring DM', version: '1.0.1', removeOnDestroy: true) {
      install source: 'file://${RIO_HOME}/deploy/springsource-dm-server-1.0.1.RELEASE.zip',
              target: 'springsource-dm-server',
              unarchive: true
    }

    // deploy Spring Travel Sample
//    data source: 'file://${EG_HOME}/deploy/spring-travel-1.2.0.zip',
//         target: 'springsource-dm-server/springsource-dm-server-1.0.1.RELEASE'

//    sla(id: 'thread-count', high: 1000) {
//      policy type: 'notify'
//      monitor name: 'Thread Count',
//              objectName: ManagementFactory.THREAD_MXBEAN_NAME,
//              attribute: 'ThreadCount', period: 5000
//    }

    sla(id: 'http-max-time', high: 1000) {
      policy type: 'notify'
      monitor name: 'HTTP Max Tome',
              objectName: 'springsource.server.catalina:type=GlobalRequestProcessor,name=http-8080',
              attribute: 'maxTime', period: 1000
    }

    execute command: 'bin/startup.sh'
    maintain 1                          // we want at least 1 instance to be running in our cluster
    maxPerMachine 1                     // we don't want to host more than 1 instance per machine
  }

}