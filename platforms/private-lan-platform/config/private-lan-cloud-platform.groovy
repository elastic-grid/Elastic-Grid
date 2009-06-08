import org.rioproject.config.Component
import org.rioproject.config.Constants

@Component ('com.elasticgrid.platforms.lan')
class ClusterManagerConfig {
  String serviceName = 'Private LAN Cloud Platform'
  String serviceComment = 'Private LAN Cloud Platform'
  String jmxName = 'com.elasticgrid.platforms:type=LAN'

  String[] getInitialLookupGroups() {
    def groups = [System.getProperty(Constants.GROUPS_PROPERTY_NAME, 'elastic-grid')]
    return groups as String[]
  }

}
