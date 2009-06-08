import org.rioproject.config.Component
import org.rioproject.config.Constants

@Component ('com.elasticgrid.platforms.ec2')
class ClusterManagerConfig {
  String serviceName = 'Amazon EC2 Cloud Platform'
  String serviceComment = 'Amazon EC2 Cloud Platform'
  String jmxName = 'com.elasticgrid.platforms:type=EC2'

  String[] getInitialLookupGroups() {
    def groups = [System.getProperty(Constants.GROUPS_PROPERTY_NAME, 'elastic-grid')]
    return groups as String[]
  }

}
