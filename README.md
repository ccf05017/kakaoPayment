# KakaoPayment
카카오페이 사전과제 프로젝트(20200513)

## 개발 프레임워크
- Java 1.8
- Spring Boot Web 2.2.7
- Spring Boot Data JPA 2.2.7
- Lombok
- Spring Doc Open API

## 테이블 설계
### 1. CARD_COMPANY_INFO table
| Column | Type | 
|:--------|:--------|
| ID | BIGINT |
| STRING_DATA | VARCHAR(500) |

### 2. PAYMENT_HISTORY table
| Column | Type | 
|:--------|:--------|
| ID | BIGINT |
| CANCELED | BOOLEAN |
| ENCRYPTED_CARD_INFO | VARCHAR(255) |
| INSTALLMENT_MONTH_FORMAT_MONT | VARCHAR(255) |
| MANAGEMENT_NUMBER | VARCHAR(255) |
| PAY_AMOUNT | DECIMAL |
| PAY_TYPE_NAME | VARCHAR(255) |
| RELATED_MANAGEMENT_NUMBER | VARCHAR(255) |
| REVISION | BIGINT |
| TAX | DECIMAL |

## 문제해결 전략
### 1. 개요
- DDD + OOP로 요구 사항의 복잡도를 줄이려 했습니다.
- 객체가 최대한 자신의 이름에 맞는 역할, 책임을 하도록 구성했습니다.
    - 각 객체는 본인의 생명주기, 유효성 검증을 진행합니다.
- 도메인 전문가가 없는 상태에서 컨텍스트를 함부로 나누는 건 유지보수에 어려움이 있을 수 있다고 생각해서 Aggregate 개념만 차용해서 사용했습니다.
- 비즈니스 도메인 복잡도 관리를 위해 각각의 Aggregate에 대해 최대한 Aggregate Root로만 접근하도록 제어하려 했습니다.
- 레이어드 아키텍처를 통해 UI와 비즈니스 로직이 최대한 섞이지 않도록 노력했습니다.

### 2. Aggregate 설계
1. Payment: 핵심 도메인. 사용자 요청에 따른 실질적인 결제 비즈니스 로직을 담당합니다.
    - Payment(VO)
        - Aggregate Root로 결제의 주요 정보를 모두 갖고 있습니다.
        - 모든 결제는 해당 VO를 생성하거나 바꾸는 작업으로 진행됩니다.
    - CardInfo(VO)
        - 결제에 사용되는 카드 정보(카드 번호, CVC, 유효기간) 관리를 담당합니다.
        - 각각의 정보에 대한 validation을 확인합니다.
    - InstallmentMonth(VO)
        - 할부 개월의 경우 0, 2 ~ 12 개월로 상수의 개념을 갖고 있어서 enum으로 구현했습니다.
    - ManagementNumber (VO)
        - 결제 진행 시 20자리의 유일한 키값을 생성하는 역할을 담당합니다.
        - UUID 생성 로직에서 앞의 20자를 가져와서 유일값을 생성합니다.
        - 중복 가능성이 아예 없지 않기 때문에 중복이 발생할 경우 예외 처리됩니다.
    - PayInfo (VO)
        - 결제 금액, 할부 개월, 결제 타입을 관리합니다.
        - 결제 금액에 대한 validation을 처리합니다.
    - PaymentFactory (VO)
        - 결제, 결제전액취소 진행 시 Payment 객체를 생성하는 역할을 담당합니다.
        - 입력한 값에 따라 불변식이 깨지지 않도록 결제를 진행합니다.
    - PayType (VO)
        - 결제 타입을 결제, 결제전체취소, 결제부분취소 세가지 상수로 관리합니다.
        - 카드사에 보내는 정보와 시스템 내부에서 사용하는 용어를 한 개의 상수로 관리합니다.
    - Tax (VO)
        - 부가가치세 생성 및 검증 책임을 갖고 있습니다.
        - 결제, 결제전체취소, 결제부분취소 시 세금을 수동, 자동으로 게산해서 객체를 생산하는 역할을 담당합니다.
        
2. CardCompany: 카드사에 보낼 데이터를 관리하는 Aggregate 입니다.
    - CardCompanyInfo (Entity): Payment Aggregate에서 생성된 정보를 기반으로 카드사에 보내는 역할을 담당합니다.
    - CardCompanyInfoRepository (Repository): 객체 영속화 영역의 추상화 객체입니다.
    
