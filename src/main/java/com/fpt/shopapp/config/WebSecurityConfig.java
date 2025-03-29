package com.fpt.shopapp.config;

import com.fpt.shopapp.filter.JwtTokenFilter;
import com.fpt.shopapp.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
//@EnableMethodSecurity
@RequiredArgsConstructor
@EnableWebSecurity
@EnableWebMvc
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix),
                                    String.format("%s/healthcheck/**", apiPrefix),
                                    "/api-docs",
                                    "/api-docs/**",
                                    "/swagger-resources",
                                    "/swagger-resources/**",
                                    "/configuration/ui",
                                    "/configuration/security",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/webjars/swagger-ui/**",
                                    "/swagger-ui/index.html"
                            )
                            .permitAll()
                            .requestMatchers(HttpMethod.GET, String.format("%s/roles**", apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.GET, String.format("%s/categories**", apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.POST, String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.PUT, String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.DELETE, String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.GET, String.format("%s/products/**", apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.GET, String.format("%s/products/images/*", apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.POST, String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.PUT, String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.DELETE, String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.POST, String.format("%s/orders/**", apiPrefix)).hasRole(Role.USER)
                            .requestMatchers(HttpMethod.GET, String.format("%s/orders/**", apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.PUT, String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.DELETE, String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.POST, String.format("%s/order_details/**", apiPrefix)).hasRole(Role.USER)
                            .requestMatchers(HttpMethod.GET, String.format("%s/order_details**", apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.PUT, String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(HttpMethod.DELETE, String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)
                            .anyRequest().authenticated();
                }).csrf(AbstractHttpConfigurer::disable);
        httpSecurity.securityMatcher(String.valueOf(EndpointRequest.toAnyEndpoint()));
        /*
        httpSecurity.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
         */
        return httpSecurity.build();
    }
}
