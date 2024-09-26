### RememberMe 인증 API
<img width="620" src="https://github.com/user-attachments/assets/ef7956f1-0524-4567-8832-e0b8683d2118">

- rememberMe 기본 파라미터명은 remember-me
- tokenValiditySeconds 기본 14일
- alywaysRemember
    - `true` : remember me 활성화되지 않아도 항상 실행
    - `false` : remember me 활성화 시에만 활성화
- userDetailsService
    - 시스템의 사용자 계정을 조회하는 과정 때 필요한 클래스

RememberMeAuthenticationFilter가 사용자의 요청을 받아서 요청을 처리하는 조건은 아래의 두 가지 조건을 충족하면 정상적으로 동작한다.

1. Authentication 인증 객체가 null일 경우
2. Form 인증 받을 당시 rememberMe 기능 활성화해서 서버로부터 rememberMe 쿠키를 발급 받은 경우
- 인증 객체는 SecurityContext에 저장 되어 있음
- 인증을 받은 사용자는 SecurityContext에 항상 인증 객체가 존재
- SecurityContext에 Authentication이 존재하지 않는 경우는 사용자 세션이 만료 되어 세션 안에서 더 이상 SecurityContext를 찾지 못하고, 그 SecurityContext가 존재하지 않기 때문에 인증 객체도 없는 경우
- 이 경우에 RememberMeAuthenticationFilter가 동작함
- 만약 Authentication 객체가 null이 아니면 해당 rememberMe 필터는 동작하지 않는다
- 즉 RememberMeAuthenticationFilter는 인증을 받은 사용자가 세션타임 아웃의 경우에 해당하여 세션이 만료되었거나 해당 브라우저가 종료되어 세션이 끊기게 되어 세션이 더 이상 활성화 되지 않아 인증 객체를 찾지 못하는 경우에 자동적으로 사용자의 인증을 유지하기 위해 이 필터가 인증을 시도한다

### RememberMe 인증 필터
RememberMeAuthenticationFilter가 사용자의 요청을 받아서 요청을 처리하는 조건은 아래의 두 가지 조건을 충족하면 정상적으로 동작한다.

1. Authentication 인증 객체가 null일 경우
2. Form 인증 받을 당시 rememberMe 기능 활성화해서 서버로부터 rememberMe 쿠키를 발급 받은 경우
- 인증 객체는 SecurityContext에 저장 되어 있음
- 인증을 받은 사용자는 SecurityContext에 항상 인증 객체가 존재
- SecurityContext에 Authentication이 존재하지 않는 경우는 사용자 세션이 만료 되어 세션 안에서 더 이상 SecurityContext를 찾지 못하고, 그 SecurityContext가 존재하지 않기 때문에 인증 객체도 없는 경우
- 이 경우에 RememberMeAuthenticationFilter가 동작함
- 만약 Authentication 객체가 null이 아니면 해당 rememberMe 필터는 동작하지 않는다
- 즉 RememberMeAuthenticationFilter는 인증을 받은 사용자가 세션타임 아웃의 경우에 해당하여 세션이 만료되었거나 해당 브라우저가 종료되어 세션이 끊기게 되어 세션이 더 이상 활성화 되지 않아 인증 객체를 찾지 못하는 경우에 자동적으로 사용자의 인증을 유지하기 위해 이 필터가 인증을 시도한다

### RememberMe 인증 흐름
<img width="620" src="https://github.com/user-attachments/assets/5e0c537d-b0e6-4eb8-b446-fbe01b8e045b">

1. 사용자가 세션이 만료되었음
2. 사용자 from 인증 당시 remember-me 활성화하여 인증 → remember-me 쿠키를 굽게 됨
3. 사용자 세션이 만료되면 RememberMeAuthentication 필터 작동
4. RememberMeSerivices 인터페이스
    - 구현체가 2개 - `TokenBasedRememberMeServices`, `PersitentT*o*kenBasedRememberMeServices`
    - 각각의 구현체가 remember-me 인증 처리 역할을 하는 클래스
    - TokenBasedRememberMeServices
        1. 메모리에서 실제로 토큰을 저장한 토큰과 사용자가 요청할 때 들고 온 쿠키 토큰과 비교해서 인증 처리
        2. 기본적으로 토큰 만료 기간은 14일
    - PersitentTokenBasedRememberMeServices
        1. 영구적인 방식 - 발급한 토큰을 db에 저장
        2. 요청 때 들어 온 쿠키 토큰 값과 db에 저장된 토큰을 비교하여 인증 처리
5. RememberMeServices가 Token 쿠키 추출
6. 사용자가 가지고 있는 토큰이 remember-me 라는 이름을 가진 토큰인지를 검사
    - 토큰이 존재하는 경우
        - Decode Token
            - 토큰에 형식이 존재
            - 토큰의 형식이 정상적으로 규칙을 지키고 있는지 정상 유무 판단
            - 정상인 경우
                - 사용자 토큰과 서버에 저장된 토큰의 값이 일치하는가 판단
                - user 계정이 존재하는가 판단
                - 새로운 Authentication 생성 → rememberMeAuthenticationToken 생성
                - 생성된 인증 객체를 AuthenticationManger[인증 관리자]에게 전달하여 인증 처리
            - 비정상인 경우
                - Exception 발생
    - 토큰이 존재하지 않는 경우 다음 필터로 이동
