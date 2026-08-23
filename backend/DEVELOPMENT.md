## AI Microservice Client Setup
(AI was used to make this markdown file prettier)

Follow these steps to generate and integrate the AI microservice client into your Spring Boot environment.

---

### 1. Environment Configuration

Create a `.env` file in the `backend/` directory using the provided template:

```bash
cp backend/.env.sample backend/.env
```

Open `backend/.env` and populate the required configuration values before running the application.

---

### 2. Generate the OpenAPI Client

Run the client generation script from the project root to fetch the FastAPI schema and recompile generated Java stubs:

```bash
./generate_client.sh
```

> **Windows Note:** Execute this command inside **Git Bash** or **WSL**.

---

### 3. Using Endpoints in Your Code

1. **Add a Method Wrapper:**  
   Open `com.football_club.Clients.LLMClient` and add a new wrapper method targeting the generated OpenAPI endpoint.
2. **Inject the Service:**  
   Inject `LLMClient` via constructor injection into any Spring component:

```java
@Service
public class ScoutingService {

    private final LLMClient llmClient;

    public ScoutingService(LLMClient llmClient) {
        this.llmClient = llmClient;
    }

    public void processScoutingReport(String query) {
        var response = llmClient.askAi(query);
        // ... process response
    }
}
```

### 4. Creating a new API-Football API key

1. Got to [their site](https://www.api-football.com/) and create your account.
2. Once you're logged in, go to their [My Access page](https://dashboard.api-football.com/profile?access).
3. In the top right corner, you will find your API key.

### 5. Adding new response DTOs for API-Football client

1. Go to the [Documentation](https://www.api-football.com/documentation-v3#tag/Timezone) for API Football and find the endpoint you want to use.
2. Find the status code 200 response samples and copy the JSON response.
3. In the `src/main/resources/json-schemas` directory, create a new JSON file with the name of the endpoint response class and copy the JSON response into it.
4. Run `.\mwnw clean compile` to recompile the project, and the new JSON response class should be available in `target/generated-sources/jsonschema2pojo`
5. You can now use the new JSON response class in your code.

### 6. Adding new endpoints for API-Football client

1. Go to the [Documentation](https://www.api-football.com/documentation-v3#tag/Timezone) for API Football and find the endpoint you want to use.
2. Copy the endpoint URL.
3. Add a new wrapper method to `com.football_club.Clients.APIFootballClient` following the example below:

```java
public class APIFootballClient {
    private final RestClient restClient;

    // Search for a team by name
    public TeamSearch searchTeams(String teamName) {
        return restClient.get()
                .uri("/teams?search={name}", teamName)
                .retrieve()
                .body(TeamSearch.class);
    }
}
```
