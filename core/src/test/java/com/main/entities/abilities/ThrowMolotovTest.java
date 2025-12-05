package com.main.abilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.main.effects.Poison;

public class ThrowMolotovTest {

    private ThrowMolotov throwMolotov;

    @Before
    public void setUp() {
        throwMolotov = new ThrowMolotov();
    }

    @Test
    public void testConstructor() {
        assertNotNull(throwMolotov);
        assertEquals("Throw Molotov", throwMolotov.getName());
        assertEquals("Throw a cocktail molotov at the designated area", throwMolotov.getDescription());
        assertEquals(30, throwMolotov.getCooldown());
        assertNotNull(throwMolotov.getEffect());
        assertTrue(throwMolotov.getEffect() instanceof Poison);
    }

    @Test
    public void testUse() {
        assertTrue(throwMolotov.isReady());
        throwMolotov.use();
        assertEquals(30, throwMolotov.getCurrentCooldown());
    }

    @Test
    public void testUpdateCooldown() {
        throwMolotov.use();
        throwMolotov.updateCooldown();
        assertEquals(29, throwMolotov.getCurrentCooldown());
    }
}