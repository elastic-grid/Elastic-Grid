/*
 * This configuration is used by the com.sun.jini.start utility to start
 * Elastic Grid services : ProvisionMonitor, Agent, Webster and a Jini 
 * Lookup Service
 */
import org.rioproject.boot.BootUtil
import com.elasticgrid.boot.ServiceDescriptorUtil
import org.rioproject.config.Component
import com.sun.jini.start.ServiceDescriptor;

@Component('com.sun.jini.start')
class StartAllConfig {
    ServiceDescriptor[] getServiceDescriptors() {
        String jiniHome = System.getProperty('JINI_HOME')
        String egHome = System.getProperty('EG_HOME')

        def websterRoots = [jiniHome+'/lib-dl', ';',
                            jiniHome+'/lib',    ';',
                            egHome+'/lib',      ';',
                            egHome+'/deploy']

        String policyFile = egHome+'/policy/policy.all'

        def serviceDescriptors = [
            /* Webster, set to serve up 4 directories */
            ServiceDescriptorUtil.getWebster(policyFile, '9010', (String[])websterRoots),
            /* Jini Lookup Service */
            ServiceDescriptorUtil.getLookup(policyFile, getLookupConfigArgs(egHome)),
            /* Elastic Grid Provision Monitor */
            ServiceDescriptorUtil.getMonitor(policyFile, getMonitorConfigArgs(egHome)),
            /* Elastic Grid Agent */
            ServiceDescriptorUtil.getCybernode(policyFile, getCybernodeConfigArgs(egHome)),
            /* Elastic Grid REST API */
            ServiceDescriptorUtil.getRestApi(policyFile, getRestConfigArgs(egHome))
        ]

        return (ServiceDescriptor[])serviceDescriptors
    }

    String[] getMonitorConfigArgs(String egHome) {
        def configArgs = ["${egHome}/config/monitor.groovy"]
        return configArgs as String[]
    }

    String[] getLookupConfigArgs(String egHome) {
        def configArgs = ["${egHome}/config/reggie.groovy"]
        return configArgs as String[]
    }

    String[] getRestConfigArgs(String egHome) {
        def configArgs = ["${egHome}/config/rest-api.groovy"]
        return configArgs as String[]
    }

    String[] getCybernodeConfigArgs(String egHome) {
        def configArgs = ["${egHome}/config/agent.groovy",
                          "${egHome}/config/compute_resource.groovy"]
        return configArgs as String[]
    }
}