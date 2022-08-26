package de.eckyl.oauth2demo.authentication.principal;

import de.eckyl.oauth2demo.authentication.AuthenticationType;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;
import java.util.UUID;

public interface DemoPrincipal extends Principal {
    AuthenticationType getAuthenticationType();

    String getUserId();

    UUID getProfileId();

    void setProfileId(final UUID profileId);

    Collection<GrantedAuthority> getAuthorities();
}
