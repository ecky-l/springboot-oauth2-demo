package de.eckyl.oauth2demo.authentication;

import org.springframework.security.access.vote.RoleVoter;

public class DemoRoleVoter extends RoleVoter {
    @Override
    public String getRolePrefix() {
        return "SCOPE_";
    }
}
