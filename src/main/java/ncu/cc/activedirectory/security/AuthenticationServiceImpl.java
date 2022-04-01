package ncu.cc.activedirectory.security;

import ncu.cc.activedirectory.models.MyAuthorityEnum;
import ncu.cc.activedirectory.models.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationServiceImpl implements AuthenticationService {
//    @Autowired
//    private SystemUserRepository systemUserRepository;

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public String currentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public Principal loginAs(String loginName) {
        Principal principal = new Principal(loginName, MyAuthorityEnum.ROLE_USER);

//        Optional<SystemUser> userOptional = systemUserRepository.findById(loginName);
//
//        if (userOptional.isPresent()) {
//            Set<RoleEnum> roleEnumSet = RoleEnum.byValue(userOptional.get().getSpecialRols());
//
//            if (roleEnumSet.contains(RoleEnum.ROLE_ADMIN)) {
//                principal.addAuthroity(MyAuthorityEnum.ROLE_ADMIN);
//            }
//            if (roleEnumSet.contains(RoleEnum.ROLE_DEVELOPER)) {
//                principal.addAuthroity(MyAuthorityEnum.ROLE_DEVELOPER);
//            }
//        }

//        Authentication authentication = new PortalAuthAuthenticationToken(principal);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        return principal;
    }

    @Override
    public Principal currentUserPrincipal() {
        return (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}