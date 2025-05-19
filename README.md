# 프로젝트
- Cody Suggest System

# Spec
- JDK 17
- Gradle 8.x
- Spring Boot 3.2.3
- H2 Database

# 빌드 방법
> ./gradlew build

# 전체 테스트 방법 (unit + integration)
> ./gradlew test

# integration 테스트 방법
> ./gradlew test --tests "musinsa.recruitmemt.integration.AdminAndStatsIntegrationTest"

# 애플리케이션 실행 
> ./gradlew bootRun

> ./gradlew build
> 
> java -jar build/libs/codySuggestService-1.0-SNAPSHOT.jar

# REST API
1. 카테고리별 최저가
> GET /api/items/lowest-prices

2. 최저가 브랜드 총액
> GET /api/items/lowest-brand

3. 카테고리 가격 범위 API
> GET /api/items/category-range?categoryName={categoryName}

# WEB 
> http://localhost:8080/

카테고리별 최저가
> http://localhost:8080/items/lowest-prices

브랜드별 총액
> http://localhost:8080/items/brand-totals

카테고리 가격 범위 API


상품관리
> http://localhost:8080/items

브랜드관리
> http://localhost:8080/brands



# 작업 목록
- CS-1: 규모추정 & 시스템디자인
- CS-2: 요구사항 작성
- CS-3: 테이블 설계
- CS-4: 테이블 데이터 초기화 및 프로젝트 구조 설계
- CS-5: api 서버 구현 - 관리자기능
- CS-6: api 서버 구현 - 코디통계기능
- CS-7: ui 구현
- CS-8: 문서화
- CS-9: 고가용성을 위한 설계

# CS-2: 요구사항 작성
관리자기능
1. 브랜드 추가, 삭제, 변경 관리
- 브랜드 제거 시 상품도 제거한다.
2. 상품 추가, 삭제, 변경 관리
_- 브랜드, 카테고리 당 아이템은 N개 존재할 수 있다.
3. 브랜드, 카테고리별 요약 페이지
- 요약 페이지에서는 브랜드, 카테고리 당 가장 최신 아이템만 표출한다.


코디통계기능
1. 카테고리별 최저가
- 카테고리 별로 최저 가격인 브랜드와 가격 총액 확인
2. 브랜드별 총액
- 단일 브랜드로 전체 카테고리 상품을 구매할 경우 최저가격인 브랜드와 가격 총액 확인
3. 카테고리별 최저, 최고 가격
- 특정 카테고리에서 최저가격 브랜드와 최고가격 브랜드 및 가격 확인

# CS-3 : 테이블 설계

### Brand (브랜드)
| 컬럼명 | 타입 | 설명 | 제약조건 |
|--------|------|------|-----------|
| brand_id | BIGINT | 브랜드 식별자 | PRIMARY KEY, AUTO_INCREMENT |
| brand_name | VARCHAR | 브랜드명 | NOT NULL, UNIQUE |

### Category (카테고리)
| 컬럼명 | 타입 | 설명 | 제약조건 |
|--------|------|------|-----------|
| category_id | BIGINT | 카테고리 식별자 | PRIMARY KEY, AUTO_INCREMENT |
| category_name | VARCHAR | 카테고리명 | NOT NULL, UNIQUE |

### Item (상품)
| 컬럼명 | 타입 | 설명 | 제약조건 |
|--------|------|------|-----------|
| item_id | BIGINT | 상품 식별자 | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR | 상품명 | NOT NULL |
| price | INTEGER | 가격 | NOT NULL, > 0 |
| brand_id | BIGINT | 브랜드 외래키 | FOREIGN KEY (brand.brand_id) |
| category_id | BIGINT | 카테고리 외래키 | FOREIGN KEY (category.category_id) |
| update_time | TIMESTAMP | 수정 시간 | NOT NULL, DEFAULT CURRENT_TIMESTAMP |

## 관계
- Item - Brand: Many-to-One
  - 하나의 브랜드는 여러 상품을 가질 수 있음
  - 상품은 하나의 브랜드에만 속함
  - 브랜드 삭제 시 관련 상품도 함께 삭제 (CASCADE)

- Item - Category: Many-to-One
  - 하나의 카테고리는 여러 상품을 가질 수 있음
  - 상품은 하나의 카테고리에만 속함

## 인덱스
- Brand: brand_name (UNIQUE)
- Category: category_name (UNIQUE)
- Item: brand_id, category_id (외래키 인덱스)
- Item: update_time (최신 상품 조회 성능 향상)

## JPA 설정
- Item 엔티티
  - `@PrePersist`, `@PreUpdate`: update_time 자동 갱신
  - Brand, Category와의 관계: `FetchType.LAZY`로 설정하여 성능 최적화

## 데이터 정합성
- 상품 가격은 0보다 커야 함
- 브랜드명과 카테고리명은 중복될 수 없음
- 모든 상품은 반드시 브랜드와 카테고리를 가져야 함
- 상품 생성/수정 시 update_time이 자동으로 갱신됨