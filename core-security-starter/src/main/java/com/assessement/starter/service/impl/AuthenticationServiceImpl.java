package com.assessement.starter.service.impl;

import com.assessement.starter.config.security.JwtService;
import com.assessement.starter.dto.AuthenticationRequest;
import com.assessement.starter.dto.AuthenticationResponse;
import com.assessement.starter.model.User;
import com.assessement.starter.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        Authentication auth = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = (User) auth.getPrincipal();
        String token = this.jwtService.generateAccessToken(user);
        return AuthenticationResponse.builder()
                .accessToken(token)
                .build();
    }
}







