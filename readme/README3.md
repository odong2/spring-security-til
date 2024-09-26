### Logout API
<img width="620" src="https://github.com/user-attachments/assets/3b3912ee-4dfd-4bbf-ad18-63605a9f5f0c">

### Logout Filter
<img width="620" src="https://github.com/user-attachments/assets/df61af7d-2b4e-4416-819f-f901644536e9">
<img width="620" src="https://github.com/user-attachments/assets/9e210a92-c46f-4a07-9522-afb5672b0c67">


**로그아웃 필터 실행 흐름**

1. 로그아웃 요청 시 로그아웃 필터가 요청을 받음
2. AntPathRequestMatcher `/logout` 매치 되는지 검사
    - 일치하지 않으면 다음 필터 이동
3. 매치가된 경우 SecurityContext에서 인증된 객체(Authentication) 가지고 와서 로그아웃 핸들러에게 전달
4. 로그아웃 필터가 가지고 있는 로그아웃 핸들러가 몇가지 존재. 그 중 SecurityContextLogoutHandler는 세션을 무효화, 쿠키 삭제, SecurityContext 객체 삭제, 인증 객체 null 초기화 역할 수행
5. 로그아웃 핸들러가 성공적으로 종료되면 로그아웃 필터는 그 다음 SimpleUrlLogoutSuccessHandler 호출하여 로그인 페이지로 이동하도록 처리
