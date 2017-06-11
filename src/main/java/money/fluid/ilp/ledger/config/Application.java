package money.fluid.ilp.ledger.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import static org.springframework.boot.Banner.Mode.CONSOLE;

@SpringBootApplication
@Configuration
@Import({
        EmbeddedServletContainerAutoConfiguration.EmbeddedTomcat.class,
        //DispatcherServletAutoConfiguration.class,
        //EmbeddedServletContainerAutoConfiguration.class,
        //ErrorMvcAutoConfiguration.class,
        //HttpEncodingAutoConfiguration.class,
        //HttpMessageConvertersAutoConfiguration.class,
        //MessageSourceAutoConfiguration.class,
        //ServerPropertiesAutoConfiguration.class,
        //PropertyPlaceholderAutoConfiguration.class,
        //WebMvcAutoConfiguration.class,
        ServicesConfig.class,
        PersistenceConfig.class,
        //WebSocketConfig.class,
        JsonRpcWebSocketConfig.class,
        SpringSecurityWebAppConfig.class,
        WebControllersConfig.class
})
@PropertySource("classpath:application.properties")
// TODO: Notice that the following are all ILP schemed...we should consider if it makes sense to have ILP ledger
// functionality in a separate package from the ledger itself.
@ComponentScan(basePackages = {
        "money.fluid.ilp.ledger.web.controllers",
        "money.fluid.ilp.ledger.web.factories",
        "money.fluid.ilp.ledger.web.filters"
})
public class Application {
    public static void main(String[] args) {
        final SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(CONSOLE);
        app.run(args);
    }
}