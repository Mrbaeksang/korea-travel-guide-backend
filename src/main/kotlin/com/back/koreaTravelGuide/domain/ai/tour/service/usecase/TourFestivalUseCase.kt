package com.back.koreaTravelGuide.domain.ai.tour.service.usecase

import com.back.koreaTravelGuide.domain.ai.tour.dto.TourFestivalParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse

interface TourFestivalUseCase {
    fun fetchFestivalInfo(
        festivalParams: TourFestivalParams,
        languageCode: String,
    ): TourResponse
}
