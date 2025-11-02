package com.back.koreaTravelGuide.domain.ai.aiChat.service

import com.back.koreaTravelGuide.domain.ai.aiChat.entity.AiChatMessage
import com.back.koreaTravelGuide.domain.ai.aiChat.entity.AiChatSession
import com.back.koreaTravelGuide.domain.ai.aiChat.entity.SenderType
import com.back.koreaTravelGuide.domain.ai.aiChat.repository.AiChatMessageRepository
import com.back.koreaTravelGuide.domain.ai.aiChat.repository.AiChatSessionRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.client.ChatClient

/**
 * AiChatService 단위 테스트
 * Mock을 사용하여 실제 AI API 호출 없이 비즈니스 로직만 테스트
 *
 * 참고: sendMessage 메서드는 ChatClient의 복잡한 체이닝으로 인해
 * 통합 테스트(AiChatControllerTest)에서 검증합니다.
 */
@DisplayName("AiChatService 단위 테스트")
class AiChatServiceTest {
    private lateinit var aiChatMessageRepository: AiChatMessageRepository
    private lateinit var aiChatSessionRepository: AiChatSessionRepository
    private lateinit var chatClient: ChatClient
    private lateinit var aiChatService: AiChatService

    private val testUserId = 1L
    private val testSessionId = 100L
    private val otherUserId = 2L

    @BeforeEach
    fun setUp() {
        aiChatMessageRepository = mockk()
        aiChatSessionRepository = mockk()
        chatClient = mockk()
        aiChatService =
            AiChatService(
                aiChatMessageRepository,
                aiChatSessionRepository,
                chatClient,
            )
    }

    @Nested
    @DisplayName("세션 조회 테스트")
    inner class GetSessionsTest {
        @Test
        @DisplayName("사용자의 모든 세션을 생성일 역순으로 조회한다")
        fun `should return all sessions for user ordered by created date desc`() {
            // given
            val sessions =
                listOf(
                    createTestSession(id = 1L, userId = testUserId, title = "첫 번째 채팅"),
                    createTestSession(id = 2L, userId = testUserId, title = "두 번째 채팅"),
                )
            every { aiChatSessionRepository.findByUserIdOrderByCreatedAtDesc(testUserId) } returns sessions

            // when
            val result = aiChatService.getSessions(testUserId)

            // then
            assertThat(result).hasSize(2)
            assertThat(result).containsExactlyElementsOf(sessions)
            verify(exactly = 1) { aiChatSessionRepository.findByUserIdOrderByCreatedAtDesc(testUserId) }
        }

        @Test
        @DisplayName("세션이 없는 경우 빈 리스트를 반환한다")
        fun `should return empty list when user has no sessions`() {
            // given
            every { aiChatSessionRepository.findByUserIdOrderByCreatedAtDesc(testUserId) } returns emptyList()

            // when
            val result = aiChatService.getSessions(testUserId)

            // then
            assertThat(result).isEmpty()
        }
    }

    @Nested
    @DisplayName("세션 생성 테스트")
    inner class CreateSessionTest {
        @Test
        @DisplayName("새로운 세션을 생성한다")
        fun `should create new session with default title`() {
            // given
            val newSession = createTestSession(id = testSessionId, userId = testUserId, title = "새로운 채팅방")
            val sessionSlot = slot<AiChatSession>()
            every { aiChatSessionRepository.save(capture(sessionSlot)) } returns newSession

            // when
            val result = aiChatService.createSession(testUserId)

            // then
            assertThat(result.userId).isEqualTo(testUserId)
            assertThat(result.sessionTitle).isEqualTo("새로운 채팅방")
            assertThat(sessionSlot.captured.userId).isEqualTo(testUserId)
            assertThat(sessionSlot.captured.sessionTitle).isEqualTo("새로운 채팅방")
            verify(exactly = 1) { aiChatSessionRepository.save(any()) }
        }
    }

