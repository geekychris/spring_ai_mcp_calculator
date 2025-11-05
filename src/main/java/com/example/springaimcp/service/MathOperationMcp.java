package com.example.springaimcp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Model Composition Pattern implementation for mathematical operations.
 * Handles arithmetic calculations and generates explanations using Spring AI.
 */
@Component
public class MathOperationMcp {
    private static final Logger logger = LoggerFactory.getLogger(MathOperationMcp.class);
    
    public static final String OP_ADD = "add";
    public static final String OP_SUBTRACT = "subtract";
    public static final String OP_MULTIPLY = "multiply";
    public static final String OP_DIVIDE = "divide";
    
    private static final Set<String> VALID_OPERATIONS = Set.of(OP_ADD, OP_SUBTRACT, OP_MULTIPLY, OP_DIVIDE);
    private static final String PROMPT_TEMPLATE_PATH = "classpath:prompts/calculator.st";
    private static final String DEFAULT_ERROR_EXPLANATION = "Unable to generate explanation due to an error.";
    private static final String OVERFLOW_MESSAGE = "Operation would cause integer overflow";
    private static final String DIVISION_BY_ZERO_MESSAGE = "Division by zero is not allowed";
    private static final String NULL_OPERATION_MESSAGE = "Operation cannot be null";
    private static final String AI_SERVICE_TIMEOUT = "AI service timed out while generating explanation";
    
    private final OllamaApi ollamaApi;
    private final ResourceLoader resourceLoader;
    private final String calculatorPromptTemplate;
    private final OllamaChatClient ollamaChatClient;

    @Value("${spring.ai.ollama.model}")
    private String model;

    @Autowired
    public MathOperationMcp(OllamaApi  ollamaApi, ResourceLoader resourceLoader, OllamaChatClient ollamaChatClient) {
        this.ollamaApi = ollamaApi;
        this.resourceLoader = resourceLoader;
        this.calculatorPromptTemplate = loadPromptTemplate(PROMPT_TEMPLATE_PATH);
        this.ollamaChatClient = ollamaChatClient;
    }

    /**
     * Performs the calculation and generates an explanation using Spring AI.
     *
     * @param operation The arithmetic operation to perform
     * @param first The first operand
     * @param second The second operand
     * @return Map containing the result and explanation
     * @throws IllegalArgumentException if the operation is invalid or null
     * @throws ArithmeticException if division by zero is attempted
     */
    public Map<String, Object> calculate(String operation, int first, int second) {
        if (operation == null) {
            throw new IllegalArgumentException(NULL_OPERATION_MESSAGE);
        }
        
        String normalizedOperation = operation.trim().toLowerCase();
        validateOperation(normalizedOperation);
        
        // Check for potential overflow conditions
        try {
            checkForOverflow(normalizedOperation, first, second);
        } catch (ArithmeticException e) {
            logger.warn("Arithmetic overflow prevented: {} {} {}", first, normalizedOperation, second);
            throw e;
        }
        
        if (OP_DIVIDE.equals(normalizedOperation) && second == 0) {
            logger.warn("Division by zero attempted with operands: {}/{}", first, second);
            throw new ArithmeticException(DIVISION_BY_ZERO_MESSAGE);
        }

        try {
            int result = performCalculation(normalizedOperation, first, second);
            String explanation = getExplanation(normalizedOperation, first, second, result);
            
            Map<String, Object> response = new HashMap<>();
            response.put("result", result);
            response.put("explanation", explanation);
            
            logger.debug("Calculation completed successfully: {} {} {} = {}", 
                        first, normalizedOperation, second, result);
            return response;
        } catch (Exception e) {
            logger.error("Error during calculation: {} {} {}: {}", 
                        first, normalizedOperation, second, e.getMessage());
            throw e;
        }
    }

