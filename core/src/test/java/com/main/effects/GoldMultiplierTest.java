package com.main.effects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class GoldMultiplierTest {

    private GoldMultiplier goldMultiplier;

    @Before
    public void setUp() {
        goldMultiplier = new GoldMultiplier(2);
    }

    @Test
    public void testConstructor() {
        assertNotNull(goldMultiplier);
        assertEquals("goldMultiplier", goldMultiplier.getName());
        assertEquals(Effect.Type.BUFF, goldMultiplier.getType());
        assertEquals(2, goldMultiplier.getMultiplier());
    }

    @Test
    public void testGetMultiplier() {
        assertEquals(2, goldMultiplier.getMultiplier());
    }

    @Test
    public void testConstructorWithDifferentValue() {
        GoldMultiplier multiplier = new GoldMultiplier(5);
        assertEquals(5, multiplier.getMultiplier());
    }

    @Test
    public void testConstructorWithOne() {
        GoldMultiplier multiplier = new GoldMultiplier(1);
        assertEquals(1, multiplier.getMultiplier());
    }
}