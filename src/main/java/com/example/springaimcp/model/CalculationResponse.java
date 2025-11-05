package com.example.springaimcp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing a calculation response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculationResponse {
    
    private Integer result;
    private String operation;
    private String expression;
    private String explanation;
    private Integer a;
    private Integer b;
    
    // Explicit getter/setter methods to ensure compilation works correctly
    public Integer getResult() {
        return result;
    }
    
    public void setResult(Integer result) {
        this.result = result;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public String getExpression() {
        return expression;
    }
    
    public void setExpression(String expression) {
        this.expression = expression;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
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
