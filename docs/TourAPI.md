인증키 활용 및 API 호출 방법
개발계정은 일 1,000건의 트래픽을 제공합니다.

개발계정은 자동승인으로 활용 신청 후, 약 30분 이내로 사용이 가능합니다.
(공공데이터포털과 한국관광공사 동기화)

REST방식의 URL 요청 예시
응답 표준은 XML 이며, JSON을 요청할 경우“&_type=json”을 추가하여 요청합니다.

Json요청 : http://apis.data.go.kr/B551011/KorService/areaCode?serviceKey=serviceKey&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=TestApp&_type=json

서비스키(인증키) 인코딩 방법
2015년 1월 이전에 공공데이터포털에서 받은 인증키 경우,
String myKey = “발급받은 인증키”;
String ServiceKey = URLEncoder.encode(myKey, "UTF-8");
(TourAPI의 모든 Character Set은 UTF-8 설정)

2015년 1월 이후에 공공데이터포털에서 받은 인증키 경우, 인코딩 불필요

요청 파라미터에서 서비스명 기재
MobileApp파라미터는 서비스(웹,앱 등)별로 활용 통계를 산출하기 위한 항목입니다.
URL요청 시 반드시 기재 부탁드립니다.

//====== 파라미터인코딩 예시(JSP 기준)
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
//=== 서비스명이 영문인 경우 (인코딩 불필요)
String appName = “KoreaTourismOrganization”;
//=== 서비스명이 한글(일문, 중문 등)인 경우 (인코딩 필수)
String appName = URLEncoder.encode(“한국관광공사”, "UTF-8");

http://apis.data.go.kr/B551011/KorService/areaCode?serviceKey=serviceKey&numOfRows=10&pageNo=1&MobileOS=AND&MobileApp=appName

서비스개요

서비스개요(서비스 정보, 서비스보안, 적용기술수준, 서비스 URL, 서비스 WADL, 서비스배포정보, 메시지교환유형, 메시지로깅수준, 사용제약사항(비고), 서비스제공자, 데이터갱신주기)
서비스정보	서비스ID	KorService2
서비스명(국문)	국문 관광정보 서비스
서비스명(영문)	KorService2
서비스 설명	코드조회 및 관광정보의 통합/상세검색 등의 기능을 제공하며 위치기반, 지역기반 국내 관광에 대한 전반적인 정보를 국문으로 제공합니다.
서비스보안	서비스인증/권한	[O]서비스 Key  [ ]인증서(GPKI)
[ ]Basic(ID/PW)  [ ]없음	[ ]WS-Security
메시지레벨암호화	[ ]전자서명  [ ]암호화  [O]없음
전송레벨암호화	[O]HTTPS  [O]HTTP
적용기술수준	인터페이스표준	[ ]SOAP 1.2
(PRC-Encoded, Document Literal, Document Literal Wrapped)
[O]REST (GET)
[ ]RSS 1.0   [ ]RSS 2.0  [ ]Atom 1.0  [ ]기타
교환데이터표준	[O]XML  [O]JSON  [ ]MIME  []MTOM
서비스 URL	개발환경	http://apis.data.go.kr/B551011/KorService2
운영환경	http://apis.data.go.kr/B551011/KorService2
서비스 WADL	개발환경	N/A
운영환경	N/A
서비스배포정보	서비스버전	1.1
서비스시작일	2012-12-31	배포일자	N/A
서비스이력	N/A
메시지교환유형	[O]Request-ResPonse   [ ]Publish-Subscribe
[ ]Fire-and-Forgot   [ ]Notification
메시지로깅수준	성공	[O]Header  [ ]Body	실패	[O]Header  [O]Body
사용제약사항
(비고)	N/A
서비스제공자	TourAPI운영팀 /디지털콘텐츠팀 / 033-738-3874 / tourapi@knto.or.kr
데이터갱신주기	일 1회
서비스 명세 (오퍼레이션목록)

서비스 명세(오퍼레이션목록)(번호, 서비스명(국문), 오퍼레이션(영문), 오퍼레이션명(국문))
번호	서비스명(국문)	오퍼레이션명(영문)	오퍼레이션명(국문)
1	국문 관광정보 서비스	areaCode2	지역코드 조회
2	categoryCode2	서비스 분류코드 조회
3	areaBasedList2	지역기반 관광정보 조회
4	locationBasedList2	위치기반 관광정보 조회
5	searchKeyword2	키워드 검색 조회
6	searchFestival2	행사정보조회
7	searchStay2	숙박정보조회
8	detailCommon2	공통정보조회 (상세정보1)
9	detailIntro2	소개정보조회 (상세정보2)
10	detailInfo2	반복정보조회 (상세정보3)
11	detailImage2	이미지정보조회 (상세정보4)
12	areaBasedSyncList2	국문관광정보 동기화 목록 조회
국문 콘텐츠 타입(ContentTypeId) 코드표

국문 콘텐츠 타입(ContentTypeId) 코드표(그룹,타입종류,ContentTypeId코드)
그룹	타입 종류	ContentTypeId코드
관광정보 (국문)	관광지	12
문화시설	14
행사/공연/축제	15
여행코스	25
레포츠	28
숙박	32
쇼핑	38
음식점	39
공공데이터 포털 에러코드

공공데이터 포털 에러코드(에러코드,에러메세지,설명)
에러코드	에러메세지	설명
01	APPLICATION_ERROR	어플리케이션 에러
12	NO_OPENAPI_SERVICE_ERROR	해당 오픈API서비스가 없거나 폐기됨
20	SERVICE_ACCESS_DENIED_ERROR	서비스 접근거부
22	LIMITED_NUMBER_OF_SERVICE_REQUESTS_EXCEEDS_ERROR	서비스 요청제한횟수 초과에러
30	SERVICE_KEY_IS_NOT_REGISTERED_ERROR	등록되지 않은 서비스키
31	DEADLINE_HAS_EXPIRED_ERROR	활용기간만료
32	UNREGISTERED_IP_ERROR	등록되지 않은 IP
99	UNKNOWN_ERROR	기타에러
※ 공공데이터포털에서출력되는오류메세지는 XML로만출력되며, 형태는아래와같습니다.
<OpenAPI_ServiceResponse>
<cmmMsgHeader>
<errMsg>SERVICE ERROR</errMsg>
<returnAuthMsg>SERVICE_KEY_IS_NOT_REGISTERED_ERROR</returnAuthMsg>
<returnReasonCode>30</returnReasonCode>
</cmmMsgHeader>
</OpenAPI_ServiceResponse>
제공기관 에러코드

