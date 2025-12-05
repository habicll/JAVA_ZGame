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

public class WZombieTest {

    private static Application application;
    private WZombie wZombie;
    
    @Mock
    private SpriteBatch mockBatch;
    
    @Mock
    private Base mockBase;

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
        
        wZombie = new WZombie(100, 200, mockBase);
    }

    // ===== Constructor Tests =====

    @Test
    public void testWZombieConstructor() {
        assertNotNull("WZombie should be created", wZombie);
        assertEquals("Initial X position should be 100", 100.0f, wZombie.getPosX(), 0.01f);
        assertEquals("Initial Y position should be 200", 200.0f, wZombie.getPosY(), 0.01f);
    }

    @Test
    public void testWZombieStats() {
        assertEquals("WZombie health should be 120", 120, wZombie.getHealth());
        assertEquals("WZombie speed should be 60", 60.0f, wZombie.getSpeed(), 0.01f);
        assertEquals("WZombie attack damage should be 27", 27, wZombie.getAttackDamage());
        assertEquals("WZombie attack speed should be 1.8", 1.8f, wZombie.getAttackSpeed(), 0.01f);
        assertEquals("WZombie range should be 50", 50, wZombie.getRange());
    }

    @Test
    public void testConstructorWithDifferentPositions() {
        WZombie z1 = new WZombie(0, 0, mockBase);
        WZombie z2 = new WZombie(500, 300, mockBase);
        
        assertEquals("First zombie X should be 0", 0.0f, z1.getPosX(), 0.01f);
        assertEquals("Second zombie X should be 500", 500.0f, z2.getPosX(), 0.01f);
        assertEquals("First zombie Y should be 0", 0.0f, z1.getPosY(), 0.01f);
        assertEquals("Second zombie Y should be 300", 300.0f, z2.getPosY(), 0.01f);
    }

    // ===== Movement Tests =====

    @Test
    public void testMoveDecreasesXPosition() {
        float initialX = wZombie.getPosX();
        wZombie.move(1.0f);
        assertTrue("X position should decrease after move", wZombie.getPosX() < initialX);
    }

    @Test
    public void testMoveWithDelta() {
        float initialX = wZombie.getPosX();
        float delta = 0.5f;
        wZombie.move(delta);
        
        float expectedX = initialX - (60 * delta);
        assertEquals("X position should be correct after move", expectedX, wZombie.getPosX(), 0.01f);
    }

    @Test
    public void testMoveWithZeroDelta() {
        float initialX = wZombie.getPosX();
        wZombie.move(0.0f);
        assertEquals("X position should not change with zero delta", initialX, wZombie.getPosX(), 0.01f);
    }

    @Test
    public void testMultipleMoveCalls() {
        float initialX = wZombie.getPosX();
        wZombie.move(0.1f);
        wZombie.move(0.1f);
        wZombie.move(0.1f);
        
        float expectedX = initialX - (60 * 0.3f);
        assertEquals("X position should be correct after multiple moves", expectedX, wZombie.getPosX(), 0.01f);
    }

    // ===== Combat Tests =====

    @Test
    public void testTakeDamage() {
        wZombie.takeDamage(50);
        assertEquals("Health should decrease by 50", 70, wZombie.getHealth());
    }

    @Test
    public void testTakeDamageMultipleTimes() {
        wZombie.takeDamage(100);
        assertEquals("Health should decrease correctly", 20, wZombie.getHealth());
    }

    @Test
    public void testTakeDamageUntilDeath() {
        wZombie.takeDamage(400);
        assertTrue("WZombie should be dead after fatal damage", wZombie.isDead());
    }

    @Test
    public void testIsNotDeadInitially() {
        assertFalse("WZombie should not be dead initially", wZombie.isDead());
    }

    // ===== Render Tests =====

    @Test
    public void testRenderDoesNotThrow() {
        wZombie.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    @Test
    public void testRenderAfterMove() {
        wZombie.move(1.0f);
        wZombie.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    // ===== Integration Tests =====

    @Test
    public void testContinuousMovement() {
        float initialX = wZombie.getPosX();
        float totalDelta = 0f;
        
        for (int i = 0; i < 60; i++) {
            wZombie.move(0.016f);
            totalDelta += 0.016f;
        }
        
        float expectedX = initialX - (60 * totalDelta);
        assertEquals("Position after continuous movement", expectedX, wZombie.getPosX(), 1.0f);
    }

    @Test
    public void testWZombieInheritance() {
        assertTrue("WZombie should be instance of Zombie", wZombie instanceof Zombie);
    }

    @Test
    public void testWZombieStatsAreDifferent() {
        assertEquals("WZombie specific health", 120, wZombie.getHealth());
        assertEquals("WZombie specific damage", 27, wZombie.getAttackDamage());
        assertEquals("WZombie specific speed", 60.0f, wZombie.getSpeed(), 0.01f);
    }
}
