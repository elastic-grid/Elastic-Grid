/**
 * Configuration for a Provision Monitor
 */

import com.elasticgrid.utils.amazon.AWSUtils
import com.elasticgrid.platforms.ec2.discovery.EC2LookupDiscoveryManager
import org.rioproject.core.ClassBundle
import org.rioproject.config.Component
import org.rioproject.config.Constants
import org.rioproject.fdh.FaultDetectionHandlerFactory
import org.rioproject.monitor.DeployHandler
import org.rioproject.monitor.FileSystemOARDeployHandler
import org.rioproject.monitor.ServiceResourceSelector
import org.rioproject.monitor.LeastActiveSelector
import org.rioproject.resources.client.JiniClient
import net.jini.core.discovery.LookupLocator
import net.jini.export.Exporter
import net.jini.jrmp.JrmpExporter
import java.io.File
import org.rioproject.log.LoggerConfig
import org.rioproject.log.LoggerConfig.LogHandlerConfig
import java.util.logging.ConsoleHandler
import java.util.logging.Level

/**
 * Declare Provision Monitor properties
 */
@Component ('org.rioproject.monitor')
class MonitorConfig {
  String serviceName = 'Elastic Grid Monitor'
  String serviceComment = 'Elastic Grid Dynamic Provisioning Agent'
  String jmxName = 'com.elasticgrid.monitor:type=Monitor'
  long deployMonitorPeriod = 30000

  String[] getInitialOpStrings() {
    def opstrings = []
    File egLibsDirectory = new File(System.getProperty('EG_HOME') + File.separatorChar + 'lib' + File.separatorChar + 'elastic-grid');
    def egConfigDirectory = System.getProperty('EG_HOME') + '/config'
    opstrings << egConfigDirectory + File.separatorChar + 'elastic-grid-core-services.groovy'
    egLibsDirectory.eachFileMatch(~".*platform.*.jar") {File f ->
      if (!f.name.endsWith("-dl.jar")) {
        if (f.name.startsWith('private-lan-cloud-platform'))
          opstrings << egConfigDirectory + File.separatorChar + 'elastic-grid-private-lan-services.groovy'
        if (f.name.startsWith('amazon-ec2-cloud-platform'))
          opstrings << egConfigDirectory + File.separatorChar + 'elastic-grid-amazon-services.groovy'
        else if (f.name.startsWith('rackspace-cloud-platform'))
          opstrings << egConfigDirectory + File.separatorChar + 'elastic-grid-rackspace-services.groovy'
      }
    }
    return opstrings as String[]
  }

  String[] getInitialLookupGroups() {
    if (System.getProperty(Constants.GROUPS_PROPERTY_NAME) == null)
      System.setProperty(Constants.GROUPS_PROPERTY_NAME, 'elastic-grid')
    def groups = [System.getProperty(Constants.GROUPS_PROPERTY_NAME)]
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

  ServiceResourceSelector getServiceResourceSelector() {
    return new LeastActiveSelector()
  }

  /**
   * Use a JrmpExporter for the OpStringManager.
   */
  Exporter getOpStringManagerExporter() {
    return new JrmpExporter()
  }

  LoggerConfig[] getLoggerConfigs() {
    def loggers = []
    [
            'org.rioproject.monitor': Level.INFO,
            'net.jini.lookup.JoinManager': Level.OFF,
            'org.apache.commons.httpclient': Level.WARNING,
    ].each {name, level ->
      loggers << new LoggerConfig(name, level, new LogHandlerConfig(new ConsoleHandler()))
    }
    return loggers as LoggerConfig[]
  }

  ClassBundle getFaultDetectionHandler() {
    def fdh = org.rioproject.fdh.HeartbeatFaultDetectionHandler.class.name
    def fdhConf = ['-', fdh + '.heartbeatPeriod=10000', fdh + 'heartbeatGracePeriod=10000']
    return FaultDetectionHandlerFactory.getClassBundle(fdh, fdhConf)
  }

  /* Configure DeployHandlers for the Monitor to use */

  DeployHandler[] getDeployHandlers() {
    def deployDir = System.getProperty('RIO_HOME') + '/deploy'
    return [
            new FileSystemOARDeployHandler(new File(deployDir)),
            new com.elasticgrid.monitor.S3OARDeployHandler(AWSUtils.getDropBucket(), new File(deployDir))
    ] as DeployHandler[]
  }

}

/**
 * Configures the SharedDiscoveryManager class to create
 */
@Component ('org.rioproject.resources.client.DiscoveryManagementPool')
class MonitorSharedDiscoveryManagerConfig {
  String sharedDiscoveryManager = EC2LookupDiscoveryManager.class.getName()
}