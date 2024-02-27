package edu.ycp.cs320.lab02.controller;
import static org.junit.Assert.*;
import org.junit.Test;

public class NumbersControllerTest {
    
    @Test
    public void testAdd() {
        NumbersController controller = new NumbersController();
        Double result = controller.add(5.0, 10.0, 3.0);
        assertEquals(Double.valueOf(18.0), result);
    }
    
    @Test
    public void testMultiply() {
        NumbersController controller = new NumbersController();
        Double result = controller.multiply(5.0, 10.0);
        assertEquals(Double.valueOf(50.0), result);
    }
}
