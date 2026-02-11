package com.assessement.application.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.assessement.starter.config.security.JwtService;
import com.assessement.starter.dto.AuthenticationRequest;
import com.assessement.starter.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void login_returnsJwt_whenCredentialsAreValid() throws Exception {
        // Arrange
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("admin")
                .password("password")
                .build();

        User user = User.builder()
                .id(1)
                .username("admin")
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtService.generateAccessToken(user))
                .thenReturn("jwt-token-123");

        // Act + Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.access_token").value("jwt-token-123"));

        // 🔒 Contract verification
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateAccessToken(user);
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void login_returns401_whenCredentialsAreInvalid() throws Exception {
        // Arrange
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("admin")
                .password("wrongPassword")
                .build();

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act + Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        // 🔒 Ensure token is never generated
        verify(jwtService, never()).generateAccessToken(any());
    }
}
