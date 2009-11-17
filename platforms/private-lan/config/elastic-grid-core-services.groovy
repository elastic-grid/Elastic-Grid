deployment(name: 'Elastic Grid') {
  groups '${org.rioproject.groups}'

  service(name: 'Cluster Manager') {
    interfaces {
      classes 'com.elasticgrid.cluster.ClusterManager'
      resources 'elastic-grid/elastic-grid-manager-${pom.version}.jar'
    }
    implementation(class: 'com.elasticgrid.cluster.ClusterManagerJSB') {
      resources 'elastic-grid/elastic-grid-cluster-${pom.version}.jar',
                'elastic-grid/amazon-ec2-provisioner-${pom.version}.jar',
                'elastic-grid/private-lan-provisioner-${pom.version}.jar'
    }
    association name: 'cloudPlatforms', property: 'cloudPlatforms',
                serviceType: 'com.elasticgrid.cluster.spi.CloudPlatformManager',
                type: 'requires', matchOnName: false
    maintain 1
  }

  service(name: 'Storage Manager') {
    interfaces {
      classes 'com.elasticgrid.storage.StorageManager'
      resources 'elastic-grid/elastic-grid-manager-${pom.version}.jar'
    }
    implementation(class: 'com.elasticgrid.storage.StorageManagerJSB') {
      resources 'elastic-grid/elastic-grid-storage-${pom.version}.jar'
    }
    association name: 'storageEngines', property: 'storageEngines',
                serviceType: 'com.elasticgrid.storage.spi.StorageEngine',
                type: 'requires', matchOnName: false
    maintain 1
  }

}