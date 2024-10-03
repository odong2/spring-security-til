package io.security.basicsecurity.service;

import io.security.basicsecurity.domain.entity.Resources;
import io.security.basicsecurity.repository.AccessIpRepository;
import io.security.basicsecurity.repository.ResourcesRepository;
import io.security.basicsecurity.security.service.RoleHierarchyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 설정 클래스에서 빈 생성
 */
@Slf4j
@RequiredArgsConstructor
public class SecurityResourceService {

    private final ResourcesRepository resourcesRepository;
    private final AccessIpRepository accessIpRepository;
    private final RoleHierarchyImpl roleHierarchy;
    private final RoleHierarchyService roleHierarchyService;

    /**
     * DB 에서 권한 목록 조회
     * @return
     */
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {

        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Resources> resourcesList = resourcesRepository.findAllResources();

        resourcesList.forEach(re ->
                {
                    List<ConfigAttribute> configAttributeList = new ArrayList<>();
                    re.getRoleSet().forEach(ro -> {
                        // ConfigAttribute 구현체 SecurityConfig 사용
                        configAttributeList.add(new SecurityConfig(ro.getRoleName()));
                    });
                    // requestMatcher 구현체 AntPathRequestMatcher 사용
                    result.put(new AntPathRequestMatcher(re.getResourceName()), configAttributeList);
                }
        );
        log.debug("cache test");
        return result;
    }

    public List<String> getAccessIpList() {

        return accessIpRepository.findAll().stream()
                .map(accessIp -> accessIp.getIpAddress()).collect(Collectors.toList());
    }

    public LinkedHashMap<String, List<ConfigAttribute>> getMethodResourceList() {

        LinkedHashMap<String, List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Resources> resourcesList = resourcesRepository.findAllMethodResources();
        resourcesList.forEach(re ->
                {
                    List<ConfigAttribute> configAttributeList = new ArrayList<>();
                    re.getRoleSet().forEach(ro -> {
                        configAttributeList.add(new SecurityConfig(ro.getRoleName()));
                        result.put(re.getResourceName(), configAttributeList);
                    });
                }
        );
        return result;
    }

    public void setRoleHierarchy() {
        String allHierarchy = roleHierarchyService.findAllHierarchy();
        roleHierarchy.setHierarchy(allHierarchy);
    }
}