제공기관 에러코드(에러코드,에러메세지,설명)
에러코드	에러메세지	설명
00	NORMAL_CODE	정상
01	APPLICATION_ERROR	어플리케이션 에러
02	DB_ERROR	데이터베이스 에러
03	NODATA_ERROR	데이터없음 에러
04	HTTP_ERROR	HTTP 에러
05	SERVICETIMEOUT_ERROR	서비스 연결실패 에러
10	INVALID_REQUEST_PARAMETER_ERROR	잘못된 요청 파라메터 에러
11	NO_MANDATORY_REQUEST_PARAMETERS_ERROR	필수요청 파라메터가 없음
12	NO_OPENAPI_SERVICE_ERROR	해당 오픈API서비스가 없거나 폐기됨
20	SERVICE_ACCESS_DENIED_ERROR	서비스 접근거부
21	TEMPORARILY_DISABLE_THE_SERVICEKEY_ERROR	일시적으로 사용할 수 없는 서비스 키
22	LIMITED_NUMBER_OF_SERVICE_REQUESTS_EXCEEDS_ERROR	서비스 요청제한횟수 초과에러
30	SERVICE_KEY_IS_NOT_REGISTERED_ERROR	등록되지 않은 서비스키
31	DEADLINE_HAS_EXPIRED_ERROR	기한만료된 서비스키
32	UNREGISTERED_IP_ERROR	등록되지 않은 IP
33	UNSIGNED_CALL_ERROR	서명되지 않은 호출
99	UNKNOWN_ERROR	기타에러

오퍼레이션 내용

지역코드조회-오퍼레이션 내용(오퍼레이션 번호,오퍼레이션 유형,오퍼레이션명(국문),오퍼레이션 설명,Call Back URL)
오퍼레이션 번호	1	오퍼레이션 유형	조회 (목록)
오퍼레이션명(국문)	지역코드조회
오퍼레이션 설명	지역코드, 시군구코드 목록을 조회하는 기능입니다.
지역기반 관광정보 및 키워드 검색을 통해 지역별로 목록을 보여줄 경우,
지역코드를 이용하여 지역명을 매칭하기 위한 기능입니다.
Call Back URL	http://apis.data.go.kr/B551011/KorService2/areaCode2
요청 메시지 (Request Parameter)

요청 메시지 (Request Parameter) (항목명(영문), 항목명(국문), 항목구분, 샘플데이터, 항목설명)
항목명(영문)	항목명(국문)	항목구분	샘플데이터	항목설명
numOfRows	한페이지결과수	0	10	한페이지결과수
pageNo	페이지번호	0	1	현재페이지번호
MobileOS	OS 구분	1	ETC	IOS (아이폰), AND (안드로이드), WIN (윈도우폰), ETC
MobileApp	서비스명	1	AppTest	서비스명=어플명
serviceKey	인증키(서비스키)	1	인증키
(URL- Encode)	공공데이터포털에서
발급받은인증키
areaCode	지역코드	0	1	지역코드, 시군구코드
_type	응답메세지 형식	0		REST방식의 URL호출시 Json값추가(디폴트 응답메시지 형식은XML)
※ 항목구분 :필수(1), 옵션(0)
응답 메시지 (Response Message)

응답 메시지 (Response Message) (항목명(영문), 항목명(국문), 항목구분, 샘플데이터, 항목설명)
항목명(영문)	항목명(국문)	항목구분	샘플데이터	항목설명
resultCode	결과코드	1	0000	결과코드
resultMsg	결과메시지	1	OK	결과메시지
numOfRows	한페이지결과수	1	10	한페이지결과수
pageNo	페이지번호	1	1	페이지번호
totalCount	전체결과수	1	25	전체결과수
code	코드	1	1	지역코드또는시군구코드
name	코드명	1	강남구	지역명또는시군구명
rnum	일련번호	1	1	일련번호
※ 항목구분 :필수(1), 옵션(0)
요청/응답메시지

REST (URI)
※지역코드정보를조회
https://apis.data.go.kr/B551011/KorService2/areaCode2?serviceKey=인증키&MobileApp=AppTest&MobileOS=ETC&pageNo=1&numOfRows=10
응답메시지

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response>
    <header>
        <resultCode>0000</resultCode>
        <resultMsg>OK</resultMsg>
    </header>
    <body>
        <items>
            <item>
                <rnum>1</rnum>
                <code>1</code>
                <name>서울</name>
            </item>
            <item>
                <rnum>2</rnum>
                <code>2</code>
                <name>인천</name>
            </item>
            <item>
                <rnum>3</rnum>
                <code>3</code>
                <name>대전</name>
            </item>
            <item>
                <rnum>4</rnum>
                <code>4</code>
                <name>대구</name>
            </item>
            <item>
                <rnum>5</rnum>
                <code>5</code>
                <name>광주</name>
            </item>
            <item>
                <rnum>6</rnum>
                <code>6</code>
                <name>부산</name>
            </item>
            <item>
                <rnum>7</rnum>
                <code>7</code>
                <name>울산</name>
            </item>
            <item>
                <rnum>8</rnum>
                <code>8</code>
                <name>세종특별자치시</name>
            </item>
            <item>
                <rnum>9</rnum>
                <code>31</code>
                <name>경기도</name>
            </item>
            <item>
                <rnum>10</rnum>
                <code>32</code>
                <name>강원특별자치도</name>
            </item>
        </items>
        <numOfRows>10</numOfRows>
        <pageNo>1</pageNo>
        <totalCount>17</totalCount>
    </body>
</response>

REST (URI)
※지역이 서울(areaCode=1)인 시군구코드정보를 조회
https://apis.data.go.kr/B551011/KorService2/areaCode2?serviceKey=인증키&MobileApp=AppTest&MobileOS=ETC&pageNo=1&numOfRows=10&areaCode=1
응답메시지

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response>
    <header>
        <resultCode>0000</resultCode>
        <resultMsg>OK</resultMsg>
    </header>
    <body>
        <items>
            <item>
                <rnum>1</rnum>
                <code>1</code>
                <name>강남구</name>
            </item>
            <item>
                <rnum>2</rnum>
                <code>2</code>
                <name>강동구</name>
            </item>
            <item>
                <rnum>3</rnum>
                <code>3</code>
                <name>강북구</name>
            </item>
            <item>
                <rnum>4</rnum>
                <code>4</code>
                <name>강서구</name>
            </item>
            <item>
                <rnum>5</rnum>
                <code>5</code>
                <name>관악구</name>
            </item>
            <item>
                <rnum>6</rnum>
                <code>6</code>
                <name>광진구</name>
            </item>
            <item>
                <rnum>7</rnum>
                <code>7</code>
                <name>구로구</name>
            </item>
            <item>
                <rnum>8</rnum>
                <code>8</code>
                <name>금천구</name>
            </item>
            <item>
                <rnum>9</rnum>
                <code>9</code>
                <name>노원구</name>
            </item>
            <item>
                <rnum>10</rnum>
                <code>10</code>
                <name>도봉구</name>
            </item>
        </items>
        <numOfRows>10</numOfRows>
        <pageNo>1</pageNo>
        <totalCount>25</totalCount>
    </body>
