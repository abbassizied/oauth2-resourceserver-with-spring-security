package io.github.abbassizied.sms.security; 

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.secret}")
    private String jwtSecret;

    // Configure in-memory user details
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("zied")
                        .password("{noop}password")
                        .authorities("READ", "ROLE_USER")
                        .build());
    }

    
    // Define the AuthenticationManager bean
    @SuppressWarnings({ "deprecation", "removal" })
	@Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService())  // Use the UserDetailsService defined above
                .passwordEncoder(NoOpPasswordEncoder.getInstance())  // Optional, if you want to use plain text passwords
                .and()
                .build();
    }   
    
    
    // Security filter chain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Disable Cross-Site Request Forgery (CSRF)
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/public/**", "/api/auth/**", "/api/auth/login/**").permitAll(); 
                    //auth.requestMatchers("/api/**").authenticated();
                    auth.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    // Convert JWT to authorities (roles)
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }


    // Configure the JwtDecoder using the secret key (HMAC_SHA256)
    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] secretKeyBytes = Base64.getDecoder().decode(jwtSecret); // Decode the base64 encoded secret key
        SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256"); // Use HmacSHA256 for signing/decoding
        return NimbusJwtDecoder
                .withSecretKey(secretKey) // Use the symmetric key for JWT decoding
                .build();
    }    
    

    // JwtEncoder configuration (optional, but keeps it consistent with the decoder)
    @Bean
    public JwtEncoder jwtEncoder() {
        byte[] secretKeyBytes = Base64.getDecoder().decode(jwtSecret); // Decode the base64 encoded secret key
        SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256"); // Use HmacSHA256 for signing/encoding
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey)); // Create an encoder using the symmetric key
    }
}
