# GitHub Access Report Service

## Problem Statement
Organizations need visibility into who has access to which repositories in GitHub. Manual tracking is error-prone and inefficient. This service generates a **user-to-repositories access report** for a given GitHub organization.

---

## Solution Overview
- Spring Boot backend service using **MVC + WebClient (WebFlux)**.
- Authenticates with GitHub via **Personal Access Token (PAT)**.
- Retrieves repositories and their collaborators.
- Aggregates data into a **user → repositories mapping**.
- Exposes a **REST API endpoint** returning JSON.

### Architecture Flow
                  ┌──────────────────────────┐
                  │   API Request            │
                  │ /access-report?org=ORG   │
                  └────────────┬─────────────┘
                               │
                               ▼
                  ┌──────────────────────────┐
                  │      Controller          │
                  └────────────┬─────────────┘
                               │
                               ▼
                  ┌──────────────────────────┐
                  │      GitHub Service      │
                  └────────────┬─────────────┘
                               │
               ┌───────────────┴───────────────┐
               ▼                               ▼
    ┌──────────────────────┐        ┌───────────────────────┐
    │ Fetch Repositories   │        │ Fetch Collaborators   │
    │   (with pagination)  │        │   (parallel for each) │
    └────────────┬─────────┘        └────────────┬──────────┘
                 │                               │
                 └─────────────┬─────────────────┘
                               ▼
                   ┌──────────────────────────┐
                   │ Aggregate User → Repo    │
                   │ Mapping                  │
                   └────────────┬─────────────┘
                                │
                                ▼
                   ┌──────────────────────────┐
                   │ Return JSON Response     │
                   └──────────────────────────┘



## How to Run

Clone the repo:
git clone https://github.com/YOUR_USERNAME/github-access-report.git
cd github-access-report

## Configure GitHub token in application.properties:

github.token=YOUR_GITHUB_PERSONAL_ACCESS_TOKEN
server.port=8080

## Run the application:
mvn spring-boot:run
API Usage

## Endpoint:

GET /api/github/access-report?org=ORG_NAME

## Example Request:

GET http://localhost:8080/api/github/access-report?org=google

## Authentication

Uses GitHub Personal Access Token (PAT).

Token must have:
repo → access private repos
read:org → read org membership

Token added in headers:
Authorization: Bearer YOUR_GITHUB_TOKEN
User-Agent: SpringBoot-App
