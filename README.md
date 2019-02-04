### 아라봇 프로젝트

| 이름 | 역할 |
|---|---|
| [ara.js](https://github.com/computerpark/ara.js) | `ara-android` 에서 돌아가는 스크립트 |
| [ara-rest](https://github.com/computerpark/ara-rest) | 아라봇 API 서버 |
| [**ara-android**](https://github.com/computerpark/ara-android) (여기) | `ara.js`를 구동하는 플랫폼 |

### 아라봇 구현 정도  
`(O)` - 구현 완료  
`(!)` - 구현 완료 되었으나 버그픽스 Needed  
`(Δ)` - 개발 중  
`(X)` - 개발 예정  
`(?)` - 개발 필요 여부 확실치 않음

| 기능 | ara.js | ara-rest | ara-android | 비고 |
|---|:---:|:---:|:---:|---|
| 단순 응답| O | - | - |  | 
| 급식 | ! | ! | - | 서버 속도 개선 필요 |
| 실시간 검색어 | X | X | - |  |
| 날씨 | ! | ! | - | `json` 타입으로 기온/날씨/강수량 등 따로 리턴하도록 수정 필요, 네이버 크롤링 아닌 Geolocation / Api 방식 변경 필요 |
| 미세먼지 | ! | ! | - | 날씨 `json`에서 미세먼지 데이터만 빼오기 |
| 이미지 인식 | X | X | X | `ara-android` `imageDB.getImage()` Implement 필요, Google API 사용  |
| 학교조회 | X | X | - | csv 파일로 조회하던지 아님 API를 찾던지 |
| TTS | X | X | ? | `Google Wavenet`인가? 그거 아님 네이버꺼 써야지 |
| 지하철 차량번호 조회 | X | X | - | (서울시 openapi) |
| 버스정보 조회 | X | X | - | (api 어딘가에 있을꺼야) |
| 길찾기 (a to b) | X | X | - | (이왕이면 카카오지도) |
| 뜻 찾기 / 위키 요약 | X | X | - | 제대로된 예외처리 및 분석 알고리즘 |
| LMGTFY | X | X | - |  |
| 단축 URL | X | ? | - | 자체 서비스 개발(`go.oror.kr`)? OR 존재 api? | 
| 도배 | O | ? | - | 도배 기능 개선 (비상정지 등) |
| 비상정지 | ! | - | X | `ara-android` 차원에서 디바이스를 주물럭거려도 될듯 |
| 오픈챗 욕 모니터링 | Δ | O | - |  |
| 관리자모드 (권한관리) | X | ? | ? | 특정 명령어는 관리자만. 근데 사칭방지 기능 필요 |
| 채팅분석 | X | X | ? | 채팅 데이터를 받는 즉시 모두 ara-android 아님 ara-rest가 저장하는 방법 검토 |
| 시간표 | X | X | - | ~~하드코딩 가즈ㅏㅏㅏ~~ ㄴㄴ Web UI로 입력 어떰? |
| 데일리 브리핑 (날짜, 시간표, 급식, 뉴스등 요약) | X | ? | ? | 추가 API가 필요할지 모르것네 |
| 뉴스 | X | X | X | `RSS`? |
| whois 조회 | X | X | - | api가 필요할듯 |
| 한강물온도 | O | - | - | ㅋㅋㅋㅋ |
| 도움말 | O | - | - |  |
| 버전 / 디버그 정보 | O | O | X | `ara-android`에서 버전정보 리턴하는 `Utils.getAppVersion(packagename : String)` 함수 만들ㄱㄱ |
| 봇끼리 맞선보기 ㅋ | ? | ? | ? | 심각하게 고려를 좀... |
| ~~인공지능 Tenserflow 언어학습~~ | ? | ? | ? | [테이]()가 생각난다 |
| 자동 업데이트 | X | X | X | 스크립트를 하나 더 만드는 방식으로 가던지, ara-rest가 처리해주던지. 어쨌든 서버는 필요하다 ㅋ |
| 재부팅 (su) | X | X | X | 얘도 스크립트 하나 더 만들어야 할듯 아님 ara-rest가 처리해주던가, 그리고 루팅된 기기가 필요하네 어떡하지 |
| Web 관리 UI | X | X | X | 웹으로 ara-android 디바이스 컨트롤(ex: 재부팅)까지 가능하면 짱짱 ㅋㅋ ~~현실은 뷁뷁~~|
| 타 기기 PUSH 알림 | X | X | X | 서버 필요 |


# 안드로이드 Arabot Agent
[![Travis CI Build](https://travis-ci.com/ComputerPark/ara-android.svg?branch=master)](https://travis-ci.com/ComputerPark/ara-android)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
라이센스: GPL v3. 즉, 소스 먹튀 금지.


~~Messenger봇이 그냥 싫어서~~ Messenger봇을 입맛대로 개조한 봇입니다. 덕분에 아주 완벽하게 호환이 됩니다.


https://github.com/mir99712/KakaotalkBot 에서 Violet XF님의 소스를 포크하였습니다.

--- 업데이트

Messenger봇의 소스가 내려갔습니다. ~~나쁜 새키들~~
