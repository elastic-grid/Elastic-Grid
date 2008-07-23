package com.elasticgrid.substrates.apache

import com.elasticgrid.substrates.AbstractSubstrate
import groovy.xml.MarkupBuilder
import com.elasticgrid.substrates.FirewallRule
import com.elasticgrid.substrates.FirewallRule.IpProtocol

class ApacheSubstrate extends AbstractSubstrate {
    def version

    public String getName() {
        return "Apache 2";
    }

    public void addDomainSpecificLangueFeatures(MarkupBuilder builder, ExpandoMetaClass emc) {
        emc.webserver = { Map attributes, Closure cl ->
            version = attributes.version ?: '2.2.9'
            def removeOnDestroy = attributes.removeOnDestroy ?: true
            serviceExec(name: 'Apache') {
                software(name: 'Apache', version: version, removeOnDestroy: removeOnDestroy) {
                    install source: "https://elastic-grid.s3.amazonaws.com/apache-httpd/apache-httpd-${version}.zip",
                            target: '${RIO_HOME}/system/external/apache-httpd', unarchive: true
                    postInstall(removeOnCompletion: removeOnDestroy) {
                        execute command: "/bin/chmod +x \${RIO_HOME}/system/external/apache-httpd/httpd-$version/bin/*.sh",
                                nohup: false
                    }
                }
                execute inDirectory: 'bin', command: 'apachectl start'
                cl()
                maintain 1
            }
        }
        emc.website = { Map attributes ->
            data source: attributes.source,
                 target: "\${RIO_HOME}/system/external/apache-httpd/httpd-$version/vhosts/${attributes.vhost}"
        }
    }

    /**
     * Open port 80 for TCP from anywhere.
     */
    @Override
    public List<FirewallRule> getFirewallRules() {
        [new FirewallRule(getName(), IpProtocol.TCP, 80, '??')]
    }

}