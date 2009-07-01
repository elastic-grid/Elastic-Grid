/**
 * Configuration for the EG Command Line Interface
 */
/*
 * This configuration is used to configure Rio tools (UI & CLI)
 */

import org.rioproject.config.Component
import com.elasticgrid.platforms.ec2.discovery.EC2LookupDiscoveryManager
import org.rioproject.resources.client.DiscoveryManagementPool.SharedDiscoveryManager
import org.rioproject.config.Constants
import net.jini.discovery.DiscoveryGroupManagement
import com.elasticgrid.storage.StorageManager
import com.elasticgrid.storage.amazon.s3.S3StorageEngine
import com.elasticgrid.utils.amazon.AWSUtils
import com.elasticgrid.config.EC2Configuration
import com.elasticgrid.storage.amazon.s3.S3StorageEngine

/**
 * Configures groups used by CLI.
 */
@Component ('org.rioproject.tools.cli')
class CLIConfig {
  StorageManager getStorageManager() {
    Properties awsConfig = AWSUtils.loadEC2Configuration()
    String awsAccessID = awsConfig.getProperty(EC2Configuration.AWS_ACCESS_ID)
    String awsSecretKey = awsConfig.getProperty(EC2Configuration.AWS_SECRET_KEY)
    if (awsAccessID == null) {
      throw new IllegalArgumentException("Could not find AWS Access ID")
    }
    if (awsSecretKey == null) {
      throw new IllegalArgumentException("Could not find AWS Secret Key")
    }
    StorageManager storage = new S3StorageEngine(awsAccessID, awsSecretKey)
    return storage
  }

  String[] getGroups() {
    def groups
    if (System.getProperty(Constants.GROUPS_PROPERTY_NAME) == null)
      groups = DiscoveryGroupManagement.ALL_GROUPS
    else
      groups = [System.getProperty(Constants.GROUPS_PROPERTY_NAME)]
    return groups as String[]
  }
}

/*
 * Configures the SharedDiscoveryManager class to create
 */
@Component ('org.rioproject.resources.client.DiscoveryManagementPool')
class CLIDiscoveryManagerConfig {
  String getSharedDiscoveryManager() {
    String manager
    def groups = System.getProperty(Constants.GROUPS_PROPERTY_NAME)
    if (!groups)
      manager = SharedDiscoveryManager.class.getName()
    else
      manager = EC2LookupDiscoveryManager.class.getName()
    return manager
  }
}

@Component ('net.jini.discovery.LookupDiscovery')
class ClientDiscoveryConfig {
  long multicastAnnouncementInterval = 5000
}
