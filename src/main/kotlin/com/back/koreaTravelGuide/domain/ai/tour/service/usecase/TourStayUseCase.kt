package com.back.koreaTravelGuide.domain.ai.tour.service.usecase

import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourStayParams

interface TourStayUseCase {
    fun fetchStayInfo(
        stayParams: TourStayParams,
        languageCode: String,
    ): TourResponse
}
