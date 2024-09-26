### SecurityContextHolder / SecurityContext

- **SecurityContext**
    - Authentication 객체가 저장되는 보관소로 필요 시 언제든지 Authentication 객체를 꺼내어 쓸 수 있도록 제공되는 클래스
    - ThreaLocal에 저장되어 아무 곳에서나 참조가 가능하도록 설계함
    - 인증이 완료되면 HttpSession에 저장되어 어플리케이션 전반에 걸쳐 전역적인 참조 가능
- **SecurityContextHolder**
    - SecurityContext 감싸고 있는 클래스
    - SecurityContext 객체 저장 방식 전략
        - `MODE_THREADLOCAL`
            - 기본값
            - 스레드당 ScurityContext 객체를 할당
        - `MODE_INHERITABLETHREADLOCAL`
            - 메인 스레드와 자식 스레드에 관하여 동일한 SecurityContext 유지
            - 부모 자식 스레드 공유
        - `MODE_GLOBAL` : 응용 프로그램에서 단 하나의 SecurityContext 저장
            - `static` 변수에 SecurityContext 저장 방식
            - 메모리에서 단 하나만 생성
            - 모든 스레드에서 하나의 변수에서 SecurityContext를 참조
    - `SecurityContextHolder.clearContext()` : SecurityContext 기존 정보 초기화
- `Authentication authentication = SecurityContextHolder.getContext().getAuthentication()`
    - 해당 구문을 통해 전역에서 Authentication 객체 참조 가능

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/7f2365ae-ea78-4340-b09d-9671c8c311c7/90ac49ac-5cda-49ce-830a-98546d125e21/image.png)

1. **사용자 로그인**
2. **서버에서 스레드 하나 생성**
    - Spring에서는 ThreadPool에서 하나의 스레드 가져옴
    - 하나의 스레드는 스레드 전역 저장소인 ThreadLocal 가지고 있음
3. **인증 필터가 인증 처리**
    - **인증 실패**
        - `SecurityContextHolder.clearContext()` : SecurityContext null 초기화
    - **인증성공**
        - SecurityContext 객체 안에 인증 객체 저장
        - SecurityContextHodler의 `ThreadLocalSecurityContextHolderStrategy` 클래스가 ThreadLocal 참조 변수를 가지고 있음
        - 기본 전략인 스레드 로컬 전략을 따르게 되면 해당 클래스에서 스레드 로컬에 SecurityContext 저장
        - 최종적으로 SecurityContext가 `SPRING_SECURITY_CONTEXT` 라는 이름으로 HttpSession에 저장됨
        - 이후로는 **세션 & 스레드 로컬** 둘 다에서 SecurityContext 객체 참조 가능

### SecurityContext 저장 방식 설정 예제

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
            .authorizeRequests()
            .anyRequest().authenticated();
    http
            .formLogin();
		//메인 스레드와 자식 스레드에 관하여 동일한 SecurityContext 유지되도록 설정
    SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);

}
```

### SecurityContext 객체 참조 예제

```java
@RestController
public class SequrityController {

    @GetMapping("/")
    public String index(HttpSession session) {

        // 스레드 로컬에서 참조
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 세션에서 참조
        SecurityContext context = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        Authentication authentication1 = context.getAuthentication();
        return "home";
    }
}
```