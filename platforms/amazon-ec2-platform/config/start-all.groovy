/*
 * This configuration is used by the com.sun.jini.start utility to start
 * Elastic Grid services : ProvisionMonitor, Agent, Webster and a Jini 
 * Lookup Service
 */
import org.rioproject.boot.BootUtil
import org.rioproject.boot.ServiceDescriptorUtil
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
        String monitorConfig = egHome+'/config/monitor.groovy'
        String reggieConfig = egHome+'/config/reggie.groovy'
        String restApiConfig = egHome+'/config/rest-api.groovy'
        def configArgs = [egHome+'/config/agent.groovy',
                          egHome+'/config/compute_resource.groovy']

        def serviceDescriptors = [
            /* Webster, set to serve up 4 directories */
            ServiceDescriptorUtil.getWebster(policyFile, '9010', (String[])websterRoots),
            /* Jini Lookup Service */
            ServiceDescriptorUtil.getLookup(policyFile, reggieConfig),
            /* Elastic Grid Provision Monitor */
            ServiceDescriptorUtil.getMonitor(policyFile, monitorConfig),
            /* Elastic Grid Agent */
            ServiceDescriptorUtil.getCybernode(policyFile, (String[])configArgs),
            /* Elastic Grid REST API */
            ServiceDescriptorUtil.getRestApi(policyFile, restApiConfig)
        ]

        return (ServiceDescriptor[])serviceDescriptors
    }
}