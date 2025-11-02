package com.back.koreaTravelGuide.domain.ai.tour.service

import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailItem
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailResponse
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourLocationBasedParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourAreaBasedUseCase
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourDetailUseCase
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourLocationBasedUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * TourService 단위 테스트
 * Mock을 사용하여 UseCase 의존성을 격리하고 서비스 로직만 테스트
 */
@DisplayName("TourService 단위 테스트")
class TourServiceTest {
    private lateinit var tourAreaBasedUseCase: TourAreaBasedUseCase
    private lateinit var tourLocationBasedUseCase: TourLocationBasedUseCase
    private lateinit var tourDetailUseCase: TourDetailUseCase
    private lateinit var tourParamsParser: TourParamsParser
    private lateinit var tourService: TourService

    @BeforeEach
    fun setUp() {
        tourAreaBasedUseCase = mockk()
        tourLocationBasedUseCase = mockk()
        tourDetailUseCase = mockk()
        tourParamsParser = mockk()
        tourService =
            TourService(
                tourAreaBasedUseCase,
                tourLocationBasedUseCase,
                tourDetailUseCase,
                tourParamsParser,
            )
    }

    @Test
    @DisplayName("파라미터를 파싱한다")
    fun `should parse tour parameters`() {
        // given
        val contentTypeId = "12"
        val areaAndSigunguCode = "6-10"
        val expectedParams = TourParams(areaCode = "6", sigunguCode = "10", contentTypeId = "12")

        every { tourParamsParser.parse(contentTypeId, areaAndSigunguCode) } returns expectedParams

        // when
        val result = tourService.parseParams(contentTypeId, areaAndSigunguCode)

        // then
        assertThat(result.areaCode).isEqualTo("6")
        assertThat(result.sigunguCode).isEqualTo("10")
        assertThat(result.contentTypeId).isEqualTo("12")

        verify(exactly = 1) { tourParamsParser.parse(contentTypeId, areaAndSigunguCode) }
    }

    @Test
    @DisplayName("지역기반 관광정보를 조회한다")
    fun `should fetch area-based tours`() {
        // given
        val tourParams = TourParams(areaCode = "6", sigunguCode = "10", contentTypeId = "12")
        val languageCode = "KorService2"
        val mockResponse =
            TourResponse(
                items = listOf(),
            )

        every { tourAreaBasedUseCase.fetchAreaBasedTours(tourParams, languageCode) } returns mockResponse

        // when
        val result = tourService.fetchTours(tourParams, languageCode)

        // then
        assertThat(result.items).isEmpty()
        verify(exactly = 1) { tourAreaBasedUseCase.fetchAreaBasedTours(tourParams, languageCode) }
    }

    @Test
    @DisplayName("한국어 외 다른 언어로 지역기반 관광정보를 조회한다")
    fun `should fetch area-based tours in foreign language`() {
        // given
        val tourParams = TourParams(areaCode = "1", sigunguCode = "24", contentTypeId = "76")
        val languageCode = "EngService2"
        val mockResponse =
            TourResponse(
                items = listOf(),
            )

        every { tourAreaBasedUseCase.fetchAreaBasedTours(tourParams, languageCode) } returns mockResponse

        // when
        val result = tourService.fetchTours(tourParams, languageCode)

        // then
        assertThat(result.items).isEmpty()
        verify(exactly = 1) { tourAreaBasedUseCase.fetchAreaBasedTours(tourParams, languageCode) }
    }

    @Test
    @DisplayName("위치기반 관광정보를 조회한다")
    fun `should fetch location-based tours`() {
        // given
        val tourParams = TourParams(areaCode = "1", sigunguCode = "24", contentTypeId = "39")
        val locationParams = TourLocationBasedParams(mapX = "126.98375", mapY = "37.563446", radius = "100")
        val languageCode = "KorService2"
        val mockResponse =
            TourResponse(
                items = listOf(),
            )

        every {
            tourLocationBasedUseCase.fetchLocationBasedTours(
                tourParams,
                locationParams,
                languageCode,
            )
        } returns mockResponse

        // when
        val result = tourService.fetchLocationBasedTours(tourParams, locationParams, languageCode)

        // then
        assertThat(result.items).isEmpty()
        verify(exactly = 1) {
            tourLocationBasedUseCase.fetchLocationBasedTours(
                tourParams,
                locationParams,
                languageCode,
            )
        }
    }

