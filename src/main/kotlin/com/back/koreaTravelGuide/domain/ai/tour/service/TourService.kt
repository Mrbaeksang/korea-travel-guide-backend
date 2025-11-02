package com.back.koreaTravelGuide.domain.ai.tour.service

import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailResponse
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourFestivalParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourLocationBasedParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourStayParams
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourAreaBasedUseCase
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourDetailUseCase
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourFestivalUseCase
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourLocationBasedUseCase
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourStayUseCase
import org.springframework.stereotype.Service

// 09.26 양현준
@Service
class TourService(
    private val tourAreaBasedUseCase: TourAreaBasedUseCase,
    private val tourLocationBasedUseCase: TourLocationBasedUseCase,
    private val tourDetailUseCase: TourDetailUseCase,
    private val tourFestivalUseCase: TourFestivalUseCase,
    private val tourStayUseCase: TourStayUseCase,
    private val tourParamsParser: TourParamsParser,
) {
    /**
     * contentTypeName과 languageCode를 기반으로 TourParams를 생성한다.
     * contentTypeName은 언어에 맞는 contentTypeId로 자동 변환된다.
     */
    fun parseParams(
        contentTypeName: String,
        areaAndSigunguCode: String,
        languageCode: String,
    ): TourParams {
        return tourParamsParser.parse(contentTypeName, areaAndSigunguCode, languageCode)
    }

    /**
     * 지역 기반 관광 정보를 조회한다.
     * languageCode는 AI가 사용자의 대화 언어를 파악하여 전달한다.
     */
    fun fetchTours(
        tourParams: TourParams,
        languageCode: String,
    ): TourResponse {
        return tourAreaBasedUseCase.fetchAreaBasedTours(tourParams, languageCode)
    }

    /**
     * 위치 기반 관광 정보를 조회한다.
     * languageCode는 AI가 사용자의 대화 언어를 파악하여 전달한다.
     */
    fun fetchLocationBasedTours(
        tourParams: TourParams,
        locationParams: TourLocationBasedParams,
        languageCode: String,
    ): TourResponse {
        return tourLocationBasedUseCase.fetchLocationBasedTours(tourParams, locationParams, languageCode)
    }

    /**
     * 관광지 상세 정보를 조회한다.
     * languageCode는 AI가 사용자의 대화 언어를 파악하여 전달한다.
     */
    fun fetchTourDetail(
        detailParams: TourDetailParams,
        languageCode: String,
    ): TourDetailResponse {
        return tourDetailUseCase.fetchTourDetail(detailParams, languageCode)
    }

    /**
     * 축제 정보를 조회한다.
     * languageCode는 AI가 사용자의 대화 언어를 파악하여 전달한다.
     */
    fun fetchFestivalInfo(
        festivalParams: TourFestivalParams,
        languageCode: String,
    ): TourResponse {
        return tourFestivalUseCase.fetchFestivalInfo(festivalParams, languageCode)
    }

    /**
     * 숙박 정보를 조회한다. (베니키아, 한옥, 굿스테이)
     * languageCode는 AI가 사용자의 대화 언어를 파악하여 전달한다.
     */
    fun fetchStayInfo(
        stayParams: TourStayParams,
        languageCode: String,
    ): TourResponse {
        return tourStayUseCase.fetchStayInfo(stayParams, languageCode)
    }
}
