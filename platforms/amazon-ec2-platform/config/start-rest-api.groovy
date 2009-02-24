/*
 * This configuration is used by the com.sun.jini.start utility to start a
 * the Elastic Grid REST API service
 */
import org.rioproject.boot.BootUtil
import org.rioproject.boot.ServiceDescriptorUtil
import org.rioproject.config.Component
import com.sun.jini.start.ServiceDescriptor;

@Component('com.sun.jini.start')
class StartRESTConfig {
    ServiceDescriptor[] getServiceDescriptors() {
        String jiniHome = System.getProperty('JINI_HOME')
        String egHome = System.getProperty('EG_HOME')

        def websterRoots = [jiniHome+'/lib-dl', ';',
                            jiniHome+'/lib',    ';',
                            egHome+'/lib']

        String policyFile = egHome+'/policy/policy.all'
        String restApiConfig = egHome+'/config/rest-api.groovy'

        def serviceDescriptors = [
            ServiceDescriptorUtil.getWebster(policyFile, '0', (String[])websterRoots),
            ServiceDescriptorUtil.getRestApi(policyFile, restApiConfig)
        ]

        return (ServiceDescriptor[])serviceDescriptors
    }
}