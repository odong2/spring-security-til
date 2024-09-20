## 세션 관리

### 동시 세션 제어

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/7f2365ae-ea78-4340-b09d-9671c8c311c7/bca92dde-dba6-46cb-8787-e036698d17a5/Untitled.png)

**상황**

- 하나의 계정에 최대 세션 허용 개수는 1개로 한다는 정책 존재
- 사용자1 로그인 → 서버에 사용자1 세션 생성
- 사용자2가 사용자1과 동일한 계정으로 로그인 시도 → 서버에 사용자2 세션 생성
- 1-2 과정을 통해 서버에는 동일한 계정으로 두 개의 세션이 생성됨
- 해당 정책을 위해서 서버에서 최대 세션 개수를 초과하지 않도록 전략이 필요 - **동시 세션 제어**

**방법1) 이전 사용자 세션 만료 전략**

- 사용자2의 세션이 생성되었을 때 이전 사용자의 세션 만료 설정
- 이후 사용자1 어떤 자원에 접근하려 할 때 해당 세션을 실질적으로 만료시킴
- 이 방식을 통해 최대 세션 허용 개수인 1개를 계속적으로 유지

**방법2) 현재 사용자 인증 실패 전략**

- 사용자2 로그인 할 때 이미 세션이 생성된 것이 있기 때문에 인증 예외 발생
- 로그인 차단 전략
- 동일한 계정으로 첫 번째 사용자만 서버에 존재

### 세션 제어 API

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/7f2365ae-ea78-4340-b09d-9671c8c311c7/62b30bc4-1786-4f37-811c-5bbfa3464f7d/Untitled.png)

- sessionManagement 통해 세션 제어
- 동시 세션 제어 뿐 아니라 여러 기능을 제공
- maximumSession(1) - 최대 허용 가능 세션 수 [동시 세션 제어]
- maxSessionPreventsLogin(true)
    - default는 false → 기존 세션 만료 전략
    - true 경우 동시 로그인 차단됨 → 사용자 인증 실패 전략
- ivaliadUrl 과 expiredUrl 두 가지 모두 설정하였을 경우 invalidUrl이 우선 시 되어 이동하게 됨

### 세션 고정 보호

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/7f2365ae-ea78-4340-b09d-9671c8c311c7/c3057c01-cfc1-4ffa-a4fd-b8417cfdab02/Untitled.png)

1. 공격자가 서버에 접속
2. 서버는 공격자에게 JSESSIONID 쿠키 발급
3. 공격자는 사용자에게 자신이 발급 받은 세션 쿠키를 심어 놓음
4. 사용자가 공격자에게 받은 세션 쿠키로 로그인 시도
5. 사용자 인증 성공
6. 이후 공격자가 사용자와 동일한 JSESSIONID 가진 쿠키로 서버에 접근하게 되면 인증을 거치지 않아도 인증이 필요한 자원에 접근이 가능해짐
    - 사용자의 세션과 공격자의 세션이 공유됨
    - 이것을 세션 고정 공격이라고 함
    - 이러한 세션 고정 공격을 보호하기 위해 스프링 시큐리티는 세션 고정 보호 기능을 제공

그렇다면 스프링 시큐리티는 어떻게 세션 고정 공격을 방어하는가?

- **인증에 성공 할 때 마다 기존의 세션은 그대로 두고 새로운 세션 아이디 발급하여 방지**
- 이를 통해 공격자가 심어 놓은 쿠키로 인증을 시도하더라도 새로운 세션아이디가 발급 되어 공격자의 세션과 사용자의 세션이 더 이상 공유되지 않음
- 동시 세션 제어와 같이 sessionManagement 사용
- sessionManagement().sessionFixation().changeSessionId() 통해 설정
- 기본적으로 설정이 되어 있어 따로 설정해 주지 않아도 됨
- 보안에서 아주 중요한 기능

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/7f2365ae-ea78-4340-b09d-9671c8c311c7/78056421-bd4c-4174-a7c4-6d973feb7847/Untitled.png)

### 세션 정책

- 세션을 전혀 사용하지 않는 jwt 인증 방식을 사용할 때 stateless 정책 사용

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/7f2365ae-ea78-4340-b09d-9671c8c311c7/2e75825f-2ffc-4896-b57c-596c8088bd30/Untitled.png)