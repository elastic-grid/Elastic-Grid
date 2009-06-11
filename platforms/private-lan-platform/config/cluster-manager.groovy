deployment(name: 'Elastic Grid') {
  groups 'elastic-grid'

  service(name: 'Cluster Manager') {
    interfaces {
      classes 'com.elasticgrid.cluster.ClusterManager'
      resources 'elastic-grid/elastic-grid-manager-${pom.version}.jar'
    }
    implementation(class: 'com.elasticgrid.cluster.ClusterManagerJSB') {
      resources 'elastic-grid/cluster-manager-jsb-${pom.version}-impl.jar',
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
    sla(id: 'numberOfClusters') {
      monitor(name: 'Number of Clusters', property: 'numberOfClusters', period: '30000')
    }
    sla(id: 'numberOfNodes') {
      monitor(name: 'Number of Nodes', property: 'numberOfNodes', period: '30000')
    }
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
    sla(id: 'numberOfClusters') {
      monitor(name: 'Number of Clusters', property: 'numberOfClusters', period: '30000')
    }
    sla(id: 'numberOfNodes') {
      monitor(name: 'Number of Nodes', property: 'numberOfNodes', period: '30000')
    }
    maintain 1
  }

}