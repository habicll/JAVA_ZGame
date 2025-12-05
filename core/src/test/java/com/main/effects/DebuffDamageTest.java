package com.main.effects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class DebuffDamageTest {

    private DebuffDamage debuffDamage;

    @Before
    public void setUp() {
        debuffDamage = new DebuffDamage(10);
    }

    @Test
    public void testConstructor() {
        assertNotNull(debuffDamage);
        assertEquals("debufDamage", debuffDamage.getName());
        assertEquals(Effect.Type.DEBUFF, debuffDamage.getType());
        assertEquals(10, debuffDamage.getDebuffDamage());
    }

    @Test
    public void testGetDebuffDamage() {
        assertEquals(10, debuffDamage.getDebuffDamage());
    }

    @Test
    public void testConstructorWithDifferentValue() {
        DebuffDamage debuff = new DebuffDamage(25);
        assertEquals(25, debuff.getDebuffDamage());
    }

    @Test
    public void testConstructorWithZero() {
        DebuffDamage debuff = new DebuffDamage(0);
        assertEquals(0, debuff.getDebuffDamage());
    }
}