    @Nested
    @DisplayName("세션 삭제 테스트")
    inner class DeleteSessionTest {
        @Test
        @DisplayName("소유자가 세션을 삭제한다")
        fun `should delete session when user is owner`() {
            // given
            val session = createTestSession(id = testSessionId, userId = testUserId)
            every { aiChatSessionRepository.findByIdAndUserId(testSessionId, testUserId) } returns session
            every { aiChatSessionRepository.deleteById(testSessionId) } just runs

            // when
            aiChatService.deleteSession(testSessionId, testUserId)

            // then
            verify(exactly = 1) { aiChatSessionRepository.findByIdAndUserId(testSessionId, testUserId) }
            verify(exactly = 1) { aiChatSessionRepository.deleteById(testSessionId) }
        }

        @Test
        @DisplayName("소유자가 아닌 경우 예외를 발생시킨다")
        fun `should throw exception when user is not owner`() {
            // given
            every { aiChatSessionRepository.findByIdAndUserId(testSessionId, otherUserId) } returns null

            // when & then
            assertThatThrownBy {
                aiChatService.deleteSession(testSessionId, otherUserId)
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("해당 채팅방이 없거나 접근 권한이 없습니다.")

            verify(exactly = 0) { aiChatSessionRepository.deleteById(any()) }
        }

        @Test
        @DisplayName("존재하지 않는 세션 삭제 시 예외를 발생시킨다")
        fun `should throw exception when session does not exist`() {
            // given
            every { aiChatSessionRepository.findByIdAndUserId(999L, testUserId) } returns null

            // when & then
            assertThatThrownBy {
                aiChatService.deleteSession(999L, testUserId)
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("해당 채팅방이 없거나 접근 권한이 없습니다.")
        }
    }

    @Nested
    @DisplayName("메시지 조회 테스트")
    inner class GetSessionMessagesTest {
        @Test
        @DisplayName("세션의 모든 메시지를 시간순으로 조회한다")
        fun `should return all messages for session ordered by created date`() {
            // given
            val session = createTestSession(id = testSessionId, userId = testUserId)
            val messages =
                listOf(
                    createTestMessage(id = 1L, session = session, senderType = SenderType.USER, content = "안녕하세요"),
                    createTestMessage(id = 2L, session = session, senderType = SenderType.AI, content = "안녕하세요! 무엇을 도와드릴까요?"),
                )
            every { aiChatSessionRepository.findByIdAndUserId(testSessionId, testUserId) } returns session
            every { aiChatMessageRepository.findByAiChatSessionIdOrderByCreatedAtAsc(testSessionId) } returns messages

            // when
            val result = aiChatService.getSessionMessages(testSessionId, testUserId)

            // then
            assertThat(result).hasSize(2)
            assertThat(result[0].senderType).isEqualTo(SenderType.USER)
            assertThat(result[1].senderType).isEqualTo(SenderType.AI)
        }

        @Test
        @DisplayName("소유자가 아닌 경우 예외를 발생시킨다")
        fun `should throw exception when user is not owner`() {
            // given
            every { aiChatSessionRepository.findByIdAndUserId(testSessionId, otherUserId) } returns null

            // when & then
            assertThatThrownBy {
                aiChatService.getSessionMessages(testSessionId, otherUserId)
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("해당 채팅방이 없거나 접근 권한이 없습니다.")
        }
    }

    // sendMessage 테스트는 통합 테스트에서 검증 (AiChatControllerTest)

    @Nested
    @DisplayName("세션 제목 수정 테스트")
    inner class UpdateSessionTitleTest {
        @Test
        @DisplayName("세션 제목을 수정한다")
        fun `should update session title`() {
            // given
            val session = createTestSession(id = testSessionId, userId = testUserId, title = "이전 제목")
            val newTitle = "새로운 제목"

            every { aiChatSessionRepository.findByIdAndUserId(testSessionId, testUserId) } returns session
            every { aiChatSessionRepository.save(any()) } returnsArgument 0

            // when
            val result = aiChatService.updateSessionTitle(testSessionId, testUserId, newTitle)

            // then
            assertThat(result.sessionTitle).isEqualTo(newTitle)
            verify(exactly = 1) { aiChatSessionRepository.save(any()) }
        }

        @Test
        @DisplayName("제목이 100자를 초과하면 자른다")
        fun `should truncate title to 100 characters`() {
            // given
            val session = createTestSession(id = testSessionId, userId = testUserId)
            val longTitle = "a".repeat(150)

            every { aiChatSessionRepository.findByIdAndUserId(testSessionId, testUserId) } returns session
            every { aiChatSessionRepository.save(any()) } returnsArgument 0

            // when
            val result = aiChatService.updateSessionTitle(testSessionId, testUserId, longTitle)

            // then
            assertThat(result.sessionTitle).hasSize(100)
        }

        @Test
        @DisplayName("제목의 앞뒤 공백을 제거한다")
        fun `should trim whitespace from title`() {
            // given
            val session = createTestSession(id = testSessionId, userId = testUserId)
            val titleWithSpaces = "  제목 테스트  "

            every { aiChatSessionRepository.findByIdAndUserId(testSessionId, testUserId) } returns session
            every { aiChatSessionRepository.save(any()) } returnsArgument 0

            // when
            val result = aiChatService.updateSessionTitle(testSessionId, testUserId, titleWithSpaces)

            // then
            assertThat(result.sessionTitle).isEqualTo("제목 테스트")
        }

        @Test
        @DisplayName("소유자가 아닌 경우 예외를 발생시킨다")
        fun `should throw exception when user is not owner`() {
            // given
            every { aiChatSessionRepository.findByIdAndUserId(testSessionId, otherUserId) } returns null

            // when & then
            assertThatThrownBy {
                aiChatService.updateSessionTitle(testSessionId, otherUserId, "새 제목")
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("해당 채팅방이 없거나 접근 권한이 없습니다.")
        }
    }

    // 테스트 헬퍼 메서드
    private fun createTestSession(
        id: Long,
        userId: Long,
        title: String = "테스트 채팅방",
    ): AiChatSession {
        return AiChatSession(id = id, userId = userId, sessionTitle = title)
    }

    private fun createTestMessage(
        id: Long,
        session: AiChatSession,
        senderType: SenderType,
        content: String,
    ): AiChatMessage {
        return AiChatMessage(
            id = id,
            aiChatSession = session,
            senderType = senderType,
            content = content,
        )
    }
}
