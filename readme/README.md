### Login Form 인증 흐름
실질적으로 사용자가 로그인하게 되면 인증 처리가 이루어 지는데, 그 인증 처리를 담당하고 인증 처리와 관련된 요청을 처리하는 필터가 **UsernamePasswordAuthenticationFilter**
이 필터가 각각의 인증처리 역할에 따라서 인증처리를 하게 됨

![image](https://github.com/user-attachments/assets/1b92ba68-c585-4738-97a6-014a2a265b96)

1. 사용자 인증 시도
2. UsernamePasswordAuthenticationFilter 요청 받음
3. 해당 필터가 요청 정보를 먼저 확인
    - 현재 요청 url이 로그인으로 시작되는지 확인 - 해당 값(login)은 변경 가능
        - formLogin API 에서 `loginProcessingUrl(”login”)` 에서 변경 가능
    - `YES` →  url 매칭되면 인증 처리 시작
    - `NO`  →   url 매칭되지 않으면 그 다음 필터로 이동
4. Authentication 객체를 만들어서 이 객체 안에 사용자가 입력한 username + password 값을 저장하여 인증 처리를 맡기는 역할을 함
    - 해당 Autentication은 실제로 `UsernamePasswordAuthenticationToken` 객체
    - 이 까지가 인증처리를 하기 전 필터가 하는 역할
5. AuthenticationManger - 인증 관리자
    - 필터로부터 인증 객체(Authentication) 전달 받고 인증처리를 하게 됨
    - 해당 클래스는 내부적으로 AuthenticationProvider 타입의 객체들을 가지고 있음
    - 이 객체들 중에서 하나를 선택하여 인증을 위임함
    - AuthenticationProvider 클래스가 실질적으로 인증처리를 담당
        - **인증 성공**
            1. Authentication 객체를 만듦
            2. 해당 객체에 유저 정보, 권한정보등 저장
            3. AuthenticationManger에게 다시 리턴
            4. 최종적으로 AuthenticationManger는 리턴 받은 인증 객체를 필터에게 다시 반환
        - **인증 실패**
            - AuthenticationException 발생 시킴
            - 해당 예외는 필터가 받아서 후속 작업 처리
6. 인증 성공 후 Authentication 객체를 리턴 받아 다시 필터로 돌아옴
7. 이후 필터는 인증 객체를 다시 SecurityContext 객체에 저장
    - SecurityContext는 인증 객체를 저장하는 보관소
    - 나중에는 SecurityContext 객체가 세션에도 저장이 됨
8. 인증 성공 후 SuccessHandler 성공 이후 작업들 처리