</response>

오퍼레이션 내용

서비스 분류 코드 조회-오퍼레이션 내용(오퍼레이션 번호,오퍼레이션 유형,오퍼레이션명(국문),오퍼레이션 설명,Call Back URL)
오퍼레이션 번호	2	오퍼레이션 유형	조회 (목록)
오퍼레이션명(국문)	서비스분류코드조회
오퍼레이션 설명	각 관광타입(관광지, 숙박등)에 해당하는 서비스분류코드를 대,중,소분류로 조회하는 기능입니다.
※ 25년 12월말까지만 활용가능하며, 신규 분류체계코드로 대체될 예정.
(TourAPI 사이트 > OpenAPI > OpenAPI 가이드 > 서비스 분류코드 검색 참고)
Call Back URL	http://apis.data.go.kr/B551011/KorService2/categoryCode2
요청 메시지 (Request Parameter)

요청 메시지 (Request Parameter) (항목명(영문), 항목명(국문), 항목구분, 샘플데이터, 항목설명)
항목명(영문)	항목명(국문)	항목구분	샘플데이터	항목설명
numOfRows	한페이지결과수	0	10	한페이지결과수
pageNo	페이지번호	0	1	현재페이지번호
MobileOS	OS 구분	1	ETC	IOS (아이폰), AND (안드로이드),
WIN (윈도우폰), ETC
MobileApp	서비스명	1	APPTest	서비스명=어플명
serviceKey	인증키(서비스키)	1	인증키(URL- Encode)	공공데이터포털에서 발급받은인증키
_type	응답메세지 형식	0		REST방식의 URL호출시 Json값추가(디폴트 응답메시지 형식은XML)
contentTypeId	관광타입 ID	0	12	관광타입(관광지, 숙박등) ID
cat1	대분류	0	A01	대분류코드
cat2	중분류	0	A0101	중분류코드(cat1 필수)
cat3	소분류	0	A01010100	소분류코드(cat1, cat2 필수)
※ 항목구분 :필수(1), 옵션(0)
응답 메시지 (Response Message)

응답 메시지 (Response Message) (항목명(영문), 항목명(국문), 항목구분, 샘플데이터, 항목설명)
항목명(영문)	항목명(국문)	항목구분	샘플데이터	항목설명
resultCode	결과코드	1	0000	결과코드
resultMsg	결과메시지	1	OK	결과메시지
numOfRows	한페이지결과수	1	10	한페이지결과수
pageNo	페이지번호	1	1	페이지번호
totalCount	전체결과수	1	7	전체결과수
code	코드	1	A01	대,중,소분류코드
name	코드명	1	자연	대,중,소분류코드명
rnum	일련번호	1	1	일련번호
※ 항목구분 :필수(1), 옵션(0)
요청/응답메시지

REST (URI)
※대분류전체코드를조회
https://apis.data.go.kr/B551011/KorService2/categoryCode2?serviceKey=인증키&MobileApp=AppTest&MobileOS=ETC&pageNo=1&numOfRows=10
응답메시지

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response>
    <header>
        <resultCode>0000</resultCode>
        <resultMsg>OK</resultMsg>
    </header>
    <body>
        <items>
            <item>
                <code>A01</code>
                <name>자연</name>
                <rnum>1</rnum>
            </item>
            <item>
                <code>A02</code>
                <name>인문(문화/예술/역사)</name>
                <rnum>2</rnum>
            </item>
            <item>
                <code>A03</code>
                <name>레포츠</name>
                <rnum>3</rnum>
            </item>
            <item>
                <code>A04</code>
                <name>쇼핑</name>
                <rnum>4</rnum>
            </item>
            <item>
                <code>A05</code>
                <name>음식</name>
                <rnum>5</rnum>
            </item>
            <item>
                <code>B02</code>
                <name>숙박</name>
                <rnum>6</rnum>
            </item>
            <item>
                <code>C01</code>
                <name>추천코스</name>
                <rnum>7</rnum>
            </item>
        </items>
        <numOfRows>7</numOfRows>
        <pageNo>1</pageNo>
        <totalCount>7</totalCount>
    </body>
</response>

REST (URI)
※대분류조회 :관광지(type=12) 대분류코드를조회
https://apis.data.go.kr/B551011/KorService2/categoryCode2?serviceKey=인증키&MobileApp=AppTest&MobileOS=ETC&pageNo=1&numOfRows=10&contentTypeId=12
응답메시지

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response>
    <header>
        <resultCode>0000</resultCode>
        <resultMsg>OK</resultMsg>
    </header>
    <body>
        <items>
            <item>
                <code>A01</code>
                <name>자연</name>
                <rnum>1</rnum>
            </item>
            <item>
                <code>A02</code>
                <name>인문(문화/예술/역사)</name>
                <rnum>2</rnum>
            </item>
        </items>
        <numOfRows>2</numOfRows>
        <pageNo>1</pageNo>
        <totalCount>2</totalCount>
    </body>
</response>

REST (URI)
※소분류조회 :관광지(type=12) 대분류가 자연(A01)이고 중분류가 자연관광지(A0101)인 소분류코드를 조회
https://apis.data.go.kr/B551011/KorService2/categoryCode2?serviceKey=인증키&MobileApp=AppTest&MobileOS=ETC&pageNo=1&numOfRows=10&contentTypeId=12&cat1=A01&cat2=A0101
응답메시지

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<response>
    <header>
        <resultCode>0000</resultCode>
        <resultMsg>OK</resultMsg>
    </header>
    <body>
        <items>
            <item>
                <code>A01010100</code>
                <name>국립공원</name>
                <rnum>1</rnum>
            </item>
            <item>
                <code>A01010200</code>
                <name>도립공원</name>
                <rnum>2</rnum>
            </item>
            <item>
                <code>A01010300</code>
                <name>군립공원</name>
                <rnum>3</rnum>
            </item>
            <item>
                <code>A01010400</code>
                <name>산</name>
                <rnum>4</rnum>
            </item>
            <item>
                <code>A01010500</code>
                <name>자연생태관광지</name>
                <rnum>5</rnum>
            </item>
            <item>
                <code>A01010600</code>
                <name>자연휴양림</name>
                <rnum>6</rnum>
            </item>
            <item>
                <code>A01010700</code>
                <name>수목원</name>
                <rnum>7</rnum>
            </item>
            <item>
                <code>A01010800</code>
                <name>폭포</name>
                <rnum>8</rnum>
            </item>
            <item>
                <code>A01010900</code>
                <name>계곡</name>
                <rnum>9</rnum>
            </item>
            <item>
                <code>A01011000</code>
                <name>약수터</name>
                <rnum>10</rnum>
            </item>
        </items>
        <numOfRows>10</numOfRows>
        <pageNo>1</pageNo>
        <totalCount>18</totalCount>
    </body>
