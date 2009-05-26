
/**
 * Configuration for the EG Command Line Interface
 */
/*
 * This configuration is used to configure Rio tools (UI & CLI)
 */

import org.rioproject.config.Component
import com.elasticgrid.platforms.ec2.discovery.EC2LookupDiscoveryManager

@Component('net.jini.discovery.LookupDiscovery')
class ClientDiscoveryConfig {
    long multicastAnnouncementInterval=5000
}

/*
 * Configures the SharedDiscoveryManager class to create
 */
@Component('org.rioproject.resources.client.DiscoveryManagementPool')
class CLIDiscoveryManagerConfig {
    String sharedDiscoveryManager = EC2LookupDiscoveryManager.class.getName()
}
