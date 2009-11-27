import org.rioproject.config.Component
import com.elasticgrid.boot.BannerProviderImpl
import org.rioproject.resources.util.BannerProvider

/**
 * Configures the web administration console
 */
@Component ('com.elasticgrid.admin')
class AdminConsoleConfig {
  int port = 8080
  String context = '/'

  BannerProvider getBannerProvider() {
    return new BannerProviderImpl()
  }
}