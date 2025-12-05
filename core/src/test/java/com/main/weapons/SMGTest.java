package com.main.weapons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class SMGTest {

    private SMG smg;

    @Before
    public void setUp() {
        smg = new SMG();
    }

    @Test
    public void testConstructor() {
        assertNotNull(smg);
        assertEquals(20, smg.getDamage());
        assertEquals(200, smg.getRange());
        assertEquals(0.5f, smg.getAttackSpeed(), 0.01f);
        assertEquals(35, smg.getMaxMunitions());
        assertEquals(35, smg.getMunitions());
    }

    @Test
    public void testAttack() {
        smg.attack();
        assertEquals(34, smg.getMunitions());
    }

    @Test
    public void testReload() {
        smg.attack();
        smg.attack();
        smg.reload();
        assertEquals(35, smg.getMunitions());
    }
}