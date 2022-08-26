package de.eckyl.oauth2demo;

import de.eckyl.oauth2demo.authentication.ApiAuthenticationConverter;
import de.eckyl.oauth2demo.authentication.DemoRoleVoter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final ApiAuthenticationConverter apiAuthenticationConverter;

    public WebSecurityConfig(ApiAuthenticationConverter apiAuthenticationConverter) {
        this.apiAuthenticationConverter = apiAuthenticationConverter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .antMatcher("/**")
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                //.accessDecisionManager(accessDecisionManager())
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(apiAuthenticationConverter)
        ;
    }

    //@Bean
    private AccessDecisionManager accessDecisionManager() {
        final var voters = List.<AccessDecisionVoter<?>>of(
                new WebExpressionVoter(),
                new DemoRoleVoter(),
                new AuthenticatedVoter()
                );
        return new UnanimousBased(voters);
    }
}