</response>

오퍼레이션 내용

지역기반 관광정보 조회-오퍼레이션 내용(오퍼레이션 번호,오퍼레이션 유형,오퍼레이션명(국문),오퍼레이션 설명,Call Back URL)
오퍼레이션 번호	3	오퍼레이션 유형	조회 (목록)
오퍼레이션명(국문)	지역기반관광정보조회
오퍼레이션 설명	지역 및 시군구를 기반으로 관광정보 목록을 조회하는 기능입니다.
파라미터에 따라 제목순, 수정일순(최신순), 등록일순 정렬검색을 제공합니다.
Call Back URL	http://apis.data.go.kr/B551011/KorService2/areaBasedList2
요청 메시지 (Request Parameter)

요청 메시지 (Request Parameter) (항목명(영문), 항목명(국문), 항목구분, 샘플데이터, 항목설명)
항목명(영문)	항목명(국문)	항목구분	샘플데이터	항목설명
numOfRows	한페이지결과수	0	10	한페이지결과수
pageNo	페이지번호	0	1	현재페이지번호
MobileOS	OS 구분	1	ETC	IOS (아이폰), AND (안드로이드),
WIN (윈도우폰), ETC
MobileApp	서비스명	1	APPTest	서비스명=어플명
serviceKey	인증키(서비스키)	1	인증키(URL- Encode)	공공데이터포털에서 발급받은인증키
_type	응답메세지 형식	0		REST방식의 URL호출시 Json값추가(디폴트 응답메시지 형식은XML)
arrange	정렬구분	0	A	(A=제목순, C=수정일순, D=생성일순)
대표이미지 있는 콘텐츠 우선 정렬
(O=제목순, Q=수정일순, R=생성일순)
contentTypeId	관광타입 ID	0	32	관광타입(관광지, 숙박등) ID
areaCode	지역코드	0	4	지역코드
sigunguCode	시군구코드	0	4	시군구코드(areaCode필수)
cat1	대분류	0		대분류코드
cat2	중분류	0		중분류코드(cat1필수)
cat3	소분류	0		소분류코드(cat1, cat2필수)
modifiedtime	수정일	0		콘텐츠 수정일(YYYMMDD)
※ 항목구분 :필수(1), 옵션(0)
응답 메시지 (Response Message)

응답 메시지(항목명,항목설명,항목구분,샘플데이터,항목설명)
항목명	항목설명	항목구분	샘플데이터	항목설명
resultCode	결과코드	1	0000	응답결과코드
resultMsg	결과메시지	1	OK	응답결과메시지
numOfRows	한페이지결과수	1	10	한페이지결과수
pageNo	페이지번호	1	1	현재페이지번호
totalCount	전체결과수	1	3	전체결과수
addr1	주소	0	대구광역시 동구 동부로26길 6	주소(예, 서울중구다동)를 응답
addr2	상세주소	0		상세주소
areacode	지역코드	0	4	지역코드
cat1	대분류	0	B02	대분류코드
cat2	중분류	0	B0201	중분류코드
cat3	소분류	0	B02010100	소분류코드
contentid	콘텐츠ID	1	2721572	콘텐츠ID
contenttypeid	콘텐츠타입ID	1	32	관광타입(관광지, 숙박등) ID
createdtime	등록일	1	20210616144933	콘텐츠최초등록일
firstimage	대표이미지(원본)	0	http://tong.visitkorea.or.kr/cms/resource/53/2721553_image2_1.jpg	원본대표이미지(약500*333 size) URL 응답
firstimage2	대표이미지(썸네일)	0	http://tong.visitkorea.or.kr/cms/resource/53/2721553_image2_1.jpg	썸네일대표이미지(약 150*100 size) URL 응답
cpyrhtDivCd	저작권유형	0	Type1	Type1 : 제1유형(출처표시-권장), Type3 : 제3유형(제1유형 + 변경금지)
mapx	GPS X좌표	0	128.6276707967	GPS X좌표(WGS84 경도좌표) 응답
mapy	GPS Y좌표	0	35.8753644169	GPS Y좌표(WGS84 위도좌표) 응답
mlevel	Map Level	0	6	Map Level 응답
modifiedtime	수정일	1	20210625135116	콘텐츠수정일
sigungucode	시군구코드	0	4	시군구코드
tel	전화번호	0		전화번호
title	제목	1	대구메리어트 호텔	콘텐츠제목
zipcode	우편번호 =	0	41243	우편번호
※ 항목구분 :필수(1), 옵션(0)

오퍼레이션 내용

위치기반 관광정보 조회-오퍼레이션 내용(오퍼레이션 번호,오퍼레이션 유형,오퍼레이션명(국문),오퍼레이션 설명,Call Back URL)
오퍼레이션 번호	4	오퍼레이션 유형	조회 (목록)
오퍼레이션명(국문)	위치기반관광정보조회
오퍼레이션 설명	내 주변 좌표를 기반으로 관광정보 목록을 조회하는 기능입니다.
파라미터에 따라 제목순, 수정일순(최신순), 등록일순, 거리순정렬검색을 제공합니다.
Call Back URL	http://apis.data.go.kr/B551011/KorService2/locationBasedList2
요청 메시지 (Request Parameter)

