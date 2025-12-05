package com.main.entities.enemies;

import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.map.Base;

public class CZombieTest {

    private static Application application;
    private CZombie cZombie;
    
    @Mock
    private Base mockBase;

    @Mock
    private SpriteBatch mockBatch;
    
    @Mock
    private GL20 mockGL;
    
    @Mock
    private Graphics mockGraphics;

    @BeforeClass
    public static void init() {
        application = new HeadlessApplication(new com.badlogic.gdx.ApplicationAdapter() {});
    }

    @AfterClass
    public static void cleanUp() {
        if (application != null) {
            application.exit();
        }
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Gdx.gl = mockGL;
        Gdx.gl20 = mockGL;
        Gdx.graphics = mockGraphics;
        
        when(mockGraphics.getWidth()).thenReturn(800);
        when(mockGraphics.getHeight()).thenReturn(600);
        
        cZombie = new CZombie(100, 200, mockBase);
    }

    // ===== Constructor Tests =====

    @Test
    public void testConstructor() {
        assertNotNull("CZombie should not be null", cZombie);
        assertEquals("Initial X position should be 100", 100.0f, cZombie.getPosX(), 0.01f);
        assertEquals("Initial Y position should be 200", 200.0f, cZombie.getPosY(), 0.01f);
    }

    @Test
    public void testCZombieStats() {
        assertEquals("CZombie health should be 140", 140, cZombie.getHealth());
        assertEquals("CZombie speed should be 60", 60.0f, cZombie.getSpeed(), 0.01f);
        assertEquals("CZombie attack damage should be 39", 39, cZombie.getAttackDamage());
        assertEquals("CZombie attack speed should be 0.75", 0.75f, cZombie.getAttackSpeed(), 0.01f);
        assertEquals("CZombie range should be 50", 50, cZombie.getRange());
    }

    @Test
    public void testConstructorWithDifferentPositions() {
        CZombie z1 = new CZombie(0, 0, mockBase);
        CZombie z2 = new CZombie(500, 300, mockBase);
        
        assertEquals("First zombie X should be 0", 0.0f, z1.getPosX(), 0.01f);
        assertEquals("Second zombie X should be 500", 500.0f, z2.getPosX(), 0.01f);
        assertEquals("First zombie Y should be 0", 0.0f, z1.getPosY(), 0.01f);
        assertEquals("Second zombie Y should be 300", 300.0f, z2.getPosY(), 0.01f);
    }

    // ===== Movement Tests =====

    @Test
    public void testMove() {
        float initialX = cZombie.getPosX();
        cZombie.move(1.0f);
        assertTrue("X position should decrease (move left)", cZombie.getPosX() < initialX);
    }

    @Test
    public void testMoveCalculation() {
        float initialX = cZombie.getPosX();
        cZombie.move(1.0f);
        float expectedX = initialX - 60;
        assertEquals("Position after move", expectedX, cZombie.getPosX(), 0.1f);
    }

    @Test
    public void testMoveWithRealisticDelta() {
        float initialX = cZombie.getPosX();
        cZombie.move(0.016f);
        float expectedX = initialX - (60 * 0.016f);
        assertEquals("Position with realistic delta", expectedX, cZombie.getPosX(), 0.01f);
    }

    @Test
    public void testMoveMultipleTimes() {
        float initialX = cZombie.getPosX();
        cZombie.move(0.1f);
        cZombie.move(0.1f);
        cZombie.move(0.1f);
        float expectedX = initialX - (60 * 0.3f);
        assertEquals("Position after multiple moves", expectedX, cZombie.getPosX(), 0.1f);
    }

    // ===== Combat Tests =====

    @Test
    public void testTakeDamage() {
        cZombie.takeDamage(100);
        assertEquals("Health should decrease by 100", 40, cZombie.getHealth());
        assertFalse("CZombie should not be dead", cZombie.isDead());
    }

    @Test
    public void testTakeDamageUntilDeath() {
        cZombie.takeDamage(140);
        assertEquals("Health should be 0", 0, cZombie.getHealth());
        assertTrue("CZombie should be dead", cZombie.isDead());
    }

    @Test
    public void testIsNotDeadInitially() {
        assertFalse("CZombie should not be dead initially", cZombie.isDead());
    }

    // ===== Render Tests =====

    @Test
    public void testRenderDoesNotThrow() {
        cZombie.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    @Test
    public void testRenderAfterMove() {
        cZombie.move(1.0f);
        cZombie.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    // ===== Integration Tests =====

    @Test
    public void testFullGameLoop() {
        for (int i = 0; i < 10; i++) {
            cZombie.move(0.016f);
            cZombie.updateCooldown(0.016f);
            cZombie.render(mockBatch);
        }
        
        assertTrue("Should have moved left", cZombie.getPosX() < 100);
    }

    @Test
    public void testCZombieInheritance() {
        assertTrue("CZombie should be instance of Zombie", cZombie instanceof Zombie);
    }

    @Test
    public void testCZombieStatsAreDifferent() {
        assertEquals("CZombie specific health", 140, cZombie.getHealth());
        assertEquals("CZombie specific damage", 39, cZombie.getAttackDamage());
        assertEquals("CZombie specific speed", 60.0f, cZombie.getSpeed(), 0.01f);
        assertEquals("CZombie slower attack", 0.75f, cZombie.getAttackSpeed(), 0.01f);
    }
}