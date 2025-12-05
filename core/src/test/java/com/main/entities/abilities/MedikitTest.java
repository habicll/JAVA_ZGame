package com.main.abilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.main.effects.Heal;

public class MedikitTest {

    private Medikit medikit;

    @Before
    public void setUp() {
        medikit = new Medikit();
    }

    @Test
    public void testConstructor() {
        assertNotNull(medikit);
        assertEquals("Medikit", medikit.getName());
        assertEquals("Take a big shot of morphin to cope with the pain", medikit.getDescription());
        assertEquals(60, medikit.getCooldown());
        assertNotNull(medikit.getEffect());
        assertTrue(medikit.getEffect() instanceof Heal);
    }

    @Test
    public void testUse() {
        assertTrue(medikit.isReady());
        medikit.use();
        assertEquals(60, medikit.getCurrentCooldown());
    }

    @Test
    public void testUpdateCooldown() {
        medikit.use();
        medikit.updateCooldown();
        assertEquals(59, medikit.getCurrentCooldown());
    }
}