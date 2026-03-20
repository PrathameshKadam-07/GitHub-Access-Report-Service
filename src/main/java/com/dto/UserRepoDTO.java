package com.dto;

import java.util.List;

public class UserRepoDTO {

    private String username;
    private List<String> repositories;

    public UserRepoDTO(String username, List<String> repositories) {
        this.username = username;
        this.repositories = repositories;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRepositories() {
        return repositories;
    }
}
