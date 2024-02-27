package edu.ycp.cs320.lab02.model;
import static org.junit.Assert.*;
import org.junit.Test;

public class GuessingGameTest {
    
    @Test
    public void testConstructor() {
        GuessingGame game = new GuessingGame();
        assertEquals(0, game.getMin());
        assertEquals(0, game.getMax());
    }
    
    @Test
    public void testGettersAndSetters() {
        GuessingGame game = new GuessingGame();
        game.setMin(10);
        game.setMax(50);
        assertEquals(10, game.getMin());
        assertEquals(50, game.getMax());
    }
    
    @Test
    public void testIsDone() {
        GuessingGame game = new GuessingGame();
        game.setMin(30);
        game.setMax(30);
        assertTrue(game.isDone());
        
        game.setMax(40);
        assertFalse(game.isDone());
    }
    
    @Test
    public void testGetGuess() {
        GuessingGame game = new GuessingGame();
        game.setMin(10);
        game.setMax(20);
        assertEquals(15, game.getGuess());
        
        game.setMin(5);
        game.setMax(7);
        assertEquals(6, game.getGuess());
    }
    
    @Test
    public void testSetIsLessThan() {
        GuessingGame game = new GuessingGame();
        game.setMin(10);
        game.setMax(20);
        game.setIsLessThan(15);
        assertEquals(10, game.getMin());
        assertEquals(14, game.getMax());
    }
    
    @Test
    public void testSetIsGreaterThan() {
        GuessingGame game = new GuessingGame();
        game.setMin(10);
        game.setMax(20);
        game.setIsGreaterThan(15);
        assertEquals(16, game.getMin());
        assertEquals(20, game.getMax());
    }
}
