package com.back.koreaTravelGuide.domain.ai.aiChat.tool

import com.back.koreaTravelGuide.KoreaTravelGuideApplication
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * TourTool 통합 테스트
 *
 * 실제 한국관광공사 API를 호출하여 응답을 검증합니다.
 *
 * 환경변수 TOUR_API_KEY가 설정되어 있어야 실행됩니다.
 *
 * 실행 방법:
 * ```
 * TOUR_API_KEY=your_key ./gradlew test --tests TourToolIntegrationTest
 * ```
 */
@SpringBootTest(classes = [KoreaTravelGuideApplication::class])
@ActiveProfiles("dev")
@DisplayName("TourTool 실제 API 호출 통합 테스트")
class TourToolIntegrationTest {
    @Autowired
    private lateinit var tourTool: TourTool

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @DisplayName("서울 종로구 관광지 조회 - 실제 데이터 반환 확인")
    fun `should fetch actual tourist spots in Seoul Jongno`() {
        // given: 서울(1) + 종로구(1) + 관광지(관광지)
        val contentTypeName = "관광지"
        val areaAndSigunguCode = "1,1"

        // when
        val jsonResult = tourTool.getAreaBasedTourInfo(contentTypeName, areaAndSigunguCode)

        // then
        println("📍 서울 종로구 관광지 결과:")
        println(jsonResult.take(500))

        val result: Map<String, Any> = objectMapper.readValue(jsonResult)
        val items = result["items"] as? List<*>

        assertThat(items).isNotNull
        if (items!!.isNotEmpty()) {
            val firstItem = items[0] as Map<*, *>
            assertThat(firstItem["contentId"]).isNotNull
            assertThat(firstItem["title"]).isNotNull
            println("✅ 첫 번째 관광지: ${firstItem["title"]}")
        }
    }

    @Test
    @DisplayName("부산 해운대 음식점 조회 - 실제 데이터 반환 확인")
    fun `should fetch actual restaurants in Busan Haeundae`() {
        // given: 부산(6) + 해운대(10) + 음식점(음식점)
        val contentTypeName = "음식점"
        val areaAndSigunguCode = "6,10"

        // when
        val jsonResult = tourTool.getAreaBasedTourInfo(contentTypeName, areaAndSigunguCode)

        // then
        println("📍 부산 해운대 음식점 결과:")
        println(jsonResult.take(500))

        val result: Map<String, Any> = objectMapper.readValue(jsonResult)
        val items = result["items"] as? List<*>

        assertThat(items).isNotNull
        if (items!!.isNotEmpty()) {
            val firstItem = items[0] as Map<*, *>
            assertThat(firstItem["contentId"]).isNotNull
            assertThat(firstItem["title"]).isNotNull
            println("✅ 첫 번째 음식점: ${firstItem["title"]}")
        }
    }

    @Test
    @DisplayName("경남 진주 숙박 조회 - 실제 데이터 반환 확인")
    fun `should fetch actual accommodations in Gyeongnam Jinju`() {
        // given: 경남(38) + 진주(5) + 숙박(숙박)
        val contentTypeName = "숙박"
        val areaAndSigunguCode = "38,5"

        // when
        val jsonResult = tourTool.getAreaBasedTourInfo(contentTypeName, areaAndSigunguCode)

        // then
        println("📍 경남 진주 숙박 결과:")
        println(jsonResult.take(500))

        val result: Map<String, Any> = objectMapper.readValue(jsonResult)
        val items = result["items"] as? List<*>

        assertThat(items).isNotNull
        println("✅ 숙박 시설 개수: ${items!!.size}")
    }

