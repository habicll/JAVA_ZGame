package com.main.weapons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class SniperRifleTest {

    private SniperRifle sniper;

    @Before
    public void setUp() {
        sniper = new SniperRifle();
    }

    @Test
    public void testConstructor() {
        assertNotNull(sniper);
        assertEquals(150, sniper.getDamage());
        assertEquals(450, sniper.getRange());
        assertEquals(3f, sniper.getAttackSpeed(), 0.01f);
        assertEquals(5, sniper.getMaxMunitions());
        assertEquals(5, sniper.getMunitions());
    }

    @Test
    public void testAttack() {
        sniper.attack();
        assertEquals(4, sniper.getMunitions());
    }

    @Test
    public void testReload() {
        sniper.attack();
        sniper.attack();
        sniper.reload();
        assertEquals(5, sniper.getMunitions());
    }
}