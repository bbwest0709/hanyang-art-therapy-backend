# 프로젝트 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [기술 스택](#기술-스택)
3. [API 명세서](#api-명세서)
4. [파일 관리 시스템 및 인프라 설계](#파일-관리-시스템-및-인프라-설계)
5. [구현 의도](#구현-의도)
6. [구현 코드](#구현-코드)
7. [트러블 슈팅](#트러블-슈팅)
8. [프로젝트 링크](#프로젝트-링크)

<br>

## 🎨 프로젝트 개요

- **프로젝트 주제** : 한양대학교 미술치료학과 홈페이지
- **프로젝트 목적** : 졸업 전시회 작품 수록 및 학과 정보 전달  
- **프로젝트 기간** : 2025. 04. 28 ~ 2025. 05. 20 (약 3주간, 1차 프로젝트 기간)  

<br>

## 👥 팀 구성

| 이름   | 담당                                                       |
|--------|------------------------------------------------------------|
| 유서정 | 작가 및 첨부파일 관리                                       |
| 김여원 | 전시회, 작품 관리 및 마이페이지                             |
| 김경아 | 댓글 및 갤러리 조회/검색                                    |
| 문민아 | 인증/인가 관련                                             |

<br>

## ⚙️ 기술 스택

![Java 17](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white) 
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=Spring%20Boot&logoColor=white) 
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=Spring-Security&logoColor=white) 
![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=JSON%20web%20tokens&logoColor=white) 
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat-square&logo=java&logoColor=white) 
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=flat-square&logo=mysql&logoColor=white) 
![Git](https://img.shields.io/badge/Git-E44C30?style=flat-square&logo=git&logoColor=white) 
![Notion](https://img.shields.io/badge/Notion-000000?style=flat-square&logo=notion&logoColor=white) 
![Slack](https://img.shields.io/badge/Slack-4A154B?style=flat-square&logo=slack&logoColor=white)

<br>

## 📄 API 명세서

▶️ [API 명세서 자세히보기](https://www.notion.so/third-arrow-bb79/1e5257a3a337807eb96cc42ed1ccd902?v=1e5257a3a3378144bf9d000c06ed7a20)

| API 명칭               | HTTP 메서드 | 엔드포인트                          | 설명                                      |
|------------------------|-------------|-------------------------------------|-------------------------------------------|
| **파일 업로드**         | POST        | `/api/files`                        | 여러 파일을 업로드합니다.                |
| **파일 삭제 (Soft)**    | DELETE      | `/api/files/{filesNo}`              | 파일을 soft delete 처리합니다.           |
| **파일 삭제 (Hard)**    | 자동 실행   | 매주 일요일 03:00 (Scheduled Task)  | soft 삭제된 파일 또는 미사용 파일을 완전 삭제 |
| **작가 등록**           | POST        | `/api/admin/artists`                | 새로운 작가를 등록합니다.                |
| **작가 단일 조회**      | GET         | `/api/admin/artists/{artistsNo}`    | 특정 작가 정보를 조회합니다.             |
| **작가 전체 조회**      | GET         | `/api/admin/artists`                | 모든 작가 목록을 조회합니다.             |
| **작가 정보 수정**      | PATCH       | `/api/admin/artists/{artistsNo}`    | 특정 작가 정보를 수정합니다.             |
| **작가 삭제**           | DELETE      | `/api/admin/artists/{artistsNo}`    | 특정 작가 정보를 삭제합니다.             |


<br>

## 💾 파일 관리 시스템 및 인프라 설계

#### ✅ 파일 저장소 설계 및 전환
- 로컬 및 AWS S3 저장소를 모두 지원하는 유연한 파일 저장 구조 설계  
- 환경 프로파일(`@Profile`) 기반 저장소 자동 전환 구조 설계  
- AWS CloudFront를 통한 정적 리소스 응답 속도 개선  

#### ✅ 파일 업로드 처리 및 무결성 검증
- 확장자 검증 및 UUID 기반 파일명 생성  
- 예외 처리를 통한 보안성 및 데이터 무결성 강화  

#### ✅ 자동화 및 클린업
- Spring `@Scheduled` 기반의 파일 수명 주기 자동 관리 시스템 구축  
- 미사용 또는 삭제된 파일을 주기적으로 정리하여 서버 자원 최적화  
- 매주 일요일 오전 3시에 다음 기준에 따라 하드 삭제 수행:
  - **Soft Delete 대상**: `useYn = false && delYn = true`
  - **미사용 파일**: 5일 이상 등록만 되어 있고 사용되지 않은 파일  

#### ✅ 파일 삭제 정책 (Soft vs. Hard Delete)

| 구분         | 처리 방식 |
|--------------|-----------|
| **Soft Delete** | 클라이언트 요청 시 즉시 DB에서 `useYn=false`, `delYn=true` 플래그 변경 <br> (실제 파일은 삭제하지 않음) |
| **Hard Delete** | 자동 스케줄러(`@Scheduled`)가 조건에 따라 실제 파일 시스템(S3 또는 로컬)에서 파일을 삭제하고 DB에서도 제거 |

#### ✅ 인프라 보안 및 에러 핸들링
- EC2 인스턴스에 MFA 필수 인증 적용으로 운영 보안 강화  
- 전역 예외 처리기 구현으로 일관된 에러 응답 제공  


<br>

## 🛠️ 구현 의도

#### 📁 저장소 자동 전환 구조 설계
- 배포 환경이 확정되지 않은 초기 상황에서도 유연하게 대응할 수 있도록 로컬 파일 시스템과 AWS S3 + CloudFront를 모두 지원  
- Spring `@Profile`을 활용하여 로컬 ↔ 클라우드 저장소 전환을 자동화  

#### 🔐 파일 업로드 유효성 검증 강화
- 업로드 파일의 확장자를 검증하여 허용된 형식만 저장되도록 제한  
- UUID 기반 고유 파일명을 적용하여 파일명 충돌 방지 및 추적성 강화  

#### 🧹 자동화된 파일 수명 주기 관리
- Spring `@Scheduled`를 활용하여 매주 일요일 오전 3시에 미사용 또는 삭제 대상 파일 자동 정리  
- `useYn`, `delYn` 플래그 기반의 유연한 삭제 정책 설계  

#### 🛡️ 시스템 보안 및 예외 처리
- AWS EC2 인스턴스에 MFA 인증 필수 설정을 적용하여 운영 접근 보안 강화  
- `@ControllerAdvice` 기반 전역 예외 처리기를 통해 에러 응답을 일관되게 제공  

<br>

<details>
  <summary>📁 구현 코드</summary>

| 영역      | 파일명 (링크)                                                                                              |
|-----------|--------------------------------------------------------------------------------------------------------------------------|
| Controller| [`FileController.java`](https://github.com/bbwest0709/hanyang-art-therapy-backend/blob/dev/src/main/java/com/hanyang/arttherapy/controller/FileController.java)                   |
| Service   | [`FileStorageUtils.java`](https://github.com/bbwest0709/hanyang-art-therapy-backend/blob/dev/src/main/java/com/hanyang/arttherapy/service/FileStorageUtils.java)                  |
| Service   | [`FileScheduledService.java`](https://github.com/bbwest0709/hanyang-art-therapy-backend/blob/dev/src/main/java/com/hanyang/arttherapy/service/FileScheduledService.java)          |
| Service   | [`FileStorageService.java`](https://github.com/bbwest0709/hanyang-art-therapy-backend/blob/dev/src/main/java/com/hanyang/arttherapy/service/FileStorageService.java)                |
| Domain    | [`Files.java`](https://github.com/bbwest0709/hanyang-art-therapy-backend/blob/dev/src/main/java/com/hanyang/arttherapy/domain/Files.java)                                        |
| Domain    | [`FilesType.java`](https://github.com/bbwest0709/hanyang-arttherapy-backend/blob/dev/src/main/java/com/hanyang/arttherapy/domain/enums/FilesType.java)                           |
| Domain    | [`FileExtension.java`](https://github.com/bbwest0709/hanyang-arttherapy-backend/blob/dev/src/main/java/com/hanyang/arttherapy/domain/enums/FileExtension.java)                   |
| Domain    | [`Artists.java`](https://github.com/bbwest0709/hanyang-arttherapy-backend/blob/dev/src/main/java/com/hanyang/arttherapy/domain/Artists.java)                                    |
| Controller| [`ArtistController.java`](https://github.com/bbwest0709/hanyang-arttherapy-backend/blob/dev/src/main/java/com/hanyang/arttherapy/controller/ArtistController.java)                |
| Service   | [`ArtistsService.java`](https://github.com/bbwest0709/hanyang-arttherapy-backend/blob/dev/src/main/java/com/hanyang/arttherapy/service/ArtistsService.java)                      |
| Repository| [`ArtistsRepository.java`](https://github.com/bbwest0709/hanyang-arttherapy-backend/blob/dev/src/main/java/com/hanyang/arttherapy/repository/ArtistsRepository.java)              |
| Exception | [`CustomException.java`](https://github.com/bbwest0709/hanyang-arttherapy-backend/blob/dev/src/main/java/com/hanyang/arttherapy/common/exception/CustomException.java)           |
| Exception | [`GlobalExceptionHandler.java`](https://github.com/bbwest0709/hanyang-arttherapy-backend/blob/dev/src/main/java/com/hanyang/arttherapy/common/exception/GlobalExceptionHandler.java) |

</details>

<details>
  <summary>🐞 트러블 슈팅</summary>
  🔗 <a href="https://www.notion.so/third-arrow-bb79/216257a3a3378025901eda3850d5c5de?showMoveTo=true&saveParent=true" target="_blank">파일 시스템 트러블 슈팅</a>

</details>

<details>
  <summary>🔗 프로젝트 링크</summary>

- 🔗 [한양대학교 미술치료학과 홈페이지](https://hy-erica-arttherapy.com/)

</details>
