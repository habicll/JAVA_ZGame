package com.main.effects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class HealTest {

    private Heal heal;

    @Before
    public void setUp() {
        heal = new Heal(150);
    }

    @Test
    public void testConstructor() {
        assertNotNull(heal);
        assertEquals("regen", heal.getName());
        assertEquals(Effect.Type.BUFF, heal.getType());
        assertEquals(150, heal.getRegen());
    }

    @Test
    public void testGetRegen() {
        assertEquals(150, heal.getRegen());
    }

    @Test
    public void testConstructorWithDifferentValue() {
        Heal h = new Heal(50);
        assertEquals(50, h.getRegen());
    }

    @Test
    public void testConstructorWithZero() {
        Heal h = new Heal(0);
        assertEquals(0, h.getRegen());
    }
}