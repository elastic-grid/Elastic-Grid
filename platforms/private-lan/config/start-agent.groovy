/*
 * This configuration is used by the com.sun.jini.start utility to start an
 * Elastic Grid Agent
 */
import org.rioproject.boot.BootUtil
import com.elasticgrid.boot.ServiceDescriptorUtil
import org.rioproject.config.Component
import com.sun.jini.start.ServiceDescriptor;

@Component('com.sun.jini.start')
class StartAgentConfig {
    ServiceDescriptor[] getServiceDescriptors() {
        String egHome = System.getProperty('EG_HOME')

        def websterRoots = [egHome+'/lib-dl', ';',
                            egHome+'/lib']

        String policyFile = egHome+'/policy/policy.all'

        def serviceDescriptors = [
            ServiceDescriptorUtil.getWebster(policyFile, '0', (String[])websterRoots),
                /* Elastic Grid Provision Monitor */
            ServiceDescriptorUtil.getAgent(policyFile, getAgentConfigArgs(egHome))
        ]

        return (ServiceDescriptor[])serviceDescriptors
    }

    String[] getAgentConfigArgs(String egHome) {
        def configArgs = ["${egHome}/config/agent.groovy",
                          "${egHome}/config/compute_resource.groovy"]
        return configArgs as String[]
    }
}
