
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

@Component('net.jini.discovery.LookupDiscovery')
class ClientDiscoveryConfig {
    long multicastAnnouncementInterval=5000
}

/**
 * Configures groups used by CLI.
 */
@Component('org.rioproject.tools.cli')
class CLIConfig {
    String[] groups = [System.getProperty(Constants.GROUPS_PROPERTY_NAME)];
}

/*
 * Configures the SharedDiscoveryManager class to create
 */
@Component('org.rioproject.resources.client.DiscoveryManagementPool')
class CLIDiscoveryManagerConfig {
    String getSharedDiscoveryManager() {
        String manager
        def groups = System.getProperty(Constants.GROUPS_PROPERTY_NAME)
        if(!groups)
            manager = SharedDiscoveryManager.class.getName()
        else
            manager = EC2LookupDiscoveryManager.class.getName()
        return manager
    }
}