요청 메시지 (Request Parameter) (항목명(영문), 항목명(국문), 항목구분, 샘플데이터, 항목설명)
항목명(영문)	항목명(국문)	항목구분	샘플데이터	항목설명
numOfRows	한페이지결과수	0	10	한페이지결과수
pageNo	페이지번호	0	1	현재페이지번호
MobileOS	OS 구분	1	ETC	IOS (아이폰), AND (안드로이드),
WIN (윈도우폰), ETC
MobileApp	서비스명	1	APPTest	서비스명=어플명
serviceKey	인증키(서비스키)	1	인증키(URL- Encode)	공공데이터포털에서 발급받은인증키
_type	응답메세지 형식	0		REST방식의 URL호출시 Json값추가(디폴트 응답메시지 형식은XML)
arrange	정렬구분	0	A	(A=제목순, C=수정일순, D=생성일순, E=거리순)
대표이미지 있는 콘텐츠 우선 정렬
(O=제목순, Q=수정일순, R=생성일순, S=거리순)
contentTypeId	관광타입 ID	0	15	관광타입(관광지, 숙박등) ID
mapX	x좌표	1	126.981611	GPS X좌표(WGS84 경도좌표)
mapY	y좌표	1	37.568477	GPS Y좌표(WGS84 경도좌표)
radius	거리반경	1	1000	거리반경(단위:m),
Max값 20000m=20Km
modifiedtime	수정일	0		콘텐츠수정일
※ 항목구분 :필수(1), 옵션(0)
응답 메시지 (Response Message)

응답 메시지 (Response Message)(항목명,항목설명,항목구분,샘플데이터,항목설명)
항목명	항목설명	항목구분	샘플데이터	항목설명
resultCode	결과코드	1	0000	응답결과코드
resultMsg	결과메시지	1	OK	응답결과메시지
numOfRows	한페이지결과수	1	10	한페이지결과수
pageNo	페이지번호	1	1	현재페이지번호
totalCount	전체결과수	1	155	전체결과수
addr1	주소	0	서울특별시 중구 명동길 26	주소(예, 서울중구다동)를 응답
addr2	상세주소	0	(명동2가)	상세주소
areacode	지역코드	0	1	지역코드
cat1	대분류	0	A02	대분류코드
cat2	중분류	0	A0208	중분류코드
cat3	소분류	0	A02080300	소분류코드
contentid	콘텐츠ID	1	406745	콘텐츠ID
contenttypeid	콘텐츠타입ID	1	15	관광타입(관광지, 숙박등) ID
createdtime	등록일	1	20080118180330	콘텐츠최초등록일
dist	거리	1	589.7653053395521	중심좌표로부터거리
(단위:m)
firstimage	대표이미지(원본)	0	http://tong.visitkorea.or.kr/cms/resource/86/2034286_image2_1.jpg	원본대표이미지
(약500*333 size) URL 응답
firstimage2	대표이미지(썸네일)	0	http://tong.visitkorea.or.kr/cms/resource/86/2034286_image3_1.jpg	썸네일대표이미지
(약 150*100 size) URL 응답
cpyrhtDivCd	저작권유형	0	Type1	Type1 : 제1유형(출처표시-권장), Type3 : 제3유형(제1유형 + 변경금지)
mapx	GPS X좌표	0	126.9837456304	GPS X좌표(WGS84 경도좌표) 응답
mapy	GPS Y좌표	0	37.5634463660	GPS Y좌표(WGS84 위도좌표) 응답
mlevel	Map Level	0	6	Map Level 응답
modifiedtime	수정일	1	20210125132029	콘텐츠수정일
sigungucode	시군구코드	0	24	시군구코드
tel	전화번호	0	02-739-8288	전화번호
title	제목	1	난타 (명동)	콘텐츠제목
※ 항목구분 :필수(1), 옵션(0)

오퍼레이션 내용

키워드 검색 조회-오퍼레이션 내용(오퍼레이션 번호,오퍼레이션 유형,오퍼레이션명(국문),오퍼레이션 설명,Call Back URL)
오퍼레이션 번호	5	오퍼레이션 유형	조회 (목록)
오퍼레이션명(국문)	키워드검색조회
오퍼레이션 설명	키워드로 검색을 하여 관광타입별 또는 전체 목록을 조회하는 기능입니다.
파라미터에 따라 제목순, 수정일순(최신순), 등록일순 정렬검색을 제공합니다.
Call Back URL	http://apis.data.go.kr/B551011/KorService2/searchKeyword2
요청 메시지 (Request Parameter)

요청 메시지 (Request Parameter) (항목명(영문), 항목명(국문), 항목구분, 샘플데이터, 항목설명)
항목명(영문)	항목명(국문)	항목구분	샘플데이터	항목설명
numOfRows	한페이지결과수	0	10	한페이지결과수
pageNo	페이지번호	0	1	현재페이지번호
MobileOS	OS 구분	1	ETC	IOS (아이폰), AND (안드로이드),
WIN (윈도우폰), ETC
MobileApp	서비스명	1	AppTest	서비스명=어플명
serviceKey	인증키(서비스키)	1	인증키(URL- Encode)	공공데이터포털에서 발급받은인증키
_type	응답메세지 형식	0		REST방식의 URL호출시 Json값추가(디폴트 응답메시지 형식은XML)
arrange	정렬구분	0	C	(A=제목순, C=수정일순, D=생성일순)
대표이미지 있는 콘텐츠 우선 정렬
(O=제목순, Q=수정일순, R=생성일순)
contentTypeId	관광타입 ID	0	38	관광타입(관광지, 숙박등) ID
areaCode	지역코드	0		지역코드
sigunguCode	시군구코드	0		시군구코드(areaCode필수)
cat1	대분류	0		대분류코드
cat2	중분류	0		중분류코드(cat1 필수)
cat3	소분류	0		소분류코드(cat1, cat2필수)
keyword	요청키워드	1	시장	검색요청할키워드(국문=인코딩필요)
※ 항목구분 :필수(1), 옵션(0)
응답 메시지 (Response Message)

응답 메시지 (Response Message)(항목명,항목설명,항목구분,샘플데이터,항목설명)
항목명	항목설명	항목구분	샘플데이터	항목설명
resultCode	결과코드	1	0000	응답결과코드
resultMsg	결과메시지	1	OK	응답결과메시지
numOfRows	한페이지결과수	1	10	한페이지결과수
pageNo	페이지번호	1	1	현재페이지번호
totalCount	전체결과수	1	745	전체결과수
addr1	주소	0	강원특별자치도 평창군 진부면 진부시장안길 23	주소 응답
addr2	상세주소	0		상세주소
areacode	지역코드	0	32	지역코드
cat1	대분류	0	A04	대분류코드
cat2	중분류	0	A0401	중분류코드
cat3	소분류	0	A04010100	소분류코드
contentid	콘텐츠ID	1	132533	콘텐츠ID
contenttypeid	콘텐츠타입ID	1	38	관광타입(관광지, 숙박등) ID
createdtime	등록일	1	20020422090000	콘텐츠최초등록일
firstimage	대표이미지(원본)	0	http://tong.visitkorea.or.kr/cms/resource/62/3379362_image2_1.jpg	원본대표이미지(약500*333 size) URL 응답
firstimage2	대표이미지(썸네일)	0	http://tong.visitkorea.or.kr/cms/resource/62/3379362_image3_1.jpg	썸네일대표이미지(약 150*100 size) URL 응답
cpyrhtDivCd	저작권유형	0	Type3	Type1 : 제1유형(출처표시-권장), Type3 : 제3유형(제1유형 + 변경금지)
mapx	GPS X좌표	0	128.5582979332	GPS X좌표(WGS84 경도좌표) 응답
mapy	GPS Y좌표	0	37.6378280865	GPS Y좌표(WGS84 위도좌표) 응답
mlevel	Map Level	0	6	Map Level 응답
modifiedtime	수정일	1	20250108173352	콘텐츠수정일
sigungucode	시군구코드	0	15	시군구코드
tel	전화번호	0	033-335-3133	전화번호
title	제목	1	진부장 / 진부시장 (3, 8일)	콘텐츠제목
※ 항목구분 :필수(1), 옵션(0)

