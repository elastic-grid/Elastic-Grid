import org.rioproject.config.Component

/**
 * Configures the web administration console
 */
@Component ('com.elasticgrid.admin')
class AdminConsoleConfig {
  int port = 8080
  String context = '/'
}