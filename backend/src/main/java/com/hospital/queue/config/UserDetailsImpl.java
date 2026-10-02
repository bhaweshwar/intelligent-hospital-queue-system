package com.hospital.queue.config;

import com.hospital.queue.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final String fullName;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String email, String password, String fullName, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getFullName(),
                authorities
        );
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
