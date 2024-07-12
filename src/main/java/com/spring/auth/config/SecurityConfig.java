package com.spring.auth.config;

import com.spring.auth.filter.JwtRequestFilter; // This class handles JWT token based authentication
import com.spring.auth.service.CustomUserDetailsService;  // This class handles fetching user details

import com.spring.auth.utils.JwtUtil; // This class handles JWT token creation and validation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This class configures Spring Security for the application.
 * It defines things like how users authenticate, what URLs require authentication,
 * and how to handle JWT tokens.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Defines a bean to be used for password encoding. BCryptPasswordEncoder is a common choice.
     * @return A BCryptPasswordEncoder bean
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines a bean for the AuthenticationManager. This is used for user authentication.
     * @param authenticationConfiguration Spring Security configuration
     * @return The AuthenticationManager bean
     * @throws Exception If there's an error getting the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Defines the main security configuration for the application.
     * This method configures things like:
     *  - Disabling CSRF protection (might be needed for some APIs)
     *  - Permitting access to registration and login URLs without authentication
     *  - Requiring authentication for all other URLs
     *  - Setting session management to stateless (using JWT tokens)
     *  - Adding a custom JWT filter before the UsernamePasswordAuthenticationFilter
     *
     * @param http The HttpSecurity object used for configuration
     * @return The SecurityFilterChain bean containing the security configuration
     * @throws Exception If there's an error configuring security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/api/register", "/api/login").permitAll()
                .anyRequest().authenticated()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        http.formLogin(); //Enables login form "/login"

        http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Defines a bean for the JwtRequestFilter class.
     * This filter handles checking JWT tokens in requests.
     *
     * @return The JwtRequestFilter bean
     */
    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(jwtUtil, customUserDetailsService);
    }
}
