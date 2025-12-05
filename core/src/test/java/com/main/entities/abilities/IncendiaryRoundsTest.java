package com.main.abilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.main.effects.DoDamageAOE;

public class IncendiaryRoundsTest {

    private IncendiaryRounds incendiaryRounds;

    @Before
    public void setUp() {
        incendiaryRounds = new IncendiaryRounds();
    }

    @Test
    public void testConstructor() {
        assertNotNull(incendiaryRounds);
        assertEquals("Incendiary Rounds", incendiaryRounds.getName());
        assertEquals("A brand new model of bullets, making them explosives !", incendiaryRounds.getDescription());
        assertEquals(90, incendiaryRounds.getCooldown());
        assertNotNull(incendiaryRounds.getEffect());
        assertTrue(incendiaryRounds.getEffect() instanceof DoDamageAOE);
    }

    @Test
    public void testUse() {
        assertTrue(incendiaryRounds.isReady());
        incendiaryRounds.use();
        assertEquals(90, incendiaryRounds.getCurrentCooldown());
    }

    @Test
    public void testUpdateCooldown() {
        incendiaryRounds.use();
        incendiaryRounds.updateCooldown();
        assertEquals(89, incendiaryRounds.getCurrentCooldown());
    }
}