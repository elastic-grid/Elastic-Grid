import org.rioproject.config.Component
import org.rioproject.config.Constants
import com.elasticgrid.cluster.spi.CloudPlatformManager
import com.elasticgrid.platforms.lan.LANCloudPlatformManagerFactory
import com.elasticgrid.platforms.ec2.EC2CloudPlatformManagerFactory
import com.elasticgrid.platforms.lan.LANCloudPlatformManagerFactory

@Component ('com.elasticgrid.cluster')
class ClusterManagerConfig {
  String serviceName = 'Cluster Manager'
  String serviceComment = 'Cluster Manager'
  String jmxName = 'com.elasticgrid.cluster:type=API'

  String[] getInitialLookupGroups() {
    def groups = [System.getProperty(Constants.GROUPS_PROPERTY_NAME, 'elastic-grid')]
    return groups as String[]
  }

}
