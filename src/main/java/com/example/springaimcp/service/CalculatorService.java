package com.example.springaimcp.service;

import com.example.springaimcp.model.CalculationRequest;
import com.example.springaimcp.model.CalculationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for handling arithmetic calculations using Spring AI with MCP pattern.
 */
@Service
public class CalculatorService {

    private final MathOperationMcp mathOperationMcp;

    @Autowired
    public CalculatorService(MathOperationMcp mathOperationMcp) {
        this.mathOperationMcp = mathOperationMcp;
    }

    /**
     * Processes a calculation request, performs the calculation using MCP pattern,
     * and generates an explanation using Spring AI.
     * 
     * @param request The calculation request containing operation and operands
     * @return CalculationResponse with the result and explanation
     * @throws IllegalArgumentException if the operation is invalid
     * @throws ArithmeticException if division by zero is attempted
     */
    public CalculationResponse calculate(CalculationRequest request) {
        // Add a null check to prevent NullPointerException
        if (request == null) {
            throw new IllegalArgumentException("Calculation request cannot be null");
        }
        
        Map<String, Object> result = mathOperationMcp.calculate(
            request.getOperation().toLowerCase(),
            request.getA(),
            request.getB()
        );
        
        // Create a response using setter methods instead of constructor
        CalculationResponse response = new CalculationResponse();
        response.setResult((Integer) result.get("result"));
        response.setOperation(request.getOperation());
        response.setExpression(request.getA() + " " + request.getOperation() + " " + request.getB());
        response.setExplanation((String) result.get("explanation"));
        response.setA(request.getA());
        response.setB(request.getB());
        
        return response;
    }

    /**
     * Convenience method for addition.
     * 
     * @param a the first operand
     * @param b the second operand
     * @return calculation response with result and explanation
     */
    public CalculationResponse add(int a, int b) {
        return calculate(new CalculationRequest("add", a, b));
    }

    /**
     * Convenience method for subtraction.
     * 
     * @param a the first operand
     * @param b the second operand
     * @return calculation response with result and explanation
     */
    public CalculationResponse subtract(int a, int b) {
        return calculate(new CalculationRequest("subtract", a, b));
    }

    /**
     * Convenience method for multiplication.
     * 
     * @param a the first operand
     * @param b the second operand
     * @return calculation response with result and explanation
     */
    public CalculationResponse multiply(int a, int b) {
        return calculate(new CalculationRequest("multiply", a, b));
    }

    /**
     * Convenience method for division.
     * 
     * @param a the dividend
     * @param b the divisor
     * @return calculation response with result and explanation
     * @throws ArithmeticException if b is zero
     */
    public CalculationResponse divide(int a, int b) {
        return calculate(new CalculationRequest("divide", a, b));
    }
}
