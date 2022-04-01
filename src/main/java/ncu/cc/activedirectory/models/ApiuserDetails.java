package ncu.cc.activedirectory.models;

import ncu.cc.activedirectory.entities.Apiuser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ApiuserDetails implements UserDetails {
    private final String username;
    private final String password;
    private final Set<GrantedAuthority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public ApiuserDetails(Apiuser apiuser) {
        this.username = apiuser.getId();
        this.password = apiuser.getPassword();
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
        this.authorities = new HashSet<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void addAuthority(GrantedAuthority grantedAuthority) {
        if (! grantedAuthority.getAuthority().equals(MyAuthorityEnum.ROLE_NULL)) {
            this.authorities.add(grantedAuthority);
        }
    }
}
