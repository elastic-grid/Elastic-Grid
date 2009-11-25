/*
 * This configuration is used by the com.sun.jini.start utility to start
 * Elastic Grid ProvisionMonitor, Webster and a Jini Lookup Service
 */

import com.elasticgrid.boot.ServiceDescriptorUtil
import org.rioproject.config.Component
import com.sun.jini.start.ServiceDescriptor

@Component ('com.sun.jini.start')
class StartMonitorConfig {

  ServiceDescriptor[] getServiceDescriptors() {
    String egHome = System.getProperty('EG_HOME')

    def websterRoots = [egHome + '/lib-dl', ';',
            egHome + '/lib', ';',
            egHome + '/deploy']

    String policyFile = egHome + '/policy/policy.all'

    def serviceDescriptors = [
            /* Webster, set to serve up 4 directories */
            ServiceDescriptorUtil.getWebster(policyFile, '9010', (String[]) websterRoots),
            /* Jini Lookup Service */
            ServiceDescriptorUtil.getLookup(policyFile, getLookupConfigArgs(egHome)),
            /* Elastic Grid Provision Monitor */
            ServiceDescriptorUtil.getMonitor(policyFile, getMonitorConfigArgs(egHome)),
            /* Elastic Grid REST API */
            ServiceDescriptorUtil.getRestApi(policyFile, getRestConfigArgs(egHome)),
            /* Elastic Grid Web Administration Console */
            ServiceDescriptorUtil.getAdminConsole(policyFile, getAdminConsoleConfigArgs(egHome))
    ]

    return (ServiceDescriptor[]) serviceDescriptors
  }

  String[] getMonitorConfigArgs(String egHome) {
    return ["${egHome}/config/monitor.groovy"] as String[]
  }

  String[] getRestConfigArgs(String egHome) {
    return ["${egHome}/config/rest-api.groovy"] as String[]
  }

  String[] getAdminConsoleConfigArgs(String egHome) {
    return ["${egHome}/config/admin-console.groovy"] as String[]
  }

  String[] getLookupConfigArgs(String egHome) {
    return ["${egHome}/config/reggie.groovy"] as String[]
  }

}