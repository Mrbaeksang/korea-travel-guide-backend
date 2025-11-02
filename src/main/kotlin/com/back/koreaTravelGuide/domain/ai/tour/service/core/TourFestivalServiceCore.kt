package com.back.koreaTravelGuide.domain.ai.tour.service.core

import com.back.koreaTravelGuide.domain.ai.tour.client.TourApiClient
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourFestivalParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourFestivalUseCase
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class TourFestivalServiceCore(
    private val tourApiClient: TourApiClient,
) : TourFestivalUseCase {
    @Cacheable(
        "tourFestival",
        key =
            "#festivalParams.eventStartDate + '_' + #festivalParams.eventEndDate + '_' + " +
                "#festivalParams.areaCode + '_' + #festivalParams.sigunguCode + '_' + #languageCode",
        unless = "#result == null",
    )
    override fun fetchFestivalInfo(
        festivalParams: TourFestivalParams,
        languageCode: String,
    ): TourResponse {
        return tourApiClient.fetchFestivalInfo(festivalParams, languageCode)
    }
}
