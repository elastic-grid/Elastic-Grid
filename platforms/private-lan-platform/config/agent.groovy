/**
 * Configuration for an Agent
 */

import org.rioproject.boot.BootUtil
import org.rioproject.config.Component
import org.rioproject.config.Constants
import org.rioproject.core.ClassBundle
import org.rioproject.fdh.FaultDetectionHandlerFactory
import org.rioproject.log.LoggerConfig
import org.rioproject.log.LoggerConfig.LogHandlerConfig
import org.rioproject.resources.client.JiniClient
import net.jini.core.discovery.LookupLocator
import net.jini.export.Exporter
import net.jini.jeri.BasicILFactory
import net.jini.jeri.BasicJeriExporter
import net.jini.jeri.tcp.TcpServerEndpoint
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import com.elasticgrid.platforms.ec2.discovery.EC2LookupDiscoveryManager

/**
 * Declare Agent properties
 */
@Component ('org.rioproject.cybernode')
class AgentConfig {
  String serviceName = 'Elastic Grid Agent'
  String serviceComment = 'Elastic Grid Agent Dynamic Agent'
  String jmxName = 'com.elasticgrid.agent:type=Agent'
  String provisionRoot = '/mnt'

  String[] getInitialLookupGroups() {
    def groups = [System.getProperty(Constants.GROUPS_PROPERTY_NAME, 'elastic-grid')]
    return groups as String[]
  }

  LookupLocator[] getInitialLookupLocators() {
    String locators = System.getProperty(Constants.LOCATOR_PROPERTY_NAME)
    if (locators != null) {
      def lookupLocators = JiniClient.parseLocators(locators)
      return lookupLocators as LookupLocator[]
    } else {
      return null
    }
  }

  Boolean getProvisionEnabled() {
    return Boolean.TRUE
  }

  LoggerConfig[] getLoggerConfigs() {
    def loggers = []
    ['org.rioproject.cybernode': Level.INFO,
     'org.rioproject.config': Level.INFO,
     'net.jini.discovery.LookupDiscovery': Level.OFF].each {name, level ->
      loggers << new LoggerConfig(name, level, new LogHandlerConfig(new ConsoleHandler()))
    }
    return loggers as LoggerConfig[]
  }

  ClassBundle getFaultDetectionHandler() {
    def fdh = org.rioproject.fdh.HeartbeatFaultDetectionHandler.class.name
    def fdhConf = ['-', fdh + '.heartbeatPeriod=10000', fdh + 'heartbeatGracePeriod=10000']
    return FaultDetectionHandlerFactory.getClassBundle(fdh, fdhConf)
  }
}

/**
 * The exporter to declare as the *default* exporter for services and utilities
 */
@Component ('org.rioproject')
class ExporterConfig {
  Exporter getDefaultExporter() {
    String host = BootUtil.getHostAddressFromProperty("java.rmi.server.hostname");
    return new BasicJeriExporter(TcpServerEndpoint.getInstance(host, 0), new BasicILFactory());
  }
}

/**
 * Configure the watchDataSourceExporter
 */
@Component ('org.rioproject.watch')
class WatchConfig extends ExporterConfig {
  Exporter getWatchDataSourceExporter() {
    return getDefaultExporter()
  }
}

/**
 * Default exporter to use for the ServiceDiscoveryManager is the same as the
 * exporter in the ExporterConfig class
 */
@Component ('net.jini.lookup.ServiceDiscoveryManager')
class SDMConfig extends ExporterConfig {
  Exporter getEventListenerExporter() {
    return getDefaultExporter()
  }
}

/**
 * Test the liveness of  multicast announcements from previously discovered
 * lookup services every 5 seconds
 */
@Component ('net.jini.discovery.LookupDiscovery')
class LookupDiscoConfig {
  long getMulticastAnnouncementInterval() {
    return 5000;
  }
}

/**
 * Configures the SharedDiscoveryManager class to create
 */
@Component ('org.rioproject.resources.client.DiscoveryManagementPool')
class AgentSharedDiscoveryManagerConfig {
  String sharedDiscoveryManager = EC2LookupDiscoveryManager.class.getName()
}