오퍼레이션 내용

행사정보조회-오퍼레이션 내용(오퍼레이션 번호,오퍼레이션 유형,오퍼레이션명(국문),오퍼레이션 설명,Call Back URL)
오퍼레이션 번호	6	오퍼레이션 유형	조회 (목록)
오퍼레이션명(국문)	행사정보조회
오퍼레이션 설명	행사/공연/축제 정보를 날짜로 조회하는 기능입니다.
콘텐츠 타입이 “행사/공연/축제”인 경우만 유효합니다.
파라미터에 따라 제목순, 수정일순(최신순), 등록일순 정렬검색을 제공합니다.
Call Back URL	http://apis.data.go.kr/B551011/KorService2/searchFestival2
요청 메시지 (Request Parameter)

요청 메시지 (Request Parameter) (항목명(영문), 항목명(국문), 항목구분, 샘플데이터, 항목설명)
항목명(영문)	항목명(국문)	항목구분	샘플데이터	항목설명
numOfRows	한페이지결과수	0	10	한페이지결과수
pageNo	페이지번호	0	1	현재페이지번호
MobileOS	OS 구분	1	ETC	IOS (아이폰), AND (안드로이드),
WIN (윈도우폰), ETC
MobileApp	서비스명	1	APPTest	서비스명=어플명
serviceKey	인증키(서비스키)	1	인증키(URL- Encode)	공공데이터포털에서 발급받은인증키
_type	응답메세지 형식	0		REST방식의 URL호출시 Json값추가(디폴트 응답메시지 형식은XML)
arrange	정렬구분	0	A	(A=제목순, C=수정일순, D=생성일순)
대표이미지 있는 콘텐츠 우선 정렬
(O=제목순, Q=수정일순, R=생성일순)
areaCode	지역코드	0		지역코드
sigunguCode	시군구코드	0		시군구코드(areaCode필수)
eventStartDate	행사시작일	1	20170901	행사시작일
(형식: YYYYMMDD)
eventEndDate	행사종료일	0		행사종료일
(형식: YYYYMMDD)
modifiedtime	수정일	0		콘텐츠수정일
※ 항목구분 :필수(1), 옵션(0)
응답 메시지 (Response Message)

응답 메시지 (Response Message)-(항목명,항목설명,항목구분,샘플데이터,항목설명)
항목명	항목설명	항목구분	샘플데이터	항목설명
resultCode	결과코드	1	0000	응답결과코드
resultMsg	결과메시지	1	OK	응답결과메시지
numOfRows	한페이지결과수	1	10	한페이지결과수
pageNo	페이지번호	1	1	현재페이지번호
totalCount	전체결과수	1	758	전체결과수
addr1	주소	0	충청남도 부여군 규암면백제문로 388	주소(예, 서울중구다동)를 응답
addr2	상세주소	0		상세주소
areacode	지역코드	0	34	지역코드
cat1	대분류	0	A02	대분류코드
cat2	중분류	0	A0208	중분류코드
cat3	소분류	0	A02080100	소분류코드
contentid	콘텐츠ID	1	1258353	콘텐츠ID
contenttypeid	콘텐츠타입ID	1	15	관광타입(관광지, 숙박등) ID
createdtime	등록일	1	20110418095420	콘텐츠최초등록일
eventstartdate	행사시작일	1	20210306	행사시작일
(형식: YYYYMMDD)
eventenddate	행사종료일	1	20211030	행사종료일
(형식: YYYYMMDD)
firstimage	대표이미지(원본)	0	http://tong.visitkorea.or.kr/cms/resource/54/2483454_image2_1.JPG	원본대표이미지(약500*333 size) URL 응답
firstimage2	대표이미지(썸네일)	0	http://tong.visitkorea.or.kr/cms/resource/54/2483454_image2_1.JPG	썸네일대표이미지(약 150*100 size) URL 응답
cpyrhtDivCd	저작권유형	0	Type1	Type1 : 제1유형(출처표시-권장), Type3 : 제3유형(제1유형 + 변경금지)
mapx	GPS X좌표	0	128.8997016069	GPS X좌표(WGS84 경도좌표) 응답
mapy	GPS Y좌표	0	36.3048428766	GPS Y좌표(WGS84 위도좌표) 응답
mlevel	Map Level	0	6	Map Level 응답
modifiedtime	수정일	1	20210226112411	콘텐츠수정일
sigungucode	시군구코드	0	6	시군구코드
tel	전화번호	0	041-832-5765	전화번호
title	제목	1	가무악극으로 만나는 토요 상설공연	콘텐츠제목
※ 항목구분 :필수(1), 옵션(0)

오퍼레이션 내용

숙박 정보 조회-오퍼레이션 내용(오퍼레이션 번호,오퍼레이션 유형,오퍼레이션명(국문),오퍼레이션 설명,Call Back URL)
오퍼레이션 번호	7	오퍼레이션 유형	조회 (목록)
오퍼레이션명(국문)	숙박정보조회
오퍼레이션 설명	숙박정보 중 베니키아,한옥,굿스테이목록을 조회하는 기능입니다.
콘텐츠 타입이 “숙박”인 경우만 유효합니다.
파라미터에 따라 제목순, 수정일순(최신순), 등록일순 정렬검색을 제공합니다.
Call Back URL	http://apis.data.go.kr/B551011/KorService2/searchStay2
요청 메시지 (Request Parameter)

