package io.security.basicsecurity.service;

import io.security.basicsecurity.domain.entity.Resources;
import io.security.basicsecurity.repository.ResourcesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 설정 클래스에서 빈 생성
 */
@Slf4j
@RequiredArgsConstructor
public class SecurityResourceService {

    private final ResourcesRepository resourcesRepository;

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
}
