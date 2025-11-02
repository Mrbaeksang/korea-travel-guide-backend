package com.back.koreaTravelGuide.domain.ai.aiChat.repository

import com.back.koreaTravelGuide.domain.ai.aiChat.entity.AiChatMessage
import com.back.koreaTravelGuide.domain.ai.aiChat.entity.AiChatSession
import com.back.koreaTravelGuide.domain.ai.aiChat.entity.SenderType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles

/**
 * AiChat Repository 통합 테스트
 * 실제 DB(H2)를 사용하여 Repository 레이어를 테스트
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("AiChat Repository 통합 테스트")
class AiChatRepositoryTest {
    @Autowired
    private lateinit var aiChatSessionRepository: AiChatSessionRepository

    @Autowired
    private lateinit var aiChatMessageRepository: AiChatMessageRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    private val testUserId = 1L
    private val otherUserId = 2L

    @BeforeEach
    fun setUp() {
        // 테스트 데이터 정리
        aiChatMessageRepository.deleteAll()
        aiChatSessionRepository.deleteAll()
        entityManager.flush()
        entityManager.clear()
    }

    @Nested
    @DisplayName("AiChatSessionRepository 테스트")
    inner class SessionRepositoryTest {
        @Test
        @DisplayName("사용자 ID로 세션을 생성일 역순으로 조회한다")
        fun `should find sessions by userId ordered by created date desc`() {
            // given
            val session1 = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "첫 번째"))
            Thread.sleep(10) // createdAt 차이를 위한 대기
            val session2 = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "두 번째"))
            Thread.sleep(10)
            val session3 = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "세 번째"))

            aiChatSessionRepository.save(AiChatSession(userId = otherUserId, sessionTitle = "다른 사용자"))
            entityManager.flush()
            entityManager.clear()

            // when
            val result = aiChatSessionRepository.findByUserIdOrderByCreatedAtDesc(testUserId)

            // then
            assertThat(result).hasSize(3)
            assertThat(result[0].id).isEqualTo(session3.id)
            assertThat(result[1].id).isEqualTo(session2.id)
            assertThat(result[2].id).isEqualTo(session1.id)
        }

        @Test
        @DisplayName("세션 ID와 사용자 ID로 세션을 조회한다")
        fun `should find session by id and userId`() {
            // given
            val session = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "테스트"))
            entityManager.flush()
            entityManager.clear()

            // when
            val result = aiChatSessionRepository.findByIdAndUserId(session.id!!, testUserId)

            // then
            assertThat(result).isNotNull
            assertThat(result?.userId).isEqualTo(testUserId)
            assertThat(result?.sessionTitle).isEqualTo("테스트")
        }

        @Test
        @DisplayName("다른 사용자의 세션 조회 시 null을 반환한다")
        fun `should return null when querying another user's session`() {
            // given
            val session = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "테스트"))
            entityManager.flush()

            // when
            val result = aiChatSessionRepository.findByIdAndUserId(session.id!!, otherUserId)

            // then
            assertThat(result).isNull()
        }

        @Test
        @DisplayName("존재하지 않는 세션 조회 시 null을 반환한다")
        fun `should return null when session does not exist`() {
            // when
            val result = aiChatSessionRepository.findByIdAndUserId(999L, testUserId)

            // then
            assertThat(result).isNull()
        }

        @Test
        @DisplayName("세션 제목을 수정한다")
        fun `should update session title`() {
            // given
            val session = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "원래 제목"))
            entityManager.flush()
            entityManager.clear()

            // when
            val foundSession = aiChatSessionRepository.findById(session.id!!).get()
            foundSession.sessionTitle = "수정된 제목"
            aiChatSessionRepository.save(foundSession)
            entityManager.flush()
            entityManager.clear()

            // then
            val updatedSession = aiChatSessionRepository.findById(session.id!!).get()
            assertThat(updatedSession.sessionTitle).isEqualTo("수정된 제목")
        }

        @Test
        @DisplayName("세션 삭제 시 연관된 메시지도 함께 삭제된다 (cascade)")
        fun `should cascade delete messages when session is deleted`() {
            // given
            val session = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "테스트"))
            val message1 =
                aiChatMessageRepository.save(
                    AiChatMessage(
                        aiChatSession = session,
                        senderType = SenderType.USER,
                        content = "메시지 1",
                    ),
                )
            val message2 =
                aiChatMessageRepository.save(
                    AiChatMessage(
                        aiChatSession = session,
                        senderType = SenderType.AI,
                        content = "메시지 2",
                    ),
                )
            entityManager.flush()
            entityManager.clear()

            // when
            aiChatSessionRepository.deleteById(session.id!!)
            entityManager.flush()
            entityManager.clear()

            // then
            assertThat(aiChatSessionRepository.findById(session.id!!)).isEmpty
            assertThat(aiChatMessageRepository.findById(message1.id!!)).isEmpty
            assertThat(aiChatMessageRepository.findById(message2.id!!)).isEmpty
        }
    }

    @Nested
    @DisplayName("AiChatMessageRepository 테스트")
    inner class MessageRepositoryTest {
        @Test
        @DisplayName("세션 ID로 메시지를 생성일 순으로 조회한다")
        fun `should find messages by sessionId ordered by created date asc`() {
            // given
            val session = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "테스트"))

            val message1 =
                aiChatMessageRepository.save(
                    AiChatMessage(
                        aiChatSession = session,
                        senderType = SenderType.USER,
                        content = "첫 번째 메시지",
                    ),
                )
            Thread.sleep(10)
            val message2 =
                aiChatMessageRepository.save(
                    AiChatMessage(
                        aiChatSession = session,
                        senderType = SenderType.AI,
                        content = "두 번째 메시지",
                    ),
                )
            Thread.sleep(10)
            val message3 =
                aiChatMessageRepository.save(
                    AiChatMessage(
                        aiChatSession = session,
                        senderType = SenderType.USER,
                        content = "세 번째 메시지",
                    ),
                )

            entityManager.flush()
            entityManager.clear()

            // when
            val result = aiChatMessageRepository.findByAiChatSessionIdOrderByCreatedAtAsc(session.id!!)

            // then
            assertThat(result).hasSize(3)
            assertThat(result[0].id).isEqualTo(message1.id)
            assertThat(result[1].id).isEqualTo(message2.id)
            assertThat(result[2].id).isEqualTo(message3.id)
            assertThat(result[0].senderType).isEqualTo(SenderType.USER)
            assertThat(result[1].senderType).isEqualTo(SenderType.AI)
        }

        @Test
        @DisplayName("세션 ID로 메시지 개수를 조회한다")
        fun `should count messages by sessionId`() {
            // given
            val session = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "테스트"))

            aiChatMessageRepository.save(
                AiChatMessage(
                    aiChatSession = session,
                    senderType = SenderType.USER,
                    content = "메시지 1",
                ),
            )
            aiChatMessageRepository.save(
                AiChatMessage(
                    aiChatSession = session,
                    senderType = SenderType.AI,
                    content = "메시지 2",
                ),
            )
            aiChatMessageRepository.save(
                AiChatMessage(
                    aiChatSession = session,
                    senderType = SenderType.USER,
                    content = "메시지 3",
                ),
            )

            entityManager.flush()

            // when
            val count = aiChatMessageRepository.countByAiChatSessionId(session.id!!)

            // then
            assertThat(count).isEqualTo(3)
        }

        @Test
        @DisplayName("메시지가 없는 세션의 개수는 0이다")
        fun `should return zero count for session with no messages`() {
            // given
            val session = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "빈 세션"))
            entityManager.flush()

            // when
            val count = aiChatMessageRepository.countByAiChatSessionId(session.id!!)

            // then
            assertThat(count).isEqualTo(0)
        }

        @Test
        @DisplayName("메시지 내용과 발신자 타입을 저장한다")
        fun `should save message with content and sender type`() {
            // given
            val session = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "테스트"))
            val message =
                AiChatMessage(
                    aiChatSession = session,
                    senderType = SenderType.USER,
                    content = "테스트 메시지 내용",
                )

            // when
            val savedMessage = aiChatMessageRepository.save(message)
            entityManager.flush()
            entityManager.clear()

            // then
            val foundMessage = aiChatMessageRepository.findById(savedMessage.id!!).get()
            assertThat(foundMessage.content).isEqualTo("테스트 메시지 내용")
            assertThat(foundMessage.senderType).isEqualTo(SenderType.USER)
            assertThat(foundMessage.aiChatSession.id).isEqualTo(session.id)
        }

        @Test
        @DisplayName("여러 세션의 메시지가 섞이지 않는다")
        fun `should not mix messages from different sessions`() {
            // given
            val session1 = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "세션1"))
            val session2 = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "세션2"))

            aiChatMessageRepository.save(
                AiChatMessage(
                    aiChatSession = session1,
                    senderType = SenderType.USER,
                    content = "세션1 메시지",
                ),
            )
            aiChatMessageRepository.save(
                AiChatMessage(
                    aiChatSession = session2,
                    senderType = SenderType.USER,
                    content = "세션2 메시지",
                ),
            )

            entityManager.flush()

            // when
            val session1Messages = aiChatMessageRepository.findByAiChatSessionIdOrderByCreatedAtAsc(session1.id!!)
            val session2Messages = aiChatMessageRepository.findByAiChatSessionIdOrderByCreatedAtAsc(session2.id!!)

            // then
            assertThat(session1Messages).hasSize(1)
            assertThat(session2Messages).hasSize(1)
            assertThat(session1Messages[0].content).isEqualTo("세션1 메시지")
            assertThat(session2Messages[0].content).isEqualTo("세션2 메시지")
        }
    }

    @Nested
    @DisplayName("엔티티 관계 테스트")
    inner class EntityRelationshipTest {
        @Test
        @DisplayName("세션과 메시지는 양방향 관계를 유지한다")
        fun `should maintain bidirectional relationship between session and messages`() {
            // given
            val session = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "테스트"))

            val message =
                AiChatMessage(
                    aiChatSession = session,
                    senderType = SenderType.USER,
                    content = "테스트",
                )
            aiChatMessageRepository.save(message)

            entityManager.flush()
            entityManager.clear()

            // when
            val foundSession = aiChatSessionRepository.findById(session.id!!).get()
            val foundMessage = aiChatMessageRepository.findById(message.id!!).get()

            // then
            assertThat(foundMessage.aiChatSession.id).isEqualTo(foundSession.id)
            assertThat(foundMessage.aiChatSession.sessionTitle).isEqualTo("테스트")
        }

        @Test
        @DisplayName("createdAt이 자동으로 설정된다")
        fun `should automatically set createdAt`() {
            // given & when
            val session = aiChatSessionRepository.save(AiChatSession(userId = testUserId, sessionTitle = "테스트"))
            entityManager.flush()

            // then
            assertThat(session.createdAt).isNotNull
        }
    }
}