요청 메시지 (Request Parameter) (항목명(영문), 항목명(국문), 항목구분, 샘플데이터, 항목설명)
항목명(영문)	항목명(국문)	항목구분	샘플데이터	항목설명
numOfRows	한페이지결과수	0	10	한페이지결과수
pageNo	페이지번호	0	1	현재페이지번호
MobileOS	OS 구분	1	ETC	IOS (아이폰), AND (안드로이드),
WIN (윈도우폰), ETC
MobileApp	서비스명	1	APPTest	서비스명=어플명
serviceKey	인증키(서비스키)	1	인증키(URL- Encode)	공공데이터포털에서 발급받은인증키
_type	응답메세지 형식	0		REST방식의 URL호출시 Json값추가(디폴트 응답메시지 형식은XML)
arrange	정렬구분	0	A	(A=제목순, C=수정일순, D=생성일순)
대표이미지 있는 콘텐츠 우선 정렬
(O=제목순, Q=수정일순, R=생성일순)
areaCode	지역코드	0		지역코드
sigunguCode	시군구코드	0		시군구코드(areaCode필수)
modifiedtime	수정일	0		콘텐츠수정일
※ 항목구분 :필수(1), 옵션(0)
응답 메시지 (Response Message)

응답 메시지 (Response Message)-(항목명,항목설명,항목구분,샘플데이터,항목설명)
항목명	항목설명	항목구분	샘플데이터	항목설명
resultCode	결과코드	1	0000	응답결과코드
resultMsg	결과메시지	1	OK	응답결과메시지
numOfRows	한페이지결과수	1	10	한페이지결과수
pageNo	페이지번호	1	1	현재페이지번호
totalCount	전체결과수	1	1	전체결과수
addr1	주소	0	전라남도여수시시청서6길 3	주소(예, 서울중구다동)를 응답
addr2	상세주소	0	(학동)	상세주소
areacode	지역코드	0	38	지역코드
cat1	대분류	0	B02	대분류코드
cat2	중분류	0	B0201	중분류코드
cat3	소분류	0	B02010100	소분류코드
contentid	콘텐츠ID	1	1942328	콘텐츠ID
contenttypeid	콘텐츠타입ID	1	32	관광타입(관광지, 숙박등) ID
createdtime	등록일	1	20140819161042	콘텐츠최초등록일
firstimage	대표이미지(원본)	0	http://tong.visitkorea.or.kr/cms/resource/05/1942305_image2_1.jpg	원본대표이미지(약500*333 size) URL 응답
firstimage2	대표이미지(썸네일)	0	http://tong.visitkorea.or.kr/cms/resource/05/1942305_image2_1.jpg	썸네일대표이미지(약 150*100 size) URL 응답
cpyrhtDivCd	저작권유형	0	Type1	Type1 : 제1유형(출처표시-권장), Type3 : 제3유형(제1유형 + 변경금지)
mapx	GPS X좌표	0	127.6572385410	GPS X좌표(WGS84 경도좌표) 응답
mapy	GPS Y좌표	0	34.7563147754	GPS Y좌표(WGS84 위도좌표) 응답
mlevel	Map Level	0	6	Map Level 응답
modifiedtime	수정일	1	20170414154308	콘텐츠수정일
sigungucode	시군구코드	0	13	시군구코드
tel	전화번호	0	061-686-2000	전화번호
title	제목	1	베니키아나르샤호텔	콘텐츠제목
※ 항목구분 :필수(1), 옵션(0)

오퍼레이션 내용

공통정보 조회-오퍼레이션 내용(오퍼레이션 번호,오퍼레이션 유형,오퍼레이션명(국문),오퍼레이션 설명,Call Back URL)
오퍼레이션 번호	8	오퍼레이션 유형	조회 (목록)
오퍼레이션명(국문)	공통정보조회
오퍼레이션 설명	타입별 공통정보(제목, 연락처, 주소, 좌표, 개요정보 등)를 조회하는 기능입니다.
Call Back URL	http://apis.data.go.kr/B551011/KorService2/detailCommon2
요청 메시지 (Request Parameter)

요청 메시지 (Request Parameter) (항목명(영문), 항목명(국문), 항목구분, 샘플데이터, 항목설명)
항목명(영문)	항목명(국문)	항목구분	샘플데이터	항목설명
numOfRows	한페이지결과수	0	10	한페이지결과수
pageNo	페이지번호	0	1	현재페이지번호
MobileOS	OS 구분	1	ETC	IOS (아이폰), AND (안드로이드),
WIN (윈도우폰), ETC
MobileApp	서비스명	1	APPTest	서비스명=어플명
serviceKey	인증키(서비스키)	1	인증키(URL- Encode)	공공데이터포털에서 발급받은인증키
_type	응답메세지 형식	0		REST방식의 URL호출시 Json값추가(디폴트 응답메시지 형식은XML)
contentId	콘텐츠ID	1	126508	콘텐츠ID
※ 항목구분 :필수(1), 옵션(0)
응답 메시지 (Response Message)

응답 메시지 (Response Message) (항목명, 항목설명, 항목구분, 샘플데이터, 항목설명)
항목명	항목설명	항목구분	샘플데이터	항목설명
resultCode	결과코드	1	0000	응답결과코드
resultMsg	결과메시지	1	OK	응답결과메시지
numOfRows	한페이지결과수	1	10	한페이지결과수
pageNo	페이지번호	1	1	현재페이지번호
totalCount	전체결과수	1	3	전체결과수
contentid	콘텐츠ID	1	126508	콘텐츠ID
contenttypeid	콘텐츠타입ID	1	12	관광타입(관광지, 숙박등) ID
createdtime	등록일	1	20031230000000	콘텐츠최초등록일
hmpg	홈페이지주소	0	경복궁<a href="http://www.royalpalace.go.kr/" target="_blank" title="새창 :경복궁홈페이지로이동">http://www.royalpalace.go.kr</a><br /> …<이하생략>	홈페이지주소
modifiedtime	수정일	1	20170825173054	콘텐츠수정일
tel	전화번호	0		전화번호
telname	전화번호명	0		전화번호명
title	콘텐츠명(제목)	1	경복궁	콘텐츠명(제목)
firstimage	대표이미지(원본)	0	http://tong.visitkorea.or.kr/cms/resource/23/2678623_image2_1.jpg	원본대표이미지 (약 500*333 size) URL 응답
firstimage2	대표이미지(썸네일)	0	http://tong.visitkorea.or.kr/cms/resource/23/2678623_image3_1.jpg	썸네일대표이미지 (약 150*100 size) URL 응답
cpyrhtDivCd	저작권유형	0	Type1	Type1 : 제1유형(출처표시-권장), Type3 : 제3유형(제1유형 + 변경금지)
areacode	지역코드	0	1	지역코드
sigungucode	시군구코드	0	23	시군구코드
cat1	대분류	0	A02	대분류코드
cat2	중분류	0	A0201	중분류코드
cat3	소분류	0	A02010100	소분류코드
addr1	주소	0	서울특별시 종로구 사직로 161	주소(예, 서울중구다동)를응답
addr2	상세주소	0		상세주소
zipcode	우편번호	0	03045	우편번호
mapx	GPS X좌표	0	126.9769930325	GPS X좌표(WGS84 경도좌표) 응답
mapy	GPS Y좌표	0	37.578822235	GPS Y좌표(WGS84 위도좌표) 응답
mlevel	Map Level	0	6	Map Level 응답
overview	개요	0	경복궁은 1395년태조이성계에의해서새로운조선왕조의법궁으로지어졌다. 경복궁은동궐(창덕궁)이나서궐(경희궁)에비해위치가북쪽에있어 '북궐'이라불리기도했다. …<이하생략>	콘텐츠개요조회
※ 항목구분 :필수(1), 옵션(0)

