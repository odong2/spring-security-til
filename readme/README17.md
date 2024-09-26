### AuthenticationManager

![image9](https://github.com/user-attachments/assets/af610c75-d1f6-408a-9c98-c8d0c08a3bce)

- 인증 처리 필터로 부터 인증 처리를 지시 받는 첫 클래스
- AuthenticationMnager 인터페이스이고, 구현한 구현체가 `ProviderManager`
- `ProviderManager` 는 자신이 가진 AuthenticationProvider 중 적절한 것을 선택하여 인증 처리
    - Form 인증인 경우 DaoAuthenticatiohnProvider 선택
    - RememberMe 인증인 경우 RememberMeAuthenticationProvider 선택
    - 즉 ProviderManager의 역할은 인증 처리를 할 Provider 객체를 찾아 인증 처리 위임하는 역할
    - 만약 Oauth 인증인데 ProviderManager에 적절한 AuthenticationProvider 없는 경우 아래와 같이 처리
        - ProviderManager는 parent 속성이 존재
        - parent 속성도 AuthenticationManager 타입의 클래스를 제정하는 속성
        - 부모 속성의 ProviderManager 탐색하여 OauthAuthenticationProvider 찾음
        - 부모 속성의 ProviderManager가 OauthAuthenticationProvider에게 인증 위임

정리하면 ProviderManager는 인증 필터로 부터 인증을 지시 받는데 username, password를 저장한 Authentication 전달 받음

이후 ProviderMnager는 해당 인증을 처리할 수 있는 AuthenticationProvider 찾아 해당 인증 객체를 전달

만약 자신이 가지고 있는 Provider 중 인증 처리할 수 있는 것이 없다면 자신의 속성(parent)에 있는 ProviderManager 탐색하여  처리할 수 있는 Provider있으면 인증 처리를 맡기게 됨

- 각 AuthenticationProvider 객체는 스프링 시큐리티 초기화시 AuthenticationMnagerBuilder 클래스에 의해서 생성됨
- 기본으로 AnonymousAuthenticationProvider는 생성됨

![image10](https://github.com/user-attachments/assets/5c83ff74-5065-4037-a245-e5f7105dd80a)
