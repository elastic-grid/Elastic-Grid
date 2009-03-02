/*
 * Configuration for a Cybernode
 */
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import org.rioproject.config.Component
import org.rioproject.log.LoggerConfig
import org.rioproject.log.LoggerConfig.LogHandlerConfig
import org.rioproject.fdh.FaultDetectionHandlerFactory

import net.jini.core.discovery.LookupLocator
import net.jini.jeri.BasicILFactory
import net.jini.jeri.BasicJeriExporter
import net.jini.jeri.tcp.TcpServerEndpoint
import org.rioproject.boot.BootUtil

/*
 * Declare Cybernode properties
 */
@Component('org.rioproject.cybernode')
class EC2AgentConfig extends AgentConfig {
    LookupLocator[] getInitialLookupLocators() {
        String masterHost = 'jini://'+System.getProperty('MONITOR_HOST')
        def initialLookupLocators = [new LookupLocator(masterHost)]
        return (LookupLocator[])initialLookupLocators
    }
}
