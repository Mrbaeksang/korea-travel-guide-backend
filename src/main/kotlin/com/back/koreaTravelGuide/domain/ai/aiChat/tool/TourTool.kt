package com.back.koreaTravelGuide.domain.ai.aiChat.tool

import com.back.backend.BuildConfig
import com.back.koreaTravelGuide.common.logging.log
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourFestivalParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourLocationBasedParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourStayParams
import com.back.koreaTravelGuide.domain.ai.tour.service.TourService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Component

/**
 * 한국관광공사 Tour API 연동 Tool
 *
 * Spring AI의 Function Calling을 통해 AI가 자동으로 호출하는 도구들입니다.
 * 모든 함수는 KorService2(한국어 서비스)만 사용하며, AI가 받은 데이터를 사용자 언어로 번역합니다.
 *
 * 데이터 흐름:
 * 1. AI가 사용자 요청 분석 → contentTypeName(한국어) 추출
 * 2. Tool 호출: contentTypeName + areaCode
 * 3. TourService.parseParams(): contentTypeName → contentTypeId 변환 (ContentTypeConfig 사용)
 * 4. Tour API 호출: KorService2로 한국어 데이터 수신
 * 5. JSON으로 AI에게 반환 → AI가 사용자 언어로 번역
 */
