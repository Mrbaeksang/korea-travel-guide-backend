package com.back.koreaTravelGuide.domain.ai.weather.service

import com.back.koreaTravelGuide.domain.ai.weather.dto.MidForecastDto
import com.back.koreaTravelGuide.domain.ai.weather.dto.TemperatureAndLandForecastDto
import com.back.koreaTravelGuide.domain.ai.weather.service.tools.Tools
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * WeatherService 단위 테스트
 * Mock을 사용하여 의존성을 격리하고 서비스 로직만 테스트
 */
@DisplayName("WeatherService 단위 테스트")
class WeatherServiceTest {
    private lateinit var weatherServiceCore: WeatherServiceCore
    private lateinit var tools: Tools
    private lateinit var weatherService: WeatherService

    private val testBaseTime = "202501301800"

    @BeforeEach
    fun setUp() {
        weatherServiceCore = mockk()
        tools = mockk()
        weatherService = WeatherService(weatherServiceCore, tools)
    }

    @Test
    @DisplayName("전국 중기예보를 조회한다")
    fun `should fetch weather forecast`() {
        // given
        val mockForecasts =
            listOf(
                MidForecastDto(
                    regionCode = "11B00000",
                    baseTime = testBaseTime,
                    precipitation = "서울: 맑음",
                    temperature = "최고 15도",
                    maritime = null,
                    variability = null,
                ),
                MidForecastDto(
                    regionCode = "11H20000",
                    baseTime = testBaseTime,
                    precipitation = "부산: 흐림",
                    temperature = "최고 18도",
                    maritime = null,
                    variability = null,
                ),
            )

        every { tools.getCurrentBaseTime() } returns testBaseTime
        every { weatherServiceCore.fetchMidForecast(testBaseTime) } returns mockForecasts

        // when
        val result = weatherService.getWeatherForecast()

        // then
        assertThat(result).isNotNull
        assertThat(result).hasSize(2)
        assertThat(result?.get(0)?.regionCode).isEqualTo("11B00000")
        assertThat(result?.get(1)?.regionCode).isEqualTo("11H20000")

        verify(exactly = 1) { tools.getCurrentBaseTime() }
        verify(exactly = 1) { weatherServiceCore.fetchMidForecast(testBaseTime) }
    }

    @Test
    @DisplayName("전국 중기예보 조회 실패 시 null을 반환한다")
    fun `should return null when forecast fetch fails`() {
        // given
        every { tools.getCurrentBaseTime() } returns testBaseTime
        every { weatherServiceCore.fetchMidForecast(testBaseTime) } returns null

        // when
        val result = weatherService.getWeatherForecast()

        // then
        assertThat(result).isNull()
        verify(exactly = 1) { tools.getCurrentBaseTime() }
        verify(exactly = 1) { weatherServiceCore.fetchMidForecast(testBaseTime) }
    }

    @Test
    @DisplayName("특정 지역의 기온 및 날씨 예보를 조회한다")
    fun `should fetch temperature and land forecast for region`() {
        // given
        val regionCode = "11B00000" // 서울
        val mockForecasts =
            listOf(
                TemperatureAndLandForecastDto(
                    regionCode = regionCode,
                    baseTime = testBaseTime,
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

        every { tools.getCurrentBaseTime() } returns testBaseTime
        every { weatherServiceCore.fetchTemperatureAndLandForecast(regionCode, testBaseTime) } returns mockForecasts

        // when
        val result = weatherService.getTemperatureAndLandForecast(regionCode)

        // then
        assertThat(result).isNotNull
        assertThat(result).hasSize(1)
        assertThat(result?.get(0)?.regionCode).isEqualTo(regionCode)
        assertThat(result?.get(0)?.minTemp).isEqualTo(10)
        assertThat(result?.get(0)?.maxTemp).isEqualTo(15)

        verify(exactly = 1) { tools.getCurrentBaseTime() }
        verify(exactly = 1) { weatherServiceCore.fetchTemperatureAndLandForecast(regionCode, testBaseTime) }
    }

    @Test
    @DisplayName("지역 날씨 예보 조회 실패 시 null을 반환한다")
    fun `should return null when regional forecast fetch fails`() {
        // given
        val regionCode = "11B00000"

        every { tools.getCurrentBaseTime() } returns testBaseTime
        every { weatherServiceCore.fetchTemperatureAndLandForecast(regionCode, testBaseTime) } returns null

        // when
        val result = weatherService.getTemperatureAndLandForecast(regionCode)

        // then
        assertThat(result).isNull()
    }

    @Test
    @DisplayName("여러 지역의 날씨 예보를 순차적으로 조회한다")
    fun `should fetch forecasts for multiple regions sequentially`() {
        // given
        val seoulCode = "11B00000"
        val busanCode = "11H20000"

        val seoulForecast =
            listOf(
                TemperatureAndLandForecastDto(
                    regionCode = seoulCode,
                    baseTime = testBaseTime,
                    minTemp = 10,
                    maxTemp = 15,
                    minTempRange = null,
                    maxTempRange = null,
                    amRainPercent = 10,
                    pmRainPercent = 10,
                    amWeather = "맑음",
                    pmWeather = "맑음",
                ),
            )
        val busanForecast =
            listOf(
                TemperatureAndLandForecastDto(
                    regionCode = busanCode,
                    baseTime = testBaseTime,
                    minTemp = 12,
                    maxTemp = 18,
                    minTempRange = null,
                    maxTempRange = null,
                    amRainPercent = 30,
                    pmRainPercent = 40,
                    amWeather = "흐림",
                    pmWeather = "흐림",
                ),
            )

        every { tools.getCurrentBaseTime() } returns testBaseTime
        every { weatherServiceCore.fetchTemperatureAndLandForecast(seoulCode, testBaseTime) } returns seoulForecast
        every { weatherServiceCore.fetchTemperatureAndLandForecast(busanCode, testBaseTime) } returns busanForecast

        // when
        val seoulResult = weatherService.getTemperatureAndLandForecast(seoulCode)
        val busanResult = weatherService.getTemperatureAndLandForecast(busanCode)

        // then
        assertThat(seoulResult?.get(0)?.regionCode).isEqualTo(seoulCode)
        assertThat(busanResult?.get(0)?.regionCode).isEqualTo(busanCode)

        verify(exactly = 2) { tools.getCurrentBaseTime() }
        verify(exactly = 1) { weatherServiceCore.fetchTemperatureAndLandForecast(seoulCode, testBaseTime) }
        verify(exactly = 1) { weatherServiceCore.fetchTemperatureAndLandForecast(busanCode, testBaseTime) }
    }

    @Test
    @DisplayName("baseTime이 매번 최신으로 조회된다")
    fun `should fetch current base time for each request`() {
        // given
        val firstBaseTime = "202501301800"
        val secondBaseTime = "202501302000"

        every { tools.getCurrentBaseTime() } returnsMany listOf(firstBaseTime, secondBaseTime)
        every { weatherServiceCore.fetchMidForecast(any()) } returns emptyList()

        // when
        weatherService.getWeatherForecast()
        weatherService.getWeatherForecast()

        // then
        verify(exactly = 2) { tools.getCurrentBaseTime() }
        verify(exactly = 1) { weatherServiceCore.fetchMidForecast(firstBaseTime) }
        verify(exactly = 1) { weatherServiceCore.fetchMidForecast(secondBaseTime) }
    }
}
