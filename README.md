<h1>Glim(글:림)</h1>

## 📄 목차
- [📄 목차](#-목차)
- [✍🏻 프로젝트 개요](#✍🏻-프로젝트-개요)
- [🚀 핵심 기능](#🚀-핵심-기능)
- [⚙️ 기술 스택](#️-기술-스택)
- [🏛️ 시스템 아키텍처](#️-시스템-아키텍처)
- [🧡 팀원 소개](#-팀원-소개)
<br />

## ✍ 프로젝트 개요
`글귀 + 울림`, ‘글귀 + film’ 또는 ‘glimpse(흘낏보다)’의 조합. 
짧지만 깊은 인상을 남기는 글귀를 공유하며, 
숏츠(글림으로 명명) 폼으로 다른 사람들과 감성을 공유하며 
글림을 통해 책에 대한 관심을 유발하고,
알라딘 등 온라인 서점으로 연결하여 구매를 유도

### 기획의도
현대인의 디지털 과부하 문제
- 무분별한 숏폼 콘텐츠 소비 (틱톡, 인스타 릴스)
- 자극적이고 빠른 콘텐츠에 익숙해진 뇌
- 집중력 저하 및 깊이 있는 사고 부족
- 책 읽기 시간과 독서량 급격한 감소

글림의 디지털 디톡스 접근법
"완전한 차단이 아닌, 건전한 대안 제시"

### 서비스 소개
- 개발 기간: 25.07.07 ~ 25.08.18
- 인원: 6명
- 서비스 개요 : [도서 중개 웹/앱 플랫폼 서비스] 글:림, Glim
- **SSAFY 공통프로젝트 우수상 수상**

<br />

## 🚀 핵심 기능
### Android

> **나만의 글귀 생성**
![스크린샷](/ScreenShot/나만의글귀생성.gif) 

| **나만의 글귀 생성** | **글림 둘러보기** | **글림 공유 & 저장** |
|----------------------|-------------------|----------------------|
| <img src="/ScreenShot/도서_글귀큐레이션.gif" width="300"/> | <img src="/ScreenShot/글귀둘러보기.gif" width="300"/> | <img src="/ScreenShot/공유저장.gif" width="300"/> |


| **글귀 기반**<br>**AI 배경 이미지 생성** | **글귀 확인** | **마이페이지**| **잠금화면 글귀 설정** |
|--------|------|------|------|
| ![스크린샷](/ScreenShot/Screenshot_1755143017.png) | ![스크린샷](/ScreenShot/Screenshot_1755142934.png) |  ![스크린샷](/ScreenShot/Screenshot_1755143065.png) | ![스크린샷](/ScreenShot/Screenshot_1755143056.png) |


| **글귀(글림), 도서,**<br>**작가 검색** | **인기 글귀 조회** | **키워드/테마별**<br>**큐레이션** | **도서 상세 정보** | **관련 글귀 보기** |
|------|--------|------|-----|------|
| ![스크린샷](/ScreenShot/Screenshot_1755142916.png) | ![스크린샷](/ScreenShot/Screenshot_1755142903.png) | ![스크린샷](/ScreenShot/Screenshot_1755142911.png) |  ![스크린샷](/ScreenShot/Screenshot_1755142951.png)  | ![스크린샷](/ScreenShot/Screenshot_1755142956.png) | 

### Web Front

> **랜딩페이지**
![스크린샷](/ScreenShot/랜딩페이지.gif) 

| **사용자 정보 수정 및 삭제**| **글귀편집 관리 및 삭제** | **큐레이션 관리 및 편지** |
|--------|------|------|
| ![스크린샷](/ScreenShot/어드민페이지.png) | ![스크린샷](/ScreenShot/어드민페이지2.png) | ![스크린샷](/ScreenShot/어드민페이지.png) | 



## ⚙️ 기술 스택
| **파트** | **기술 스택** |
|----------|---------------|
| **Android** | Kotlin 2.1.12, ComposeUI, MVI(Orbit), Hilt, MLKit(OCR), Datastore, Coroutine-Flow |
| **Backend** | Java 21, Spring Boot, Spring Data JPA, Spring Web (REST API), Spring Actuator, Flyway, OpenFeign, JWT, Mustache, Spring REST Docs, JaCoCo, JUnit 5, H2 Database |
| **Frontend** | React, Redux, TypeScript |


## 🏛️ 시스템 아키텍처
<img src="./ScreenShot/image.png" alt="대체 텍스트" width="600">


## 🧡 팀원 소개

| [윤문정](https://github.com/yuuuuuu32) | [박성준](https://github.com/park99999) | [홍지표](https://github.com/devMuscle) | [오인성](https://github.com/ois0886) | [박승준](https://github.com/ootr47) | [윤준석](https://github.com/JunSeok-Yun) |
|:---:|:---:|:---:|:---:|:---:|:---:|
| <img src="https://avatars.githubusercontent.com/u/207114587?v=4" alt="대체 텍스트" width="600"> | <img src="https://avatars.githubusercontent.com/u/111122515?v=4" alt="대체 텍스트" width="600"> | <img src="https://avatars.githubusercontent.com/u/91146369?v=4" alt="대체 텍스트" width="600"> | <img src="https://avatars.githubusercontent.com/u/58154638?v=4" alt="대체 텍스트" width="600"> | <img src="https://avatars.githubusercontent.com/u/83055885?v=4" alt="대체 텍스트" width="600"> | <img src="https://avatars.githubusercontent.com/u/109099464?v=4" alt="대체 텍스트" width="600"> |
| 웹프론트 | **팀장**<br/>안드로이드, 백엔드, 디자인 | 백엔드, 인프라 | 안드로이드, 디자인 | 안드로이드, 디자인 | 백엔드 |
