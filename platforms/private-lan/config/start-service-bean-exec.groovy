/*
 * This configuration is used by the com.sun.jini.start utility to start a 
 * service that will exec a single service bean
 */
import org.rioproject.boot.RioServiceDescriptor
import org.rioproject.config.Component
import com.sun.jini.start.ServiceDescriptor

@Component('com.sun.jini.start')
class StartServiceBeanExecConfig {

    ServiceDescriptor[] getServiceDescriptors() {
        String jiniHome = System.getProperty('JINI_HOME')
        String egHome = System.getProperty('EG_HOME')
        String codebase = "file://${egHome}/lib/cybernode-dl.jar"
        String classpath = egHome+'/lib/cybernode.jar'

        String policyFile = egHome+'/policy/policy.all'
        def configArgs = [egHome+'/config/agent.groovy',
                          egHome+'/config/compute_resource.groovy']

        def serviceDescriptors = [
            new RioServiceDescriptor(codebase,
                                     policyFile,
                                     classpath,
                                     'org.rioproject.cybernode.exec.ServiceBeanExec',
                                     getServiceBeanExecConfigArgs(egHome))
        ]

        return (ServiceDescriptor[])serviceDescriptors
    }

    String[] getServiceBeanExecConfigArgs(String egHome) {
        def configArgs = ["${egHome}/config/agent.groovy",
                          "${egHome}/config/compute_resource.groovy"]
        return configArgs as String[]
    }
}