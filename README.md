# 4조 포포먼스(POPOMANCE) 백엔드 팀

> **TTS, VC, Concat 음성 처리 웹 사이트** with AI Park  
> backend url : http://api.popomance.kr/
> frontend url : http://dev.popomance.kr/

## 1. 프로젝트 소개

TTS, VC, Concat 음성 처리 웹 사이트를 개발하는 프로젝트입니다.

- TTS(Text To Speech): 사용자가 입력한 텍스트를 음성으로 변환합니다.
- VC(Voice Conversion): 사용자가 입력한 음성을 원하는 목소리로 변환합니다.
- Concat: 사용자가 입력한 여러개의 음성을 하나의 오디오 파일로 병합합니다.

## 2. 프로젝트 기술 스택

## **1. 언어 & 프레임워크**
- **언어**: Java 17
- **프레임워크**: Spring Boot 3.3.5

## **2. 데이터베이스**
- **RDBMS**: MySQL  

## **3. 주요 Dependencies (라이브러리)**
- Lombok  
- Spring Data JPA  
- MySQL Driver  
- QueryDSL  
- SpringAMQP (RabbitMQ)  
- Spring Cloud AWS  

## **4. 실시간 통신**
- Server-Sent Events (SSE)

## **5. 인프라**
- **AWS EC2**: 서버 호스팅  
- **Docker**: 컨테이너 환경 구성  
- **GitHub Actions**: CI/CD 자동화  


## 3. 구현 기능

### [ 주요 기능 ]

#### < TTS >

1. **텍스트 -> 음성 변환**
    - 사용자가 입력한 텍스트를 음성으로 변환합니다.
2. **옵션 설정**
    - 목소리, 속도, 볼륨, 피치 등 옵션을 설정할 수 있습니다.
    - 다양한 국가의 언어를 지원합니다.
3. **음성 파일 다운로드**
    - 생성한 음성 파일을 다운로드할 수 있습니다.
4. **프로젝트 저장**
    - 현재 프로젝트 상태를 저장해서 이후에 불러올 수 있습니다.

#### < VC >

1. **음성 -> 음성 변환**
    - 사용자가 입력한 음성을 원하는 목소리로 변환합니다.
2. **텍스트 파일 업로드**
    - 오디오에 대한 스크립트 파일을 업로드할수 있습니다.
3. **음성 파일 다운로드**
    - 생성한 음성 파일을 다운로드할 수 있습니다.
4. **프로젝트 저장**
    - 현재 프로젝트 상태를 저장해서 이후에 불러올 수 있습니다.

#### < Concat >

1. **음성 파일 병합**
    - 사용자가 입력한 여러개의 음성을 하나의 오디오 파일로 병합합니다.
    - 병합 순서를 설정할 수 있습니다.
2. **텍스트 파일 업로드**
    - 오디오에 대한 스크립트 파일을 업로드할수 있습니다.
3. **음성 파일 다운로드**
    - 생성한 음성 파일을 다운로드할 수 있습니다.
4. **프로젝트 저장**
    - 현재 프로젝트 상태를 저장해서 이후에 불러올 수 있습니다.

### [ 워크스페이스 ]

1. **HOME**
    - 서비스에 대한 간단한 소개를 제공합니다.
    - 사용장의 최근 프로젝트와 생성한 오디오 목록을 제공합니다.
2. **PROJECT**
    - 사용자의 프로젝트를 관리합니다.
    - 프로젝트 이름, 프로젝트 상태, 프롲게트 유형, 프로젝트 내용으로 검색 가능합니다.
    - 선택한 프로젝트를 삭제할 수 있습니다.
    - 페이지 기능을 제공합니다.
3. **EXPORTS**
    - 사용자가 생성한 오디오 파일을 관리합니다.
    - 오디오 파일 이름, 오디오 상태, 프로젝트 유형, 오디오 내용으로 검색 가능합니다.
    - 선택한 오디오 파일을 삭제할 수 있습니다.
    - 페이지 기능을 제공합니다.

### [ 오디오 관리 (S3) ]

1. **오디오 파일 버킷에 업로드**
    - 서비스에서 다루는 모든 오디오를 S3 버킷에 업로드합니다.
2. **오디오 파일 버킷에서 삭제**
    - 프로젝트 삭제시 해당 프로젝트에서 사용한 오디오 파일을 S3 버킷에서 즉시 삭제합니다.
    - 개별 오디오 파일 삭제시 S3 버킷에서 즉시 삭제합니다.
