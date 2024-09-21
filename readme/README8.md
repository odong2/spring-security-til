### ExceptionTranslationFilter & RequestCacheAwareFilter

- **ExceptionTranslationFilter**
  - 해당 필터는 아래의 두 예외를 처리
    - `AuthenticationException`
    - `AccessDeniedException`
  - 해당 예외는 FilterSecurityInterceptor에서 발생
    - `FilterSecurityInterceptor`는 스프링 시큐리티의 보안 필터 중 가장 마지막에 위치
    - 이 필터 바로 앞에 위치하는 필터가 `ExceptionTranslationFilter`
  - `ExceptionTranslationFilter`에서 try-catch 감싸서 `FilterSecurityInterceptor` 호출하기 때문에 `FilterSecurityInterceptor`에서 발생한 인증 및 인가 예외를 해당 필터에서 처리하게 됨

### AuthenticationException & AccessDeniedException

- **AuthenticationException**
  - **인증 예외** 처리
  - 인증 예외 시 두 가지 처리를 하게 됨
    1. **AuthenticationEntryPoint 호출**
      - `AuthenticationEntryPoint`는 인터페이스로 스프링 시큐리티가 구현체를 제공하고 그 구현체를 호출
      - `AuthenticationEntryPoint` 인터페이스를 직접 구현하여 만들어 설정하게 되면 직접 구현한 엔티리 포인트 클래스를 호출함 → 이로인해 다양한 커스텀 처리 가능
      - 구현체 안에서 로그인 페이지로 이동하거나 401 오류 코드를 전달
      - 즉 사용자에 의해 인증 예외가 발생하였으므로 다시 인증을 시도할 수 있도록 처리가 이루어짐
    2. **인증 예외가 발생하기 전의 요청 정보를 저장**
      1. 인증이 필요한 자원에 접근 시도하였는데 인증된 상태가 아니라면 로그인 페이지 이동
      2. `AuthenticationEntryPoint`가 그 역할을 수행
      3. 인증 예외가 발생하기 전 사용자가 요청한 자원 정보 즉 URL 저장(캐싱)
      4. 이후 사용자가 인증에 성공하면 이전에 사용자가 요청했던 자원 정보(URL) 캐싱에서 꺼내어 그 페이지로 이동 시킴
      - `RequestCache`
        - 요청 정보를 저장 역할하는 인터페이스
        - 해당 인터페이스 구현체(`HttpSessionRequestCache` )가 역할을 수행함
        - 사용자의 이전 요청 정보를 세션에 저장하고 이를 꺼내 오는 캐시 메커니즘
        - `SavedRequest`
          - 인터페이스이고 구현체(`DefaultSavedRequest`)가 역할을 수행함
          - 사용자가 요청했던 request 파라미터 값들, 그 당시의 헤더값 등이 저장

          <aside>
          💡

        **즉 SavedRequest에 사용자의 요청 정보가 저장되고, SavedRequest를 세션에 저장하는 클래스가 RequestCached 구현체이다**

          </aside>

- **AccessDeniedException**
  - 인가 예외 처리
    - **권한 예외**가 발생
    - 권한이 없어서 접근되지 않는다는 메시지 또는 여러 처리 가능
    - `AccessDeniedHandler`인터페이스를 구현한 구현체 호출하여 후속 예외 처리

### ExceptionTranslationFilter 흐름

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/7f2365ae-ea78-4340-b09d-9671c8c311c7/9a2bc1c2-4261-4866-9baa-f785c963df0a/Untitled.png)

**인증 예외 프로세스**

1. 인증을 받지 않는 사용자가 request(/user) 요청
2. 맨 마지막에 위치한 `FilterSecurityInterceptor`(인가 처리를 하는 권한 필터)가 요청을 받음
3. 권한이 필요한 요청인데 권한이 없는 사용자임을 판단
4. 인가 예외(`AccessDeniedException`)발생(익명 사용자)
  - 인증을 받지 않고 자원에 접근 했기 때문에 익명 사용자가 자원에 접근하는 것이므로 인증 예외가 발생하지 않음
  - 단 권한이 없기 때문에 `AccessDeniedException` 발생
