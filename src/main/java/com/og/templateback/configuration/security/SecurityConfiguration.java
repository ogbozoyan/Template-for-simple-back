package com.og.templateback.configuration.security;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * @author ogbozoyan
 * @since 20.10.2023
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity //for preAuthorize()
@Slf4j
public class SecurityConfiguration {
    @Setter
    @Value("#{'${auth.whitelist}'.split(' ')}")
    private String[] AUTH_WHITELIST;
    @Value("${keycloak-client.issuer-uri}")
    private String issuerUri;


    @Autowired
    CustomJwtAuthenticationConverter jwtAuthenticationConverter;

    /*
     * spring security 7 whitelist migration
     * referenced materials:
     * https://www.youtube.com/watch?v=E03Q77IgU9g&ab_channel=BoualiAli
     * https://github.com/ali-bouali/keycloak-integration-with-spring-boot-3
     * https://github.com/rickors560/spring-security-keycloak-exmaple/blob/main/keycloak-security/src/main/java/com/rick/keycloaksecurity/WebSecurityConfig.java
     * https://stackoverflow.com/questions/76307796/configure-keycloak-21-with-spring-security-6
     * https://medium.com/keycloak/keycloak-as-an-identity-broker-an-identity-provider-af1b150ea94
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Whitelist endpoints: {}", Arrays.asList(AUTH_WHITELIST));
        http.
                formLogin(
                        AbstractHttpConfigurer::disable
                );

        http.authorizeHttpRequests(
                req -> req
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated()
        );

        //jwt token convertor
        http
                .oauth2ResourceServer(
                        oauth2Configurer ->
                                oauth2Configurer.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter))
                );

        //turning off session
        http.
                sessionManagement(
                        session ->
                                session
                                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                                        .sessionFixation().none()
                );

        http
                .csrf(AbstractHttpConfigurer::disable);

        http.
                exceptionHandling(
                        exceptionHandling ->
                                exceptionHandling
                                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                );

        http
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(issuerUri);
    }

    @Bean
    @Order(0)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();

//        configuration.setAllowedOrigins(ImmutableList.of("https://www.yourdomain.com")); // www - obligatory
        configuration.setAllowedOriginPatterns(List.of("*"));  //set access from all domains
        configuration.setAllowedMethods(List.of("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE"
        ));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*", "API-KEY"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
