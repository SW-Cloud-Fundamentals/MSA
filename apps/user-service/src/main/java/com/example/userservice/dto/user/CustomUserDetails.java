package com.example.userservice.dto.user;

import com.example.userservice.model.User;
import com.example.userservice.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
    public User getUser() {
        return this.user;
    }
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getNickname() {
        return user.getNickname();
    }

    public UserRole getRole() {
        return user.getRole();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않았음을 의미
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠기지 않았음을 의미
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명이 만료되지 않았음을 의미
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정이 활성화되어 있음을 의미
    }
}
