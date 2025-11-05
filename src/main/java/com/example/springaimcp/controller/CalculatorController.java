package com.example.springaimcp.controller;

import com.example.springaimcp.model.CalculationRequest;
import com.example.springaimcp.model.CalculationResponse;
import com.example.springaimcp.service.CalculatorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calculator")
@Validated
public class CalculatorController {

    private final CalculatorService calculatorService;

    @Autowired
    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<CalculationResponse> calculate(@Valid @RequestBody CalculationRequest request) {
        CalculationResponse response = calculatorService.calculate(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/add")
    public ResponseEntity<CalculationResponse> add(
            @RequestParam @NotNull Integer a,
            @RequestParam @NotNull Integer b) {
        CalculationRequest request = new CalculationRequest("add", a, b);
        return ResponseEntity.ok(calculatorService.calculate(request));
    }
    
    @GetMapping("/subtract")
    public ResponseEntity<CalculationResponse> subtract(
            @RequestParam @NotNull Integer a,
            @RequestParam @NotNull Integer b) {
        CalculationRequest request = new CalculationRequest("subtract", a, b);
        return ResponseEntity.ok(calculatorService.calculate(request));
    }
    
    @GetMapping("/multiply")
    public ResponseEntity<CalculationResponse> multiply(
            @RequestParam @NotNull Integer a,
            @RequestParam @NotNull Integer b) {
        CalculationRequest request = new CalculationRequest("multiply", a, b);
        return ResponseEntity.ok(calculatorService.calculate(request));
    }
    
    @GetMapping("/divide")
    public ResponseEntity<CalculationResponse> divide(
            @RequestParam @NotNull Integer a,
            @RequestParam @NotNull Integer b) {
        CalculationRequest request = new CalculationRequest("divide", a, b);
        return ResponseEntity.ok(calculatorService.calculate(request));
    }
}
