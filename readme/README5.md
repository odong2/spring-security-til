### AnonymousAuthenticationFilter

- 인증을 받지 않은 사용자를 null 판단해서 처리하는 것이 아닌 별도의 익명 사용자용 인증 객체를 만들어서 사용

<img width="824" alt="Untitled" src="https://github.com/user-attachments/assets/5e0c537d-b0e6-4eb8-b446-fbe01b8e045b">

1. 사용자 요청
2. AnoymousAuthenticationFilter가 요청 받음
    1. 현재 요청을 보내 온 사용자가 인증 객체가 존재하는지 여부를 먼저 판단
    2. 사용자가 인증을 받았다면 SecurityContext 안 Authentication 존재
    3. 인증된 사용자라면 AnonymousAutenticationFilter 에서는 특별한 처리를 하지 않고 다음 필터로 이동
    4. 인증된 사용자가 아니라면 익명 사용자용 인증 객체인 **Anonymous Authentication Token** 생성
    5. 즉 익명 사용자라도 null 처리하는 것이 아닌 익명 사용자 토큰을 생성해서 SecurityContextHolder 안에 익명 사용자 토큰을 저장
- 이처럼 익명 사용자라 할 지라도 인증 성공 이후 인증 객체를 저장하는 프로세스와 동일하게 익명 객체를 저장
- 즉 이러한 익명 객체 타입을 통해서 익명 사용자 인지 인증된 사용자인지 내부적으로 구분함
    - `isAnonymous() == true` [익명 사용자] → 로그인 화면 구성
    - `isAuthenticated() == true` [인증 받은 사용자] → 로그아웃 메뉴 구성
