package com.back.koreaTravelGuide.domain.userChat.chatmessage.usecase

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

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
