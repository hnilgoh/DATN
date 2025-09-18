package com.duantn.services;

import com.duantn.entities.Role;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.RoleRepository;
import com.duantn.repositories.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String avatar = oauthUser.getAttribute("picture");

        TaiKhoan user = taiKhoanRepository.findByEmail(email).orElseGet(() -> {
            Role defaultRole = roleRepository.findByName("ROLE_HOCVIEN")
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Role HOCVIEN"));
            TaiKhoan newUser = TaiKhoan.builder()
                    .email(email)
                    .name(name)
                    .avatar(avatar)
                    .password("")
                    .status(true)
                    .role(defaultRole)
                    .build();
            return taiKhoanRepository.save(newUser);
        });

        String roleName = user.getRole().getName(); // KHÔNG .toUpperCase()
        return new CustomOAuth2User(
                new DefaultOAuth2User(
                        Collections.singletonList(new SimpleGrantedAuthority(roleName)),
                        oauthUser.getAttributes(),
                        "email"),
                user);

    }
}