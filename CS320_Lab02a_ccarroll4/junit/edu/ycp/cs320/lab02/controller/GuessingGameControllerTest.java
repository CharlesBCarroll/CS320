package edu.ycp.cs320.lab02.controller;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.ycp.cs320.lab02.controller.GuessingGameController;
import edu.ycp.cs320.lab02.model.GuessingGame;

public class GuessingGameControllerTest {
    private GuessingGameController controller;
    private GuessingGame model;

    @Before
    public void setUp() {
        model = new GuessingGame();
        controller = new GuessingGameController();
        controller.setModel(model);
    }

    @Test
    public void testStartGame() {
        controller.startGame();
        assertEquals(1, model.getMin());
        assertEquals(100, model.getMax());
    }

    @Test
    public void testSetNumberFound() {
        model.setMin(42);
        model.setMax(42);
        controller.setNumberFound();
        assertEquals(42, model.getMin());
        assertEquals(42, model.getMax());
    }

    @Test
    public void testSetNumberIsLessThanGuess() {
        model.setIsLessThan(54);
    	model.setMin(42);
        controller.setNumberIsLessThanGuess();
        assertTrue(model.getMin() < model.getMax());
    }

    @Test
    public void testSetNumberIsGreaterThanGuess() {
        controller.setNumberIsGreaterThanGuess();
        model.setIsGreaterThan(42);
        model.setMax(53);
        assertTrue(model.getMax() > model.getMin());
    }
}
