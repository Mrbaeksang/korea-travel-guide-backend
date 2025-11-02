package com.back.koreaTravelGuide.domain.ai.tour.service.core

import com.back.koreaTravelGuide.domain.ai.tour.client.TourApiClient
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourAreaBasedUseCase
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class TourAreaBasedServiceCore(
    private val tourApiClient: TourApiClient,
) : TourAreaBasedUseCase {
    @Cacheable(
        "tourAreaBased",
        key =
            "#tourParams.contentTypeId + '_' + #tourParams.areaCode + '_' + #tourParams.sigunguCode + '_' + #languageCode",
        unless = "#result == null",
    )
    override fun fetchAreaBasedTours(
        tourParams: TourParams,
        languageCode: String,
    ): TourResponse {
        return tourApiClient.fetchTourInfo(tourParams, languageCode)
    }
}
