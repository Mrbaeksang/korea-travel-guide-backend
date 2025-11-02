package com.back.koreaTravelGuide.domain.ai.tour.service.core

import com.back.koreaTravelGuide.domain.ai.tour.client.TourApiClient
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourStayParams
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourStayUseCase
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class TourStayServiceCore(
    private val tourApiClient: TourApiClient,
) : TourStayUseCase {
    @Cacheable(
        "tourStay",
        key =
            "#stayParams.areaCode + '_' + #stayParams.sigunguCode + '_' + " +
                "#stayParams.modifiedtime + '_' + #stayParams.arrange + '_' + #languageCode",
        unless = "#result == null",
    )
    override fun fetchStayInfo(
        stayParams: TourStayParams,
        languageCode: String,
    ): TourResponse {
        return tourApiClient.fetchStayInfo(stayParams, languageCode)
    }
}
