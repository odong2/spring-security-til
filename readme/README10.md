### DelegatingFilterProxy

<img width="600" src="https://github.com/user-attachments/assets/a11e73ec-588b-4037-877d-cde0f0971cb6">

- `DelegatingFilterProxy`는 **서블릿 필터이다**
- Spring에서 관리하는 필터가 아니다
- 요청을 받아서 스프링에서 관리하는 필터 필터 빈에게 위임하는 역할을 수행
- `SpringSecurityFilterChain` 이름을 가진 빈을 Spring의 ApplicationContext에서 찾아 요청을 위임
- 즉 이 클래스는 실제 보안처리는 하지 않고 요청을 위임하는 역할만 함

### FilterChainProxy

<p>
<img width="400" height="400" alt="image" src="https://github.com/user-attachments/assets/0cdef3e8-3582-4c82-85de-4abdbe45853f">
</p>


<aside>
💡

FilterChainProxy는 실제로 보안처리를 하게 되는 시작점

</aside>

1. `DelegatingFilterProxy`가 `SpringSecurityFilterChain` 의 이름으로 생성되는 필터 빈을 찾음
    - `SpringSecurityFilterChain` 이름을 가진 빈이 `FilterChainProxy` 이다
    - 스프링 시큐리티 초기화 시 FilterChaingProxy 스프링 빈으로 생성됨
2. `DelegatingFilterProxy`로 부터 요청을 위임 받고 실제 보안 처리하는 클래스
3. 스프링 시큐리티 초기화 시 생성되는 필터들을 관리하고 제어하는 역할 수행
    - 시큐리티가 기본적으로 생성하는 필터
    - 설정 클래스에서 API 추가 시 생성하는 필터
4. 사용자의 요청을 필터 순서대로 호출하여 전달
    - 0번째 필터 처리 후 다시 `FilterChainProxy`호출 → 다음 필터인 1번째 필터 호출 … → 14번째 필터 처리
    - 위와 같이 반복하여 마지막 필터까지 호출됨
5. 사용자 정의 필터를 생성해서 기존의 필터 전후로 추가 가능
    - 필터의 순서를 잘 정의해야함
6. 마지막 필터까지 인증 및 인가 예외가 발생하지 않으면 보안 통과 → 서블릿 접근

### 전체적 흐름

<img width="600" src="https://github.com/user-attachments/assets/35bb64ee-4b75-4d2d-8d46-52c01b9000ac">


1. 사용자 요청하면 서블릿 컨테이너에서 가장 먼저 요청을 받게 됨
2. 요청에 대해서 각각의 서블릿 필터들이 요청을 처리
3. `DelegatingFilterProxy`가 요청을 받게 되면 전달 받은 요청 객체를 `SpringSecurityFilterChain`이름을 가진 빈 즉 `FilterChainProxy`에게 요청을 위임하면서 전달
    - 모든 요청의 가장 처음은 DelegatingFilterProxy이다
4. FilterChainProxy는 자신이 관리하는 모든 필터들을 호출하여 보안 처리 수행
5. 모든 필터들이 수행되면 DispatcherServlet에 요청을 전달하여 해당 서블릿에서 요청에 대한 처리를 수행

<img width="600" src="https://github.com/user-attachments/assets/f5d2049b-24d1-492f-8120-aa2d290275f3">

SecurityFilterAutoConfiguration에서 DelegatingFilterProxy 등록 시 springSecurityFilterChain 이름으로 등록하는 것을 확인할 수 있다
