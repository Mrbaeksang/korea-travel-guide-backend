package com.back.koreaTravelGuide.common.security

import com.back.koreaTravelGuide.common.config.AppConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableMethodSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val customOAuth2LoginSuccessHandler: CustomOAuth2LoginSuccessHandler,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val environment: Environment,
    private val appConfig: AppConfig,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val activeProfiles = environment.activeProfiles
        val defaultProfiles = environment.defaultProfiles
        val isDev = activeProfiles.contains("dev") || (activeProfiles.isEmpty() && defaultProfiles.contains("dev"))

        http {
            csrf { disable() }
            cors { }
            formLogin { disable() }
            httpBasic { disable() }
            logout { disable() }

            headers {
                if (isDev) {
                    frameOptions { disable() }
                } else {
                    frameOptions { sameOrigin }
                    // XSS 보호 헤더
                    xssProtection { }
                    contentTypeOptions { }
                    // HSTS (HTTPS Strict Transport Security)
                    httpStrictTransportSecurity {
                        includeSubDomains = true
                        maxAgeInSeconds = 31536000 // 1년
                    }
                    // Content Security Policy
                    contentSecurityPolicy {
                        policyDirectives =
                            "default-src 'self'; " +
                            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                            "style-src 'self' 'unsafe-inline'; " +
                            "img-src 'self' data: https:; " +
                            "font-src 'self' data:; " +
                            "connect-src 'self' https://korea-travel-guide.vercel.app " +
                            "https://openrouter.ai https://apihub.kma.go.kr https://apis.data.go.kr; " +
                            "frame-ancestors 'none'"
                    }
                    // Referrer Policy
                    referrerPolicy {
                        policy =
                            org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy
                                .STRICT_ORIGIN_WHEN_CROSS_ORIGIN
                    }
                    // Permissions Policy (Feature Policy)
                    permissionsPolicy {
                        policy = "geolocation=(), microphone=(), camera=()"
                    }
                }
            }

            sessionManagement {
                sessionCreationPolicy =
                    if (isDev) {
                        SessionCreationPolicy.IF_REQUIRED
                    } else {
                        SessionCreationPolicy.STATELESS
                    }
            }

            oauth2Login {
                userInfoEndpoint {
                    userService = customOAuth2UserService
                }
                authenticationSuccessHandler = customOAuth2LoginSuccessHandler
            }

            authorizeHttpRequests {
                authorize("/h2-console/**", permitAll)
                authorize("/swagger-ui.html", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize("/api-docs/**", permitAll)
                authorize("/webjars/swagger-ui/**", permitAll)
                authorize("/api/auth/**", permitAll)
                authorize("/actuator/health", permitAll)
                authorize("/favicon.ico", permitAll)
                // WebSocket 핸드셰이크 허용 - STOMP 인증은 UserChatStompAuthChannelInterceptor에서 처리
                authorize("/ws/**", permitAll)
                if (isDev) {
                    authorize(anyRequest, permitAll)
                } else {
                    authorize(anyRequest, authenticated)
                }
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
        }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration(
                "/**",
                CorsConfiguration().apply {
                    allowedOriginPatterns =
                        listOf(
                            "http://localhost:3000",
                            "http://localhost:63342",
                            AppConfig.siteFrontUrl,
                        )
                    allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    allowedHeaders = listOf("*")
                    allowCredentials = true
                    maxAge = 3600
                },
            )
        }
    }
}