    @Test
    @DisplayName("여러 contentType 조합 - 관광지, 문화시설, 축제, 레포츠")
    fun `should fetch different content types successfully`() {
        // given: 서울(1) + 중구(24)
        val areaCode = "1,24"
        val contentTypes = listOf("관광지", "문화시설", "축제공연행사", "레포츠")

        // when & then
        contentTypes.forEach { contentType ->
            val jsonResult = tourTool.getAreaBasedTourInfo(contentType, areaCode)
            val result: Map<String, Any> = objectMapper.readValue(jsonResult)
            val items = result["items"] as? List<*>

            println("📍 서울 중구 $contentType: ${items?.size ?: 0}개")
            assertThat(items).isNotNull
        }
    }

    @Test
    @DisplayName("하이픈(-) 구분자로 지역코드 전달 - 쉼표와 동일하게 작동")
    fun `should accept hyphen separator for area code`() {
        // given: 하이픈 구분자 사용
        val contentTypeName = "관광지"
        val areaAndSigunguCodeHyphen = "1-1"

        // when
        val jsonResult = tourTool.getAreaBasedTourInfo(contentTypeName, areaAndSigunguCodeHyphen)

        // then
        val result: Map<String, Any> = objectMapper.readValue(jsonResult)
        val items = result["items"] as? List<*>

        assertThat(items).isNotNull
        println("✅ 하이픈 구분자로 ${items?.size ?: 0}개 결과 조회 성공")
    }

    @Test
    @DisplayName("위치기반 조회 - 명동(서울 중구) 주변 음식점")
    fun `should fetch location-based restaurants near Myeongdong`() {
        // given: 명동 GPS 좌표 + 500m 반경
        val contentTypeName = "음식점"
        val areaAndSigunguCode = "1,24" // 서울 중구
        val mapX = "126.98375" // 명동 경도
        val mapY = "37.563446" // 명동 위도
        val radius = "500"

        // when
        val jsonResult =
            tourTool.getLocationBasedTourInfo(
                contentTypeName,
                areaAndSigunguCode,
                mapX,
                mapY,
                radius,
            )

        // then
        println("📍 명동 주변 500m 음식점 결과:")
        println(jsonResult.take(500))

        val result: Map<String, Any> = objectMapper.readValue(jsonResult)
        val items = result["items"] as? List<*>

        assertThat(items).isNotNull
        if (items!!.isNotEmpty()) {
            val firstItem = items[0] as Map<*, *>
            assertThat(firstItem["dist"]).isNotNull // 거리 정보 확인
            println("✅ 가장 가까운 음식점: ${firstItem["title"]} (거리: ${firstItem["dist"]}m)")
        }
    }

    @Test
    @DisplayName("상세정보 조회 - 특정 contentId로 조회")
    fun `should fetch tour detail by contentId`() {
        // given: 먼저 지역기반 조회로 contentId 획득
        val areaResult = tourTool.getAreaBasedTourInfo("관광지", "1,1")
        val areaData: Map<String, Any> = objectMapper.readValue(areaResult)
        val items = areaData["items"] as? List<*>

        if (items.isNullOrEmpty()) {
            println("⚠️ 조회된 관광지가 없어 상세정보 테스트 스킵")
            return
        }

        val firstItem = items[0] as Map<*, *>
        val contentId = firstItem["contentId"] as String

        // when
        val jsonResult = tourTool.getTourDetailInfo(contentId)

        // then
        println("📍 contentId=$contentId 상세정보:")
        println(jsonResult.take(500))

        val result: Map<String, Any> = objectMapper.readValue(jsonResult)
        val detailItems = result["items"] as? List<*>

        assertThat(detailItems).isNotNull
        if (detailItems!!.isNotEmpty()) {
            val detail = detailItems[0] as Map<*, *>
            assertThat(detail["contentId"]).isEqualTo(contentId)
            println("✅ 상세정보 조회 성공: ${detail["title"]}")
            println("   개요: ${detail["overview"]?.toString()?.take(100)}...")
        }
    }

