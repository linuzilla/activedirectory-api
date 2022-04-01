package ncu.cc.activedirectory.security;

import ncu.cc.activedirectory.models.Principal;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {
    Authentication getAuthentication();
    String currentUser();
    Principal loginAs(String loginName);
    Principal currentUserPrincipal();
}
