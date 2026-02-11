
package com.assessement.application.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/public/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/user/me")
    public String me() {
        return "Authenticated user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public String admin() {
        return "Admin users list";
    }
}
