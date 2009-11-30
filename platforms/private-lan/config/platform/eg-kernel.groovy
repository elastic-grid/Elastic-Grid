/*
 * Elastic Grid Core platform.
 * Copyright Elastic Grid, LLC.
 */

import org.rioproject.config.PlatformCapabilityConfig

class EGKernelConfig {
    
    def getPlatformCapabilityConfig() {
        String sep = File.separator
        String egLib = System.getProperty("EG_HOME")+sep+"lib"+sep+"elastic-grid"+sep
        StringBuilder classpath = new StringBuilder()
        ['amazon-ec2-discovery-${pom.version}.jar',
         'amazon-ec2-provisioner-${pom.version}.jar',
         'elastic-grid-model-${pom.version}.jar',
         'elastic-grid-utils-${pom.version}.jar',
         'elastic-grid-manager-${pom.version}.jar',
         'substrate-api-${pom.version}.jar',
         'activation-1.1.jar',
         'commons-lang-2.3.jar',
         'commons-logging-1.1.1.jar',
         'commons-io-1.4.jar',
         'commons-httpclient-3.1.jar',
         'commons-codec-1.3.jar',
         'jaxb-api-2.1.jar',
         'jaxb-impl-2.1.6.jar',
         'jibx-run-1.2.1.jar',
         'joda-time-1.5.2.jar',
         'stax-api-1.0.1.jar',
         'typica-1.5.jar'].each { jar  ->
              classpath.append(egLib+jar+File.pathSeparator)
        }
        return new PlatformCapabilityConfig("Elastic Grid Kernel",
                                            '${pom.version}',
                                            "Elastic Grid Kernel",
                                            "Elastic Grid, LLC.",
                                            classpath.toString())
    }

}
