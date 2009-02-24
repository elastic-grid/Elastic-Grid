/*
 * This configuration is used by the com.sun.jini.start utility to start an
 * Elastic Grid Agent
 */
import org.rioproject.boot.BootUtil
import org.rioproject.boot.ServiceDescriptorUtil
import org.rioproject.config.Component
import com.sun.jini.start.ServiceDescriptor;

@Component('com.sun.jini.start')
class StartAgentConfig {
    ServiceDescriptor[] getServiceDescriptors() {
        String jiniHome = System.getProperty('JINI_HOME')
        String egHome = System.getProperty('EG_HOME')

        def websterRoots = [jiniHome+'/lib-dl', ';',
                            jiniHome+'/lib',    ';',
                            egHome+'/lib']

        String policyFile = egHome+'/policy/policy.all'
        def configArgs = [egHome+'/config/agent.groovy',
                          egHome+'/config/compute_resource.groovy']

        def serviceDescriptors = [
            ServiceDescriptorUtil.getWebster(policyFile, '0', (String[])websterRoots),
            ServiceDescriptorUtil.getCybernode(policyFile, (String[])configArgs)
        ]

        return (ServiceDescriptor[])serviceDescriptors
    }
}
