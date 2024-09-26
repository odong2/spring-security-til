### Authentication Flow

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/7f2365ae-ea78-4340-b09d-9671c8c311c7/4a734a71-2416-48b5-9cf5-22e67bcfd8ee/image.png)

1. **사용자 로그인 요청**
2. **UsernamePasswordAuthenticationFilter 요청 받음**
    - Form인증 경우 해당
    - Authentication (username + password) 생성
    - AuthenticationManager 에게 생성한 인증 객체 전달하여 인증 처리 맡김
3. **AuthenticationManager 인증 위임**
    - 인증의 전반적인 관리 [인터페이스]
    - 실제 구현체는 `ProviderMnager`
    - 하지만 실제 인증 역할 하지 않고 적절한 AuthenticationProvider 에 위임
        - Form 인증의 경우 `DaoAuthenticationProvider`선택됨
    - 이 말은 즉 사용자의 username, password 가 일치 하는 지 등의 검증을 전혀 관여하지 않음
    - 해당 클래스에 리스트 변수에 AuthenticationProvider 들이 존재
    - 현재 인증에 사용 가능한 AuthenticationProvider 선택하여 인증 처리(검증)를 위임
4. **AuthenticationProvider**
    - AuthenticationManager 로 부터 Authentication 전달 받음
    - username, password 검증
    1. **username 검증**
        - `loadUserByUsername(username)` 메서드
            - 유저 객체 요청
            - UserDetailsService 인터페이스에게 유저 객체 요청
            - **유저가 존재 하는 경우**
                - 유저 객체 생성하여 리턴 받음
                - UserDetails 타입으로 반환
            - **유저가 존재하지 않는 경우**
                - `UsernameNotFoundException` 예외 발생
                - UsernamePasswordAuthenticationFilter 가 예외를 받아 후속 처리 진행
    2. **password 검증**
        - 반환 받은 UserDetails 타입 객체의 패스워드와 로그인 할 때 입력한 패스워드 값과 매치하여 일치하는지 체크
        - **패스워드 일치하는 경우**
            - 인증 성공
            - Authentication 인증 객체를 생성 (UserDetails + authorities)
            - AuthenticationManager 에게 반환
        - **패스워드 일치하지 않는 경우**
            - `BadCredentialsException` 예외 발생
5. AuthenticationManager
    - 인증 성공 후 AuthenticationProvider 로 부터 반환 받은 인증 객체(UserDetails + authorities)를  AuthenticationFilter에게 다시 전달
6. UsernamePassswordAuthenticationFilter
    - 인증 관리자로 부터 받은 인증 객체를 SecurityContext에 저장