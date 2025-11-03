package com.back.koreaTravelGuide.domain.auth.controller

import com.back.koreaTravelGuide.common.ApiResponse
import com.back.koreaTravelGuide.common.security.getUserId
import com.back.koreaTravelGuide.domain.auth.dto.request.UserRoleUpdateRequest
import com.back.koreaTravelGuide.domain.auth.dto.response.AccessTokenResponse
import com.back.koreaTravelGuide.domain.auth.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    @Value("\${jwt.refresh-token-expiration-days}") private val refreshTokenExpirationDays: Long,
    @Value("\${custom.cookie.secure}") private val cookieSecure: Boolean,
) {
    @PostMapping("/refresh")
    fun refreshAccessToken(
        @CookieValue("refreshToken") refreshToken: String,
        response: HttpServletResponse,
    ): ResponseEntity<ApiResponse<AccessTokenResponse>> {
        val (newAccessToken, newRefreshToken) = authService.refreshAccessToken(refreshToken)

        val cookie =
            ResponseCookie
                .from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(cookieSecure) // dev: false, prod: true
                // domain 제거: 크로스 도메인 쿠키를 위해 domain 속성 사용 안함
                .path("/")
                .maxAge(Duration.ofDays(refreshTokenExpirationDays))
                .sameSite(if (cookieSecure) "None" else "Lax")
                .build()

        response.addHeader("Set-Cookie", cookie.toString())

        return ResponseEntity.ok(ApiResponse("Access Token이 성공적으로 재발급되었습니다.", AccessTokenResponse(newAccessToken)))
    }

    @Operation(summary = "신규 사용자 역할 선택")
    @PostMapping("/role")
    fun updateUserRole(
        authentication: Authentication?,
        @RequestBody request: UserRoleUpdateRequest,
        response: HttpServletResponse,
    ): ResponseEntity<ApiResponse<AccessTokenResponse>> {
        val userId = authentication?.getUserId() ?: throw IllegalArgumentException("인증되지 않은 사용자입니다.")
        val (accessToken, refreshToken) = authService.updateRoleAndLogin(userId, request.role)

        val cookie =
            ResponseCookie
                .from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure) // dev: false, prod: true
                // domain 제거: 크로스 도메인 쿠키를 위해 domain 속성 사용 안함
                .path("/")
                .maxAge(Duration.ofDays(refreshTokenExpirationDays))
                .sameSite(if (cookieSecure) "None" else "Lax")
                .build()

        response.addHeader("Set-Cookie", cookie.toString())

        return ResponseEntity.ok(ApiResponse("역할이 선택되었으며 로그인에 성공했습니다.", AccessTokenResponse(accessToken = accessToken)))
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    fun logout(
        request: HttpServletRequest,
        @CookieValue(value = "refreshToken", required = false) refreshToken: String?,
        response: HttpServletResponse,
    ): ResponseEntity<ApiResponse<Unit>> {
        val accessToken =
            request.getHeader("Authorization")?.substring(7)
                ?: throw IllegalArgumentException("토큰이 없습니다.")

        authService.logout(accessToken, refreshToken)

        // refreshToken 쿠키 삭제 (maxAge=0)
        val expiredCookie =
            ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                // domain 제거: 크로스 도메인 쿠키를 위해 domain 속성 사용 안함
                .path("/")
                .maxAge(0) // 쿠키 즉시 만료
                .sameSite(if (cookieSecure) "None" else "Lax")
                .build()

        response.addHeader("Set-Cookie", expiredCookie.toString())

        return ResponseEntity.ok(ApiResponse("로그아웃 되었습니다."))
    }
}
