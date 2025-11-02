package com.back.koreaTravelGuide.domain.ai.tour.service.core

import com.back.koreaTravelGuide.domain.ai.tour.client.TourApiClient
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourLocationBasedParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourLocationBasedUseCase
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class TourLocationBasedServiceCore(
    private val tourApiClient: TourApiClient,
) : TourLocationBasedUseCase {
    @Cacheable(
        "tourLocationBased",
        key =
            "#tourParams.contentTypeId + '_' + #tourParams.areaCode + '_' + #tourParams.sigunguCode + '_' + " +
                "#locationParams.mapX + '_' + #locationParams.mapY + '_' + #locationParams.radius + '_' + " +
                "#languageCode",
        unless = "#result == null",
    )
    override fun fetchLocationBasedTours(
        tourParams: TourParams,
        locationParams: TourLocationBasedParams,
        languageCode: String,
    ): TourResponse {
        return tourApiClient.fetchLocationBasedTours(tourParams, locationParams, languageCode)
    }
}
