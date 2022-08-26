package de.eckyl.oauth2demo.authentication;

import de.eckyl.oauth2demo.authentication.principal.DemoPrincipal;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class AuthoritiesDecisionVoter implements AccessDecisionVoter<FilterInvocation> {
    private static final Pattern ACCOUNT_TOPUP_PAYMENT_RECEIVED_PATTERN =
            Pattern.compile("/resources/echo");

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
        return Objects.isNull(pctPrincipal) ? 0 : checkAuthorities(pctPrincipal, object.getRequestUrl());
    }

    private int checkAuthorities(final DemoPrincipal cbPrincipal, final String requestUrl) {
        final var topupPaymentReceivedMatcher = ACCOUNT_TOPUP_PAYMENT_RECEIVED_PATTERN.matcher(requestUrl);
        final AtomicInteger result = new AtomicInteger(0);
        if (topupPaymentReceivedMatcher.find()) {
            cbPrincipal.getAuthorities()
                       .stream()
                       .filter(auth -> "SCOPE_echo".equals(auth.getAuthority()))
                       .findFirst()
                       .ifPresentOrElse(auth -> result.set(1), () -> result.set(-1));
        }
        return result.get();
    }
}
