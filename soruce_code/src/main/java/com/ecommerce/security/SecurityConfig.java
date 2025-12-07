package com.ecommerce.security;

import com.ecommerce.ApplicationConstants;
import com.ecommerce.entities.user.UserRoles;
import com.ecommerce.security.ExceptionHandling.AuthenticationEntryPointCustom;
import com.ecommerce.security.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;


@Configuration
public class SecurityConfig {



//    @Bean
//    List<String>corsAllowedOrigins(){
//        return List.of();
//    }
//     cors configration
//    @Bean
//    public CorsConfigurationSource corsConfig(@Qualifier("corsAllowedOrigins") List<String>corsAllowedOrigins ){
//        var source = new UrlBasedCorsConfigurationSource();
//        var general = new CorsConfiguration();
//
//        // allowed origins
//        general.setAllowedOrigins(corsAllowedOrigins);
//
//        general.setAllowedHeaders(List.of(ApplicationConstants.JWT_HEADER_NAME,"X-XSRF-TOKEN"));
//        general.setAllowCredentials(false);
//        general.setMaxAge(Duration.ofHours(12));
//        general.setExposedHeaders(List.of(ApplicationConstants.JWT_HEADER_NAME," X-XSRF-TOKEN"));
//        source.registerCorsConfiguration("/**",general);
//
//        return source;
//    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http , CorsConfigurationSource corsConfig
            , JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {


//        http.csrf(
//                csrf -> {
//
//                    CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
//                    repo.setCookieCustomizer(cu -> {
//                        cu.sameSite("lax");
//                        cu.maxAge(Duration.ofHours(12));
//                    });
//                    csrf.csrfTokenRepository(repo);
//                    csrf.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler());
//                    csrf.ignoringRequestMatchers(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET,"/api/products/**"));
//                    csrf.ignoringRequestMatchers(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST,"/api/auth/login"));
//                    csrf.ignoringRequestMatchers(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST,"/api/auth/register"));
//                    csrf.ignoringRequestMatchers(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST,"/api/webhook/**"));
//                }
//        );
        http
                .authorizeHttpRequests(
                        (requests) ->
                            requests
                                    // product config
                                    .requestMatchers(HttpMethod.GET,"/api/products/**").permitAll()
                                    .requestMatchers(HttpMethod.DELETE,"/api/products/**").hasRole(UserRoles.ADMIN.toString())
                                    .requestMatchers(HttpMethod.POST,"/api/products/**").hasRole(UserRoles.ADMIN.toString())
                                    .requestMatchers(HttpMethod.PUT,"/api/products/**").hasRole(UserRoles.ADMIN.toString())
                                    // reg
                                    .requestMatchers(HttpMethod.POST,"/api/auth/register").not().authenticated()
                                    .requestMatchers(HttpMethod.POST, "/api/auth/login").not().authenticated()
                                    // cart config
                                    .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                                    .requestMatchers(HttpMethod.POST, "/api/categories").hasRole(UserRoles.ADMIN.toString())
                                    .requestMatchers(HttpMethod.DELETE, "/api/categories").hasRole(UserRoles.ADMIN.toString())
                                    // cart config
                                    .requestMatchers(HttpMethod.GET, "/api/me/cart").hasRole(UserRoles.CUSTOMER.toString())
                                    .requestMatchers(HttpMethod.PUT, "/api/me/cart/**").hasRole(UserRoles.CUSTOMER.toString())
                                    .requestMatchers(HttpMethod.POST, "/api/me/cart/**").hasRole(UserRoles.CUSTOMER.toString())
                                    .requestMatchers(HttpMethod.DELETE, "/api/me/cart/**").hasRole(UserRoles.CUSTOMER.toString())

                                    // orders config
                                    .requestMatchers(HttpMethod.POST, "/api/me/checkout").hasRole(UserRoles.CUSTOMER.toString())
                                    .requestMatchers(HttpMethod.GET, "/api/me/orders/**").hasRole(UserRoles.CUSTOMER.toString())
                                    .requestMatchers(HttpMethod.POST, "/api/me/orders/**").hasRole(UserRoles.CUSTOMER.toString())
                                    // web hook config
                                    .requestMatchers(HttpMethod.POST,"/api/webhook/**").not().authenticated()
                );
        // disable session creation
        http.sessionManagement(s ->{
            s.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });
        // disable form login and http basic authentication
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        // cors protection. disable for restapi with jwt token authentication
        http.cors(AbstractHttpConfigurer::disable);
        // csrf protection. diable because of restapi with stateless jwt token
        http.csrf(AbstractHttpConfigurer::disable);
        // logout
        http.logout(c-> {
            c.deleteCookies("XSRF-TOKEN");
            c.clearAuthentication(true);
        });

        // jwt validation
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // exception handling
//        http.exceptionHandling(
//                c -> c.authenticationEntryPoint(new AuthenticationEntryPointCustom())
//        );
        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    // custom filter to validate the jwt token
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService ){
        return new JwtAuthenticationFilter(publicApis(),jwtService);
    }
    // for jwt validation filter to skip validation
    List<PathPatternRequestMatcher> publicApis(){
        var products = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET,"/api/products/**");
        var login = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST,"/api/auth/login");
        var reg = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST,"/api/auth/register");
        var webhook = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST,"/api/webhook/**");
        var categories = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET , "/api/categories");
        return List.of(products,login,reg,webhook,categories);
    }
}
