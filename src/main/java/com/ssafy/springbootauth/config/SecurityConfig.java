package com.ssafy.springbootauth.config;

import com.ssafy.springbootauth.filter.CustomLoginFilter;
import com.ssafy.springbootauth.filter.CustomLogoutFilter;
import com.ssafy.springbootauth.filter.JWTFilter;
import com.ssafy.springbootauth.handler.CustomOAuth2SuccessHandler;
import com.ssafy.springbootauth.handler.PreAuthorizeExceptionHandler;
import com.ssafy.springbootauth.repository.CustomAuthorizationRequestRepository;
import com.ssafy.springbootauth.repository.RefreshTokenRepository;
import com.ssafy.springbootauth.repository.UserRepository;
import com.ssafy.springbootauth.service.CustomOAuth2UserService;
import com.ssafy.springbootauth.service.CustomUserDetailsService;
import com.ssafy.springbootauth.service.JWTService;
import com.ssafy.springbootauth.util.CookieUtil;
import com.ssafy.springbootauth.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PreAuthorizeExceptionHandler preAuthorizeExceptionHandler;
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final CustomUserDetailsService userDetailsService;


    public SecurityConfig(
            AuthenticationConfiguration authenticationConfiguration,
            RefreshTokenRepository refreshTokenRepository,
            PreAuthorizeExceptionHandler preAuthorizeExceptionHandler,
            UserRepository userRepository,
            JWTService jwtService,
            JWTUtil jwtUtil,
            CookieUtil cookieUtil,
            CustomUserDetailsService userDetailsService
    ) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.refreshTokenRepository = refreshTokenRepository;
        this.preAuthorizeExceptionHandler = preAuthorizeExceptionHandler;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Role 사이의 관계를 설정
     * 부등호 등으로 직관적으로 Role사이의 관계 설정이 가능
     * 부등호로 나타내면 하위 Role의 권한을 모두 획득
     */
    @Bean
    public RoleHierarchy roleHierarchy() {

        String hierarchy = "ROLE_ADMIN > ROLE_MANAGER > ROLE_USER";
        return RoleHierarchyImpl.fromHierarchy(hierarchy);
    }

    /**
     * Spring Security에서의 Cors권한 설정
     */
    private CorsConfiguration corsConfiguration(HttpServletRequest request) {

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:9000", "http://localhost:9001"));
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowCredentials(true); // 쿠키 등의 자격증명 전송을 허용
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setMaxAge(60 * 60L);
        corsConfiguration.setExposedHeaders(Collections.singletonList("Authorization"));

        return corsConfiguration;
    }

    /**
     * Spring Security의 존재의의
     *
     * Spring Application 접근 전 필터단을 거치다가 DelegatingFilterProxy의 FilterChainProxy를 거치면서 작동
     * 해당 Proxy를 거치면서 SpringApplication 내부에 있는 SecurityFilterChain을 미리 접근하여 수행하며 작동
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomOAuth2UserService customOAuth2UserService,
            CustomOAuth2SuccessHandler customOAuth2SuccessHandler,
            CustomAuthorizationRequestRepository customAuthorizationRequestRepository
    ) throws Exception {

        /**
         * csrf, fromLogin, httpBasicLogin Disable
         */
        http.csrf(AbstractHttpConfigurer::disable); // 사용자 요청 위조 (Cross Site Request Forgery)
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        /**
         */
        http.exceptionHandling((exceptionHandling) -> exceptionHandling
                .accessDeniedHandler(preAuthorizeExceptionHandler.customAccessDeniedHandler())
                .authenticationEntryPoint(preAuthorizeExceptionHandler.customAuthenticationEntryPoint())
        );

        /**
         * cors 권한 허용
         */
        http.cors((cors) -> cors
                .configurationSource(this::corsConfiguration)
        );

        /**
         * custom login filter
         */
        CustomLoginFilter customLoginFilter = new CustomLoginFilter(
                authenticationManager(authenticationConfiguration),
                jwtService,
                userRepository,
                cookieUtil
        );

        customLoginFilter.setFilterProcessesUrl("/auth/login");
        http.addFilterAt(customLoginFilter, UsernamePasswordAuthenticationFilter.class);

        /**
         * custom logout filter
         */
        CustomLogoutFilter customLogoutFilter = new CustomLogoutFilter(
                refreshTokenRepository,
                jwtService,
                cookieUtil
        );
        http.addFilterBefore(customLogoutFilter, LogoutFilter.class);

        /**
         * JWT 가 유효한지 인증을 거치는 filter
         */
        JWTFilter jwtFilter = new JWTFilter(jwtUtil, userRepository, userDetailsService);
        http.addFilterBefore(jwtFilter, CustomLoginFilter.class);


        /**
         * OAtuh2 관련 설정
         */
        http.oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuth2UserService))
                .successHandler(customOAuth2SuccessHandler)
                .authorizationEndpoint(endpoint -> endpoint
                        .authorizationRequestRepository(customAuthorizationRequestRepository)
                        .baseUri("/oauth2/authorization"))
        );

        /**
         * JWT 를 사용하면서 세션을 통한 로그인을 하지 않으므로 세션의 생성을 하지 않도록 설정
         */
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }
}
