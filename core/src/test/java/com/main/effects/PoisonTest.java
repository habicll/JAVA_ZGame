package com.main.effects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class PoisonTest {

    private Poison poison;

    @Before
    public void setUp() {
        poison = new Poison(20);
    }

    @Test
    public void testConstructor() {
        assertNotNull(poison);
        assertEquals("poison", poison.getName());
        assertEquals(Effect.Type.DOT, poison.getType());
        assertEquals(20, poison.getDamagePerSecond());
    }

    @Test
    public void testGetDamagePerSecond() {
        assertEquals(20, poison.getDamagePerSecond());
    }

    @Test
    public void testConstructorWithDifferentValue() {
        Poison p = new Poison(50);
        assertEquals(50, p.getDamagePerSecond());
    }

    @Test
    public void testConstructorWithZero() {
        Poison p = new Poison(0);
        assertEquals(0, p.getDamagePerSecond());
    }
}