    @Test
    @DisplayName("다양한 반경으로 위치기반 관광정보를 조회한다")
    fun `should fetch location-based tours with different radius`() {
        // given
        val tourParams = TourParams(areaCode = "1", sigunguCode = "24", contentTypeId = "39")
        val smallRadius = TourLocationBasedParams(mapX = "126.98375", mapY = "37.563446", radius = "50")
        val largeRadius = TourLocationBasedParams(mapX = "126.98375", mapY = "37.563446", radius = "500")
        val languageCode = "KorService2"

        val smallRadiusResponse = TourResponse(items = listOf())
        val largeRadiusResponse = TourResponse(items = listOf())

        every {
            tourLocationBasedUseCase.fetchLocationBasedTours(tourParams, smallRadius, languageCode)
        } returns smallRadiusResponse
        every {
            tourLocationBasedUseCase.fetchLocationBasedTours(tourParams, largeRadius, languageCode)
        } returns largeRadiusResponse

        // when
        val smallResult = tourService.fetchLocationBasedTours(tourParams, smallRadius, languageCode)
        val largeResult = tourService.fetchLocationBasedTours(tourParams, largeRadius, languageCode)

        // then
        assertThat(smallResult.items).isEmpty()
        assertThat(largeResult.items).isEmpty()
    }

    @Test
    @DisplayName("관광정보 상세조회를 수행한다")
    fun `should fetch tour detail`() {
        // given
        val contentId = "127974"
        val detailParams = TourDetailParams(contentId = contentId)
        val languageCode = "KorService2"
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

        every { tourDetailUseCase.fetchTourDetail(detailParams, languageCode) } returns mockResponse

        // when
        val result = tourService.fetchTourDetail(detailParams, languageCode)

        // then
        assertThat(result.items).hasSize(1)
        assertThat(result.items[0].contentId).isEqualTo(contentId)
        assertThat(result.items[0].title).isEqualTo("경복궁")
        assertThat(result.items[0].overview).isEqualTo("조선시대 궁궐")

        verify(exactly = 1) { tourDetailUseCase.fetchTourDetail(detailParams, languageCode) }
    }

    @Test
    @DisplayName("외국어로 관광정보 상세조회를 수행한다")
    fun `should fetch tour detail in foreign language`() {
        // given
        val contentId = "264247"
        val detailParams = TourDetailParams(contentId = contentId)
        val languageCode = "EngService2"
        val mockResponse =
            TourDetailResponse(
                items =
                    listOf(
                        TourDetailItem(
                            contentId = contentId,
                            title = "Gyeongbokgung Palace",
                            overview = "Royal palace of Joseon Dynasty",
                            addr1 = "Seoul",
                            mapX = null,
                            mapY = null,
                            firstImage = null,
                            tel = null,
                            homepage = null,
                        ),
                    ),
            )

        every { tourDetailUseCase.fetchTourDetail(detailParams, languageCode) } returns mockResponse

        // when
        val result = tourService.fetchTourDetail(detailParams, languageCode)

        // then
        assertThat(result.items).hasSize(1)
        assertThat(result.items[0].title).isEqualTo("Gyeongbokgung Palace")
        assertThat(result.items[0].overview).contains("Royal palace")
    }

    @Test
    @DisplayName("파라미터 파싱과 조회를 연속으로 수행한다")
    fun `should parse params and fetch tours sequentially`() {
        // given
        val contentTypeId = "12"
        val areaAndSigunguCode = "6-10"
        val parsedParams = TourParams(areaCode = "6", sigunguCode = "10", contentTypeId = "12")
        val languageCode = "KorService2"
        val mockResponse = TourResponse(items = listOf())

        every { tourParamsParser.parse(contentTypeId, areaAndSigunguCode) } returns parsedParams
        every { tourAreaBasedUseCase.fetchAreaBasedTours(parsedParams, languageCode) } returns mockResponse

        // when
        val params = tourService.parseParams(contentTypeId, areaAndSigunguCode)
        val result = tourService.fetchTours(params, languageCode)

        // then
        assertThat(params.areaCode).isEqualTo("6")
        assertThat(result.items).isEmpty()

        verify(exactly = 1) { tourParamsParser.parse(contentTypeId, areaAndSigunguCode) }
        verify(exactly = 1) { tourAreaBasedUseCase.fetchAreaBasedTours(parsedParams, languageCode) }
    }

    @Test
    @DisplayName("여러 contentTypeId로 관광정보를 조회한다")
    fun `should fetch tours with different content types`() {
        // given
        val tourSiteParams = TourParams(areaCode = "6", sigunguCode = "10", contentTypeId = "12") // 관광지
        val restaurantParams = TourParams(areaCode = "6", sigunguCode = "10", contentTypeId = "39") // 음식점
        val languageCode = "KorService2"

        val tourSiteResponse = TourResponse(items = listOf())
        val restaurantResponse = TourResponse(items = listOf())

        every { tourAreaBasedUseCase.fetchAreaBasedTours(tourSiteParams, languageCode) } returns tourSiteResponse
        every { tourAreaBasedUseCase.fetchAreaBasedTours(restaurantParams, languageCode) } returns restaurantResponse

        // when
        val tourSites = tourService.fetchTours(tourSiteParams, languageCode)
        val restaurants = tourService.fetchTours(restaurantParams, languageCode)

        // then
        assertThat(tourSites.items).isEmpty()
        assertThat(restaurants.items).isEmpty()
    }
}
