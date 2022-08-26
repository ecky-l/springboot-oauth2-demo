package de.eckyl.oauth2demo.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.eckyl.oauth2demo.authentication.principal.CurrentPrincipalSetter;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ApiAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final ObjectMapper jsonObjectMapper = new ObjectMapper();

    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final CurrentPrincipalSetter currentPrincipalSetter;

    @Autowired
    public ApiAuthenticationConverter(CurrentPrincipalSetter currentPrincipalSetter) {
        this.currentPrincipalSetter = currentPrincipalSetter;
        this.jwtAuthenticationConverter = new JwtAuthenticationConverter();
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        final var token = jwtAuthenticationConverter.convert(source);
        //final var principal = new DemoJwtPrincipal(source, token.getAuthorities());
        final var principal = new DemoJwtPrincipal(source, extractAuthorities(token));
        principal.setProfileId(UUID.randomUUID());
        currentPrincipalSetter.set(principal);
        return principal;
    }

    private List<SimpleGrantedAuthority> extractAuthorities(
            final AbstractAuthenticationToken token) {
        final var rolesObj = ((JwtAuthenticationToken) token).getToken().getClaim("realm_access");
        if (Objects.isNull(rolesObj)) {
            throw new AuthenticationException();
        }
        try {
            final var access = jsonObjectMapper.readValue(
                    rolesObj.toString(), OauthAccess.class);
            return access.getRoles().stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .toList();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    private static class OauthAccess {
        private ArrayList<String> roles;
    }
}