3. Encrypt: 카드 정보와 관련된 내용을 암복호화 하는 역할을 갖습니다.
    - EncryptDecrypt (VO): AES256 알고리즘으로 카드 정보를 암복호화합니다.
    - EncryptedCardInfo (VO): 카드 정보를 암복호화 해서 시스템 내부에서 읽을 수 있도록 합니다. 저장 시 무조건 암호화됩니다.
    
4. History: 결제 이력 정보를 관리합니다.
    - PaymentHistory (Entity): Payment 핵심 Aggregate에서 진행된 결제 정보를 영속화하는 역할을 담당합니다. 저장시 카드 정보는 모두 암호화됩니다.
    - PaymentHistoryRepository (Repository): 객체 영속호 영역의 추상화 객체입니다.
    
5. Parser: Payment 정보를 카드사 정보로 바꾸는 책임을 담당합니다.
    - ParserType: 각 데이터 타입별로 카드사 포멧에 맞추는 역할을 상수로 관리합니다.
    
6. Domain Service: 하나의 Aggregate로 엮기 힘든 동작들을 묶어서 처리하는 영역입니다.


## 빌드 및 실행 방법
### 1. 순수 jar
#### 1.1 UNIX(Mac or Linux)
```shell script
# 프로젝트 루트 경로를 기준으로 진행해주세요
export payment-key={type your own encryption key}
./gradlew bootjar
cd build/libs
java -jar payment-0.0.1-SNAPSHOT.jar
```
#### 1.2 Window
```shell script
# 프로젝트 루트 경로를 기준으로 진행해주세요
set payment-key={type your own encryption key}
./gradlew.bat bootjar
cd build/libs
java -jar payment-0.0.1-SNAPSHOT.jar
```

## 2. Container
### 2.1 Docker
```shell script
# 프로젝트 루트 경로를 기준으로 진행해주세요
./gradlew bootjar
docker build -t kakao-payment .
docker run -d -p 8080:8080 --rm -e PAYMENT_KEY={put your own key value} kakao-payment
```

### 2.2 Docker Compose
```shell script
# 프로젝트 루트 경로를 기준으로 진행해주세요
./gradlew bootjar
cp .env.default .env 
# 복사한 .env 파일 내의 payment-key 환경변수를 설정해주세요.
docker-compose up
```

### 2.3 Kubernetes
```shell script
# 프로젝트 루트 경로를 기준으로 진행해주세요
# 노드 포트를 통해 외부로 연결되며 30001 포트를 사용합니다.
echo -n '{put your own key value}' > ./payment-key
kubectl create secret generic payment-key --from-file=./payment-key
kubectl create -f kakao-payment.yaml
```

## 엔드 포인트 테스트
- Swagger URL: http://localhost:8080/swagger-ui/index.html
    - Swagger 창에 http://localhost:8080/v3/api-docs/ 입력
    - 쿠버네티스로 배포한 경우 {쿠버네티스 노드 IP}:30001로 접근하셔야 됩니다.

## Todo List
- [X] CardInfo
    - [X] 카드번호(long), 유효기간(int), cvc(int)로 구성된다.
    - [X] 카드번호는 10 ~ 16자리 숫자이다
    - [X] 유효기간은 4자리 숫자이다
        - [X] 기능 구현 끝난 뒤 날짜 형식 validation 기능 구현
    - [X] cvc는 3자리 숫자이다.
    - [X] 암복호화가 가능해야 한다.
    - [X] 각각의 속성에 접근할 수 있어야 한다.
    - [X] 동등성 비교를 지원해야 한다.

- [X] EncryptedCardInfo
    - [X] AES256 알고리즘으로 암복호화를 할 수 있다.
    - [X] 키를 설정파일에서 주입받을 수 있어야 한다.
        - [X] 배포 시 환경변수로 주입받아야 한다. 

- [X] PayInfo
    - [X] 할부개월수, 결제금액, 결제타입으로 구성된다.
    - [X] 할부개월은 일시불이거나 1 ~ 12가지의 값을 갖는다.
    - [X] 결제금액은 100원 ~ 10억원의 범위를 갖는다.
    - [X] 결제 타입은 결제, 결제 취소가 있다.
    - [X] 동등성 비교가 가능해야 한다.
    
