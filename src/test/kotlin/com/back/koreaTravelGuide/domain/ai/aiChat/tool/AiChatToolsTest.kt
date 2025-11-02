package com.back.koreaTravelGuide.domain.ai.aiChat.tool

import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailItem
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailResponse
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourItem
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import com.back.koreaTravelGuide.domain.ai.tour.service.TourService
import com.back.koreaTravelGuide.domain.ai.weather.dto.MidForecastDto
import com.back.koreaTravelGuide.domain.ai.weather.dto.TemperatureAndLandForecastDto
import com.back.koreaTravelGuide.domain.ai.weather.service.WeatherService
import com.back.koreaTravelGuide.domain.guide.service.GuideService
import com.back.koreaTravelGuide.domain.user.dto.response.GuideResponse
import com.back.koreaTravelGuide.domain.user.enums.Region
import com.back.koreaTravelGuide.domain.user.enums.UserRole
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * AI Chat Tools 단위 테스트
 * WeatherTool, TourTool, GuideFinderTool의 비즈니스 로직 테스트
 */
@DisplayName("AI Chat Tools 단위 테스트")
class AiChatToolsTest {
    private lateinit var objectMapper: ObjectMapper

    @Nested
    @DisplayName("WeatherTool 테스트")
    inner class WeatherToolTest {
        private lateinit var weatherService: WeatherService
        private lateinit var weatherTool: WeatherTool

        @BeforeEach
        fun setUp() {
            weatherService = mockk()
            objectMapper = ObjectMapper()
            weatherTool = WeatherTool(weatherService, objectMapper)
        }

        @Test
        @DisplayName("전국 중기예보를 JSON 형식으로 반환한다")
        fun `should return weather forecast as JSON`() {
            // given
            val mockForecasts =
                listOf(
                    MidForecastDto(
                        regionCode = "11B00000",
                        baseTime = "202501301800",
                        precipitation = "서울: 맑음",
                        temperature = "최고 15도",
                        maritime = null,
                        variability = null,
                    ),
                )
            every { weatherService.getWeatherForecast() } returns mockForecasts

            // when
            val result = weatherTool.getWeatherForecast()

            // then
            assertThat(result).contains("11B00000")
            assertThat(result).contains("서울")
            verify(exactly = 1) { weatherService.getWeatherForecast() }
        }

        @Test
        @DisplayName("날씨 데이터가 없으면 안내 메시지를 반환한다")
        fun `should return error message when forecast is null`() {
            // given
            every { weatherService.getWeatherForecast() } returns null

            // when
            val result = weatherTool.getWeatherForecast()

            // then
            assertThat(result).isEqualTo("중기예보 데이터를 가져올 수 없습니다.")
        }

        @Test
        @DisplayName("특정 지역의 상세 날씨를 조회한다")
        fun `should return regional weather details`() {
            // given
            val location = "서울"
            val mockDetails =
                listOf(
                    TemperatureAndLandForecastDto(
                        regionCode = "11B00000",
                        baseTime = "202501301800",
                        minTemp = 10,
                        maxTemp = 15,
                        minTempRange = "9~11",
                        maxTempRange = "14~16",
                        amRainPercent = 10,
                        pmRainPercent = 20,
                        amWeather = "맑음",
                        pmWeather = "구름많음",
                    ),
                )
            every { weatherService.getTemperatureAndLandForecast(location) } returns mockDetails

            // when
            val result = weatherTool.getRegionalWeatherDetails(location)

            // then
            assertThat(result).contains("11B00000")
            assertThat(result).contains("15")
            verify(exactly = 1) { weatherService.getTemperatureAndLandForecast(location) }
        }

        @Test
        @DisplayName("지역 날씨 데이터가 없으면 안내 메시지를 반환한다")
        fun `should return error message when regional weather is null`() {
            // given
            val location = "서울"
            every { weatherService.getTemperatureAndLandForecast(location) } returns null

            // when
            val result = weatherTool.getRegionalWeatherDetails(location)

            // then
            assertThat(result).isEqualTo("$location 지역의 상세 날씨 정보를 가져올 수 없습니다.")
        }
    }

