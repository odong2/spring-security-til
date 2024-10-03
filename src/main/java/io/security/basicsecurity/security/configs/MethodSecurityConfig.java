package io.security.basicsecurity.security.configs;

import io.security.basicsecurity.security.enums.SecurityMethodType;
import io.security.basicsecurity.security.factory.MethodResourcesMapFactoryBean;
import io.security.basicsecurity.security.factory.UrlResourcesMapFactoryBean;
import io.security.basicsecurity.security.filter.PermitAllFilter;
import io.security.basicsecurity.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import io.security.basicsecurity.security.voter.IpAddressVoter;
import io.security.basicsecurity.service.SecurityResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Slf4j
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration{

    private String[] permitAllPattern = {"/", "/home", "/users", "/login"};

    @Autowired
    private SecurityResourceService securityResourceService;

    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return mapBasedMethodSecurityMetadataSource();
    }

    @Bean
    public MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource() {
        return new MapBasedMethodSecurityMetadataSource(methodResourcesMapFactoryBean().getObject());
    }

    @Bean
    public MethodResourcesMapFactoryBean methodResourcesMapFactoryBean(){
        MethodResourcesMapFactoryBean methodResourcesMapFactoryBean = new MethodResourcesMapFactoryBean();
        methodResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);
        methodResourcesMapFactoryBean.setResourceType(SecurityMethodType.METHOD.getValue());
        return methodResourcesMapFactoryBean;
    }

    @Bean
    public PermitAllFilter permitAllFilter() {
        PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllPattern);
        permitAllFilter.setAccessDecisionManager(affirmativeBased());
        permitAllFilter.setSecurityMetadataSource(urlSecurityMetadataSource());
        return permitAllFilter;
    }

    @Bean
    public UrlFilterInvocationSecurityMetadataSource urlSecurityMetadataSource() {
        return new UrlFilterInvocationSecurityMetadataSource(urlResourcesMapFactoryBean().getObject(), securityResourceService);
    }

    @Bean
    public UrlResourcesMapFactoryBean urlResourcesMapFactoryBean(){
        UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean();
        urlResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);
        return urlResourcesMapFactoryBean;
    }

    @Bean
    public AccessDecisionManager affirmativeBased() {
        AffirmativeBased accessDecisionManager = new AffirmativeBased(getAccessDecisionVoters());
        return accessDecisionManager;
    }

    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {

        IpAddressVoter ipAddressVoter = new IpAddressVoter(securityResourceService);
        List<AccessDecisionVoter<? extends Object>> accessDecisionVoterList = Arrays.asList(/*ipAddressVoter,*/ roleVoter());
        return accessDecisionVoterList;
    }

    @Bean
    public RoleHierarchyVoter roleVoter() {
        RoleHierarchyVoter roleHierarchyVoter = new RoleHierarchyVoter(roleHierarchy());
        roleHierarchyVoter.setRolePrefix("ROLE_");
        return roleHierarchyVoter;
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        return roleHierarchy;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() throws Exception {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(permitAllFilter());
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }
}