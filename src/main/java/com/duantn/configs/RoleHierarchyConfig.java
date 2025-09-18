package com.duantn.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class RoleHierarchyConfig {

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("""
                    ROLE_ADMIN > ROLE_EMPLOYEE
                    ROLE_EMPLOYEE > ROLE_INSTRUCTOR
                    ROLE_INSTRUCTOR > ROLE_STUDENT
                """);
        return hierarchy;
    }
}
