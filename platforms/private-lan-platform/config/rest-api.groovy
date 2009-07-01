/*
 * Configuration for the REST service
 */

import org.rioproject.config.Component
import org.rioproject.config.Constants
import com.elasticgrid.storage.StorageManager
import com.elasticgrid.storage.amazon.s3.S3StorageEngine
import com.elasticgrid.utils.amazon.AWSUtils

/*
 * Declare REST API properties
 */
@Component ('com.elasticgrid.rest')
class RestServiceConfig {
  String serviceName = 'Elastic Grid REST API'
  String serviceComment = 'Elastic Grid REST API'
  String jmxName = 'com.elasticgrid.rest:type=API'

  StorageManager storageManager = new S3StorageEngine(AWSUtils.accessID, AWSUtils.secretKey)
//  StorageManager storageManager = new CloudFilesStorageManager(RackspaceUtils.username, RackspaceUtils.apiKey)

  String[] getInitialLookupGroups() {
    def groups = [System.getProperty(Constants.GROUPS_PROPERTY_NAME, 'elastic-grid')]
    return groups as String[]
  }

}