    @Test
    @DisplayName("축제정보 조회 - 2025년 3월 전국 축제")
    fun `should fetch festival info for March 2025`() {
        // given: 2025년 3월 전국 축제
        val eventStartDate = "20250301"
        val eventEndDate = "20250331"

        // when
        val jsonResult = tourTool.getFestivalInfo(eventStartDate, eventEndDate, null, null)

        // then
        println("📍 2025년 3월 전국 축제:")
        println(jsonResult.take(500))

        val result: Map<String, Any> = objectMapper.readValue(jsonResult)
        val items = result["items"] as? List<*>

        assertThat(items).isNotNull
        println("✅ 3월 축제 개수: ${items!!.size}")

        if (items.isNotEmpty()) {
            val firstFestival = items[0] as Map<*, *>
            println("   첫 번째 축제: ${firstFestival["title"]}")
            println("   행사 시작일: ${firstFestival["eventstartdate"]}")
        }
    }

    @Test
    @DisplayName("숙박정보 조회 - 베니키아/한옥/굿스테이")
    fun `should fetch stay info`() {
        // given: 서울 지역 숙박 (대표이미지+제목순)
        val areaCode = "1"
        val arrange = "O"

        // when
        val jsonResult = tourTool.getStayInfo(areaCode, null, null, arrange)

        // then
        println("📍 서울 숙박 시설 (대표이미지+제목순):")
        println(jsonResult.take(500))

        val result: Map<String, Any> = objectMapper.readValue(jsonResult)
        val items = result["items"] as? List<*>

        assertThat(items).isNotNull
        println("✅ 서울 숙박 시설 개수: ${items!!.size}")

        if (items.isNotEmpty()) {
            val firstStay = items[0] as Map<*, *>
            println("   첫 번째 숙박: ${firstStay["title"]}")
            assertThat(firstStay["firstimage"]).isNotNull // 대표이미지 정렬이므로 이미지 있어야 함
        }
    }

    @Test
    @DisplayName("에러 케이스 - 잘못된 지역코드")
    fun `should handle invalid area code gracefully`() {
        // given: 존재하지 않는 지역코드
        val contentTypeName = "관광지"
        val invalidAreaCode = "999,999"

        // when
        val jsonResult = tourTool.getAreaBasedTourInfo(contentTypeName, invalidAreaCode)

        // then
        println("📍 잘못된 지역코드 결과:")
        println(jsonResult)

        val result: Map<String, Any> = objectMapper.readValue(jsonResult)
        val items = result["items"] as? List<*>

        // API가 에러를 반환하거나 빈 배열을 반환할 것으로 예상
        assertThat(items).isNotNull
        println("✅ 에러 처리 확인 - items size: ${items!!.size}")
    }

    @Test
    @DisplayName("전체 지역코드 샘플링 테스트 - 각 지역에서 1개씩 조회")
    fun `should test sample area codes from all regions`() {
        // given: 각 지역의 대표 지역코드 (서울, 인천, 대전, 대구, 광주, 부산, 울산, 세종, 경기, 강원, 충북, 충남, 경북, 경남, 전북, 전남, 제주)
        val sampleAreaCodes =
            listOf(
                "1" to "서울",
                "2" to "인천",
                "3" to "대전",
                "4" to "대구",
                "5" to "광주",
                "6" to "부산",
                "7" to "울산",
                "8" to "세종",
                "31" to "경기",
                "32" to "강원",
                "33" to "충북",
                "34" to "충남",
                "35" to "경북",
                "36" to "경남",
                "37" to "전북",
                "38" to "전남",
                "39" to "제주",
            )

        println("\n📊 전국 지역별 관광지 샘플링 테스트:")
        println("=".repeat(60))

        // when & then
        sampleAreaCodes.forEach { (areaCode, regionName) ->
            val jsonResult = tourTool.getAreaBasedTourInfo("관광지", areaCode)
            val result: Map<String, Any> = objectMapper.readValue(jsonResult)
            val items = result["items"] as? List<*>

            val count = items?.size ?: 0
            val status = if (count > 0) "✅" else "⚠️"
            println("$status $regionName(code=$areaCode): $count 개")

            assertThat(items).isNotNull
        }

        println("=".repeat(60))
    }
}
