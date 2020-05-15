# kakaoPayment
카카오페이 사전과제 프로젝트(20200513)

## 요구사항
- [X] CardInfo
    - [X] 카드번호(long), 유효기간(int), cvc(int)로 구성된다.
    - [X] 카드번호는 10 ~ 16자리 숫자이다
    - [X] 유효기간은 4자리 숫자이다
        - [ ] 기능 구현 끝난 뒤 날짜 형식 validation 기능 구현(요구사항에는 없음)
    - [X] cvc는 3자리 숫자이다.
    - [X] 암복호화가 가능해야 한다.
        - [ ] 하드코딩 된 키값 설정으로 뺄 것
    - [X] 각각의 속성에 접근할 수 있어야 한다.
    - [X] 동등성 비교를 지원해야 한다.

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

- [ ] Payment
    - [X] 관리번호, PayInfo, CardInfo(암호화 된), Tax를 갖는다.
        - [X] 관리번호 추가
        - [X] 연관된 Payment 관리번호 추가 (결제 시엔 null, 결제 취소 시에는 결제 번호)
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
    - [ ] DB에 해당 값을 저장해야 한다.
    - [ ] CardInfo를 속성으로 갖는다.(DB에는 저장하지 않는다. 암호화 된 것만 저장한다.)
        - 이게 없으면 암복호화 회수가 너무 늘어난다.
    - [ ] 카드사 전송용 String data로 바꿔야 한다.    
- [ ] CardInfo
    - [ ] id, 카드사용 정보를 갖는다.
        - [ ] 카드사용 정보는 Payment 정보를 기반으로 만들어진 String 데이터다
