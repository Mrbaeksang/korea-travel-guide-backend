package com.back.koreaTravelGuide.domain.ai.tour.service.core

import com.back.koreaTravelGuide.domain.ai.tour.client.TourApiClient
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailResponse
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourDetailUseCase
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class TourDetailServiceCore(
    private val tourApiClient: TourApiClient,
) : TourDetailUseCase {
    @Cacheable(
        "tourDetail",
        key = "#detailParams.contentId + '_' + #languageCode",
        unless = "#result == null",
    )
    override fun fetchTourDetail(
        detailParams: TourDetailParams,
        languageCode: String,
    ): TourDetailResponse {
        return tourApiClient.fetchTourDetail(detailParams, languageCode)
    }
}
