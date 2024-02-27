package edu.ycp.cs320.lab02.model;
import static org.junit.Assert.*;
import org.junit.Test;

public class NumbersTest {
    
    @Test
    public void testConstructorWithTwoNumbers() {
        Numbers numbers = new Numbers(5.0, 10.0);
        assertEquals(Double.valueOf(5.0), numbers.getFirst());
        assertEquals(Double.valueOf(10.0), numbers.getSecond());
        assertNull(numbers.getThird());
    }
    
    @Test
    public void testConstructorWithThreeNumbers() {
        Numbers numbers = new Numbers(5.0, 10.0, 3.0);
        assertEquals(Double.valueOf(5.0), numbers.getFirst());
        assertEquals(Double.valueOf(10.0), numbers.getSecond());
        assertEquals(Double.valueOf(3.0), numbers.getThird());
    }
    
    @Test
    public void testGettersAndSetters() {
        Numbers numbers = new Numbers(5.0, 10.0);
        numbers.setFirst(8.0);
        numbers.setSecond(12.0);
        assertEquals(Double.valueOf(8.0), numbers.getFirst());
        assertEquals(Double.valueOf(12.0), numbers.getSecond());
        
        numbers.setThird(15.0);
        assertEquals(Double.valueOf(15.0), numbers.getThird());
        
        numbers.setResult(40.0);
        assertEquals(Double.valueOf(40.0), numbers.getResult());
    }
}
