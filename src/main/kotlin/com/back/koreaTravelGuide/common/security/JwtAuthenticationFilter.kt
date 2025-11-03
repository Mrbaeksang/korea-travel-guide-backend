package com.back.koreaTravelGuide.common.security

import com.back.koreaTravelGuide.common.ApiResponse
import com.back.koreaTravelGuide.common.logging.log
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = resolveToken(request)

        if (token != null) {
            val isBlacklisted =
                try {
                    redisTemplate.opsForValue().get("blacklist:$token") != null
                } catch (e: Exception) {
                    log.debug("Redis 블랙리스트 체크 실패 (Redis 미사용 환경): ${e.message}")
                    false
                }

            if (isBlacklisted) {
                sendUnauthorizedResponse(response, "토큰이 무효화되었습니다")
                return
            }

            if (!jwtTokenProvider.validateToken(token)) {
                sendUnauthorizedResponse(response, "토큰이 만료되었거나 유효하지 않습니다")
                return
            }

            val authentication = jwtTokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")

        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }

    private fun sendUnauthorizedResponse(
        response: HttpServletResponse,
        message: String,
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"

        val apiResponse = ApiResponse<Void>(message)
        response.writer.write(objectMapper.writeValueAsString(apiResponse))
        response.writer.flush()
    }
}
