package ncu.cc.activedirectory.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Principal implements UserDetails {
    private String username;
    private Set<GrantedAuthority> authorities;

    public Principal() {
        this.authorities = new HashSet<>();
    }

    public Principal(String username, GrantedAuthority authority) {
        this();
        this.username = username;
        this.authorities.add(authority);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void addAuthroity(GrantedAuthority authority) {
        this.authorities.add(authority);
    }

    public void revokeAuthroity(GrantedAuthority authority) {
        this.authorities.remove(authority);
    }

    public boolean hasRole(GrantedAuthority authority) {
        return this.authorities.contains(authority);
    }

    public boolean hasAnyRole(GrantedAuthority...authorities) {
        for (GrantedAuthority authority: authorities) {
            if (this.authorities.contains(authority)) {
                return true;
            }
        }
        return false;
    }
}
