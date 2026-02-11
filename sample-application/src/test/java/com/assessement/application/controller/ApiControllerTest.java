package com.assessement.application.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.assessement.starter.config.security.JwtService;
import com.assessement.starter.model.Role;
import com.assessement.starter.model.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private String validUserToken;
    private String validAdminToken;

    private User user;
    private User admin;

    @BeforeEach
    void setup() {
        // Mock User
        user = User.builder()
                .id(1)
                .username("user")
                .roles(List.of(Role.builder().name("USER").build()))
                .build();

        // Mock Admin
        admin = User.builder()
                .id(2)
                .username("admin")
                .roles(List.of(Role.builder().name("ADMIN").build()))
                .build();

        // Generate fake JWTs (mock)
        validUserToken = "Bearer valid-user-token";
        validAdminToken = "Bearer valid-admin-token";
    }

    // ---------------- PUBLIC ----------------
    @Test
    void healthEndpoint_shouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/public/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    // ---------------- USER ----------------
    @Test
    void userEndpoint_shouldFailWithoutToken() throws Exception {
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userEndpoint_shouldPassWithValidUserToken() throws Exception {
        // Mock JwtService behavior

        when(userDetailsService.loadUserByUsername("user"))
                .thenReturn(user);

        when(jwtService.extractUsernameFromToken("valid-user-token"))
                .thenReturn(user.getUsername());
        when(jwtService.isTokenValid("valid-user-token"))
                .thenReturn(true);

        mockMvc.perform(get("/api/user/me")
                        .header(HttpHeaders.AUTHORIZATION, validUserToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Authenticated user"));
    }

    // ---------------- ADMIN ----------------
    @Test
    void adminEndpoint_shouldFailWithoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpoint_shouldFailForNonAdminUser() throws Exception {
        // Mock JwtService behavior
        when(userDetailsService.loadUserByUsername("user"))
                .thenReturn(user);
        when(jwtService.extractUsernameFromToken("valid-user-token"))
                .thenReturn(user.getUsername());
        when(jwtService.isTokenValid("valid-user-token"))
                .thenReturn(true);

        mockMvc.perform(get("/api/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, validUserToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_shouldPassForAdminUser() throws Exception {

        // Mock JwtService behavior

        when(userDetailsService.loadUserByUsername("admin"))
                .thenReturn(admin);

        when(jwtService.extractUsernameFromToken("valid-admin-token"))
                .thenReturn(admin.getUsername());

        when(jwtService.isTokenValid("valid-admin-token"))
                .thenReturn(true);

        mockMvc.perform(get("/api/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, validAdminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin users list"));
    }
}