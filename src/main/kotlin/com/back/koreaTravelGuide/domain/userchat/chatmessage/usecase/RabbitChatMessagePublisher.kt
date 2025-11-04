package com.back.koreaTravelGuide.domain.userChat.chatmessage.usecase

import org.springframework.context.annotation.Profile
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

// 프로덕션 환경에서 RabbitMQ STOMP Broker Relay 사용
@Profile("prod")
@Component
class RabbitChatMessagePublisher(
    private val messagingTemplate: SimpMessagingTemplate,
) : ChatMessagePublisher {
    override fun publishUserChat(
        roomId: Long,
        payload: Any,
    ) {
        // STOMP Broker Relay를 통해 RabbitMQ로 메시지 발행
        // RabbitMQ AMQP topic exchange는 routing key에 점(.)만 허용 (슬래시 금지)
        messagingTemplate.convertAndSend("/topic/userchat.$roomId", payload)
    }
}
