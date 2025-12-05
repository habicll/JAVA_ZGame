package com.main.effects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class DoDamageAOETest {

    private DoDamageAOE doDamageAOE;

    @Before
    public void setUp() {
        doDamageAOE = new DoDamageAOE(30, 50);
    }

    @Test
    public void testConstructor() {
        assertNotNull(doDamageAOE);
        assertEquals("doDamageAOE", doDamageAOE.getName());
        assertEquals(Effect.Type.AOE, doDamageAOE.getType());
        assertEquals(30, doDamageAOE.getDamage());
        assertEquals(50, doDamageAOE.getAoe());
    }

    @Test
    public void testGetDamage() {
        assertEquals(30, doDamageAOE.getDamage());
    }

    @Test
    public void testGetAoe() {
        assertEquals(50, doDamageAOE.getAoe());
    }

    @Test
    public void testConstructorWithDifferentValues() {
        DoDamageAOE aoe = new DoDamageAOE(100, 150);
        assertEquals(100, aoe.getDamage());
        assertEquals(150, aoe.getAoe());
    }

    @Test
    public void testConstructorWithZero() {
        DoDamageAOE aoe = new DoDamageAOE(0, 0);
        assertEquals(0, aoe.getDamage());
        assertEquals(0, aoe.getAoe());
    }
}