package com.main.effects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class DoDamageTest {

    private DoDamage doDamage;

    @Before
    public void setUp() {
        doDamage = new DoDamage(50, 100);
    }

    @Test
    public void testConstructor() {
        assertNotNull(doDamage);
        assertEquals("doDamage", doDamage.getName());
        assertEquals(Effect.Type.DAMAGE, doDamage.getType());
        assertEquals(50, doDamage.getDamage());
        assertEquals(100, doDamage.getRange());
    }

    @Test
    public void testGetDamage() {
        assertEquals(50, doDamage.getDamage());
    }

    @Test
    public void testGetRange() {
        assertEquals(100, doDamage.getRange());
    }

    @Test
    public void testConstructorWithDifferentValues() {
        DoDamage damage = new DoDamage(75, 200);
        assertEquals(75, damage.getDamage());
        assertEquals(200, damage.getRange());
    }

    @Test
    public void testConstructorWithZero() {
        DoDamage damage = new DoDamage(0, 0);
        assertEquals(0, damage.getDamage());
        assertEquals(0, damage.getRange());
    }
}