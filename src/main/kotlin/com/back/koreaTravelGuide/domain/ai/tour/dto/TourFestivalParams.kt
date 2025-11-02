package com.back.koreaTravelGuide.domain.ai.tour.dto

data class TourFestivalParams(
    val eventStartDate: String,
    val eventEndDate: String? = null,
    val areaCode: String? = null,
    val sigunguCode: String? = null,
)
