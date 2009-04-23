/*
 * Configuration for the REST service
 */
import org.rioproject.config.Component
import org.rioproject.core.ClassBundle
import org.rioproject.log.LoggerConfig
import org.rioproject.log.LoggerConfig.LogHandlerConfig
import org.rioproject.fdh.FaultDetectionHandlerFactory
import java.util.logging.ConsoleHandler
import java.util.logging.Level

/*
 * Declare REST API properties
 */
@Component('com.elasticgrid.rest')
class RestServiceConfig {
    String serviceName = 'Elastic Grid REST API'
    String serviceComment = 'Elastic Grid REST API'
    String jmxName = 'com.elasticgrid.rest:type=API'

    String[] getInitialLookupGroups() {
        def groups = ['elastic-grid']
        return groups as String[]
    }

    LoggerConfig[] getLoggerConfigs() {
        def loggers = []
        ['com.elasticgrid' : Level.FINE,
         'org.springframework' : Level.SEVERE,
         'com.noelios.restlet' : Level.SEVERE].each { name, level ->
            loggers.add(new LoggerConfig(name,
                                         level,
                                         new LogHandlerConfig(new ConsoleHandler())))
        }
        return loggers as LoggerConfig[]
    }

}
