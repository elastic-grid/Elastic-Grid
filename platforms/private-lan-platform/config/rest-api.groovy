/*
 * Configuration for the REST service
 */
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import org.rioproject.config.Component
import org.rioproject.core.ClassBundle
import org.rioproject.log.LoggerConfig
import org.rioproject.log.LoggerConfig.LogHandlerConfig
import org.rioproject.fdh.FaultDetectionHandlerFactory

/*
 * Declare REST API properties
 */
@Component('com.elasticgrid.rest')
class RestServiceConfig {
    String serviceName = 'Elastic Grid REST API'
    String serviceComment = 'Elastic Grid REST API'
    String jmxName = 'com.elasticgrid.rest:type=API'

    String getInitialLookupGroups() {
        def groups = ['rio']
        return (String[])groups
    }

    LoggerConfig[] getLoggerConfigs() {
        def loggers = []
        ['com.elasticgrid' : Level.FINE,
         'org.springframework' : Level.FINEST,
         'com.noelios.restlet' : Level.FINEST].each { name, level ->
            loggers.add(new LoggerConfig(name,
                                         level,
                                         new LogHandlerConfig(new ConsoleHandler())))
        }
        return (LoggerConfig[])loggers
    }

    ClassBundle getFaultDetectionHandler() {
        def fdh = org.rioproject.fdh.HeartbeatFaultDetectionHandler.class.name
        def fdhConf = ['-', fdh+'.heartbeatPeriod=10000', fdh+'heartbeatGracePeriod=10000']
        return FaultDetectionHandlerFactory.getClassBundle(fdh, fdhConf)
    } 
}
