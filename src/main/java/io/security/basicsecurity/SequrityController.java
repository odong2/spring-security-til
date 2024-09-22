package io.security.basicsecurity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

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

    @GetMapping("/thread")
    public String thread() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        // MODE_INHERITABLETHREADLOCAL 전략 사용해야 자식 스레드 공유가능
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    }
                }
        ).start();

        return "thread";
    }

}
