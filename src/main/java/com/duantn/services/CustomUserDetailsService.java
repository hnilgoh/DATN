package com.duantn.services;

import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

        @Autowired
        private TaiKhoanRepository accountRepo;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                TaiKhoan account = accountRepo.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại"));

                return new CustomUserDetails(account); // ✅ sử dụng class custom
        }
}
