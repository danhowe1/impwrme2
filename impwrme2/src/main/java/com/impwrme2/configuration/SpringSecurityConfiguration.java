package com.impwrme2.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SpringSecurityConfiguration {

    @Autowired
    ClientRegistrationRepository clientRegistrationRepository; 

    /**
     * Logout of the SSO provider.
     * @return the success handler.
     */
    private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() { 
        OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri("{baseUrl}");
        return successHandler;
    }

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// Allow various paths.
		http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/index", "/error", "/resources/**", "/css/**", "/img/**", "/js/**").permitAll());
		
		// Deny everything else.
		http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());

        // Enable OAuth redirect login.
        http.oauth2Login(withDefaults());

        // Enables OAuth Client configuration.
        http.oauth2Client(withDefaults());
        
        // Logout of the app as well as the SSO provider.
        // (To just logout of the app use:
        // http.logout(logout -> logout.invalidateHttpSession(true).logoutSuccessUrl("/"));
        http.logout(logout -> logout.invalidateHttpSession(true).logoutSuccessHandler(oidcLogoutSuccessHandler()));

		return http.build();
	}
}
