package de.eckyl.oauth2demo.authentication;

import de.eckyl.oauth2demo.authentication.principal.DemoPrincipal;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.UUID;

public class DemoJwtPrincipal extends JwtAuthenticationToken implements DemoPrincipal {
    @Getter
    private final AuthenticationType authenticationType;
    @Getter
    private final String clientId;
    @Getter
    @Setter
    private UUID profileId;

    public DemoJwtPrincipal(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        super(jwt, authorities);
        if (getTokenAttributes().containsKey("sub")) {
            authenticationType = AuthenticationType.OIDC_USER;
            this.clientId = null;
        } else if (getTokenAttributes().containsKey("client_id")) {
            authenticationType = AuthenticationType.OIDC_CLIENT;
            this.clientId = getTokenAttributes().get("client_id").toString();
        } else {
            throw new IllegalArgumentException("unknown authentication type");
        }
    }

    @Override
    public String getUserId() {
        return getName();
    }

    @Override
    public String getName() {
        return (authenticationType == AuthenticationType.OIDC_CLIENT) ? clientId : super.getName();
    }
}
