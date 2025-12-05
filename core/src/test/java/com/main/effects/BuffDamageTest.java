package com.main.effects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class BuffDamageTest {

    private BuffDamage buffDamage;

    @Before
    public void setUp() {
        buffDamage = new BuffDamage(15);
    }

    @Test
    public void testConstructor() {
        assertNotNull(buffDamage);
        assertEquals("buffDamage", buffDamage.getName());
        assertEquals(Effect.Type.BUFF, buffDamage.getType());
        assertEquals(15, buffDamage.getBoostDamageValue());
    }

    @Test
    public void testGetBoostDamageValue() {
        assertEquals(15, buffDamage.getBoostDamageValue());
    }

    @Test
    public void testConstructorWithDifferentValue() {
        BuffDamage buff = new BuffDamage(50);
        assertEquals(50, buff.getBoostDamageValue());
    }

    @Test
    public void testConstructorWithZero() {
        BuffDamage buff = new BuffDamage(0);
        assertEquals(0, buff.getBoostDamageValue());
    }
}