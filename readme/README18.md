### AuthenticationProvider

![image11](https://github.com/user-attachments/assets/a968cd9d-82f4-473b-a5a7-36847b86a9a4)

- AuthenticationProvider 인터페이스
- 두 개의 메서드 제공
    - `authenticate(authentication)`
        - 인증처리 검증 메서드
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
        3. **추가 검증**
        4. 최종 Authentication(user, authorities) 저장 후 AuthenticationManager에 전달
    - `supports(authentication)`
        - 인증 처리할 수 있는 provider 인지 체크
