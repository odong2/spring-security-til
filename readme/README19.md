### Authorization

> **당신에게 무엇이 허가 되었는지 증명하는 것**
>

![image12](https://github.com/user-attachments/assets/acca8364-c674-464c-808a-951bd0e88851)

- 인가 처리란 인증을 받은 사용자가 어떤 특정한 자원에 접근하고자 할 때 그 자원에 접근할 자격이 되는지 처리를 하는 것
- 요청이 오면 인증을 여부 판단
- 인증 판단이 완료되면 인가 영역에 와서 자원 접근에 여부 판단

### 스프링 시큐리티가 지원하는 권한 계층

![image13](https://github.com/user-attachments/assets/6ab9aed6-6346-4474-bba8-1fb6720a4591)

- 웹 계층
    - URL 요청에 따른 메뉴 혹은 화면단위의 레벨 보안
    - `/user` 경로 자원 요청
    - 해당 자원에 설정된 권한은 ROLE_USER
    - 사용자가 가진 권한과 해당 자원의 권한 검증해서 자원에 접근 가능한지 판단하는 것이 웹 계층
- 서비스 계층
    - 화면 단위가 아닌 메서드 같은 기능 단위의 레벨 보안
    - 사용자가 `user()` 메서드 호출하여 진입하고자 할 때
    - `user()` 메서드 위에 설정된 권한이 존재
    - 메서드에 진입하고자 할 때 사용자의 권한과 메서드의 권한 검증하여 인가 처리
- 도메인 계층(Access Control List, 접근제어 목록)
    - 객체 단위의 레벨 보안
    - user 객체 존재
    - 도메인에 설정된 권한 존재
    - user 객체에 쓰고자 할 때 도메인에 설정된 권한과 사용자 권한 검증하여 인가 처리

### FilterSecurityInterceptor

- 마지막에 위치한 필터로써 인증된 사용자에 대하여 특정 요청의 승인/거부 여부를 최종적으로 결정
- 인증객체 없이 보호자원에 접근을 시도할 경우 AuthenticationException 발생
- 인증 후 자원에 접근 가능한 권한이 존재하지 않을 경우 AccessDeniedException 발생
- 권한 제어 방식 중 HTTP 자원의 보안을 처리하는 필터
- 권한 처리를 `AccessDecisionManager`에게 맡김

### FilterSecurityInterceptor Flow

![image14](https://github.com/user-attachments/assets/7f7a6d6f-e8eb-4ae8-b488-1e819e5f3fa4)

1. 사용자 요청
2. `FilterSecurityInterceptor`가 인증 객체 존재 여부 판단
    - 인증 객체가 없다면 `AuthenticationException` 예외 발생
    - ExceptionTranslantionFilter에 의해 사용자 인증 받도록 유도 처리
3. `SecurityMetadataSource`
    - 사용자가 요청한 자원에 필요한 권한 정보 조회해서 전달
    - **요청 자원에 권한 설정되지 않은 경우**
        - 권한 심사 하지 않음
    - **요청 자원에 권한이 설정된 경우**
        - AccessDecisionManager에게 요청 자원의 요구된 권한을 전달
4. `AccessDecisionMnager`
    - 최종 심의 결정자
    - 전달 받은 자원의 권한 정보를 통해서 인가 심사
    - 내부적으로 AccessDecisionVoter(심의자) 클래스 통해서 심의요청
        - 해당 사용자가 자원에 접근 가능하지 심의하여 승인/거부 결과값 리턴
    - 승인에 대한 결과값을 가지고 해당 자원에 접근 가능한지 판단하게 됨
    - 접근이 거부되면 `AccessDeniedException` 예외 발생
        - ExceptionTranslationFilter가 받아서 인가 예외 처리
