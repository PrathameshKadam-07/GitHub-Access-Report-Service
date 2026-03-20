# GitHub Access Report Service

## Features
- Fetch repositories of an organization
- Fetch collaborators of each repo
- Generate user → repositories mapping
- Parallel processing for speed

## How to Run
1. Add GitHub token in application.properties
2. Run Spring Boot app
3. Call:
   GET /api/github/access-report?org=ORG_NAME

## Authentication
Uses GitHub Personal Access Token (PAT)

## Design Decisions (IMPORTANT)
- Used MVC structure (Controller → Service → DTO)
- Used parallelStream to handle 100+ repos efficiently
- Implemented pagination for scalability
- Basic error handling added for API failures

## Assumptions
- Token has access to organization repos
- Only direct collaborators are considered

## Example Response
[
  {
    "username": "john",
    "repositories": ["repo1", "repo2"]
  }
]

## FLOW
        🌐 User / Postman
                │
                ▼
     GET /api/github/access-report?org=xyz
                │
                ▼
        🎮 Controller Layer
                │
                ▼
        🧠 Service Layer
                │
     ┌──────────┴──────────┐
     ▼                     ▼
    📂 Fetch Repos       👥 Fetch Collaborators
    (Pagination)		 (Parallel Calls)
        │                     │
        └──────────┬──────────┘
                   ▼
           🔄 Transform Data
          (user → repos mapping)
                   ▼
           📦 DTO Response
                   ▼
            🌐 JSON Output