3. **오디오 파일 스케줄링 관리**
    - 화면상에서 삭제할 수 없는 오디오에 대해 스케줄링을 통해 삭제합니다.
    - 오래된 소스 오디오 파일에 대해 스케줄링을 통해 삭제합니다.
    - 매일 자정에 삭제할 오디오가 있는지 확인하고 삭제합니다.

### [ 사용자 관리 ]

1. **회원가입**
    - 회원 이메일(ID)에 대해 중복 검사를 수행합니다.
2. **로그인**
    - 회원 이메일 찾기 기능을 제공합니다.
    - 회원 비밀번호 찾기 기능을 제공합니다.
3. **로그아웃**
4. **회원정보 수정**
    - 다음과 같은 회원 정보에 대해 수정이 가능합니다.
        - 회원 전화번호
        - 회원 이름
        - 회원 비밀번호

### [ 다중 처리 ]

1. **작업 큐**
    - TTS/VC/Concat 오디오 생성 작업이 큐에 담겨 비동기로 처리됩니다.
2. **실패 작업 재실행**
    - 실패된 작업을 별도의 큐에 관리하며, '실패 작업 재실행' 버튼 클릭 시 작업을 재개 할 수 있습니다.
3. **작업 초기화**
    - 대기 중인 모든 작업을 초기화(삭제) 합니다.
4. **실시간 통신**
    - 작업 상태(대기/진행/실패/완료)가 변경될 때마다 실시간으로 작업 상태를 확인합니다.


## 4. 구성원 역할

- **오승민** (백엔드 팀장) : 팀 운영 및 관리, 프로젝트 구조 설계/환경 설정, 버전 관리 체계 구축(Git 브랜치 전략 및 협업 워크플로우 설정), DB 모델링(요구사항분석, ERD설계), TTS 음성 데이터 크롤링, 다중 작업 큐 처리(RabbitMQ 설정, 작업큐 관리, 에러 핸들링), 실시 작업 조회/초기화, TTS/VC/Concat 프로젝트 불러오기&삭제 API 개발

- **남유람** : 

- **안재홍** : DB 모델링, 회원 기능 (회원가입, 이메일 중복 체크, 회원 ID 찾기, 비밀번호 찾기), VC 생성 기능 (기존 기능 통합 및 확장: S3 오디오 파일 업/다운로드, 로컬 및 S3 오디오 결합 처리, 프로젝트 생성 및 상태 관리, 멀티 파일 처리 지원), Swagger API 문서화 기여, 주요 비즈니스 로직 테스트 및 예외 처리 검증

- **이의준** : CI/CD & 배포 등 인프라, 프로젝트 초기 세팅, DB 모델링, 초기 JPA 엔티티 모델링, S3 버킷 오디오 업/다운로드, 오디오 파일 스케줄링 관리, Concat - ( 오디오 삭제 &
  프로젝트 삭제 & 상태 저장 ), 워크스페이스 - (최근 프로젝트 내역 조회 & 최근 생성한 오디오 내역 조회 & 프로젝트 검색 및 페이징 & 생성한 오디오 검색 및 페이징), ~~리드미 작성~~,

- **정원우** : TTS 생성 기능 (텍스트 입력 기반 음성 합성 로직 개발 및 Google TTS API 연동), CONCAT 생성 기능 (FFMPEG를 활용한 오디오 파일 병합, 간격 삽입 및 품질 최적화), 회원 기능 (데이터 저장 및 인증/유효성 검사 로직 개발), DB 모델링, 로깅 및 오류 처리 구조 개선, Swagger API 문서화 기여, 주요 비즈니스 로직 테스트 및 예외 처리 검증

- **조소정** : 프로젝트 설계 참여, S3 버킷 오디오 업/다운로드, 오디오 삭제,  오디오 파일 스케줄링 관리, 워크스페이스 - (최근 프로젝트 내역 조회 & 최근 생성한 오디오 내역 조회 & 생성한 오디오 검색 및 페이징) , 테스트, Swagger Api 작성

## 5. 추가 자료

### [ ERD ]

- https://www.erdcloud.com/d/EwCmMrDjMXKrMRipa

### [ 시연 영상 ]

### [ 팀 노션 ]

- https://www.notion.so/4-b105aaddd7bc42c9842b92c98e465b67

