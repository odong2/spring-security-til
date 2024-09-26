### 인증처리 프로세스 간략 정리

<img width="500" alt="image17" src="https://github.com/user-attachments/assets/7807a8ee-ed9d-480d-b8d2-cdb9733adac4">

1. 사용자가 get 방식으로 home url 요청
2. home url은 인증된 사용자만 접근할 수 있도록 보안 설정이 설정된 상태
3. 사용자가 인증받지 않은 경우 로그인 페이지로 이동시킴
4. 로그인 성공하면 스프링 시큐리티가 세션을 생성
5. 최종적으로 세션에 인증 성공한 결과를 담은 인증 토큰을 생성(Security Context 객체)하여 세션에 저장
6. 이 후 다시 home url 요청 시 서버는 인증 토큰의 존재 여부를 판단
7. 세션에 인증 객체가 있으면 인증된 사용자로 보고 해당 자원 제공

### formLogin 인증 API 기능 정리

<img width="636" alt="image17" src="https://github.com/user-attachments/assets/facf5882-cb5a-48a7-a9c0-6a719e148e3b">

- **loginPage :** 사용자 정의 로그인 페이지 사용
- **defaultSucessUrl :** 이동할 페이지 경로
- **failureUrl :** 로그인 실패 시 이동페이지
- **usernameParameter :** 아이디 파라미터명 설정
- **passwordParameter :** 패스워드 파라미터명 설정
- **loginProcessingUrl :** 로그인 Form Action Url
- **successHandler :** 로그인 성공 후 핸들러
- **failureHandler :** 로그인 실패 후 핸들러

### 예제 코드

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 인가 정책
        http
                .authorizeRequests()
                .anyRequest().authenticated();

        // 인증 정책
        http
                .formLogin()
//              .loginPage("/loginPage")
                .defaultSuccessUrl("/")
                .failureUrl("/login")
                .usernameParameter("userId")
                .passwordParameter("passwd")
                .loginProcessingUrl("/login_proc")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        System.out.println(" authentication " + authentication.getName());
                        response.sendRedirect("/");
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        System.out.println(" exception " + e.getMessage());
                        response.sendRedirect("/login");
                    }
                })
                .permitAll()
        ;
    }
}

```
