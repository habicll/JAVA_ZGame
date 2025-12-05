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

public class FZombieTest {

    private static Application application;
    private FZombie fZombie;
    
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
        
        fZombie = new FZombie(100, 200, mockBase);
    }

    // ===== Constructor Tests =====

    @Test
    public void testConstructor() {
        assertNotNull("FZombie should not be null", fZombie);
        assertEquals("Initial X position should be 100", 100.0f, fZombie.getPosX(), 0.01f);
        assertEquals("Initial Y position should be 200", 200.0f, fZombie.getPosY(), 0.01f);
    }

    @Test
    public void testFZombieStats() {
        assertEquals("FZombie health should be 400", 400, fZombie.getHealth());
        assertEquals("FZombie speed should be 30", 30.0f, fZombie.getSpeed(), 0.01f);
        assertEquals("FZombie attack damage should be 18", 18, fZombie.getAttackDamage());
        assertEquals("FZombie attack speed should be 1.2", 1.2f, fZombie.getAttackSpeed(), 0.01f);
        assertEquals("FZombie range should be 50", 50, fZombie.getRange());
    }

    @Test
    public void testConstructorWithDifferentPositions() {
        FZombie z1 = new FZombie(0, 0, mockBase);
        FZombie z2 = new FZombie(500, 300, mockBase);
        
        assertEquals("First zombie X should be 0", 0.0f, z1.getPosX(), 0.01f);
        assertEquals("Second zombie X should be 500", 500.0f, z2.getPosX(), 0.01f);
        assertEquals("First zombie Y should be 0", 0.0f, z1.getPosY(), 0.01f);
        assertEquals("Second zombie Y should be 300", 300.0f, z2.getPosY(), 0.01f);
    }

    // ===== Movement Tests =====

    @Test
    public void testMove() {
        float initialX = fZombie.getPosX();
        fZombie.move(1.0f);
        assertTrue("X position should decrease (move left)", fZombie.getPosX() < initialX);
    }

    @Test
    public void testMoveCalculation() {
        float initialX = fZombie.getPosX();
        fZombie.move(1.0f);
        float expectedX = initialX - 30;
        assertEquals("Position after move", expectedX, fZombie.getPosX(), 0.1f);
    }

    @Test
    public void testMoveWithRealisticDelta() {
        float initialX = fZombie.getPosX();
        fZombie.move(0.016f);
        float expectedX = initialX - (30 * 0.016f);
        assertEquals("Position with realistic delta", expectedX, fZombie.getPosX(), 0.01f);
    }

    @Test
    public void testMoveMultipleTimes() {
        float initialX = fZombie.getPosX();
        fZombie.move(0.1f);
        fZombie.move(0.1f);
        fZombie.move(0.1f);
        float expectedX = initialX - (30 * 0.3f);
        assertEquals("Position after multiple moves", expectedX, fZombie.getPosX(), 0.1f);
    }

    // ===== Combat Tests =====

    @Test
    public void testTakeDamage() {
        fZombie.takeDamage(100);
        assertEquals("Health should decrease by 100", 300, fZombie.getHealth());
        assertFalse("FZombie should not be dead", fZombie.isDead());
    }

    @Test
    public void testTakeDamageMultipleTimes() {
        fZombie.takeDamage(100);
        fZombie.takeDamage(100);
        fZombie.takeDamage(100);
        fZombie.takeDamage(100);
        assertEquals("Health should be 0", 0, fZombie.getHealth());
        assertTrue("FZombie should be dead", fZombie.isDead());
    }

    @Test
    public void testIsNotDeadInitially() {
        assertFalse("FZombie should not be dead initially", fZombie.isDead());
    }

    // ===== Render Tests =====

    @Test
    public void testRenderDoesNotThrow() {
        fZombie.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    @Test
    public void testRenderAfterMove() {
        fZombie.move(1.0f);
        fZombie.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    // ===== Integration Tests =====

    @Test
    public void testFullGameLoop() {
        for (int i = 0; i < 10; i++) {
            fZombie.move(0.016f);
            fZombie.updateCooldown(0.016f);
            fZombie.render(mockBatch);
        }
        
        assertTrue("Should have moved left", fZombie.getPosX() < 100);
    }

    @Test
    public void testFZombieInheritance() {
        assertTrue("FZombie should be instance of Zombie", fZombie instanceof Zombie);
    }

    @Test
    public void testFZombieStatsAreDifferent() {
        assertEquals("FZombie specific health", 400, fZombie.getHealth());
        assertEquals("FZombie specific damage", 18, fZombie.getAttackDamage());
        assertEquals("FZombie specific speed", 30.0f, fZombie.getSpeed(), 0.01f);
    }
}