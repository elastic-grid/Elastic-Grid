import org.rioproject.config.Component

@Component('org.rioproject.tools.ui')
class UI {
    String waitMessage="Waiting to discover the Elastic Grid ..."
    String title="Elastic Grid Management Console"
    String bannerIcon="com/elasticgrid/tools/ui/Elastic-Grid-Logo.png"
    String aboutIcon="com/elasticgrid/tools/ui/Elastic-Grid-Logo.png"
    String aboutInfo="Elastic Grid<br>Management Console<br>"
    String build="Early Access"
    String version="${pom.version}"
}
