import org.rioproject.config.Component
import org.rioproject.config.Constants
import com.elasticgrid.cluster.spi.CloudPlatformManager
import com.elasticgrid.platforms.lan.LANCloudPlatformManagerFactory
import com.elasticgrid.platforms.ec2.EC2CloudPlatformManagerFactory

@Component ('com.elasticgrid.cluster')
class ClusterManagerConfig {
  String serviceName = 'Cluster Manager'
  String serviceComment = 'Cluster Manager'
  String jmxName = 'com.elasticgrid.cluster:type=API'

  List<CloudPlatformManager> getCloudPlatformManagers() {
    def lan = new LANCloudPlatformManagerFactory().getInstance()
    def ec2 = new EC2CloudPlatformManagerFactory().getInstance()
    return [lan, ec2] as List
  }

  String[] getInitialLookupGroups() {
    def groups = [System.getProperty(Constants.GROUPS_PROPERTY_NAME, 'elastic-grid')]
    return groups as String[]
  }

}
