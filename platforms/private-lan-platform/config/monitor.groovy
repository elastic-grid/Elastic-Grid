/*
 * Configuration for a Provision Monitor
 */
import com.elasticgrid.utils.amazon.AWSUtils
import com.elasticgrid.platforms.ec2.monitor.S3OARDeployHandler
import org.rioproject.core.ClassBundle
import org.rioproject.config.Component
import org.rioproject.config.Constants
import org.rioproject.log.LoggerConfig
import org.rioproject.log.LoggerConfig.LogHandlerConfig
import org.rioproject.fdh.FaultDetectionHandlerFactory
import org.rioproject.monitor.DeployHandler
import org.rioproject.monitor.FileSystemOARDeployHandler
import org.rioproject.monitor.ServiceResourceSelector
import org.rioproject.monitor.LeastActiveSelector
import net.jini.export.Exporter
import net.jini.jrmp.JrmpExporter
import java.io.File
import java.util.logging.ConsoleHandler
import java.util.logging.Level

/*
 * Declare Provision Monitor properties
 */
@Component('org.rioproject.monitor')
class MonitorConfig {
    String serviceName = 'Elastic Grid Monitor'
    String serviceComment = 'Elastic Grid Dynamic Provisioning Agent'
    long deployMonitorPeriod = 30000

    String[] getInitialLookupGroups() {
        def groups = [System.getProperty(Constants.GROUPS_PROPERTY_NAME, 
                      'elastic-grid')]
        return groups as String[]
    }

    ServiceResourceSelector getServiceResourceSelector() {
        return new LeastActiveSelector()
    }

    /*
     * Use a JrmpExporter for the OpStringManager.
     */
    Exporter getOpStringManagerExporter() {
        return new JrmpExporter()
    }

    ClassBundle getFaultDetectionHandler() {
        def fdh = org.rioproject.fdh.HeartbeatFaultDetectionHandler.class.name
        def fdhConf = ['-', fdh+'.heartbeatPeriod=10000', fdh+'heartbeatGracePeriod=10000']
        return FaultDetectionHandlerFactory.getClassBundle(fdh, fdhConf)
    } 

    /* Configure DeployHandlers for the Monitor to use */
    DeployHandler[] getDeployHandlers() {
        def deployDir = System.getProperty('RIO_HOME')+'/deploy'
        def deployHandlers =
            [new FileSystemOARDeployHandler(new File(deployDir)),
             new S3OARDeployHandler(AWSUtils.getDropBucket(), new File(deployDir))]
        return deployHandlers as DeployHandler[]
    }

}