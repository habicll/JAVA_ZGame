package com.main.abilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.main.effects.GoldMultiplier;

public class MidasTouchTest {

    private MidasTouch midasTouch;

    @Before
    public void setUp() {
        midasTouch = new MidasTouch();
    }

    @Test
    public void testConstructor() {
        assertNotNull(midasTouch);
        assertEquals("Midas Touch", midasTouch.getName());
        assertEquals("Coat your bullets in gold and earn more goldo", midasTouch.getDescription());
        assertEquals(120, midasTouch.getCooldown());
        assertNotNull(midasTouch.getEffect());
        assertTrue(midasTouch.getEffect() instanceof GoldMultiplier);
    }

    @Test
    public void testUse() {
        assertTrue(midasTouch.isReady());
        midasTouch.use();
        assertEquals(120, midasTouch.getCurrentCooldown());
    }

    @Test
    public void testUpdateCooldown() {
        midasTouch.use();
        midasTouch.updateCooldown();
        assertEquals(119, midasTouch.getCurrentCooldown());
    }
}