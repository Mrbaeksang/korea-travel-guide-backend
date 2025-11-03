package com.back.koreaTravelGuide.domain.userChat.chatmessage.usecase

import org.springframework.context.annotation.Profile
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Profile("!rabbitmq") // RabbitMQ가 아닌 환경에서만 사용 (dev 로컬)
@Component
class SimpleChatMessagePublisher(
    private val messagingTemplate: SimpMessagingTemplate,
) : ChatMessagePublisher {
    override fun publishUserChat(
        roomId: Long,
        payload: Any,
    ) {
        messagingTemplate.convertAndSend("/topic/userchat/$roomId", payload)
    }
}
