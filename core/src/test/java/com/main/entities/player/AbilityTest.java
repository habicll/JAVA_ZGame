package com.main.entities.player;

import com.main.effects.Effect;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class AbilityTest {

    private Ability ability;
    
    @Mock
    private Effect mockEffect;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ability = new Ability("Test Ability", "Test Description", 60, mockEffect);
    }

    @Test
    public void testConstructor() {
        assertNotNull(ability);
        assertEquals("Test Ability", ability.getName());
        assertEquals("Test Description", ability.getDescription());
        assertEquals(60, ability.getCooldown());
        assertEquals(mockEffect, ability.getEffect());
        assertEquals(0, ability.getCurrentCooldown());
    }

    @Test
    public void testGetName() {
        assertEquals("Test Ability", ability.getName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Test Description", ability.getDescription());
    }

    @Test
    public void testGetCooldown() {
        assertEquals(60, ability.getCooldown());
    }

    @Test
    public void testGetEffect() {
        assertEquals(mockEffect, ability.getEffect());
    }

    @Test
    public void testGetCurrentCooldown() {
        assertEquals(0, ability.getCurrentCooldown());
    }

    @Test
    public void testIsReady() {
        assertTrue(ability.isReady());
    }

    @Test
    public void testIsNotReady() {
        ability.use();
        assertFalse(ability.isReady());
    }

    @Test
    public void testUse() {
        assertTrue(ability.isReady());
        ability.use();
        assertEquals(60, ability.getCurrentCooldown());
        assertFalse(ability.isReady());
    }

    @Test
    public void testUseWhenNotReady() {
        ability.use();
        ability.use();
        assertEquals(60, ability.getCurrentCooldown());
    }

    @Test
    public void testUpdateCooldown() {
        ability.use();
        assertEquals(60, ability.getCurrentCooldown());
        
        ability.updateCooldown();
        assertEquals(59, ability.getCurrentCooldown());
    }

    @Test
    public void testUpdateCooldownMultipleTimes() {
        ability.use();
        
        for (int i = 0; i < 10; i++) {
            ability.updateCooldown();
        }
        
        assertEquals(50, ability.getCurrentCooldown());
    }

    @Test
    public void testUpdateCooldownToZero() {
        ability.use();
        
        for (int i = 0; i < 60; i++) {
            ability.updateCooldown();
        }
        
        assertEquals(0, ability.getCurrentCooldown());
        assertTrue(ability.isReady());
    }

    @Test
    public void testUpdateCooldownWhenReady() {
        assertEquals(0, ability.getCurrentCooldown());
        ability.updateCooldown();
        assertEquals(0, ability.getCurrentCooldown());
    }
}