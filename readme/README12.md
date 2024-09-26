### Authentication

> Authentication 즉 인증이란 **당신이 누군인지 증명하는 것**
>
- **사용자의 인증 정보를 저장하는 토큰 개념**
- **인증 시** id와 password를 담고 인증 검증을 위해 전달되어 사용된다
- **인증 후** 최종 인증 결과 (user 객체, 권한정보) 담고 SecurityContext에 저장되어 전역적으로 참조가 가능

    ```java
    // 인증객체 얻음
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
    ```

- **구조**
    - `principal` : 사용자 아이디 혹은 User 객체를 저장
    - `credentials` : 사용자 비밀번호
    - `authorities` : 인증된 사용자의 권한 목록
    - `details`: 인증 부가 정보
    - `Authenticated` : 인증 여부

<img width="600" src="https://github.com/user-attachments/assets/659e170e-402c-4688-9e8d-ff344f3fe537">

1. 사용자 로그인 (username + password)
2. UsernamePasswordAuthenticationFilter 인증 필터 Authentication 객체 생성
    - 해당 Authentication 객체에 유저 정보 저장
    - Principal : 아이디 저장
    - Credentials : 비밀번호 저장
3. AuthenticationManager에게 전달
    - AuthenticationManager는 Authentication 객체를 가지고 인증 처리
4. 인증을 성공하게 되면  인증전과 동일한 타입인 Authentication 구현체 객체를 만듦
    - Principal: UserDetails 저장
    - Credentials
    - Authorities : 권한 목록 저장
    - Authenticated : TRUE 저장
5. SecurityContextHolder 안에 SecurityContext 객체 존재
    - 해당 SecurityContext에 Authentication 인증 객체 저장
