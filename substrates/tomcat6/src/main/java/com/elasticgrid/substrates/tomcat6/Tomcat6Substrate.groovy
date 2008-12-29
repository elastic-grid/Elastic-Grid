package com.elasticgrid.substrates.tomcat6

import com.elasticgrid.substrates.AbstractSubstrate
import groovy.xml.MarkupBuilder
import com.elasticgrid.substrates.FirewallRule
import com.elasticgrid.substrates.FirewallRule.IpProtocol

class Tomcat6Substrate extends AbstractSubstrate {
    def version

    public String getName() {
        return "Tomcat 6";
    }

    public void addDomainSpecificLangueFeatures(MarkupBuilder builder, ExpandoMetaClass emc) {
        emc.tomcat = { Map attributes, Closure cl ->
            version = attributes.version ?: '6.0.18'
            def removeOnDestroy = attributes.removeOnDestroy ?: true
            serviceExec(name: 'Tomcat') {
                software(name: 'Tomcat', version: version, removeOnDestroy: removeOnDestroy) {
                    install source: "https://elastic-grid-substrates.s3.amazonaws.com/tomcat/apache-tomcat-${version}.zip",
                            target: 'tomcat', unarchive: true
                    postInstall(removeOnCompletion: removeOnDestroy) {
                        execute command: "/bin/chmod +x \${EG_HOME}/system/external/tomcat/apache-tomcat-$version/bin/*.sh",
                                nohup: false
                    }
                }
                execute inDirectory: 'bin', command: 'catalina.sh run'
                cl()
                maintain 1
                maxPerMachine 1
            }
        }
        emc.webapp = { Map attributes ->
            data source: attributes.source,
                 target: "\${EG_HOME}/system/external/tomcat/apache-tomcat-$version/webapps"
        }
    }

    /**
     * Open port 8080 for TCP from anywhere.
     * @todo temporary situation until we have the Apache frontend working fine.
     */
    @Override
    public List<FirewallRule> getFirewallRules() {
        [new FirewallRule(getName(), IpProtocol.TCP, 8080, '??')]
    }

}
