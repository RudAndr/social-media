package com.rudenko.socialmedia.data.security

import com.rudenko.socialmedia.data.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import java.beans.ConstructorProperties

class SocialUserDetails implements UserDetails {
    private String username
    private User user

    @ConstructorProperties(["username", "user"])
    SocialUserDetails(String username, User user) {
        this.username = username
        this.user = user
    }

    @Override
    Collection<? extends GrantedAuthority> getAuthorities() {
        return [new SimpleGrantedAuthority("ROLE_USER")]
    }

    @Override
    String getPassword() {
        return null
    }

    @Override
    String getUsername() {
        return username
    }

    User getUser() {
        return user
    }

    @Override
    boolean isAccountNonExpired() {
        return true
    }

    @Override
    boolean isAccountNonLocked() {
        return true
    }

    @Override
    boolean isCredentialsNonExpired() {
        return true
    }

    @Override
    boolean isEnabled() {
        return true
    }
}
