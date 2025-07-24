# 프로젝트 이름

이 프로젝트는 관리자 페이지를 포함하는 웹 애플리케이션입니다.

## 프로젝트 구조

```
.
├── backend
│   └── ... (스프링 부트 백엔드 관련 파일)
└── frontend
    └── ... (Next.js 프론트엔드 관련 파일)
```

- **backend**: Spring Boot로 구현된 API 서버입니다.
- **frontend**: Next.js로 구현된 관리자 페이지 웹 애플리케이션입니다.

## 실행 방법

### 백엔드

1. `backend` 디렉토리로 이동합니다.
2. 다음 명령어를 실행하여 Spring Boot 애플리케이션을 실행합니다.

```bash
./gradlew bootRun
```

> **참고:** 백엔드 서버는 50850, 50858, 50871 포트에서 실행됩니다.

### 프론트엔드

1. `frontend` 디렉토리로 이동합니다.
2. 다음 명령어를 실행하여 필요한 라이브러리를 설치합니다.

```bash
npm install
```

3. 다음 명령어를 실행하여 Next.js 개발 서버를 실행합니다.

```bash
npm run dev
```

4. 브라우저에서 `http://localhost:3000`으로 접속하여 관리자 페이지를 확인할 수 있습니다.


# GEULGWI-AOS

## Build Notes

If you encounter build failures related to non-ASCII characters in the project path (especially on Windows), the `android.overridePathCheck=true` line has been added to `gradle.properties` to bypass this issue. It is highly recommended to move the project to a path containing only ASCII characters for long-term stability and to avoid potential future build problems.


17로 변경
