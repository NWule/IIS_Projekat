## AI Microservice Client Setup
(AI was used to make this markdown file prettier)

Follow these steps to generate and integrate the AI microservice client into your Spring Boot environment.

---

### 1. Environment Configuration

Create a `.env` file in the `backend/` directory using the provided template:

```bash
cp backend/.env.example backend/.env
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
   Open `com.football_club.client.LLMClient` and add a new wrapper method targeting the generated OpenAPI endpoint.
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