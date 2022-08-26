package de.eckyl.oauth2demo.authentication.principal;

import java.util.UUID;

public interface CurrentPrincipal {
    DemoPrincipal get();

    String getUserId();

    UUID getProfileId();
}
