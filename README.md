# Spring AI Calculator with MCP

A Spring Boot application that uses Spring AI with Ollama to perform basic arithmetic operations (add, subtract, multiply, divide) and provide AI-generated explanations.

## Prerequisites

- Java 21
- Maven 3.6+
- [Ollama](https://ollama.ai/) installed and running locally
- Llama 2 model pulled in Ollama

## Setup

1. Install Ollama following the instructions at [ollama.ai](https://ollama.ai/)

2. Pull the Llama 2 model:
   ```
   ollama pull llama2
   ```

3. Start the Ollama service:
   ```
   ollama serve
   ```

## Building the Application

```bash
mvn clean package
```

## Running the Application

```bash
java -jar target/springai-mcp-0.0.1-SNAPSHOT.jar
```

The application will start on port 8080.

## Testing the Application

Once the application is running, you can test it using:

1. cURL:
   ```bash
   # Test the health endpoint
   curl http://localhost:8080/health
   
   # Test the calculation endpoint
   curl -X POST http://localhost:8080/calculator/calculate \
     -H "Content-Type: application/json" \
     -d '{"operation":"add","firstOperand":5,"secondOperand":3}'
     
   # Test a convenience endpoint
   curl http://localhost:8080/calculator/multiply?a=4&b=5
   ```

2. Use Postman, Insomnia, or any other API client tool

## API Endpoints

### POST /calculator/calculate

Performs a calculation based on the provided operation and operands.

Request body:
```json
{
  "operation": "add",
  "firstOperand": 5,
  "secondOperand": 3
}
```

Response:
```json
{
  "operation": "add",
  "firstOperand": 5,
  "secondOperand": 3,
  "result": 8,
  "explanation": "To add 5 and 3, you combine the values. 5 + 3 = 8."
}
```

### Convenience Endpoints

- GET `/calculator/add?a=5&b=3`
- GET `/calculator/subtract?a=5&b=3`
- GET `/calculator/multiply?a=5&b=3`
- GET `/calculator/divide?a=6&b=3`

### Health Check

- GET `/health` - Returns application and Ollama service status

## Features

- Uses Spring AI with Ollama LLM integration
- Implements Model Composition Pattern (MCP) for arithmetic operations
- Provides AI-generated explanations for calculations
- Includes comprehensive validation and error handling

# Spring AI Calculator with MCP

This is a Spring Boot application that demonstrates the use of Spring AI and Model Composition Pattern (MCP) to perform basic arithmetic operations (add, subtract, multiply, divide) using Ollama as the LLM model.

## Prerequisites

- Java 21
- Maven 3.6+
- [Ollama](https://ollama.ai/) installed and running locally
- Llama3 model pulled in Ollama (`ollama pull llama3:8b`)

## Getting Started

1. Ensure Ollama is running:
   ```
   ollama serve
   ```

2. Build the application:
   ```
   mvn clean package
   ```

3. Run the application:
   ```
   java -jar target/springai-mcp-0.0.1-SNAPSHOT.jar
   ```

The application will start on port 8080.

## API Endpoints

### POST /api/calculator/calculate
Performs calculation based on the provided operation and operands.

Example request:
```json
{
  "operation": "add",
  "firstOperand": 5,
  "secondOperand": 3
}
```

Example response:
```json
{
  "operation": "add",
  "firstOperand": 5,
  "secondOperand": 3,
  "result": 8,
  "explanation": "To add 5 and 3, you simply combine the values. 5 + 3 = 8"
}
```

### GET Operations

The following GET endpoints are also available for convenience:

- GET `/api/calculator/add?a=5&b=3`
- GET `/api/calculator/subtract?a=5&b=3`
- GET `/api/calculator/multiply?a=5&b=3`
- GET `/api/calculator/divide?a=6&b=3`

## Features

- Spring AI integration with Ollama
- Model Composition Pattern (MCP) for handling different operations

