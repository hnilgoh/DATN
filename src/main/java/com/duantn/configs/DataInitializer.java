package com.duantn.configs;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.duantn.entities.Role;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.RoleRepository;
import com.duantn.repositories.TaiKhoanRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final TaiKhoanRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private void ensureRoleExists(String roleName) {
        roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = Role.builder().name(roleName).build();
            return roleRepository.save(role);
        });
    }

    @PostConstruct
    @Transactional
    public void init() {
        String superAdminEmail = "globaledu237@gmail.com";

        // Tạo các vai trò cần thiết
        ensureRoleExists("ROLE_ADMIN");
        ensureRoleExists("ROLE_NHANVIEN");
        ensureRoleExists("ROLE_GIANGVIEN");
        ensureRoleExists("ROLE_HOCVIEN");

        if (accountRepository.existsByEmail(superAdminEmail)) {
            return; // Đã tồn tại Super Admin
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();

        TaiKhoan superAdminUser = TaiKhoan.builder()
                .name("Super Admin")
                .email(superAdminEmail)
                .phone("0774132105")
                .password(passwordEncoder.encode("superpassword"))
                .role(adminRole)
                .build();

        accountRepository.saveAndFlush(superAdminUser);

        System.out.println("✅ Super Admin đã được khởi tạo thành công!");
    }
}