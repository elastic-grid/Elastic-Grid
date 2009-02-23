/*
 * Configuration for a Provision Monitor
 */
import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import org.rioproject.core.ClassBundle;
import org.rioproject.log.LoggerConfig;
import org.rioproject.log.LoggerConfig.LogHandlerConfig;
import org.rioproject.fdh.FaultDetectionHandlerFactory;
import org.rioproject.monitor.DeployHandler;
import org.rioproject.monitor.FileSystemOARDeployHandler;
import com.elasticgrid.platforms.ec2.monitor.S3OARDeployHandler;

/*
 * Declare Provision Monitor properties
 */
@Component('org.rioproject.monitor')
class MonitorConfig {

    String[] getInitialLookupGroups() {
        def groups = ['rio']
        return (String[])groups
    }

    String getServiceName() {
        return 'Elastic Grid Monitor'
    }

    String getServiceComment() {
        return 'Elastic Grid Dynamic Provisioning Agent'
    }

    String getJmxName() {
        return 'com.elasticgrid.monitor:type=Monitor'
    }

    ServiceResourceSelector getServiceResourceSelector() {
        return new LeastActiveSelector()
    }

    /*
     * Use a JrmpExporter here.
     */
    Exporter getOpStringManagerExporter() {
        return new JrmpExporter()
    }

    LoggerConfig[] getLoggerConfigs() {
        def loggers = []
        ['org.rioproject.monitor' : Level.INFO,
         'org.rioproject.monitor.provision' : Level.FINEST,
         'org.rioproject.monitor.sbi' : Level.FINEST,
         'org.rioproject.monitor.peer' : Level.FINE,
         'net.jini.lookup.JoinManager' : Level.OFF].each { name, level ->
            loggers.add(new LoggerConfig(name,
                                         level,
                                         new LogHandlerConfig(new ConsoleHandler())))
        }
        return (LoggerConfig[])loggers
    }

    /* Configure a FaultDetectionHandler for the ProvisionMonitor */
    ClassBundle getFaultDetectionHandler() {
        def fdh = org.rioproject.fdh.HeartbeatFaultDetectionHandler.class.name
        def fdhConf = ['-', fdh+'.heartbeatPeriod=10000', fdh+'heartbeatGracePeriod=10000']
        ClassBundle bundle = new ClassBundle(fdh);
        bundle.addMethod("setConfiguration", (Object[])fdhConf);
        return bundle
    }

    long getDeployMonitorPeriod() {
        return 30000
    }

    /* Configure DeployHandlers for the Monitor to use */
    // TODO: make sure the S3 bucket is parameterized
    DeployHandler[] getDeployHandlers() {
        deployDir = System.getProperty('RIO_HOME')+'/deploy'
        def deployHandlers =
            [new FileSystemOARDeployHandler(new File(deployDir)),
             new S3OARDeployHandler("elastic-grid-drop-target", new File(deployDir))]
        return (DeployHandler[])deployHandlers
    }

}