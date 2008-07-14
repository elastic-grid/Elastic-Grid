package com.elasticgrid.substrates.tomcat6

import com.elasticgrid.substrates.AbstractSubstrate
import groovy.xml.MarkupBuilder

class Tomcat6Substrate extends AbstractSubstrate {
    def version

    public String getName() {
        return "Tomcat 6";
    }

    public void addDomainSpecificLangueFeatures(MarkupBuilder builder, ExpandoMetaClass emc) {
        emc.tomcat = { Map attributes, Closure cl ->
            version = attributes.version ?: '6.0.16'
            serviceExec(name: 'Tomcat') {
                software(name: 'Tomcat', version: version, removeOnDestroy: attributes.removeOnDestroy) {
                    download source: "https://elastic-grid.s3.amazonaws.com/tomcat/apache-tomcat-${version}.zip",
                             installRoot: '${RIO_HOME}/system/external/tomcat', unarchive: true
                    postInstall(removeOnCompletion: attributes.removeOnDestroy) {
                        cl()
                        execute command: "/bin/chmod +x \${RIO_HOME}/system/external/tomcat/apache-tomcat-$version/bin/*.sh",
                                nohup: false
                    }
                }
                execute inDirectory: 'bin', command: 'catalina.sh run'
                maintain 1
            }
        }
        emc.webapp = { Map attributes ->
            download source: attributes.source,
                     installRoot: "\${RIO_HOME}/system/external/tomcat/apache-tomcat-$version/webapps"
        }
    }


}