package com.duantn.configs;

import com.duantn.services.CustomOAuth2UserService;
import com.duantn.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private CustomUserDetailsService userDetailsService;

        @Autowired
        private CustomLoginSuccessHandler loginSuccessHandler;

        @Autowired
        private CustomFailureHandler customFailureHandler;

        @Autowired
        private CustomAccessDeniedHandler customAccessDeniedHandler;

        @Autowired
        private CustomOAuth2UserService customOAuth2UserService;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                // Static resources
                                                .requestMatchers("/css/**", "/js/**", "/photos/**", "/favicon.ico")
                                                .permitAll()

                                                // Public pages
                                                .requestMatchers("/auth/**", "/", "/home", "/dangky", "/verify")
                                                .permitAll()

                                                // Role-based access
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/nhanvien/**").hasRole("NHANVIEN")
                                                .requestMatchers("/hocvien/**").hasRole("HOCVIEN")
                                                .requestMatchers("/giang-vien/dang-ky").hasRole("HOCVIEN")
                                                .requestMatchers("/giang-vien/**").hasRole("GIANGVIEN")
                                                .requestMatchers("/hoc-vien/**").hasAnyRole("HOCVIEN", "GIANGVIEN")
                                                .requestMatchers("/giangvien/trang-giang-vien").hasAnyRole("GIANGVIEN")

                                                // All other requests
                                                .anyRequest().permitAll())
                                .formLogin(form -> form
                                                .loginPage("/auth/dangnhap")
                                                .loginProcessingUrl("/login/form")
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                                                .successHandler(loginSuccessHandler)
                                                .failureHandler(customFailureHandler)
                                                .permitAll())
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/auth/dangnhap")
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService))
                                                .successHandler(loginSuccessHandler))
                                .rememberMe(remember -> remember
                                                .rememberMeParameter("remember-me") // phải giống tên trong checkbox
                                                                                    // form
                                                .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 ngày
                                                .key("globaledu-secret-key-123") // khóa bí mật
                                                .userDetailsService(userDetailsService) // quan trọng
                                )
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/auth/dangnhap?logout=true")
                                                .deleteCookies("JSESSIONID", "remember-me")
                                                .permitAll())
                                .exceptionHandling(exception -> exception
                                                .accessDeniedHandler(customAccessDeniedHandler)

                                )
                                .csrf(AbstractHttpConfigurer::disable);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @SuppressWarnings("removal")
        @Bean
        public AuthenticationManager authManager(HttpSecurity http) throws Exception {
                return http.getSharedObject(AuthenticationManagerBuilder.class)
                                .userDetailsService(userDetailsService)
                                .passwordEncoder(passwordEncoder())
                                .and()
                                .build();
        }
}
