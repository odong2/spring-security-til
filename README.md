## spring-security
spring-security 5.2.1 버전 학습

## 환경
- Spring-boot 2.2.1.RELEASE
- Spring-security 5.2.1.RELEASE
- JDK 1.8
- JDK 1.8 설정한 이유는?
    - Spring Security 5.2.x 버전까지는 jdk 1.8까지 사용가능하며, 그 이상의 버전 사용 불가
    - Spring Security 5.4.0 이상 부터는 최소 JDK 11 부터 사용 사용 가능하며 그 이상의 버전도 지원
    - 현재 버전 5.2.1 버전과 마이그레이션 후 6 버전 모듈 생성 후 어느 환경에서든 재사용하기 위함

## 학습목표
1. 스프링 시큐리티 5.x 버전에 전반적 이해
2. 인증 및 인가 처리 프로세스 이해
3. 시큐리티 적용한 세션 방식 프로젝트 적용
4. 5.x 버전 학습 후 스프링 부트 3버전 및 시큐리티 6.x 버전 마이그레이션


### Security Filter 이해
- [Login Form 인증 흐름](https://github.com/odong2/spring-security/tree/main/readme)   
- [Logout API](https://github.com/odong2/spring-security/blob/main/readme/README3.md)   
- [RememberMe 인증 API](https://github.com/odong2/spring-security/blob/main/readme/README4.md)   
- [AnonymousAuthenticationFilter](https://github.com/odong2/spring-security/blob/main/readme/README5.md)   
- [세션관리](https://github.com/odong2/spring-security/blob/main/readme/README6.md)
- [권한 설정과 표현식](https://github.com/odong2/spring-security/blob/main/readme/README7.md)
- [ExceptionTranslationFilter](https://github.com/odong2/spring-security/blob/main/readme/README8.md)
- [CsrfFilter](https://github.com/odong2/spring-security/blob/main/readme/README9.md)

### Security Architecture 이해
- [DelegatingFilterProxy](https://github.com/odong2/spring_security_til/blob/main/readme/README10.md)
- [필터 초기화와 다중 설정](https://github.com/odong2/spring_security_til/blob/main/readme/README11.md)
- [Authentication](https://github.com/odong2/spring_security_til/blob/main/readme/README12.md)
- [SecurityContextHolder / SecurityContext](https://github.com/odong2/spring_security_til/blob/main/readme/README13.md)
- [SecurityContextPersistenceFilter](https://github.com/odong2/spring_security_til/blob/main/readme/README14.md)
- [Authentication Flow](https://github.com/odong2/spring_security_til/blob/main/readme/README15.md)
- [AuthenticationManager](https://github.com/odong2/spring_security_til/blob/main/readme/README17.md)
- [AuthenticationProvider](https://github.com/odong2/spring_security_til/blob/main/readme/README18.md)
- [Autorization](https://github.com/odong2/spring_security_til/blob/main/readme/README19.md)
- [AccessDecisionManager](https://github.com/odong2/spring_security_til/blob/main/readme/README20.md)

