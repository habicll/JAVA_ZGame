package com.main.weapons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class PistolTest {

    private Pistol pistol;

    @Before
    public void setUp() {
        pistol = new Pistol();
    }

    @Test
    public void testConstructor() {
        assertNotNull(pistol);
        assertEquals(15, pistol.getDamage());
        assertEquals(150, pistol.getRange());
        assertEquals(1f, pistol.getAttackSpeed(), 0.01f);
        assertEquals(12, pistol.getMaxMunitions());
        assertEquals(12, pistol.getMunitions());
    }

    @Test
    public void testAttack() {
        pistol.attack();
        assertEquals(11, pistol.getMunitions());
    }

    @Test
    public void testReload() {
        pistol.attack();
        pistol.attack();
        pistol.reload();
        assertEquals(12, pistol.getMunitions());
    }
}