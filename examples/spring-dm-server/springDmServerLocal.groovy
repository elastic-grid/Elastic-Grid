deployment(name: 'Spring DM') {
  groups('rio')

  serviceExec(name: 'DM Server') {
    software(name: 'Spring DM', version: '1.0.0', removeOnDestroy: true) {
      install source: 'file://${EG_HOME}/deploy/springsource-dm-server-1.0.0.RELEASE.zip',
              target: 'springsource-dm-server',
              unarchive: true
    }
    execute command: 'bin/startup.sh'
    maintain 1
  }

}