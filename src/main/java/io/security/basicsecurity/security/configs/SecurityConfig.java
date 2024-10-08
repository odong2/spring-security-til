package io.security.basicsecurity.security.configs;

import io.security.basicsecurity.domain.entity.RoleHierarchy;
import io.security.basicsecurity.security.common.FormAuthenticationDetailSource;
import io.security.basicsecurity.security.factory.UrlResourcesMapFactoryBean;
import io.security.basicsecurity.security.filter.PermitAllFilter;
import io.security.basicsecurity.security.handler.FormAccessDeniedHandler;
import io.security.basicsecurity.security.handler.FormAuthenticationFailureHandler;
import io.security.basicsecurity.security.handler.FormAuthenticationSuccessHandler;
import io.security.basicsecurity.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import io.security.basicsecurity.security.provider.FormAuthenticationProvider;
import io.security.basicsecurity.security.voter.IpAddressVoter;
import io.security.basicsecurity.service.SecurityResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Configuration
@EnableWebSecurity
@Order(1)
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private FormAuthenticationSuccessHandler formAuthenticationSuccessHandler;

    @Autowired
    private FormAuthenticationFailureHandler formAuthenticationFailureHandler;

    @Autowired
    private FormAuthenticationDetailSource formAuthenticationDetailSource;

    @Autowired
    private SecurityResourceService securityResourceService;

    private String[] permitAllResources = {"/", "/login", "/user/login/**"};

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 인증 처리 시 커스텀해서 만든 AuthenticationProvider 사용하여 인증 처리
        auth.authenticationProvider(authenticationProvider());
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        return new FormAuthenticationProvider();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        FormAccessDeniedHandler accessDeniedHandler = new FormAccessDeniedHandler();
        accessDeniedHandler.setErrorPage("/denied");
        return accessDeniedHandler;
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 정적 파일 보안필터 거치지 않게 설정
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_proc")
                .authenticationDetailsSource(formAuthenticationDetailSource)
                .successHandler(formAuthenticationSuccessHandler) // 인증 성공 후 처리 핸들러
                .failureHandler(formAuthenticationFailureHandler) // 인증 실패 후 처리 핸들러
                .permitAll()
            .and()
                .exceptionHandling()
                // .authenticationEntryPoint(new AjaxLoginAuthenticationEntryPoint())
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .accessDeniedPage("/denied")
                .accessDeniedHandler(accessDeniedHandler())
            .and()
                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class)
        ;

        http
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());
    }

    @Bean
    public PermitAllFilter customFilterSecurityInterceptor() throws Exception {

        PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllResources);
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        permitAllFilter.setAccessDecisionManager(affirmativeBased());          // 인가 관리자 설정
        permitAllFilter.setAuthenticationManager(authenticationManagerBean()); // 인증 관리자 설정
        return permitAllFilter;
    }

    @Bean
    public FilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() {
        return new UrlFilterInvocationSecurityMetadataSource(urlResourcesMapFactoryBean().getObject(),
                                                                securityResourceService);
    }

    @Bean
    public UrlResourcesMapFactoryBean urlResourcesMapFactoryBean(){
        UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean();
        urlResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);
        return urlResourcesMapFactoryBean;
    }

    /**
     * 접근 결정 관리자
     * @return
     */
    private AccessDecisionManager affirmativeBased() {
        AffirmativeBased affirmativeBased = new AffirmativeBased(getAccessDecisionVoters());
        return affirmativeBased;
    }

    /**
     * voter 설정
     * @return
     */
    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {

        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
        // 순서 중요
        accessDecisionVoters.add(ipAddressVoter());    // 1. ipAddress 심의
        accessDecisionVoters.add(roleVoter());         // 2. 권한 심의

        return accessDecisionVoters;
    }

    @Bean
    public AccessDecisionVoter<? extends Object> ipAddressVoter() {
        return new IpAddressVoter(securityResourceService);
    }

    /********************************************************
     * 계층 권한 관련 Bean
     ********************************************************/

    @Bean
    public AccessDecisionVoter<? extends Object> roleVoter() {
        // 규칙을 저장한 클래스(RoleHierarchy 클래스) 생성자에 전달
        return new RoleHierarchyVoter(roleHierarchy());
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        return roleHierarchy;
    }
}
