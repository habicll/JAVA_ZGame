package com.main.weapons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class AssaultRifleTest {

    private AssaultRifle rifle;

    @Before
    public void setUp() {
        rifle = new AssaultRifle();
    }

    @Test
    public void testConstructor() {
        assertNotNull(rifle);
        assertEquals(30, rifle.getDamage());
        assertEquals(200, rifle.getRange());
        assertEquals(0.7f, rifle.getAttackSpeed(), 0.01f);
        assertEquals(30, rifle.getMaxMunitions());
        assertEquals(30, rifle.getMunitions());
    }

    @Test
    public void testAttack() {
        rifle.attack();
        assertEquals(29, rifle.getMunitions());
    }

    @Test
    public void testReload() {
        rifle.attack();
        rifle.attack();
        rifle.reload();
        assertEquals(30, rifle.getMunitions());
    }
}