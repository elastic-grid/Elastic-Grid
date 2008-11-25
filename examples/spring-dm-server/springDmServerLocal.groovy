deployment(name: 'Spring dm Server') {
  groups('rio')

  serviceExec(name: 'Spring dm Server') {
    software(name: 'Spring dm Server', version: '1.0.0', removeOnDestroy: true) {
      install source: 'file:///Users/jeje/Documents/Development/eg/deploy/springsource-dm-server-1.0.0.RELEASE.zip',
              target: '${RIO_HOME}/system/external',
              unarchive: true
      postInstall(removeOnCompletion: false) {
        execute command: '/bin/chmod +x ${RIO_HOME}/system/external/springsource-dm-server-1.0.0.RELEASE/bin/*.sh',
                nohup: false
      }
    }
    execute command: '${RIO_HOME}/system/external/springsource-dm-server-1.0.0.RELEASE/bin/startup.sh'
    maintain 1
  }

}