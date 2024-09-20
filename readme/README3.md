### Logout API

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/7f2365ae-ea78-4340-b09d-9671c8c311c7/47cb426c-6003-4cb2-8f2d-d92e840bbb78/image.png)

### Logout Filter

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/7f2365ae-ea78-4340-b09d-9671c8c311c7/46228e6f-d6d5-4843-8461-e772a464200a/image.png)

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/7f2365ae-ea78-4340-b09d-9671c8c311c7/f7949927-e54c-4f30-a8df-902016456c08/image.png)

**로그아웃 필터 실행 흐름**

1. 로그아웃 요청 시 로그아웃 필터가 요청을 받음
2. AntPathRequestMatcher `/logout` 매치 되는지 검사
    - 일치하지 않으면 다음 필터 이동
3. 매치가된 경우 SecurityContext에서 인증된 객체(Authentication) 가지고 와서 로그아웃 핸들러에게 전달
4. 로그아웃 필터가 가지고 있는 로그아웃 핸들러가 몇가지 존재. 그 중 SecurityContextLogoutHandler는 세션을 무효화, 쿠키 삭제, SecurityContext 객체 삭제, 인증 객체 null 초기화 역할 수행
5. 로그아웃 핸들러가 성공적으로 종료되면 로그아웃 필터는 그 다음 SimpleUrlLogoutSuccessHandler 호출하여 로그인 페이지로 이동하도록 처리