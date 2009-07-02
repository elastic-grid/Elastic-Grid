deployment(name: 'Elastic Grid') {
  groups 'elastic-grid'

  service(name: 'Cluster Manager') {
    interfaces {
      classes 'com.elasticgrid.cluster.ClusterManager'
      resources 'elastic-grid/elastic-grid-manager-${pom.version}.jar'
    }
    implementation(class: 'com.elasticgrid.cluster.ClusterManagerJSB') {
      resources 'elastic-grid/cluster-manager-${pom.version}-impl.jar',
                'elastic-grid/amazon-ec2-provisioner-${pom.version}.jar',
                'elastic-grid/private-lan-provisioner-${pom.version}.jar'
    }
    association name: 'cloudPlatforms', property: 'cloudPlatforms',
                serviceType: 'com.elasticgrid.cluster.spi.CloudPlatformManager',
                type: 'requires', matchOnName: 'false'
    maintain 1
  }

  service(name: 'Private LAN Cloud Platform') {
    interfaces {
      classes 'com.elasticgrid.cluster.spi.CloudPlatformManager'
      resources 'elastic-grid/private-lan-cloud-platform-${pom.version}-dl.jar',
                'elastic-grid/elastic-grid-manager-${pom.version}.jar',
                'elastic-grid/elastic-grid-model-${pom.version}.jar'
    }
    implementation(class: 'com.elasticgrid.platforms.lan.LANCloudPatformManagerJSB') {
      resources 'elastic-grid/private-lan-cloud-platform-${pom.version}-impl.jar',
                'elastic-grid/private-lan-provisioner-${pom.version}.jar',
                'elastic-grid/elastic-grid-manager-${pom.version}.jar'
    }
//    sla(id: 'numberOfClusters') {
//      monitor(name: 'Number of Clusters', property: 'numberOfClusters', period: '30000')
//    }
//    sla(id: 'numberOfNodes') {
//      monitor(name: 'Number of Nodes', property: 'numberOfNodes', period: '30000')
//    }
    maintain 1
  }

  service(name: 'Amazon EC2 Cloud Platform') {
    interfaces {
      classes 'com.elasticgrid.cluster.spi.CloudPlatformManager'
      resources 'elastic-grid/amazon-ec2-cloud-platform-${pom.version}-dl.jar',
                'elastic-grid/elastic-grid-manager-${pom.version}.jar',
                'elastic-grid/elastic-grid-model-${pom.version}.jar'
    }
    implementation(class: 'com.elasticgrid.platforms.ec2.EC2CloudPatformManagerJSB') {
      resources 'elastic-grid/amazon-ec2-cloud-platform-${pom.version}-impl.jar',
                'elastic-grid/private-lan-provisioner-${pom.version}.jar',
                'elastic-grid/elastic-grid-manager-${pom.version}.jar'
    }
//    sla(id: 'numberOfClusters') {
//      monitor(name: 'Number of Clusters', property: 'numberOfClusters', period: '30000')
//    }
//    sla(id: 'numberOfNodes') {
//      monitor(name: 'Number of Nodes', property: 'numberOfNodes', period: '30000')
//    }
    maintain 1
  }

  service(name: 'Storage Manager') {
    interfaces {
      classes 'com.elasticgrid.storage.StorageManager'
      resources 'elastic-grid/elastic-grid-manager-${pom.version}.jar'
    }
    implementation(class: 'com.elasticgrid.storage.StorageManagerJSB') {
      resources 'elastic-grid/elastic-grid-storage-${pom.version}-impl.jar'
    }
    association name: 'storageEngines', property: 'storageEngines',
                serviceType: 'com.elasticgrid.storage.spi.StorageEngine',
                type: 'requires', matchOnName: 'false'
    maintain 1
  }

  service(name: 'Amazon S3 Storage Engine') {
    interfaces {
      classes 'com.elasticgrid.storage.spi.StorageEngine'
      resources 'elastic-grid/elastic-grid-manager-${pom.version}.jar'
    }
    implementation(class: 'com.elasticgrid.storage.amazon.s3.S3StorageEngineJSB') {
      resources 'elastic-grid/amazon-s3-storage-${pom.version}.jar',
                'elastic-grid/elastic-grid-manager-${pom.version}.jar'
    }
    maintain 1
  }

  service(name: 'Rackspace CloudFiles Storage Engine') {
    interfaces {
      classes 'com.elasticgrid.storage.spi.StorageEngine'
      resources 'elastic-grid/elastic-grid-manager-${pom.version}.jar'
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