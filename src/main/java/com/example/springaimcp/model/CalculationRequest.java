package com.example.springaimcp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing a calculation request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculationRequest {
    
    @NotBlank(message = "Operation is required")
    private String operation;
    
    @NotNull(message = "First operand is required")
    private Integer a;
    
    @NotNull(message = "Second operand is required")
    private Integer b;
    
    // Explicit constructor for primitive int parameters
    public CalculationRequest(String operation, int a, int b) {
        this.operation = operation;
        this.a = a;
        this.b = b;
    }
    
    // Explicit getter/setter methods to ensure compilation works correctly
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public Integer getA() {
        return a;
    }
    
    public void setA(Integer a) {
        this.a = a;
    }
    
    public Integer getB() {
        return b;
    }
    
    public void setB(Integer b) {
        this.b = b;
    }
}
