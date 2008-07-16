package com.elasticgrid.substrates.glassfish2

import com.elasticgrid.substrates.AbstractSubstrate
import groovy.xml.MarkupBuilder

class GlassFish2Substrate extends AbstractSubstrate {
    def version

    public String getName() {
        return "GlassFish 2";
    }

    public void addDomainSpecificLangueFeatures(MarkupBuilder builder, ExpandoMetaClass emc) {
        emc.glassfish = { Map attributes, Closure cl ->
            serviceExec(name: 'GlassFish') {
                version = attributes.version ?: 'v2ur2-b04'
                software(name: 'GlassFish', version: version, removeOnDestroy: attributes.removeOnDestroy) {
                    install source: "https://elastic-grid.s3.amazonaws.com/glassfish/glassfish-${version}.zip",
                            installRoot: '${RIO_HOME}/system/external/glassfish', unarchive: true
                    postInstall(removeOnCompletion: attributes.removeOnDestroy) {
                        execute command: "/bin/chmod +x \${RIO_HOME}/system/external/glassfish/glassfish-$version/bin/*.sh"
                    }
                }
                execute inDirectory: 'bin', command: 'catalina.sh run'
                cl()
                maintain 1
            }
        }
        emc.webapp = { Map attributes ->
            data source: attributes.source,
                 target: "\${RIO_HOME}/system/external/glassfish/glassfish-$version/webapps"
        }
    }


}