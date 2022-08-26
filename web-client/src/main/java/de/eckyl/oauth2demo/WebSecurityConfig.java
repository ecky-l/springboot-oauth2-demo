package de.eckyl.oauth2demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final ClientRegistrationRepository clientRegistrationRepository;

	public WebSecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
		this.clientRegistrationRepository = clientRegistrationRepository;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/", "/home").permitAll()
				.anyRequest().authenticated()
				.and()
				.oauth2Login()
				.and()
				.logout()
				.logoutSuccessHandler(logoutSuccessHandler())
				.permitAll();
	}

	public LogoutSuccessHandler logoutSuccessHandler() {
		final var result = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
		result.setPostLogoutRedirectUri("http://localhost:8080/");
		return result;
	}

	/*
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		UserDetails user =
			 User.withDefaultPasswordEncoder()
				.username("user")
				.password("password")
				.roles("USER")
				.build();

		return new InMemoryUserDetailsManager(user);
	}

	 */
}