    /**
     * Validates that the given operation is supported.
     *
     * @param operation the operation to validate
     * @throws IllegalArgumentException if the operation is not supported
     */
    private void validateOperation(String operation) {
        if (!VALID_OPERATIONS.contains(operation)) {
            String message = String.format("Invalid operation: '%s'. Supported operations are: %s",
                operation, String.join(", ", VALID_OPERATIONS));
            logger.warn(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Performs the arithmetic calculation based on the operation.
     *
     * @param operation the arithmetic operation to perform
     * @param first the first operand
     * @param second the second operand
     * @return the result of the calculation
     * @throws ArithmeticException if division by zero is attempted
     * @throws IllegalStateException if an unexpected operation is encountered
     */
    private int performCalculation(String operation, int first, int second) {
        return switch (operation) {
            case OP_ADD -> first + second;
            case OP_SUBTRACT -> first - second;
            case OP_MULTIPLY -> first * second;
            case OP_DIVIDE -> first / second;
            default -> throw new IllegalStateException("Unexpected operation: " + operation);
        };
    }
    
    /**
     * Checks for potential integer overflow conditions based on the operation and operands.
     *
     * @param operation the arithmetic operation to check
     * @param first the first operand
     * @param second the second operand
     * @throws ArithmeticException if the operation would cause an integer overflow
     */
    private void checkForOverflow(String operation, int first, int second) {
        switch (operation) {
            case OP_ADD -> {
                // Check if adding a positive number would exceed MAX_VALUE
                if (second > 0 && first > Integer.MAX_VALUE - second) {
                    throw new ArithmeticException(OVERFLOW_MESSAGE + " (addition)");
                }
                // Check if adding a negative number would exceed MIN_VALUE
                if (second < 0 && first < Integer.MIN_VALUE - second) {
                    throw new ArithmeticException(OVERFLOW_MESSAGE + " (addition)");
                }
            }
            case OP_SUBTRACT -> {
                // Check if subtracting a negative number would exceed MAX_VALUE
                // (subtracting a negative is equivalent to adding a positive)
                if (second < 0 && first > Integer.MAX_VALUE + second) {
                    throw new ArithmeticException(OVERFLOW_MESSAGE + " (subtraction)");
                }
                // Check if subtracting a positive number would exceed MIN_VALUE
                if (second > 0 && first < Integer.MIN_VALUE + second) {
                    throw new ArithmeticException(OVERFLOW_MESSAGE + " (subtraction)");
                }
            }
            case OP_MULTIPLY -> {
                // Check positive * positive overflow
                if (first > 0 && second > 0 && first > Integer.MAX_VALUE / second) {
                    throw new ArithmeticException(OVERFLOW_MESSAGE + " (multiplication)");
                }
                // Check positive * negative overflow
                if (first > 0 && second < 0 && second < Integer.MIN_VALUE / first) {
                    throw new ArithmeticException(OVERFLOW_MESSAGE + " (multiplication)");
                }
                // Check negative * positive overflow
                if (first < 0 && second > 0 && first < Integer.MIN_VALUE / second) {
                    throw new ArithmeticException(OVERFLOW_MESSAGE + " (multiplication)");
                }
                // Check negative * negative overflow
                if (first < 0 && second < 0 && first < Integer.MAX_VALUE / second) {
                    throw new ArithmeticException(OVERFLOW_MESSAGE + " (multiplication)");
                }
            }
        }
    }

    /**
     * Generates an explanation for the calculation using Spring AI.
     *
     * @param operation the arithmetic operation performed
     * @param first the first operand
     * @param second the second operand
     * @param result the calculation result
     * @return an AI-generated explanation of the calculation
     */
    private String getExplanation(String operation, int first, int second, int result) {
        try {
            Map<String, Object> promptVariables = new HashMap<>();
            promptVariables.put("operation", operation);
            promptVariables.put("first", first);
            promptVariables.put("second", second);
            promptVariables.put("result", result);

            logger.debug("Requesting explanation for: {} {} {} = {}", first, operation, second, result);
            
            // Create a prompt string from the template
            String promptText;
            try {
                // Simple template substitution
                promptText = calculatorPromptTemplate
                    .replace("${operation}", operation)
                    .replace("${first}", String.valueOf(first))
                    .replace("${second}", String.valueOf(second))
                    .replace("${result}", String.valueOf(result));
                    
                if (promptText.isEmpty()) {
                    promptText = "Explain how to calculate " + first + " " + operation + " " + second + " = " + result;
                }
            } catch (Exception e) {
                // Fallback if there's any issue with the template
                logger.warn("Error processing prompt template, using fallback prompt", e);
                promptText = "Explain how to calculate " + first + " " + operation + " " + second + " = " + result;
            }
            
            // Create proper GenerateRequest for the OllamaApi
            // Using the model name injected from application properties
            // Create proper GenerateRequest for the OllamaApi using builder pattern
//            var request = OllamaApi.GenerateRequest.builder(model)
//                .messages(promptText)
//                .build();

            var request = OllamaApi.GenerateRequest.builder(promptText)
                     .withModel(model)
                     .build();
                
            // Call generateStream with the properly configured request object
            String content;
            try {

//                content = ollamaApi.generate(request)
//                    .map(GenerateResponse::getContent)
//                    .blockFirst(); // This blocks until the first element is available

                content = ollamaChatClient.call(new Prompt(promptText)).getResult().getOutput().getContent();

                if (content == null || content.trim().isEmpty()) {
                    logger.error("Received null or empty response from AI service");
                    return DEFAULT_ERROR_EXPLANATION;
                }
                
                content = content.trim();
            } catch (Exception e) {
                logger.error("Error processing AI response stream", e);
                return DEFAULT_ERROR_EXPLANATION;
            }
            return content;
        } catch (Exception e) {
            logger.error("Error generating explanation for calculation: {} {} {} = {}", 
                        first, operation, second, result, e);
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("timeout")) {
                logger.warn("AI service timeout detected");
                return AI_SERVICE_TIMEOUT;
            }
            return DEFAULT_ERROR_EXPLANATION;
        }
    }

    /**
     * Loads the prompt template from the specified resource path.
     *
     * @param resourcePath the path to the prompt template resource
     * @return the content of the prompt template
     * @throws UncheckedIOException if the template cannot be loaded or does not exist
     */
    private String loadPromptTemplate(String resourcePath) {
        try {
            Resource resource = resourceLoader.getResource(resourcePath);
            if (!resource.exists()) {
                throw new IOException("Template resource not found: " + resourcePath);
            }
            
            try (InputStream inputStream = resource.getInputStream();
                 Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                return FileCopyUtils.copyToString(reader);
            }
        } catch (IOException e) {
            logger.error("Failed to load prompt template: {}", resourcePath, e);
            throw new UncheckedIOException("Failed to load prompt template: " + resourcePath, e);
        }
    }
}
