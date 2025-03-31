# Count10Shop

## 간단 소개
아이템을 선착순으로 구매를 할 수 있는 쇼핑몰
    
싸게 잘 구매하는 방법은 쿠폰을 얻은 다음에
    
싸게 올라와 있는 아이템을 빠르게 구매해보세요!

## 주요 기능
- **상품 탐색 및 검색**: 상품에 있는 이름, 카테고리, 설명으로 빠르게 원하는 아이템을 찾을 수 있습니다.
- **결제 시스템**: 한정판 아이템을 동시성 처리를 통해서 공정하게 구매를 할 수 있습니다.
- **쿠폰**: 상품을 더 싸게 구매할 수 있도록 한정수량 쿠폰을 생성 및 관리, 발급을 받을 수 있습니다.
- **인기순위**: 사용자들이 많이 찾은 아이템을 인기순위로 확인할 수 있습니다!
- **JWT를 이용한 회원가입**: JWT를 통한 인증인가 시스템을 구현하였습니다.

## 기술 스택
- **백엔드**: Spring Boot, Spring Security
- **검색 엔진**: ElasticSearch, Kibana
- **로그 수집**: Log Stash
- **데이터베이스**: Redis, Mysql, JPA, H2
- **배포 및 인프라**: Docker, AWS EC2, AWS ECR
- **테스트 툴**: nGrinder, JMeter
- **기타 도구**: Git, GitHub Actions

## 프로젝트 구조
```plaintext
count10shop/ 
├── Dockerfile.elasticsearch
├── Dockerfile.kibana
├── Dockerfile.logstash
├── Dockerfile.spring
├── docker-compose.yml
├── logstash
│   └── pipeline
└── src
    └── main
        ├── java
        │   └── xyz
        │       └── tomorrowlearncamp
        │           └── count10shop
        │               ├── Count10shopApplication.java
        │               ├── auth
        │               ├── config
        │               └── domain
        │                   ├── common
        │                   ├── coupon
        │                   ├── item
        │                   ├── payment
        │                   ├── popular
        │                   └── user
        └── resources
            ├── application-dev.yml
            ├── application-release.yml
            ├── application.yml
            ├── elastic
            │   ├── item-mapping.json
            │   └── item-setting.json
            ├── static
            └── templates
                └── naver-intellij-formatter.xml
```

## 커밋 컨밴션
    https://www.notion.so/teamsparta/Commit-Conventions-1c02dc3ef5148018a22dea7b72cba9db?pvs=4

## 코드 컨밴션
    https://www.notion.so/teamsparta/Code-Convention-1c02dc3ef514811e9ce6d9526d7db22a?pvs=4

## 와이어프레임
    https://docs.google.com/presentation/d/1t5SFCsNv7WwHh9oiKu0ee6jRXS_p2aBbdlW9QAsUn2M/edit?usp=sharing
    
## ERD
    https://www.erdcloud.com/d/XFXMNFyoi47oZ3KnJ

## API 명세서
    https://www.notion.so/teamsparta/10-1c02dc3ef514801a89fefffeda79ea66?pvs=4#1c02dc3ef514818aa79cf21fe6b45c08

## 트러블 슈팅 및 학습점
    https://www.notion.so/teamsparta/10-1c02dc3ef514801a89fefffeda79ea66?pvs=4#1c72dc3ef514806e87b3fcfae344f7ee

## 시연 영상
    https://www.notion.so/teamsparta/10-1c02dc3ef514801a89fefffeda79ea66?pvs=4#1c02dc3ef514817487dde3d28e2467a7

## 노션 링크
    https://www.notion.so/teamsparta/10-1c02dc3ef514801a89fefffeda79ea66

## 팀원 소개
- **이은성** - 팀장 / 인기순위, CICD
  
      https://strnetwork.tistory.com/

      https://github.com/mixedsider/

  
- **황제** - 팀원 / 상품, 결제, nGrinder

      https://velog.io/@jhwang2/posts

      https://github.com/JeisaNewbie
  
- **백승민** - 팀원 / 검색

      https://seungmin576.tistory.com/

      https://github.com/songkongji
  
- **유은호** - 팀원 / Log, ELK stask

      https://velog.io/@mapleclover/posts

      https://github.com/mapleclover
  
- **문성준** - 팀원 / 쿠폰, JMeter

      https://velog.io/@ohoh7391/posts

      https://github.com/sjMun09

---
