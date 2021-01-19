# SPRINKLE-PAYMONEY
카카오페이 프로젝트 과제 : 카카오페이 머니 뿌리기 
* 요구사항
  * 뿌리기, 받기, 조회 기능을 수행하는 REST API 를 구현합니다.
    * 요청한 사용자의 식별값은 숫자 형태이며 "X-USER-ID" 라는 HTTP Header로
    전달됩니다.
    * 요청한 사용자가 속한 대화방의 식별값은 문자 형태이며 "X-ROOM-ID" 라는
    HTTP Header로 전달됩니다.
    * 모든 사용자는 뿌리기에 충분한 잔액을 보유하고 있다고 가정하여 별도로
    잔액에 관련된 체크는 하지 않습니다.
  * 작성하신 어플리케이션이 다수의 서버에 다수의 인스턴스로 동작하더라도 기능에
    문제가 없도록 설계되어야 합니다.
  * 각 기능 및 제약사항에 대한 단위테스트를 반드시 작성합니다.

# 개발환경
```
java 11
springboot 2.3.6
openJDK 15.0.1
junit4.12
lombok
commons-lang3
jpa
H2 in-memory 
```
# 실행방법
```
# Git clone
% git clone https://github.com/ma29mi/paymoney-sprinkle.git

# build
% cd paymoney-sprinkle
% ./gradlew clean build

# jar실행
% java -jar build/libs/paymoney-sprinkle-0.0.1-SNAPSHOT.jar

```
## API
### 뿌리기 API
- Request
```
POST v1/sprinkle
Host: localhost:8080
X-USER-ID: {member_id}
X-ROOM-ID: {room_id}
Content-Type: application/json
```
| Parametter | Description | Type |
|---|:---:|---:|
| `totalAmount` | 뿌릴 금액 | `number` |
| `totalEventCount` | 이벤트 참여가능 인원 | `number` |
    
- Response
```
HTTP/1.1 200 OK
{
    "code": "SUCCESS",
    "result": {
        "token": "r01"
    }
}
```

### 받기 API
- Request
```
PATCH v1/sprinkle
Host: localhost:8080
X-USER-ID: {member_id}
X-ROOM-ID: {room_id}
X-TOKEN-ID: {token_id}
Content-Type: application/json
```
| Parametter | Description | Type |
|---|:---:|---:|
| `token` | 뿌리기 시 발급된 토큰 | `string` |
- Response
```
HTTP/1.1 200 OK
{
    "code": "SUCCESS",
    "result": {
        "amount": 839.00
    }
}
```

### 조회 API
- Request
```
GET v1/sprinkle
Host: localhost:8080
X-USER-ID: {member_id}
X-ROOM-ID: {room_id}
X-TOKEM-ID: {token_id}
```
| Parametter | Description | Type |
|---|:---:|---:|
| `token` | 뿌리기 시 발급된 토큰 | `string` |
- Success Response
```
HTTP/1.1 200 OK
{
    "code": "SUCCESS",
    "result": {
        "details": [
            {
                "amount": 4045.00,
                "userId": 12345
            },
            {
                "amount": 30555.00,
                "userId": 11
            },
            {
                "amount": 14560.00,
                "userId": 147
            },
            {
                "amount": 845.00,
                "userId": 173
            }
        ],
        "info": {
            "sprayingId": 1,
            "roomId": "r01",
            "totalCount": 4,
            "totalAmount": 50000.00,
            "remitAmount": 0.00,
            "token": "r10",
            "userId": 12345,
            "createDateTime": "2020-11-21 19:42:51.709"
            "expireDateTime": "2020-11-21 19:52:51.709"
        }
    }
}
```
---
- Fail Response (공통 에러응답 형식)
```
HTTP/1.1 400
{
    "code": "FAIL",
    "message": "뿌리기 이벤트는 한번만 참여가능합니다"
}
```
---
## 문제 해결 전략 
* 비즈니스 로직 접근 전략
  * 뿌리기 이벤트 데이터 생성 시 
    * 뿌리기금액을 랜덤하게 나눠 인원 수 만큼 이벤트 받기 데이터를 생성
    * 뿌리기이벤트seq를 발급하여 맵핑
    * 뿌리기 이벤트 데이터 생성 시 토큰만료시간을 계산하여 함께 저장
  * 뿌리기 받기 처리 시 
    * 다양한 예외사항 확인 통해 DB조회 횟수 최소화
  * 3자리 고유 토큰 생성
    * 토큰은 UUID의 앞자리 3개를 가져오며, 별도의 중복 체크는 하지 않음 
    * roomID와 token기준으로 유니크한 뿌리기 이벤트seq를 발급하고, 뿌리기이벤트seq기준으로 뿌리기 지급 처리
  * 요청 API에 유니크한 데이터값이 직접 노출되지않도록 토큰도 header에 실어서 전송 
  * API 응답에 대하여 공통적으로 code와 message로 나누어 Object result로 공통화

* 아쉬운점
  * 토큰발급&조회기능과 뿌리기 API 비즈니스 로직을 구조적으로 분리하여 느슨한 연결구조로 리펙토링 필요 
  * 캐싱처리를 통한 처리속도 향상  
  * 실서비스를 고려한 서버구조도 고민을 반영하지 못했음 
    * 테스트 용이 및 빠른 데이터 조회를 위해 H2 인메모리 DB에 구현했으나, 서버장애시 데이터 휘발됨 
    * 캐싱처리 활용과 적절한 주기로 RDB 동기화 필요함, 장애대응을 고려한 M-S 구조 및 수평 확장구조 구성 필요 등 )