@Component
class TourTool(
    private val tourService: TourService,
    private val objectMapper: ObjectMapper,
) {
    /**
     * 지역기반 관광정보 조회
     *
     * 흐름:
     * 1. contentTypeName("관광지") + areaAndSigunguCode("38,5") 입력
     * 2. parseParams: "관광지" + KorService2 → contentTypeId="12" 변환
     * 3. Tour API 호출: areaCode=38, sigunguCode=5, contentTypeId=12
     * 4. 진주시 진주성면 관광지 목록 JSON 반환
     *
     * 예시 입력:
     * - contentTypeName: "관광지"
     * - areaAndSigunguCode: "38,5" (경남-진주) 또는 "38-5"
     *
     * 예시 출력: [{"contentId":"126508", "title":"진주성", "addr":"경상남도 진주시...", ...}]
     */
    @Tool(description = "특정 지역의 관광지, 음식점, 숙박, 쇼핑, 문화시설, 축제, 레포츠 정보를 조회합니다")
    fun getAreaBasedTourInfo(
        @ToolParam(
            description =
                "관광 타입 이름 (타입 이름만 전달하면 언어에 맞는 코드로 자동 변환됩니다). " +
                    "사용 가능한 타입: ${BuildConfig.CONTENT_TYPE_DESCRIPTION}",
            required = true,
        )
        contentTypeName: String,
        @ToolParam(
            description =
                "지역 코드 (하이픈(-) 또는 쉼표(,) 형식 가능). " +
                    "사용 가능 코드: ${BuildConfig.AREA_CODES_DESCRIPTION}",
            required = true,
        )
        areaAndSigunguCode: String,
    ): String {
        val languageCode = "KorService2"

        log.debug(
            "🔧 [TOOL CALLED] getAreaBasedTourInfo - " +
                "contentTypeName: $contentTypeName, areaAndSigunguCode: $areaAndSigunguCode, languageCode: $languageCode",
        )

        return try {
            val tourParams = tourService.parseParams(contentTypeName, areaAndSigunguCode, languageCode)
            val tourInfo = tourService.fetchTours(tourParams, languageCode)
            val result = tourInfo.let { objectMapper.writeValueAsString(it) }
            log.debug("✅ [TOOL RESULT] getAreaBasedTourInfo - 결과: ${result.take(100)}...")
            result
        } catch (e: Exception) {
            log.error("❌ [TOOL ERROR] getAreaBasedTourInfo - 예외 발생", e)
            "지역기반 관광정보 조회를 가져올 수 없습니다."
        }
    }

    /**
     * 위치기반 관광정보 조회
     *
     * 흐름:
     * 1. contentTypeName("음식점") + areaCode("1,24") + GPS 좌표(mapX, mapY) + radius(100m) 입력
     * 2. parseParams: "음식점" + KorService2 → contentTypeId="39" 변환
     * 3. Tour API 호출: 명동(126.98375, 37.563446) 중심 100m 반경 음식점 검색
     * 4. 반경 내 음식점 목록 JSON 반환
     *
     * 예시 입력:
     * - contentTypeName: "음식점"
     * - areaAndSigunguCode: "1,24" (서울-중구)
     * - mapX: "126.98375", mapY: "37.563446" (명동 좌표)
     * - radius: "100" (100미터)
     *
     * 예시 출력: [{"contentId":"264247", "title":"명동교자", "dist":"45.3", ...}]
     */
    @Tool(description = "특정 GPS 좌표 기반으로 주변의 관광지, 음식점, 숙박, 쇼핑, 문화시설 정보를 조회합니다")
    fun getLocationBasedTourInfo(
        @ToolParam(
            description =
                "관광 타입 이름 (타입 이름만 전달하면 언어에 맞는 코드로 자동 변환됩니다). " +
                    "사용 가능한 타입: ${BuildConfig.CONTENT_TYPE_DESCRIPTION}",
            required = true,
        )
        contentTypeName: String,
        @ToolParam(
            description =
                "지역 코드 (하이픈(-) 또는 쉼표(,) 형식 가능). " +
                    "사용 가능 코드: ${BuildConfig.AREA_CODES_DESCRIPTION}",
            required = true,
        )
        areaAndSigunguCode: String,
        @ToolParam(description = "WGS84 경도 좌표", required = true)
        mapX: String,
        @ToolParam(description = "WGS84 위도 좌표", required = true)
        mapY: String,
        @ToolParam(description = "검색 반경 (미터 단위)", required = false)
        radius: String = "1000",
    ): String {
        val languageCode = "KorService2"

        log.debug(
            "🔧 [TOOL CALLED] getLocationBasedTourInfo - " +
                "contentTypeName: $contentTypeName, area: $areaAndSigunguCode, " +
                "mapX: $mapX, mapY: $mapY, radius: $radius, languageCode: $languageCode",
        )

        return try {
            val tourParams = tourService.parseParams(contentTypeName, areaAndSigunguCode, languageCode)
            val locationBasedParams = TourLocationBasedParams(mapX, mapY, radius)
            val tourLocationBasedInfo = tourService.fetchLocationBasedTours(tourParams, locationBasedParams, languageCode)
            val result = tourLocationBasedInfo.let { objectMapper.writeValueAsString(it) }
            log.debug("✅ [TOOL RESULT] getLocationBasedTourInfo - 결과: ${result.take(100)}...")
            result
        } catch (e: Exception) {
            log.error("❌ [TOOL ERROR] getLocationBasedTourInfo - 예외 발생", e)
            "위치기반 관광정보 조회를 가져올 수 없습니다."
        }
    }

    /**
     * 관광정보 상세조회
     *
     * 흐름:
     * 1. 이전 Tool 호출(getAreaBasedTourInfo 등)에서 받은 contentId 입력
     * 2. Tour API 호출: contentId로 상세정보 조회
     * 3. 해당 관광지의 상세정보(개요, 이용시간, 요금 등) JSON 반환
     *
     * 예시 입력:
     * - contentId: "126508" (진주성의 contentId)
     *
     * 예시 출력: {"contentId":"126508", "overview":"임진왜란 때...", "usagetime":"09:00~18:00", ...}
     */
    @Tool(description = "관광지, 음식점, 숙박시설 등의 상세 정보(개요, 이용시간, 요금, 주차정보 등)를 조회합니다")
    fun getTourDetailInfo(
        @ToolParam(
            description =
                "조회할 관광정보의 콘텐츠 ID. " +
                    "이전 Tool 호출 결과에서 받은 contentId를 사용하세요.",
            required = true,
        )
        contentId: String,
    ): String {
        val languageCode = "KorService2"

        log.debug("🔧 [TOOL CALLED] getTourDetailInfo - contentId: $contentId, languageCode: $languageCode")

        return try {
            val tourDetailParams = TourDetailParams(contentId)
            val tourDetailInfo = tourService.fetchTourDetail(tourDetailParams, languageCode)
            val result = tourDetailInfo.let { objectMapper.writeValueAsString(it) }
            log.debug("✅ [TOOL RESULT] getTourDetailInfo - 결과: ${result.take(100)}...")
            result
        } catch (e: Exception) {
            log.error("❌ [TOOL ERROR] getTourDetailInfo - 예외 발생", e)
            "관광정보 상세조회를 가져올 수 없습니다."
        }
    }

    /**
     * 축제/공연/행사 정보 조회
     *
     * 흐름:
     * 1. 날짜 범위(eventStartDate~eventEndDate) + 선택적 지역 코드 입력
     * 2. Tour API 호출: 해당 기간의 축제/행사 검색
     * 3. 축제/행사 목록 JSON 반환
     *
     * 예시 입력:
     * - eventStartDate: "20250301"
     * - eventEndDate: "20250331" (null이면 시작일만 검색)
     * - areaCode: "38" (경남, null이면 전국)
     * - sigunguCode: "5" (진주, null 가능)
     *
     * 예시 출력: [{"contentId":"...", "title":"진주남강유등축제", "eventstartdate":"20251001", ...}]
     */
    @Tool(description = "특정 기간 동안 열리는 축제, 공연, 행사 정보를 조회합니다")
    fun getFestivalInfo(
        @ToolParam(
            description = "행사 시작일 (YYYYMMDD 형식, 예: 20250101)",
            required = true,
        )
        eventStartDate: String,
        @ToolParam(
            description = "행사 종료일 (YYYYMMDD 형식, 예: 20250131). 생략시 시작일만 검색",
            required = false,
        )
        eventEndDate: String? = null,
        @ToolParam(
            description =
                "지역 코드 (예: 1-서울, 6-부산). 생략시 전국 검색. " +
                    "코드: ${BuildConfig.AREA_CODES_DESCRIPTION}",
            required = false,
        )
        areaCode: String? = null,
        @ToolParam(
            description = "시군구 코드. 지역 코드와 함께 사용. 생략 가능",
            required = false,
        )
        sigunguCode: String? = null,
    ): String {
        val languageCode = "KorService2"

        log.debug(
            "🔧 [TOOL CALLED] getFestivalInfo - " +
                "eventStartDate: $eventStartDate, eventEndDate: $eventEndDate, " +
                "areaCode: $areaCode, sigunguCode: $sigunguCode, languageCode: $languageCode",
        )

        return try {
            val festivalParams = TourFestivalParams(eventStartDate, eventEndDate, areaCode, sigunguCode)
            val festivalInfo = tourService.fetchFestivalInfo(festivalParams, languageCode)
            val result = festivalInfo.let { objectMapper.writeValueAsString(it) }
            log.debug("✅ [TOOL RESULT] getFestivalInfo - 결과: ${result.take(100)}...")
            result
        } catch (e: Exception) {
            log.error("❌ [TOOL ERROR] getFestivalInfo - 예외 발생", e)
            "축제 정보 조회를 가져올 수 없습니다."
        }
    }

    /**
     * 베니키아/한옥/굿스테이 숙박 정보 조회
     *
     * 흐름:
     * 1. 선택적 지역 코드 + 정렬 옵션 입력
     * 2. Tour API 호출: 한국 대표 숙박시설 검색
     * 3. 숙박 목록 JSON 반환
     *
     * 예시 입력:
     * - areaCode: "38" (경남, null이면 전국)
     * - sigunguCode: "5" (진주, null 가능)
     * - modifiedtime: "20240101" (해당 날짜 이후 수정된 것만, null 가능)
     * - arrange: "O" (대표이미지있는콘텐츠+제목순, null이면 기본정렬)
     *
     * 예시 출력: [{"contentId":"...", "title":"진주 전통한옥", "addr":"경상남도 진주시...", ...}]
     */
    @Tool(description = "베니키아, 한옥, 굿스테이 등 한국 대표 숙박시설 정보를 조회합니다")
    fun getStayInfo(
        @ToolParam(
            description =
                "지역 코드 (예: 1-서울, 6-부산). 생략시 전국 검색. " +
                    "코드: ${BuildConfig.AREA_CODES_DESCRIPTION}",
            required = false,
        )
        areaCode: String? = null,
        @ToolParam(
            description = "시군구 코드. 지역 코드와 함께 사용. 생략 가능",
            required = false,
        )
        sigunguCode: String? = null,
        @ToolParam(
            description = "수정일 (YYYYMMDD 형식, 예: 20240101). 해당 날짜 이후 수정된 숙박만 검색",
            required = false,
        )
        modifiedtime: String? = null,
        @ToolParam(
            description =
                "정렬 구분. A=제목순, C=수정일순, D=생성일순, " +
                    "O=대표이미지있는콘텐츠+제목순, Q=대표이미지있는콘텐츠+수정일순, R=대표이미지있는콘텐츠+생성일순",
            required = false,
        )
        arrange: String? = null,
    ): String {
        val languageCode = "KorService2"

        log.debug(
            "🔧 [TOOL CALLED] getStayInfo - " +
                "areaCode: $areaCode, sigunguCode: $sigunguCode, " +
                "modifiedtime: $modifiedtime, arrange: $arrange, languageCode: $languageCode",
        )

        return try {
            val stayParams = TourStayParams(areaCode, sigunguCode, modifiedtime, arrange)
            val stayInfo = tourService.fetchStayInfo(stayParams, languageCode)
            val result = stayInfo.let { objectMapper.writeValueAsString(it) }
            log.debug("✅ [TOOL RESULT] getStayInfo - 결과: ${result.take(100)}...")
            result
        } catch (e: Exception) {
            log.error("❌ [TOOL ERROR] getStayInfo - 예외 발생", e)
            "숙박 정보 조회를 가져올 수 없습니다."
        }
    }
}
