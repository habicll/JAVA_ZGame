package com.main.effects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class DebuffMoveSpeedTest {

    private DebuffMoveSpeed debuffMoveSpeed;

    @Before
    public void setUp() {
        debuffMoveSpeed = new DebuffMoveSpeed(20);
    }

    @Test
    public void testConstructor() {
        assertNotNull(debuffMoveSpeed);
        assertEquals("debuffMoveSpeed", debuffMoveSpeed.getName());
        assertEquals(Effect.Type.DEBUFF, debuffMoveSpeed.getType());
        assertEquals(20, debuffMoveSpeed.getDebuffMoveSpeedValue());
    }

    @Test
    public void testGetDebuffMoveSpeedValue() {
        assertEquals(20, debuffMoveSpeed.getDebuffMoveSpeedValue());
    }

    @Test
    public void testConstructorWithDifferentValue() {
        DebuffMoveSpeed debuff = new DebuffMoveSpeed(50);
        assertEquals(50, debuff.getDebuffMoveSpeedValue());
    }

    @Test
    public void testConstructorWithZero() {
        DebuffMoveSpeed debuff = new DebuffMoveSpeed(0);
        assertEquals(0, debuff.getDebuffMoveSpeedValue());
    }
}