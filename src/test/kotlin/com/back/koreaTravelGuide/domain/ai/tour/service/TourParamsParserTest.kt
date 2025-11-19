package com.back.koreaTravelGuide.domain.ai.tour.service

import com.back.koreaTravelGuide.domain.ai.tour.config.ContentTypeConfig
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TourParamsParserTest {
    private lateinit var contentTypeConfig: ContentTypeConfig
    private lateinit var parser: TourParamsParser

    @BeforeEach
    fun setUp() {
        contentTypeConfig = mockk()
        parser = TourParamsParser(contentTypeConfig)
    }

    @DisplayName("parse - 공백이 섞인 입력을 정리해 DTO를 만든다")
    @Test
    fun parseTrimsTokens() {
        val languageCode = "KorService2"
        val contentTypeName = "관광지"
        every { contentTypeConfig.resolveContentTypeId(contentTypeName, languageCode) } returns "12"

        val result = parser.parse(contentTypeName, " 6 , 10 ", languageCode)

        assertEquals("12", result.contentTypeId)
        assertEquals("6", result.areaCode)
        assertEquals("10", result.sigunguCode)
    }

    @DisplayName("parse - 시군구 코드가 없으면 null 로 남긴다")
    @Test
    fun parseWhenSigunguMissing() {
        val languageCode = "KorService2"
        val contentTypeName = "문화시설"
        every { contentTypeConfig.resolveContentTypeId(contentTypeName, languageCode) } returns "15"

        val result = parser.parse(contentTypeName, "7", languageCode)

        assertEquals("15", result.contentTypeId)
        assertEquals("7", result.areaCode)
        assertNull(result.sigunguCode)
    }

    @DisplayName("parse - 콤마가 여러 번 등장하면 빈 문자열을 허용한다")
    @Test
    fun parseWhenCommaRepeated() {
        val languageCode = "KorService2"
        val contentTypeName = "레포츠"
        every { contentTypeConfig.resolveContentTypeId(contentTypeName, languageCode) } returns "32"

        val result = parser.parse(contentTypeName, "1,,2", languageCode)

        assertEquals("32", result.contentTypeId)
        assertEquals("1", result.areaCode)
        assertEquals("", result.sigunguCode)
    }

    @DisplayName("parse - 완전히 비어 있는 입력은 빈 문자열과 null 로 파싱된다")
    @Test
    fun parseWhenInputBlank() {
        val languageCode = "KorService2"
        val contentTypeName = "축제"
        every { contentTypeConfig.resolveContentTypeId(contentTypeName, languageCode) } returns "25"

        val result = parser.parse(contentTypeName, "", languageCode)

        assertEquals("25", result.contentTypeId)
        assertEquals("", result.areaCode)
        assertNull(result.sigunguCode)
    }
}
