package com.example.chat.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.chat.registration.repository.LocalUserRepository;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@SuppressWarnings("null")
public class SecurityConfig {

    private LocalUserRepository localUserRepository;
    public SecurityConfig(LocalUserRepository localUserRepository) {
        this.localUserRepository = localUserRepository;
    }

    /*****************************************************************************************************/

    @Bean
    public UserDetailsService userDetailsService() {

        return username -> localUserRepository.findByEmail(username)
                .map(LocaUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /*****************************************************************************************************/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /***************************************************************************************************** */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    /***************************************************************************************************** */
@Bean TokenUtil tokenUtil() {
    return new TokenUtil();
}

    @Bean
    AuthFilter authFilter() {
        return new AuthFilter( userDetailsService(), tokenUtil());
    }

    /***************************************************************************************************** */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                      "/api/v1/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                        "/api/**", "/websocket/**", "/oauth2/**")
                .permitAll()
                .anyRequest().hasAnyRole("USER", "ADMIN", "INSTRUCTOR"))
                .addFilterAfter(authFilter(), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Access Denied");
                }));
        http.oauth2Login(login -> login.defaultSuccessUrl("/login/oauth2/success"));
        // http.oauth2Login(login -> login.defaultSuccessUrl("/login/oauth2/success"));
        // http.sessionManagement(session -> {
        // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // });
        return http.build();
    }

    /***************************************************************************************************** */

}
