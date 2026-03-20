package com.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.dto.UserRepoDTO;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class GitHubService {

	private final WebClient webClient;

	@Value("${github.token}")
	private String token;

	public GitHubService(WebClient.Builder builder) {
		this.webClient = builder.baseUrl("https://api.github.com").build();
	}

	public List<UserRepoDTO> generateReport(String org) {
		List<String> repos = getRepositories(org);

		Map<String, List<String>> map = new ConcurrentHashMap<>();

		repos.parallelStream().forEach(repo -> {
			List<String> users = getCollaborators(org, repo);

			for (String user : users) {
				map.computeIfAbsent(user, k -> new ArrayList<>()).add(repo);
			}
		});

		return map.entrySet().stream().map(e -> new UserRepoDTO(e.getKey(), e.getValue())).collect(Collectors.toList());
	}

	//  Handles BOTH org + user + pagination
	private List<String> getRepositories(String org) {
		List<String> repos = new ArrayList<>();
		int page = 1;

		while (true) {

			final int currentPage = page;
			List<Map> response = null;

			try {

				// Try ORG
				response = webClient.get()
						.uri(uriBuilder -> uriBuilder.path("/orgs/{org}/repos").queryParam("per_page", 100)
								.queryParam("page", currentPage).build(org))
						.header("Authorization", "Bearer " + token).retrieve().bodyToFlux(Map.class).collectList()
						.block();

			} catch (Exception e) {

				// Fallback to USER
				response = webClient.get()
						.uri(uriBuilder -> uriBuilder.path("/users/{org}/repos").queryParam("per_page", 100)
								.queryParam("page", currentPage).build(org))
						.header("Authorization", "Bearer " + token).retrieve().bodyToFlux(Map.class).collectList()
						.block();
			}

			if (response == null || response.isEmpty())
				break;

			for (Map repo : response) {
				repos.add(repo.get("name").toString());
			}

			page++;
		}

		return repos;
	}

	//  Safe collaborator fetch (handles 404 internally)
	private List<String> getCollaborators(String org, String repo) {
		try {
			return webClient.get().uri("/repos/{org}/{repo}/collaborators", org, repo)
					.header("Authorization", "Bearer " + token).header("User-Agent", "SpringBoot-App").retrieve()
					.bodyToFlux(Map.class).map(u -> u.get("login").toString()).collectList().block();

		} catch (Exception e) {
			System.out.println("Skipping repo (no access): " + repo);
			return Collections.emptyList();
		}
	}
}