    @Nested
    @DisplayName("TourTool 테스트")
    inner class TourToolTest {
        private lateinit var tourService: TourService
        private lateinit var tourTool: TourTool

        @BeforeEach
        fun setUp() {
            tourService = mockk()
            objectMapper = ObjectMapper()
            tourTool = TourTool(tourService, objectMapper)
        }

        @Test
        @DisplayName("지역기반 관광정보를 조회한다")
        fun `should return area-based tour information`() {
            // given
            val languageCode = "KorService2"
            val contentTypeId = "12"
            val areaAndSigunguCode = "6-10"
            val mockParams = TourParams(areaCode = "6", sigunguCode = "10", contentTypeId = "12")
            val mockResponse =
                TourResponse(
                    items =
                        listOf(
                            TourItem(
                                contentId = "1",
                                contentTypeId = "12",
                                createdTime = "20250101",
                                modifiedTime = "20250102",
                                title = "테스트 관광지",
                                addr1 = "부산 사하구",
                                areaCode = "6",
                                firstimage = null,
                                firstimage2 = null,
                                mapX = null,
                                mapY = null,
                                distance = null,
                                mlevel = null,
                                sigunguCode = "10",
                                lDongRegnCd = null,
                                lDongSignguCd = null,
                            ),
                        ),
                )

            every { tourService.parseParams(contentTypeId, areaAndSigunguCode) } returns mockParams
            every { tourService.fetchTours(mockParams, languageCode) } returns mockResponse

            // when
            val result = tourTool.getAreaBasedTourInfo(languageCode, contentTypeId, areaAndSigunguCode)

            // then
            assertThat(result).contains("items")
            verify(exactly = 1) { tourService.parseParams(contentTypeId, areaAndSigunguCode) }
            verify(exactly = 1) { tourService.fetchTours(mockParams, languageCode) }
        }

        @Test
        @DisplayName("지역기반 관광정보 조회 실패 시 에러 메시지를 반환한다")
        fun `should return error message when area-based tour fetch fails`() {
            // given
            val languageCode = "KorService2"
            val contentTypeId = "12"
            val areaAndSigunguCode = "6-10"

            every { tourService.parseParams(any(), any()) } throws RuntimeException("API Error")

            // when
            val result = tourTool.getAreaBasedTourInfo(languageCode, contentTypeId, areaAndSigunguCode)

            // then
            assertThat(result).isEqualTo("지역기반 관광정보 조회를 가져올 수 없습니다.")
        }

        @Test
        @DisplayName("관광정보 상세조회를 수행한다")
        fun `should return tour detail information`() {
            // given
            val languageCode = "KorService2"
            val contentId = "127974"
            val mockResponse =
                TourDetailResponse(
                    items =
                        listOf(
                            TourDetailItem(
                                contentId = contentId,
                                title = "경복궁",
                                overview = "조선시대 궁궐",
                                addr1 = "서울",
                                mapX = null,
                                mapY = null,
                                firstImage = null,
                                tel = null,
                                homepage = null,
                            ),
                        ),
                )

            every { tourService.fetchTourDetail(any<TourDetailParams>(), languageCode) } returns mockResponse

            // when
            val result = tourTool.getTourDetailInfo(languageCode, contentId)

            // then
            assertThat(result).contains(contentId)
            assertThat(result).contains("경복궁")
            verify(exactly = 1) { tourService.fetchTourDetail(any<TourDetailParams>(), languageCode) }
        }

        @Test
        @DisplayName("관광정보 상세조회 실패 시 에러 메시지를 반환한다")
        fun `should return error message when tour detail fetch fails`() {
            // given
            val languageCode = "KorService2"
            val contentId = "127974"

            every { tourService.fetchTourDetail(any<TourDetailParams>(), any()) } throws RuntimeException("API Error")

            // when
            val result = tourTool.getTourDetailInfo(languageCode, contentId)

            // then
            assertThat(result).isEqualTo("관광정보 상세조회를 가져올 수 없습니다.")
        }
    }

    @Nested
    @DisplayName("GuideFinderTool 테스트")
    inner class GuideFinderToolTest {
        private lateinit var guideService: GuideService
        private lateinit var guideFinderTool: GuideFinderTool

        @BeforeEach
        fun setUp() {
            guideService = mockk()
            objectMapper = ObjectMapper()
            guideFinderTool = GuideFinderTool(guideService, objectMapper)
        }

        @Test
        @DisplayName("특정 지역의 가이드 목록을 조회한다")
        fun `should return guides list for region`() {
            // given
            val region = "SEOUL"
            val mockGuides =
                listOf(
                    GuideResponse(
                        id = 1L,
                        email = "guide1@test.com",
                        nickname = "서울 가이드 1",
                        profileImageUrl = "url1",
                        role = UserRole.GUIDE,
                        location = Region.SEOUL,
                        description = "서울 전문 가이드",
                    ),
                    GuideResponse(
                        id = 2L,
                        email = "guide2@test.com",
                        nickname = "서울 가이드 2",
                        profileImageUrl = "url2",
                        role = UserRole.GUIDE,
                        location = Region.SEOUL,
                        description = "서울 역사 가이드",
                    ),
                )

            every { guideService.findGuidesByRegion(region) } returns mockGuides

            // when
            val result = guideFinderTool.findGuidesByRegion(region)

            // then
            assertThat(result).contains("서울 가이드 1")
            assertThat(result).contains("서울 가이드 2")
            verify(exactly = 1) { guideService.findGuidesByRegion(region) }
        }

        @Test
        @DisplayName("가이드가 없으면 안내 메시지를 반환한다")
        fun `should return message when no guides found`() {
            // given
            val region = "JEJU"
            every { guideService.findGuidesByRegion(region) } returns emptyList()

            // when
            val result = guideFinderTool.findGuidesByRegion(region)

            // then
            assertThat(result).isEqualTo("해당 지역에서 활동하는 가이드를 찾을 수 없습니다.")
            verify(exactly = 1) { guideService.findGuidesByRegion(region) }
        }

        @Test
        @DisplayName("가이드 조회 중 예외 발생 시 에러 메시지를 반환한다")
        fun `should return error message when exception occurs`() {
            // given
            val region = "BUSAN"
            every { guideService.findGuidesByRegion(region) } throws RuntimeException("DB Error")

            // when
            val result = guideFinderTool.findGuidesByRegion(region)

            // then
            assertThat(result).isEqualTo("가이드 정보를 JSON으로 변환하는 중 오류가 발생했습니다.")
        }
    }
}
