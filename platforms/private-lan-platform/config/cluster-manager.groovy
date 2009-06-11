import java.util.logging.Level

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
//      serviceLevelAgreements {
//          systemRequirements ref: 'Elastic Grid Platform'
//          sla(id: 'Queue VideoConversion', low: 2, high: 5) {
//              policy type: 'scaling', class: 'com.elasticgrid.amazon.ec2.sla.EC2ScalingPolicyHandler',
//                     max: 3, lowerDampener: 3600, upperDampener: 3600
//          }
//      }
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
    maintain 1
  }

}