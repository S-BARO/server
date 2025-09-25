# 👕 스와이프로 찾는 내 스타일, 바로(BARO)
[**[Part 0] 스와이프로 찾는 내 스타일, ‘바로’를 기획하며..(Ft. 기술적 목표)**](https://chobo-backend.tistory.com/49)

- 무신사, 에이블리, 퀸잇과 같은 패션 E-Commerce 플랫폼입니다
- 유튜브, 인스타를 통해 여러 코디들을 살펴보는 과정에서 옷을 구매하기 위해 여러 플랫폼을 오가며 찾는 과정이 번거로웠습니다
- ‘바로(BARO)’는 이러한 불편함을 해결하기 위해 코디를 직관적이고 빠르게 탐색할 수 있는 스와이프(좌우로 사진을 넘기며 좋아요/싫어요)를 핵심 기능으로 두었습니다
- 또한 AI 가상 피팅 기능(Google Nano Banana 활용)을 통해 옷을 직접 입어보지 못하는 E-Commerce의 단점을 보완하고 구매를 망설여하는 사용자들의 구매 전환율을 높일 수 있도록 했습니다


<br>

# 👨‍👧‍👦 Team

| <img src="https://avatars.githubusercontent.com/u/67588757?v=4" width="150" height="150"/> | <img src="https://avatars.githubusercontent.com/u/118061713?v=4" width="150" height="150"/> |
| :----------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------: |
|                            [howu](https://github.com/choihooo)                             |                             [Hee Sang](https://github.com/codrin2)                         |
|                            FrontEnd                             |                             PM / BackEnd                             |


<br>

# 🗂️ 코드 구조도

```
src/
├── main/
│   ├── kotlin/
│   │   └── com/dh/baro/
│   │               ├── cart            # 장바구니 관련 모듈
│   │               │   ├── application   # 애플리케이션 서비스
│   │               │   ├── domain        # 도메인 엔티티 및 리포지토리
│   │               │   └── presentation  # API (Controller) 및 DTO
│   │               ├── core            # 공통 유틸리티 및 설정
│   │               │   ├── annotation    # 커스텀 어노테이션
│   │               │   ├── auth          # 세션 관리 및 인증
│   │               │   ├── config        # Spring, Redis, Kafka 설정
│   │               │   ├── dlq           # Dead-Letter Queue
│   │               │   ├── serialization # 직렬화
│   │               │   ├── outbox        # Transactional Outbox 패턴
│   │               │   └── exception     # 커스텀 예외
│   │               ├── identity        # 사용자 및 로그인 관련 모듈
│   │               │   ├── application
│   │               │   ├── domain      
│   │               │   ├── infra         # 외부 API 연동
│   │               │   └── presentation
│   │               ├── look            # 룩(코디) 관련 모듈
│   │               ├── order           # 주문 관련 모듈
│   │               ├── product         # 상품 관련 모듈
│   │               └── BaroApplication.kt
│   └── resources                       # 리소스 파일 (설정, 스크립트 등)
│       ├── application-dev.yaml          # 개발 환경 설정
│       ├── application-prod.yaml         # 운영 환경 설정
│       └── lua                           # Redis Lua 스크립트 파일
└── test/                               # 테스트 관련 코드
    ├── kotlin                            # 테스트 코드
    └── resources                         # 테스트용 리소스
```

<br>

# 🗺️ System Architecture

<img width="1356" height="1024" alt="스크린샷 2025-09-18 오후 11 29 42" src="https://github.com/user-attachments/assets/ea287da6-bbef-4869-ba23-d1ae17e0d084" />


<br>

# 💾 ERD
[**[바로(BARO) ERD]**](https://dbdiagram.io/d/BARO_ERD-6870cfbaf413ba3508661df3)

<img width="1139" height="905" alt="image" src="https://github.com/user-attachments/assets/939f8592-2480-4811-89cf-9699243414ed" />


<br>

# 📈 목표 성능
MAU 5만명, DAU 5,000명(DAU/MAU 비율 10%) 기준

동시 접속자 수 : 500명(DAU/10)

목표 응답 속도 : 200ms

<br>

# 🤔 Technical Issue
[**[Part 1] JWT는 정말 괜찮은 방법일까? (Ft. 세션저장소 선택 이유)**](https://chobo-backend.tistory.com/50)

[**[Part 2] 확장성과 성능을 고려한 ERD 설계하기**](https://chobo-backend.tistory.com/51)

[**[Part 3] 분산 시스템에서 ID가 유일하려면?(Ft. Snowflake VS TSID 성능테스트)**](https://chobo-backend.tistory.com/52)

[**[Part 4] 반복되는 인증,인가 처리 없애버리기(Ft. AOP & ArgumentResolver)**](https://chobo-backend.tistory.com/53)

[**[Part 5] 단일 주문 성능 개선 삽질기 (Ft. JPA save, FK)**](https://chobo-backend.tistory.com/54)

[**[Part 6] DeadLock 범인 찾기 (Ft. 위험한 FK?)**](https://chobo-backend.tistory.com/55)

[**[Part 7] 일괄 주문 기능 개선 Vol.1 (Ft. Eventual Consistency, Lua Script)**](https://chobo-backend.tistory.com/56)

[**[Part 8] 일괄 주문 기능 개선 Vol.2 (Ft. Kafka, Transactional Outbox)**](https://chobo-backend.tistory.com/61)

[**[Part 9] Redis의 Lua Script는 Atomic 하지 않다..?**](https://chobo-backend.tistory.com/57)