- [X] Tax
    - [X] 부가가치세는 자동으로 생성될 수 있다.
        - [X] 자동 생성 시 계산: 결제금액 / 11(소수점이하 반올림)
    - [X] 부가가치세는 0원 ~ 결제 금액의 범위를 갖는다.
    - [X] 동등성 비교가 가능해야 한다.

- [X] Parser
    - [X] 현재 기능을 enum으로 추출해서 응집도를 높인다.

- [X] Payment
    - [X] 관리번호, 연관된 관리번호, PayInfo, CardInfo, Tax를 갖는다.
        - [X] 관리번호 추가
        - [X] 연관된 관리번호는 결제 시엔 null, 결제 취소 시에는 연관된 결제건의 관리번호이다.
    - [X] CardInfo가 String 타입이어서 암호화를 보장할 수 없기 때문에 따로 객체를 만든다.
    - [X] Tax를 수동으로 입력받아서 객체를 만들 수 있다.
    - [X] Tax를 받지 않고도 객체를 만들 수 있다.
    - [X] 관리번호는 식별자로 유일한 값으로 20자리여야 한다.
    - [X] 세금 자동 계산으로 결제이력 객체를 만들 수 있다.
    - [X] 세금 수동 계산으로 결제이력 객체를 만들 수 있다.
    - [X] 세금 자동 계산으로 결제전액취소이력 객체를 만들 수 있다.
    - [X] 세금 수동 계산으로 결제전액취소이력 객체를 만들 수 있다.
    - [X] PaymentFactory 구현
        - [X] Payment 생성 관련 책임 자체가 복잡해지기 때문에 Factory로 추출
    - [X] DB에 해당 값을 저장하지 않는다.(도메인 로직만 수행한다.)
    - [X] CardInfo를 속성으로 갖는다.
        - 이게 없으면 암복호화 회수가 너무 늘어난다.
    - [X] 카드사 전송용 String data로 바꿔야 한다.
        - [X] 해당 일을 처리하는 객체를 따로 생성(limit, parserType, data를 입력)
    - [X] CardInfo 암호화 관련 기능을 별도 Aggregate로 빼낸다
        - 암호화 자체가 큰 역할을 지닌 책임이기 때문에 분리
        - 암호화 데이터는 실제 DB에 저장되는 CardCompanyData, PaymentHistory에서만 필요하다.
    - [X] 카드사 전달용 데이터를 별도 Aggregate로 빼낸다.
        - 현재 parsing 로직은 실제로 Payment 객체 관련 로직을 수행하기 필요한 사항이 아님.
    - [X] 결제전액취소는 PaymentHistory 객체를 주입받아서 만들 수 있도록 해야한다.
        - [X] PaymentFactory의 결제전액취소 객체 생성 메서드 변경
        - [X] 결제 취소 관련 도메인 로직에 어긋나지 않는지 확인
            - [X] 부가가치세 자동 계산으로 결제 건에 대해서만 취소할 수 있다.
            - [X] 부가가치세 자동 계산으로 이미 결제 취소한 건에 대해서 취소할 수 없다.
            - [X] 부가가치세 수동 계산으로 결제 건에 대해서만 취소할 수 있다.
            - [X] 부가가치세 수동 계산으로 이미 결제 취소한 건에 대해서 취소할 수 없다.
                
- [X] CardCompanyData
    - [X] id, 카드사용 정보를 갖는다.
        - [X] 카드사용 정보는 Payment 정보를 기반으로 만들어진 String 데이터다
    - [X] 카드사에 보내는 걸 가정하고 DB에 저장할 수 있다.

- [X] PaymentHistory
    - [X] 관리번호, 연관된 관리번호, 암호화 된 카드정보, 할부개월수, 결제금액, 결제타입, 취소여부를 속성으로 갖는다.
        - [X] 결제전액취소 History 건은 취소 여부 속성을 true로 갖는다.
        - [X] 결제 History 건은 취소 여부 속성을 바꿀 수 있다.
    - [X] Payment 도메인의 수행 결과를 DB에 저장한다.
    
- [X] PaymentStrategy
    - [X] 부가가치세 항목의 존재 여부에 따라 결제 생성 규칙 변경 필요

