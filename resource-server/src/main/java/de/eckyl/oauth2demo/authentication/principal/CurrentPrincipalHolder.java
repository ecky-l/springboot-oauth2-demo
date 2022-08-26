package de.eckyl.oauth2demo.authentication.principal;

import de.eckyl.oauth2demo.authentication.AuthenticationException;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.UUID;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CurrentPrincipalHolder implements CurrentPrincipal, CurrentPrincipalSetter {

    private DemoPrincipal currentPrincipal = null;

    @Override
    public DemoPrincipal get() {
        return Optional.ofNullable(currentPrincipal).orElseThrow(AuthenticationException::new);
    }

    @Override
    public String getUserId() {
        return get().getUserId();
    }

    @Override
    public UUID getProfileId() {
        return get().getProfileId();
    }

    @Override
    public void set(DemoPrincipal principal) {
        this.currentPrincipal = principal;
    }
}
