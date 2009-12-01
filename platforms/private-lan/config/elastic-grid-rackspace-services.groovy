import org.rioproject.config.Constants

deployment(name: 'Elastic Grid for Rackspace') {
  groups System.getProperty(Constants.GROUPS_PROPERTY_NAME, 'elastic-grid')

  service(name: 'Rackspace CloudFiles StorageEngine Engine') {
    interfaces {
      classes 'com.elasticgrid.storage.spi.StorageEngine'
      resources 'elastic-grid/rackspace-cloudfiles-storage-${pom.version}.jar'
    }
    implementation(class: 'com.elasticgrid.storage.rackspace.CloudFilesStorageEngineJSB') {
      resources 'elastic-grid/rackspace-cloudfiles-storage-${pom.version}.jar',
                'elastic-grid/cloud-files-1.3.0.jar',
                'elastic-grid/log4j-1.2.13.jar',
                'elastic-grid/elastic-grid-manager-${pom.version}.jar'
    }
    maintain 1
  }

}