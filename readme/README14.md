### SecurityContextPersistenceFilter

> **SecurityContext 객체의 생성, 저장, 조회하는 역할 담당**
>
- **익명 사용자**
    - 새로운 SecurityContext 객체를 생성하여 SecurityContextHolder에 저장
    - AnonymousAuthenticationFilter에서 AnonymousAuthenticationToken 객체를 SecurityContext에 저장
- **인증 시**
    - 새로운 SecurityContext 객체를 생성하여 SecurityContextHolder 에 저장
    - UsernamePasswordAuthenticationFilter 에서 인증 성공 후 SecurityContext 에 UsernamePasswordAuthenticationToken 객체를 SecurityContext 에 저장
    - 인증이 최종 완료되면 Session 에 SecurityContext 저장 → SecurityContextPersistenceFilter가 저장하는 역할 수행
- **인증 후**
    - Session 에서 SecurityContext 꺼내어 SecurityContextHolder 에 저장
    - SecurityContext 안에 Authentication 객체가 존재하면 계속 인증을 유지
- **최종 응답 시 공통**
    - SecurityContextHolder.clearContext() → SecurityContextPersistenceFilter가 수행
    - 이유는?
        1. 스프링은 기본적으로 스레드 풀을 사용하는데  요청 처리가 끝난 후 SecurityContext 를 비우지 않으면 다른 요청에 재사용될 수 있음
        2. 다음 요청에서 새로운 인증 정보를 안전하게 설정할 수 있음. 즉, 이전 요청의 정보가 다음 요청에 영향을 미치지 않도록 보장
        3. 요청 처리가 끝난 후, 사용하지 않는 인증 정보를 지워 리소스를 해제하여 자원 관리


![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/7f2365ae-ea78-4340-b09d-9671c8c311c7/700e6b8d-b791-4fe1-aeb9-42f4b8f902d4/image.png)

1. 사용자 요청
2. SecurityContextPersistenceFilter 매 요청 시 요청을 받음
    - 내부적으로 HttpSecurityContextRepository 클래스가 존재
    - `HttpSecurityContextRepository`
        - SecurityContext 객체 생성, 조회하는 역할
        - 실질적인 요청을 처리하는 클래스
        - 필터는 단지 처리를 해당 클래스에게 전달하는 역할 수행
3. 인증여부
    - 세션에서 SecurityContext 존재 여부 및 Authentication으로 판단
    - **인증 받기 전 or 인증 시도 중 프로세스**
        1. 새로운 SecurityContext 생성 [익명 사용자든 인증을 받는 시점이든]
        2. 인증 필터 수행 - Form 인증인 경우 UsernamePasswordAuthenticationFilter 동작
            - 인증 필터가 SecurityContext 인증 객체 저장
        3. 클라이언트에게 응답하는 시점 세션에 SecurityContext 저장
        4. SecurityContext를 SecurityContextHolder에서 제거
        5. 클라이언트에게 최종 응답
        - 위 과정에서 1, 4, 5 과정을 `SecurityContextPersistenceFilter`가 수행함
    - **인증 받은 후 프로세스**
        1. 세션에 SecurityContext 있는지 판단
        2. 세션의 SecurityContext 꺼내어 SecurityContextHolder 에 저장