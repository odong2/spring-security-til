### 필터 초기화와 다중 설정 클래스

<img width="600" src="https://github.com/user-attachments/assets/2184fbc9-8750-4596-adc9-ad849fea5d2a">

- WebSecurityConfigurerAdapter 상속 받아 httpSecurity 통해 인증 및 인가 API 설정 (SequrityConfig 클래스)
- 위 이미지는 기존 SecurityConfig를 하나만 설정하는 것이 아닌 두 개를 생성하여 다중으로 설정하는 방법의 예시
- 설정 클래스 별로 보안 기능이 각각 작동
- 설정 클래스 별로 RequestMatcher 설정 가능
    - `http.antMatcher(”admin/**”)`
    - 만약 첫 번째 보안 설정 클래스가 위의 antMatcher로 admin 경로를 보안 설정한 상황에서 /admin/** URL이 아닌 다른 URL로 요청이 오면 두 번째 설정 클래스가 동작
- 설정 클래스 별로 필터가 생성
- 스프링 시큐리티 초기화 시 두 개의 설정 클래스가 어떻게 다르게 생성이 되는가?
    - SecurityFilterChain 클래스 존재
    - 설정에서 생성된 필터가  해당 클래스 객체 안의 Filters 변수 안에 담기
    - antMatcher(”/admin/**”) 설정한 것은 RequestMacher 타입의 변수에 담김
    - 필터와 RequestMatcher 정보가 담긴 객체(SecurityFilterChain)가 생성됨
    - 이렇게 각각의 생성된 객체를 FilterChainProxy가 SecurityFilterChains 리스트 변수에 저장
- SecurityConfig1, 2의 설정 클래스에서 생성한 필터 목록들을 FilterChainProxy가 가지고 있게 됨
- 요청에 따라 RequestMatcher와 매칭되는 필터가 작동하도록 함
- 두 설정 클래스를 사용할 때는 @Order 어노테이션을 통해 어느 설정 정보를 먼저 초기화 할 것인지 지정해야 에러가 나지 않음
- @Order 순서에 따라 사용자 요청 시 체크하는 설정 클래스 순서도 달라짐
- 그러므로 구체적인 `/admin` 과 같이 구체적인 URL 설정 클래스의 Order를 더 앞에 두고 범용적인 URL을 후순위로 배치한다

### 다중 설정 클래스 흐름

![image4](https://github.com/user-attachments/assets/acc98afd-ad3a-4e94-8175-db3b114a6b02)

1. 사용자가 `/admin` 요청
2. FilterChainProxy 요청 받음
    - 요청을 처리할 필터 선택 필요
3. 각각의 설정 클래스의 RequestMacher 정보와 요청 URL이 매치되는 정보를 찾음
4. 첫 번째 설정 클래스에서 생성한 RequestMacher와 일치
5. 해당 설정 클래스에서 설정한 필터가 동작하도록 선택
6. 인증 및 인가 진행

### 테스트 코드

```java
@Configuration
@EnableWebSecurity
@Order(0)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/admin/**")
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
    }
}

@Configuration
@Order(1)
class SecurityConfig2 extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .formLogin();
    }
}
```
