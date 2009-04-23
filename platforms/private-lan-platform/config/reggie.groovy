/*
 * Configuration for a Lookup Service
 */
import org.rioproject.boot.BootUtil
import org.rioproject.config.Component

@Component('com.sun.jini.reggie')
class ReggieConfig {

    String[] getInitialMemberGroups() {
        def groups = ['elastic-grid']
        return groups as String[]
    }

    String getUnicastDiscoveryHost() {
        return BootUtil.getHostAddress()
    }

}