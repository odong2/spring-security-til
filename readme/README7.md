### 권한 설정과 표현식

- **선언적 방식**
    - URL 방식
        - http.antMatchers(”/user/**/”).hasRole(”USER”)
    - Method 방식

        ```java
        @PreAuthorize(”hasRole(’USER’)”)
        public void user(){System.out.println("user")}
        ```

- **동적 방식 - DB 연동 프로그래밍**
    - URL
    - Method

### URL 방식

```java
@Override
protected void configure(HttpSecurity http) throws Exception{
	
	http
		.antMatcher("/shop/**")
		.authorizeRequests()
			.antMatchers("shop/login", "shop/user/**").permitAll()
			.antMatchers("shop/mypage"),hasRole("USER")
			.antMatchers("shop/admin/pay").access("hasRole('ADMIN')");
			.antMatchers("shop/admin/**).access("hasRole('ADMIN) or hasRole('SYS')")
			.anyRequest().authenticated();
}
```

- antMatcher
    - 특정 경로를 명시하면 사용자 요청이 명시한 특정 경로로 요청할 때만 설정된 보안 기능 동작
    - 즉 특정한 자원의 요청에 대해 인증과 인가를 하고자 할 때 antMatcher 사용
- antMatchers
    - 여러 경로 명시 가능
    - 설정한 URL과 일치하면 처리됨
- anyRequest().authenticated()
    - 모든 요청에는 인증을 받은 사용자만 요청하는 자원에 접근이 가능하게 하는 설정

<aside>
	
**주의사항 - 설정 시 구체적인 경로가 먼저 오고 그것 보다 큰 범위의 경로가 뒤에 오도록 설정해야한다**

</aside>

### 인가 API 표현식

| 메서드 | 동작 |
| --- | --- |
| **authenticated()** | 인증된 사용자의 접근을 허용  |
| **fullyAuthenticated()** | 인증된 사용자의 접근을 허용, rememberMe 인증 제외  |
| **permitAll()** | 무조건 접근을 허용 |
| **denyAll()** | 무조건 접근을 허용하지 않음 |
| **anonymous()** | 익명 사용자의 접근 허용 / 인증된 사용자는 접근 불가능 |
| **rememberMe()** | rememberMe 통해 인증된 사용자의 접근을 허용 |
| **access(String)** | 주어진 SpEL 표현식의 평가 결과가 true이면 접근을 허용 |
| **hasRole(String)** | 사용자가 주어진 역할이 있다면 접근을 허용 |
| **hasAuthority(String)** | 사용자가 주어진 권한이 있다면 |
| **hasAnyRole(String…)** | 사용자가 주어진 권한이 있다면 접근을 허용 |
| **hasAnyAuthority(String…)** | 사용자가 주어진 권한 중 어떤 것이라도 있다면 접근을 허용 |
| **hasIpAddress(String)** | 주어진 IP로부터 요청이 왔다면 접근을 허용 |

### 테스트 코드

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication().withUser("user").password("{noop}1111").roles("USER");
    auth.inMemoryAuthentication().withUser("sys").password("{noop}1111").roles("SYS");
    auth.inMemoryAuthentication().withUser("admin").password("{noop}1111").roles("ADMIN");
}

@Override
protected void configure(HttpSecurity http) throws Exception {

    http
            .authorizeRequests()
            .antMatchers("/user").hasRole("USER")
            .antMatchers("/admin/pay").hasRole("ADMIN")
            .antMatchers("/admin/**").access("hasRole('ADMIN') or hasRole('SYS')")
            .anyRequest().authenticated();

    http
            .formLogin();
}
```

- configure(AuthenticationManagerBuilder auth) 메서드 오버라이딩
    - AuthenticationMnagerBUilder 클래스는 사용자를 생성하고 권한을 설정할 수 있도록 하는 클래스
    - 테스트 용으로 인메모리 형식으로 생성
- password는 앞에 {} 특별한 prefix가 붙음
    - password encode
    - 패스워드를 암호화 할 때 암고리즘 방식을 prefix 형태로 명시 해야 함
    - 이후 인증 시 패스워드 매치 작업을 할 때 어떠한 알고리즘으로 암호화 했는지 필요하기 때문
    - {noop}은 사용하지 않는다는 의미
