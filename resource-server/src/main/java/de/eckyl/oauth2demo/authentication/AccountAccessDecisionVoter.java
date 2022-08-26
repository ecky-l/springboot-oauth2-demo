package de.eckyl.oauth2demo.authentication;

import de.eckyl.oauth2demo.authentication.principal.DemoPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

@Component
@Slf4j
public class AccountAccessDecisionVoter implements AccessDecisionVoter<FilterInvocation> {


    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public int vote(Authentication authentication, FilterInvocation object,
                    Collection<ConfigAttribute> attributes) {
        final var pctPrincipal = (DemoPrincipal) authentication;
        return checkPermissions(object.getRequestUrl(), Objects.requireNonNull(pctPrincipal));
    }

    private int checkPermissions(String requestUrl, DemoPrincipal requireNonNull) {
        return 1;
    }
}
