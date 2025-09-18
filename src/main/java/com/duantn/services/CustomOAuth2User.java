package com.duantn.services;

import com.duantn.entities.TaiKhoan;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;
    private final TaiKhoan taiKhoan;

    public CustomOAuth2User(OAuth2User oauth2User, TaiKhoan taiKhoan) {
        this.oauth2User = oauth2User;
        this.taiKhoan = taiKhoan;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oauth2User.getName(); // hoặc email
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public String getEmail() {
        return taiKhoan.getEmail();
    }
}