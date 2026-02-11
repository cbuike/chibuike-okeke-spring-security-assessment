package com.assessement.starter.service;

import com.assessement.starter.dto.AuthenticationRequest;
import com.assessement.starter.dto.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse login(AuthenticationRequest request);
}
