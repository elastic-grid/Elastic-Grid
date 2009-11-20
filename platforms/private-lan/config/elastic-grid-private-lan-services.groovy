deployment(name: 'Elastic Grid for Private LAN') {
  groups '${org.rioproject.groups}'

  service(name: 'Private LAN Cloud Platform') {
    interfaces {
      classes 'com.elasticgrid.cluster.spi.CloudPlatformManager'
      resources 'elastic-grid/private-lan-cloud-platform-${pom.version}-dl.jar',
                'elastic-grid/elastic-grid-manager-${pom.version}.jar',
                'elastic-grid/elastic-grid-model-${pom.version}.jar'
    }
    implementation(class: 'com.elasticgrid.platforms.lan.LANCloudPatformManagerJSB') {
      resources 'elastic-grid/private-lan-cloud-platform-${pom.version}.jar',
                'elastic-grid/private-lan-provisioner-${pom.version}.jar',
                'elastic-grid/elastic-grid-manager-${pom.version}.jar',
                'elastic-grid/elastic-grid-model-${pom.version}.jar'
    }
//    sla(id: 'numberOfClusters') {
//      monitor(name: 'Number of Clusters', property: 'numberOfClusters', period: '30000')
//    }
//    sla(id: 'numberOfNodes') {
//      monitor(name: 'Number of Nodes', property: 'numberOfNodes', period: '30000')
//    }
    maintain 1
  }

  service(name: 'Amazon S3 Storage Engine') {
    interfaces {
      classes 'com.elasticgrid.storage.spi.StorageEngine'
      resources 'elastic-grid/amazon-s3-storage-${pom.version}.jar'
    }
    implementation(class: 'com.elasticgrid.storage.amazon.s3.S3StorageEngineJSB') {
      resources 'elastic-grid/amazon-s3-storage-${pom.version}.jar',
                'elastic-grid/elastic-grid-manager-${pom.version}.jar'
    }
    maintain 1
  }
}