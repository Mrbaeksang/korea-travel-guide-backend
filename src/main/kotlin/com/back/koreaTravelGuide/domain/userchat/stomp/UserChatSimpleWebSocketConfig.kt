package com.back.koreaTravelGuide.domain.userChat.stomp

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Profile("!prod") // 개발 환경에서 SimpleBroker 사용 (로컬)
@Configuration
@EnableWebSocketMessageBroker
class UserChatSimpleWebSocketConfig(
    private val userChatStompAuthChannelInterceptor: UserChatStompAuthChannelInterceptor,
) : WebSocketMessageBrokerConfigurer {
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws/userchat")
            .setAllowedOrigins(
                "http://localhost:3000",
                "http://localhost:63342",
                com.back.koreaTravelGuide.common.config.AppConfig.siteFrontUrl,
            )
            .withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.setApplicationDestinationPrefixes("/pub")
        registry.enableSimpleBroker("/topic")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(userChatStompAuthChannelInterceptor)
    }
}
