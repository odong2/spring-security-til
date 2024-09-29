package io.security.basicsecurity.security.voter;

import io.security.basicsecurity.service.SecurityResourceService;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Collection;
import java.util.List;

public class IpAddressVoter implements AccessDecisionVoter<Object> {
    private SecurityResourceService securityResourceService;

    public IpAddressVoter(SecurityResourceService securityResourceService) {
        this.securityResourceService = securityResourceService;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return (attribute.getAttribute() != null);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    /**
     * 권한 심의
     * @param authentication : 인증 객체 정보
     * @param object         : FilterInvocation 객체 -> request 정보 얻을 수 있음
     * @param configList     : FilterInvocationMataDataSource 에서 넘어온 자원에 필요한 권한 정보 리스트
     *
     * @return
     */
    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> configList) {

        if (!(authentication.getDetails() instanceof WebAuthenticationDetails)) {
            return ACCESS_DENIED;
        }
        // Authentication 객체에는 Details 존재
        // 여기에는 기본적으로 사용자의 IP 주소값을 얻을 수 있음 -> WebAuthenticationDetails 타입
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();

        // 요청 사용자 ip
        String address = details.getRemoteAddress();

        // 허용된 ip 리스트 조회
        List<String> accessIpList = securityResourceService.getAccessIpList();

        int result = ACCESS_DENIED;

        for (String ipAddress : accessIpList) {

            if (address.equals(ipAddress)) {
                // IP 심의 후 다른 심의 거치기 위해 ABSTAIN
                // GRANTED 주게 되면 권한 심의 거치지 못하므로 주의!
                return ACCESS_ABSTAIN;
            }
        }

        if(result == ACCESS_DENIED){
            throw new AccessDeniedException("Invalid ipAddress can not accessed");
        }

        return result;
    }
}
