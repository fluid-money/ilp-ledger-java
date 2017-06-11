package money.fluid.ilp.ledger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SpringSecurityWebAppConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Starting with Spring Security 4.2 we do not need to explicitly apply the Stormpath configuration in Spring Boot
        // any more (note that it is still required in regular Spring)
        http.anonymous()
                .and()
                .authorizeRequests()
                .antMatchers("/accounts").fullyAuthenticated()
                .antMatchers("/websockets").permitAll();

        //.antMatchers("/**").permitAll();

        // TODO: Make each public endpoint permitAll, but restrict all others...
    }
}
