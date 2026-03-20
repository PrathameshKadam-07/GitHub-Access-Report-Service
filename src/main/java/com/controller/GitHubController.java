package com.controller;

import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dto.UserRepoDTO;
import com.services.GitHubService;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService service;

    public GitHubController(GitHubService service) {
        this.service = service;
    }

    @GetMapping("/access-report")
    public ResponseEntity<List<UserRepoDTO>> getReport(@RequestParam String org) {
        return ResponseEntity.ok(service.generateReport(org));
    }
}