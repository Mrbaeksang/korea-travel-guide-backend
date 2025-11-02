package com.back.koreaTravelGuide.domain.ai.tour.service

import com.back.koreaTravelGuide.domain.ai.tour.config.ContentTypeConfig
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import org.springframework.stereotype.Component

@Component
class TourParamsParser(
    private val contentTypeConfig: ContentTypeConfig,
) {
    /**
     * contentTypeName과 languageCode를 기반으로 TourParams를 생성한다.
     * contentTypeName은 contentTypeConfig를 통해 적절한 contentTypeId로 변환된다.
     *
     * @param contentTypeName 관광 타입 이름 (예: "관광지", "문화시설", "음식점")
     * @param areaAndSigunguCode 지역 코드 (예: "1,24" 또는 "1-24")
     * @param languageCode 언어 서비스 코드 (예: "KorService2", "EngService2")
     * @return TourParams 객체
     */
    fun parse(
        contentTypeName: String,
        areaAndSigunguCode: String,
        languageCode: String,
    ): TourParams {
        // contentTypeName + languageCode → contentTypeId 변환
        val contentTypeId = contentTypeConfig.resolveContentTypeId(contentTypeName, languageCode)

        // 하이픈(-) 또는 쉼표(,) 둘 다 처리
        val codes = areaAndSigunguCode.split(",", "-").map { it.trim() }

        val areaCode = codes.getOrNull(0)
        val sigunguCode = codes.getOrNull(1)

        return TourParams(
            contentTypeId = contentTypeId,
            areaCode = areaCode,
            sigunguCode = sigunguCode,
        )
    }
}
