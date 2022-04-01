package ncu.cc.activedirectory.models;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public enum MyAuthorityEnum implements GrantedAuthority {
    ROLE_NULL,          // dummy, null
    ROLE_USER,          // any logged in user
    ROLE_ADMIN,         // system administrator
    ROLE_DEVELOPER;     // developer

    private static final String LEADING = "ROLE_";
    private GrantedAuthority authority;

    MyAuthorityEnum() {
        authority = () -> this.name();
    }

    public String getAuthority() {
        return authority.getAuthority();
    }

    public static final MyAuthorityEnum byName(final String name) {
        for (MyAuthorityEnum entry: MyAuthorityEnum.values()) {
            if (name.equals(entry.authority.getAuthority())) {
                return entry;
            }
        }

        return ROLE_NULL;
    }

    public String shortName() {
        if (this.name().startsWith(LEADING)) {
            return this.name().substring(LEADING.length());
        } else {
            return this.name();
        }
    }
}
