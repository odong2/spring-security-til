package io.security.basicsecurity.config;

import io.security.basicsecurity.domain.entity.RoleHierarchy;
import io.security.basicsecurity.repository.AccessIpRepository;
import io.security.basicsecurity.repository.ResourcesRepository;
import io.security.basicsecurity.security.service.RoleHierarchyService;
import io.security.basicsecurity.service.SecurityResourceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class AppConfig {

    /**
     * SecurityResourceService 빈 생성
     *
     * @param resourcesRepository
     * @param accessIpRepository
     * @param roleHierarchy
     * @param roleHierarchyService
     * @return
     */
    @Bean
    public SecurityResourceService securityResourceService(ResourcesRepository resourcesRepository,
                                                           AccessIpRepository accessIpRepository,
                                                           RoleHierarchyImpl roleHierarchy,
                                                           RoleHierarchyService roleHierarchyService
                                                           ) {

        return new SecurityResourceService(resourcesRepository, accessIpRepository, roleHierarchy, roleHierarchyService);
    }

}
