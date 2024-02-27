package edu.ycp.cs320.lab02.controller;
import edu.ycp.cs320.lab02.model.Numbers;

public class NumbersController {
    public void add(Numbers numbers) {
        double result = numbers.getFirst() + numbers.getSecond() + numbers.getThird();
        numbers.setResult(result);
    }

    public void multiply(Numbers numbers) {
        double result = numbers.getFirst() * numbers.getSecond();
        numbers.setResult(result);
    }
    
    public Double add(Double first, Double second, Double third) {
		return first + second + third;
	}
    
	public Double multiply(Double first, Double second) {
		return first * second;
	}
}