서비스 목록

서비스 목록(서비스 구분,서비스명)
서비스 구분	서비스명
영문	EngService2
일문	JpnService2
중문간체	ChsService2
중문번체	ChtService2
독어(독일어)	GerService2
불어(프랑스어)	FreService2
서어(스페인어)	SpnService2
노어(러시아어)	RusService2
- 다국어 API는 제공 항목이 모두 같기 때문에, 본 문서에서는 영문을 기준으로 TourAPI 서비스 이용방법을 설명합니다.
- 일문 관광정보 등을 이용하기 위해서는 아래와 같이 URL중 서비스명 부분을 수정하여 사용합니다.
  예) http://apis.data.go.kr/B551011/JpnService/~~
- 개별 API를 활용신청해야 사용 가능합니다.
  서비스 개요

서비스 개요(서비스 정보,서비스 보안,적용 기술 수준,서비스 URL,서비스 WADL,서비스 배포정보,메시지 교환 유형,메시지 로깅 수준,사용 제약 사항,서비스 제공자,데이터 갱신주기)
서비스 정보	서비스ID	EngService2
서비스명(국문)	영문 관광정보 서비스
서비스명(영문)	EngService2
서비스 설명	코드조회 및 관광정보의 통합/상세검색 등의 기능을 제공하며 위치기반, 지역기반 국내 관광에 대한 전반적인 정보를 영문으로 제공합니다.
서비스 보안	서비스 인증/권한	[O]서비스 Key  [ ]인증서(GPKI)
[ ]Basic(ID/PW)  [ ]없음	[ ]WS-Security
메시지 레벨 암호화	[ ]전자서명  [ ]암호화  [O]없음
전송 레벨 암호화	[ ]SSL  [O]없음
적용 기술 수준	인터페이스 표준	[ ]SOAP 1.2
(PRC-Encoded, Document Literal, Document Literal Wrapped)
[O]REST (GET)
[ ]RSS 1.0   [ ]RSS 2.0  [ ]Atom 1.0  [ ]기타
교환 데이터 표준	[O]XML  [O]JSON  [ ]MIME  []MTOM
서비스 URL	개발환경	http://apis.data.go.kr/B551011/EngService2
운영환경	http://apis.data.go.kr/B551011/EngService2
서비스 WADL	개발환경	N/A
운영환경	N/A
서비스 배포정보	서비스 버전	1.1
서비스 시작일	2012-12-31	배포 일자	N/A
서비스 이력	N/A
메시지 교환 유형	[O]Request-ResPonse   [ ]Publish-Subscribe
[ ]Fire-and-Forgot   [ ]Notification
메시지 로깅 수준	성공	[O]Header  [ ]Body	실패	[O]Header  [O]Body
사용 제약 사항 (비고)	N/A
서비스 제공자	TourAPI운영팀/ 디지털콘텐츠팀 / 033-738-3874 / tourapi@knto.or.kr
데이터 갱신주기	일 1회
서비스 명세

서비스명세(번호,서비스명(국문),오퍼레이션,오퍼레이션명)
번호	서비스명(국문)	오퍼레이션	오퍼레이션명
1	영문 관광정보
서비스	areaCode	지역코드조회
2	categoryCode2	서비스 분류코드 조회
3	areaBasedList2	지역기반 관광정보 조회
4	locationBasedList2	위치기반 관광정보 조회
5	searchKeyword2	키워드 검색 조회
6	searchFestival2	행사정보 조회
7	searchStay2	숙박정보 조회
8	detailCommon2	공통정보 조회 (상세정보1)
9	detailIntro2	소개정보 조회 (상세정보2)
10	detailInfo2	반복정보 조회 (상세정보3)
11	detailImage2	이미지정보 조회 (상세정보4)
다국어콘텐츠 타입(ContentTypeId)코드표

다국어콘텐츠 타입(ContentTypeId)코드표(그룹,타입 종류,ContentTypeId코드)
그룹	타입 종류	ContentTypeId코드
관광정보
(다국어 공통)	관광지	76
문화시설	78
행사/공연/축제	85
여행코스	국문만 서비스
레포츠	75
숙박	80
쇼핑	79
음식점	82
교통(다국어만 서비스)	77
공공데이터 포털 에러코드

공공데이터 포털 에러코드(에러코드,에러메세지,설명)
에러코드	에러메세지	설명
01	APPLICATION_ERROR	어플리케이션 에러
12	NO_OPENAPI_SERVICE_ERROR	해당 오픈API서비스가 없거나 폐기됨
20	SERVICE_ACCESS_DENIED_ERROR	서비스 접근거부
22	LIMITED_NUMBER_OF_SERVICE_REQUESTS_EXCEEDS_ERROR	서비스 요청제한횟수 초과에러
30	SERVICE_KEY_IS_NOT_REGISTERED_ERROR	등록되지 않은 서비스키
31	DEADLINE_HAS_EXPIRED_ERROR	활용기간 만료
32	UNREGISTERED_IP_ERROR	등록되지 않은 IP
99	UNKNOWN_ERROR	기타에러
※ 공공데이터포털에서출력되는오류메세지는 XML로만출력되며, 형태는아래와같습니다.
<OpenAPI_ServiceResponse>
<cmmMsgHeader>
<errMsg>SERVICE ERROR</errMsg>
<returnAuthMsg>SERVICE_KEY_IS_NOT_REGISTERED_ERROR</returnAuthMsg>
<returnReasonCode>30</returnReasonCode>
</cmmMsgHeader>
</OpenAPI_ServiceResponse>
