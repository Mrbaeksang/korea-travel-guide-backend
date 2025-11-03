package com.back.koreaTravelGuide.common.security

import com.back.koreaTravelGuide.common.config.AppConfig
import com.back.koreaTravelGuide.domain.user.enums.UserRole
import com.back.koreaTravelGuide.domain.user.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class CustomOAuth2LoginSuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    @Value("\${jwt.refresh-token-expiration-days}") private val refreshTokenExpirationDays: Long,
    @Value("\${custom.cookie.secure}") private val cookieSecure: Boolean,
    private val appConfig: AppConfig,
) : SimpleUrlAuthenticationSuccessHandler() {
    @Transactional
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val customUser = authentication.principal as CustomOAuth2User

        val email = customUser.email

        val user =
            userRepository.findByEmail(email)
                ?: throw IllegalStateException("OAuth 인증 후 사용자를 찾을 수 없습니다. email: $email")

        if (user.role == UserRole.PENDING) {
            val registerToken = jwtTokenProvider.createRegisterToken(user.id!!)

            val targetUrl = "${AppConfig.siteFrontUrl}/signup/role?token=$registerToken"

            redirectStrategy.sendRedirect(request, response, targetUrl)
        } else {
            println("=== [CustomOAuth2LoginSuccessHandler] 기존 사용자 로그인 ===")
            println("User ID: ${user.id}, Role: ${user.role}")
            println("cookieSecure: $cookieSecure")

            val refreshToken = jwtTokenProvider.createRefreshToken(user.id!!)
            println("RefreshToken 생성 완료: ${refreshToken.substring(0, 20)}...")

            val redisKey = "refreshToken:${user.id}"
            redisTemplate.opsForValue().set(redisKey, refreshToken, refreshTokenExpirationDays, TimeUnit.DAYS)
            println("Redis에 RefreshToken 저장 완료")

            // Spring ResponseCookie로 SameSite 속성 지원
            val cookie =
                ResponseCookie
                    .from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(cookieSecure) // dev: false, prod: true
                    // domain 제거: 크로스 도메인 쿠키를 위해 domain 속성 사용 안함
                    .path("/")
                    .maxAge(Duration.ofDays(refreshTokenExpirationDays))
                    .sameSite(if (cookieSecure) "None" else "Lax") // prod: None+Secure, dev: Lax
                    .build()

            val cookieString = cookie.toString()
            println("Cookie 생성: $cookieString")

            response.addHeader("Set-Cookie", cookieString)
            println("Set-Cookie 헤더 추가 완료")

            val targetUrl = "${AppConfig.siteFrontUrl}/oauth/callback"
            println("리다이렉트 URL: $targetUrl")

            redirectStrategy.sendRedirect(request, response, targetUrl)
            println("=== [CustomOAuth2LoginSuccessHandler] 완료 ===")
        }
    }
}
