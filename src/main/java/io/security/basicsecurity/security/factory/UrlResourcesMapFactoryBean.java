package io.security.basicsecurity.security.factory;

import io.security.basicsecurity.service.SecurityResourceService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * DB로 부터 자원 목록을 가져와서 그 자원과 권한 정보로
 * 매핑된 데이터를 가지고 있음
 */
public class UrlResourcesMapFactoryBean implements FactoryBean<LinkedHashMap<RequestMatcher, List<ConfigAttribute>>> {

    private SecurityResourceService securityResourceService;
    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourcesMap;

    public void setSecurityResourceService(SecurityResourceService securityResourceService) {
        this.securityResourceService = securityResourceService;
    }

    /**
     * DB 에서 권한 목록 조회 후 resourceMap 초기화
     */
    public void init() {
            resourcesMap = securityResourceService.getResourceList();
    }

    /**
     * resourceMap 조회
     * null 인 경우 db 조회 후 리턴
     * @return resourceMap
     */
    @Override
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getObject() {
        if (resourcesMap == null) {
            init();
        }
        return resourcesMap;
    }

    @Override
	public Class<LinkedHashMap> getObjectType() {
        return LinkedHashMap.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
