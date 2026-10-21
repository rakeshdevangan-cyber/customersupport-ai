# Restart Guide — Ollama + Spring AI Customer Support

Project: `D:\workspace\customersupport-ai`  
Platform: Windows / PowerShell  
Spring Boot API: `http://localhost:8080/api/support/chat`  
Ollama API: `http://localhost:11434`

## After restarting your computer

### 1. Check whether Ollama is running

Open a **new PowerShell window** and run:

```powershell
Invoke-RestMethod http://localhost:11434/api/tags
```

- If this returns JSON with a `models` array, Ollama is already running. **Do not start another server.**
- If the connection is refused, start Ollama using the Windows Start menu (search **Ollama**) or run `ollama serve` in a PowerShell window and leave it open.

### 2. If `ollama` is not recognized

First try closing and reopening Windows Terminal and IntelliJ. If it still fails, refresh PATH in that PowerShell session:

```powershell
$env:Path = [Environment]::GetEnvironmentVariable('Path','Machine') + ';' + [Environment]::GetEnvironmentVariable('Path','User')
ollama --version
```

If that fails, locate the executable:

```powershell
Get-Command ollama -ErrorAction SilentlyContinue
Test-Path "$env:LOCALAPPDATA\Programs\Ollama\ollama.exe"
```

If the file exists at the default location, run it directly:

```powershell
& "$env:LOCALAPPDATA\Programs\Ollama\ollama.exe" list
```

Make sure the **directory containing** `ollama.exe` is in your Windows **User PATH**. Restart Windows Terminal / IntelliJ after changing PATH.

### 3. Confirm the model is installed

```powershell
ollama list
```

Your Spring AI project was configured to use `llama3.2`. If it is missing, download it **once**:

```powershell
ollama pull llama3.2
```

You do **not** need to download the model after every restart. Model files remain on disk.

Optional direct model test:

```powershell
ollama run llama3.2
```

Ask a question; type `/bye` to exit the interactive session. Ollama can continue serving in the background.

### 4. Check Spring AI configuration

In `src/main/resources/application.properties`:

```properties
spring.application.name=customer
server.port=8080
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama3.2
```

The model name must match `ollama list`. An error such as `model 'mistral' not found` means the application is requesting a different, unavailable model. Check for overrides and restart Spring Boot after editing properties.

### 5. Start Spring Boot

Open a PowerShell terminal:

```powershell
cd D:\workspace\customersupport-ai
mvn spring-boot:run
```

Alternatively run the Spring Boot main class in IntelliJ. Keep the app running while testing.

### 6. Test the customer support API

Run in a **second** PowerShell terminal:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/support/chat" -Method POST -ContentType "application/json" -Body '{"message":"Where is my order?"}'
```

You should receive a `reply` field with model-generated text. At this stage the model does **not** have access to real order data.

## Quick troubleshooting

| Symptom | Check / fix |
| --- | --- |
| `ollama` command not recognized | Refresh PATH, reopen parent terminal/IntelliJ, or run executable by full path. |
| `Connection refused` on port 11434 | Ollama server is not running; launch Ollama or use `ollama serve`. |
| `HTTP 404 model 'mistral' not found` | Set `spring.ai.ollama.chat.options.model=llama3.2` (or pull the requested model), restart Spring Boot. |
| Port 11434 already in use | Ollama is probably already running; check `/api/tags`. Do not launch a second server. |
| Spring Boot endpoint unavailable on port 8080 | Start the Spring Boot application and inspect its console. |
| Slow first response | The model may need time to load into memory. |

## Important reminders

- Ollama is the **model server**; Llama 3.2 is the **downloaded model**; Spring AI is the **Java integration**.
- Running `ollama serve` in a terminal keeps that terminal occupied; closing it stops that server unless the background Ollama app is running separately.
- You normally only need to start Ollama and Spring Boot after a restart, **not reinstall either one**.
- For this lab, test the model and API locally before moving on to the next coding exercise.
