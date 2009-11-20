/*
 * Configuration for the REST service
 */

import org.rioproject.config.Component
import org.rioproject.config.Constants
import java.util.logging.Level
import org.rioproject.log.LoggerConfig
import org.rioproject.log.LoggerConfig.LogHandlerConfig
import java.util.logging.ConsoleHandler

/*
 * Declare REST API properties
 */
@Component ('com.elasticgrid.rest')
class RestServiceConfig {
  String serviceName = 'Elastic Grid REST API'
  String serviceComment = 'Elastic Grid REST API'
  String jmxName = 'com.elasticgrid.api:type=REST'

  String[] getInitialLookupGroups() {
    def groups = [System.getProperty(Constants.GROUPS_PROPERTY_NAME, 'elastic-grid')]
    return groups as String[]
  }

  LoggerConfig[] getLoggerConfigs() {
    def loggers = []
    [
            'org.reslet': Level.FINEST,
            'com.noelios': Level.FINEST,
    ].each {name, level ->
      loggers << new LoggerConfig(name, level, new LogHandlerConfig(new ConsoleHandler()))
    }
    return loggers as LoggerConfig[]
  }

}