- [X] 결제 시 부가가치세 관련 로직 점검 필요
    - [X] 세금 자동 계산 로직 점검 필요
        - 정상. 눈으로 점검할 때 착각함
    - [X] 결제 금액보다 많은 금액으로 세금 부과 로직 점검 필요
        - 현재 내부에서 무조건 자동 계산방식으로 진행되기 때문
        - 세금 존재 여부에 따라 로직이 바뀌어야 함

- [X] PayStatus -> PayType
    - 이력에서는 실행된 당시의 상태만을 기억하기 때문에 그 당시 상태보다 결제된 형식이 더 맞는 이름이라 판된되어 변경
    
- [X] 결제 기능 리팩토링
    - [X] Request DTO에서 결제액 BigDecimal 타입으로 변경
    - [X] Request DTO에서 부가가치세 BigDecimal 타입으로 변경
    - [X] 서비스 레이어에서 PaymentHistory 객체 리턴하도록 변경
    
- [X] 결제전액취소 기능 리팩토링
    - [X] 서비스 레이어에서 PaymentHistory 객체 리턴하도록 변경
    
- [X] 결제 전체 취소 기능
    - [X] 결제취소한 적 없는 결제 이력에 대해 결제전액취소 진행 가능(부가가치세 자동 계산)
        - [X] 취소 요청 금액이 결제된 금액과 일치하지 않을 경우(작거나 큰 경우 모두 포함) 결제전액취소 불가능
    - [X] 결제취소한 적 없는 결제 이력에 대해 결제전액취소 진행 가능(부가가치세 수동 계산)
        - [X] 취소 요청 금액이 결제된 금액과 일치하지 않을 경우(작거나 큰 경우 모두 포함) 결제전액취소 불가능
        - [X] 제시한 부가가치세가 결제한 금액의 부가가치세보다 높은 경우 결제전액취소 불가능
    - [X] 이미 취소한 적 있는 결제 이력에 대해 결제전액취소 불가능
    - [X] 결제 취소 이력에 대한 결제전액취소 불가능
    - [X] 부분 취소된 결제는 결제전액취소 불가능
    
- [X] 결제전액취소 기능 중 카드사 전송 시 금액 데이터에 소수점이 들어가는 것으로 추정. 확인할 것.
    - 전송할 때 정수 String으로 포맷되도록 변경

- [X] 결제부분취소 자동세금계산 불변식
    1. 결제이력금액 검증 (`결제부분취소 요청 금액 >= 결제이력 금액` 진행 불가능)
    2. 결제이력잔액 검증 (`결제부분취소 요청 금액 > 결제이력 잔액(=결제이력 금액 - 결제부분취소 금액합)` 진행 불가능)
    3. 최종취소 여부 확인 (`결제부분취소 요청 금액 = 결제이력 잔액`에 따라 분기 진행)
        1. 최종취소 진행 (같은 경우)
            - 자동계산 된 부가가치세가 결제이력 부가가치세 잔액보다 적은 경우 진행 불가능 
            - 자동계산된 부가가치세가 결제이력 부가가치세 잔액보다 높은 경우 결제이력 부가가치세 잔액으로 취소한다. 
        2. 최종취소 미진행 (결제부분취소 요청금액 < 결제이력 잔액)
            - 무조건 진행한다.
            - 자동계산 된 부가가치세가 결제이력 부가가치세 잔액보다 높은 경우 결제이력 부가가치세 잔액으로 취소한다.

- [X] 결제부분취소 수동세금계산 불변식
    1. 결제이력금액 검증 (`결제부분취소 요청 금액 >= 결제이력 금액` 진행 불가능)
    2. 결제이력잔액 검증 (`결제부분취소 요청 금액 > 결제이력 잔액(=결제이력 금액 - 결제부분취소 금액합)` 진행 불가능)
    3. 최종취소 여부 확인 (`결제부분취소 요청 금액 = 결제이력 잔액`에 따라 분기 진행)
        1. 최종취소 진행 (같은 경우)
            - 결제부분취소 부가가치세 요청 금액과 결제이력 부가가치세 잔액이 무조건 일치해야 한다.
            - 그 외 모두 실패
        2. 최종취소 미진행 (결제부분취소 요청금액 < 결제이력 잔액)
            - 결제부분취소 부가가치세 요청 금액 = 결제이력 부가가치세 잔액: 진행
            - 결제부분취소 부가가치세 요청 금액 < 결제이력 부가가치세 잔액: 진행
            - 결제부분취소 부가가치세 요청 금액 > 결제이력 부가가치세 잔액: 불가능