5. `ExceptionTranslationFilter` 가 받아서 `AcceDeniedException`으로 보냄
6. 하지만 AccessDeiniedHandler 호출하지 않고`AuthenticationException`으로 보냄
  - 해당 프로세스는 아래 두 경우에만 해당
    1. 익명 사용자일 경우
    2. Remember-me 인증으로 인증된 사용자일 경우
  - 인증 예외에서 처리하는 처리 과정으로 보내버림
7. `AuthenticationException`인증 예외 처리 프로세스 시작
  1. `AuthenticationEntryPoint`구현체 호출
    1. `SecurityContext`안의 인증 객체 null 처리 작업
    2. 로그인 페이지 이동하여 인증을 받도록 유도
  2. 그 다음으로 로그인 전에 가고자 했던 엔드 포인트인 `/user` 저장하기 위해 `DefaultSavedRequest`객체 안에 저장하고 이 정보는 다시 session에 저장이 됨 → 이 역할을 `HttpSessionRequestCache` 클래스가 담당

**인가 예외 프로세스**

1. 로그인한 유저의 권한이 user 인데 admin 페이지 요청
2. 인가 예외(`AccessDeniedException`) 발생
3. `AccessDeniedHandler` 호출하여 후속 작업 처리 → 보통 `/deinied` 페이지로 이동
  - 이 자원에 요청할 권한이 없습니다

위와 같이 `ExceptionTranslationFilter` 는 인증 예외와 인가 예외를 처리하는 필터이고, 이 예외를 던져주는 인터셉터는 `FilterSecurityInterceptor` (인가 처리를 담당하는 필터) 이다.

### 예외 처리 API

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/7f2365ae-ea78-4340-b09d-9671c8c311c7/2e63314f-04ae-4cc1-bb72-dfad1a876878/Untitled.png)

- 인증 예외 처리
  - `AuthenticationEntryPoint`인터페이스를 구현하여 설정
  - 해당 인터페이스의 `commence` 메서드 호출
- 인가 예외 처리
  - accessDeniedHandler 인터페이스를 구현한 구현체를 설정
- `RequestCacheAwareFilter`
  - 해당 필터는 인증 예외가 발생하기 전의 요청 정보 담고 있는 SavedRequest 객체가 존재하는지 확인
  - 존재하는 경우 SavedRequest 객체를 다음 필터로 넘기는 역할
  - 즉 기존의 request 객체가 아닌 SavedReuqest 객체를 넘김으로써 사용자 요청 정보를 다음 필터에서도 활용할 수 있도록 해주는 필터
- Config 코드

    ```java
        @Override
        protected void configure(HttpSecurity http) throws Exception {
    
            http
                    .authorizeRequests()
                    .antMatchers("/login").permitAll()
                    .antMatchers("/user").hasRole("USER")
                    .antMatchers("/admin/pay").hasRole("ADMIN")
                    .antMatchers("/admin/**").access("hasRole('ADMIN') or hasRole('SYS')")
                    .anyRequest().authenticated();
    
            http
                    .formLogin()
                    .successHandler(new AuthenticationSuccessHandler() {
                        @Override
                        public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException, ServletException {
                            RequestCache requestCache =  new HttpSessionRequestCache();
                            SavedRequest savedRequest = requestCache.getRequest(req, res);
                            String redirectUrl = savedRequest.getRedirectUrl();
                            // 인증 성공 후 세션에 저장되어 있던 이전 요청 페이지로 이동
                            res.sendRedirect(redirectUrl);
                        }
                    })
            ;
    
            http
                    .exceptionHandling()
                    .authenticationEntryPoint(new AuthenticationEntryPoint() {
                        @Override
                        public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException) throws IOException, ServletException {
                            res.sendRedirect("/login");
                        }
                    })
                    .accessDeniedHandler(new AccessDeniedHandler() {
                        @Override
                        public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                            res.sendRedirect("/denied");
                        }
                    });
    ```