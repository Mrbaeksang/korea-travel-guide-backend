package com.back.koreaTravelGuide.domain.ai.tour.config

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

/**
 * content-type-id.yml 파일의 구조를 표현하는 데이터 클래스
 */
data class ContentTypeMapping(
    val tour: TourConfig,
) {
    data class TourConfig(
        @JsonProperty("content-type-id")
        val contentTypeId: ContentTypeId,
    ) {
        data class ContentTypeId(
            val mapping: Map<String, Map<String, String>>,
        )
    }
}

/**
 * content-type-id.yml 파일을 로드하고 contentTypeName + languageCode를 contentTypeId로 변환하는 설정 클래스
 */
@Configuration
class ContentTypeConfig {
    private lateinit var contentTypeMapping: ContentTypeMapping

    @PostConstruct
    fun loadContentTypeMapping() {
        val yamlMapper =
            ObjectMapper(YAMLFactory())
                .registerKotlinModule()
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val resource = ClassPathResource("content-type-id.yml")

        contentTypeMapping = yamlMapper.readValue(resource.inputStream)
    }

    /**
     * contentTypeName과 languageCode를 받아서 해당하는 contentTypeId를 반환한다.
     *
     * @param contentTypeName 관광 타입 이름 (예: "관광지", "문화시설", "음식점")
     * @param languageCode 언어 서비스 코드 (예: "KorService2", "EngService2")
     * @return contentTypeId (예: "12", "76")
     * @throws IllegalArgumentException contentTypeName 또는 languageCode가 유효하지 않을 경우
     */
    fun resolveContentTypeId(
        contentTypeName: String,
        languageCode: String,
    ): String {
        val typeMapping =
            contentTypeMapping.tour.contentTypeId.mapping[contentTypeName]
                ?: throw IllegalArgumentException(
                    "지원하지 않는 관광 타입입니다: $contentTypeName. " +
                        "사용 가능한 타입: ${contentTypeMapping.tour.contentTypeId.mapping.keys.joinToString(", ")}",
                )

        return typeMapping[languageCode]
            ?: throw IllegalArgumentException(
                "타입 '$contentTypeName'은(는) 언어 코드 '$languageCode'를 지원하지 않습니다. " +
                    "사용 가능한 언어 코드: ${typeMapping.keys.joinToString(", ")}",
            )
    }
}
