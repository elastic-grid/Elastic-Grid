/*
 * Configuration for the REST service
 */

import org.rioproject.config.Component
import org.rioproject.config.Constants

/*
 * Declare REST API properties
 */
@Component ('com.elasticgrid.rest')
class RestServiceConfig {
  String serviceName = 'Elastic Grid REST API'
  String serviceComment = 'Elastic Grid REST API'
  String jmxName = 'com.elasticgrid.rest:type=API'

  String[] getInitialLookupGroups() {
    def groups = [System.getProperty(Constants.GROUPS_PROPERTY_NAME, 'elastic-grid')]
    return groups as String[]
  }

}
