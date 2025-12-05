package com.main.entities.enemies;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.entities.Unit;
import com.main.entities.Unit.UnitState;
import com.main.map.Base;

public class ZombieTest {

    private static Application application;
    private TestZombie zombie;

    @Mock
    private SpriteBatch mockBatch;
    
    @Mock
    private GL20 mockGL;
    
    @Mock
    private Graphics mockGraphics;
    
    @Mock
    private Base mockBase;
    
    private List<Unit> enemies;

    // Classe pour tester sans charger de texture
    private class TestZombie extends Zombie {
        private boolean forceShouldStop = false;

        public TestZombie(float posX, float posY) {
            super(null, posX, posY, mockBase);
            this.texture = mock(Texture.class);
            this.sprite = mock(Sprite.class);
            this.health = 100;
            this.speed = 50.0f;
            this.attackDamage = 10;
            this.attackSpeed = 2.0f;
            this.range = 50;
        }

        public void setForceShouldStop(boolean value) {
            this.forceShouldStop = value;
        }
        
        @Override
        protected boolean shouldStopMoving() {
            if (forceShouldStop) return true;
            return super.shouldStopMoving();
        }
    }

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
        
        zombie = new TestZombie(100, 200);
        enemies = new ArrayList<>();
    }

    // ===== Constructor Tests =====

    @Test
    public void testConstructor() {
        assertNotNull("Zombie should not be null", zombie);
        assertEquals("Initial X position should be 100", 100.0f, zombie.getPosX(), 0.01f);
        assertEquals("Initial Y position should be 200", 200.0f, zombie.getPosY(), 0.01f);
    }

    @Test
    public void testConstructorInitializesStats() {
        assertEquals("Zombie health should be 100", 100, zombie.getHealth());
        assertEquals("Zombie speed should be 50", 50.0f, zombie.getSpeed(), 0.01f);
        assertEquals("Zombie attack damage should be 10", 10, zombie.getAttackDamage());
        assertEquals("Zombie attack speed should be 2.0", 2.0f, zombie.getAttackSpeed(), 0.01f);
        assertEquals("Zombie range should be 50", 50, zombie.getRange());
    }

    // ===== Health Tests =====

    @Test
    public void testInitialHealth() {
        assertFalse("Zombie should not be dead initially", zombie.isDead());
        assertEquals("Initial health should be 100", 100, zombie.getHealth());
    }

    @Test
    public void testTakeDamage() {
        zombie.takeDamage(50);
        assertEquals("Health should be 50 after damage", 50, zombie.getHealth());
        assertFalse("Zombie should not be dead", zombie.isDead());
    }

    @Test
    public void testTakeDamageMultipleTimes() {
        zombie.takeDamage(30);
        zombie.takeDamage(40);
        assertEquals("Health should be 30", 30, zombie.getHealth());
        assertFalse("Zombie should not be dead", zombie.isDead());
    }

    @Test
    public void testTakeDamageExact() {
        zombie.takeDamage(100);
        assertEquals("Health should be 0", 0, zombie.getHealth());
        assertTrue("Zombie should be dead", zombie.isDead());
    }

    @Test
    public void testTakeDamageOverkill() {
        zombie.takeDamage(150);
        assertEquals("Health should be 0 with overkill", 0, zombie.getHealth());
        assertTrue("Zombie should be dead", zombie.isDead());
    }

    // ===== Movement Tests =====

    @Test
    public void testMove() {
        float initialX = zombie.getPosX();
        zombie.move(1.0f);
        assertTrue("X position should decrease (move left)", zombie.getPosX() < initialX);
    }

    @Test
    public void testMoveAfterAttack() {
        Unit target = mock(Unit.class);
        when(target.getPosX()).thenReturn(100.0f);
        when(target.getPosY()).thenReturn(200.0f);
        when(target.isDead()).thenReturn(false);
        float initialX = zombie.getPosX();
        zombie.setTarget(target);
        zombie.attack();
        zombie.move(1.0f);
        assertEquals("X position not decrease", initialX, zombie.getPosX(), 0.01f);
    }

    @Test
    public void testIdle(){
        Unit target = mock(Unit.class);
        when(target.getPosX()).thenReturn(100.0f);
        when(target.getPosY()).thenReturn(200.0f);
        when(target.isDead()).thenReturn(false);
        zombie.setForceShouldStop(true); // ✅ Utiliser la méthode publique
        zombie.move(1.0f);
        assertEquals("Zombie should be idle", UnitState.IDLE, zombie.getCurrentState());
    }

    @Test
    public void testGetAttackAnimationDuration(){
        assertEquals("Animation should be 0.5", 0.5f, zombie.getAttackAnimationDuration(), 0.01f);
    }

    @Test
    public void testMoveCalculation() {
        float initialX = zombie.getPosX();
        zombie.move(1.0f);
        // Movement left = -speed * delta = -50 * 1.0 = -50
        float expectedX = initialX - 50;
        assertEquals("Position after move", expectedX, zombie.getPosX(), 0.1f);
    }

    @Test
    public void testMoveMultipleTimes() {
        float initialX = zombie.getPosX();
        zombie.move(1.0f);
        zombie.move(1.0f);
        zombie.move(1.0f);
        float expectedX = initialX - (50 * 3);
        assertEquals("Position after 3 moves", expectedX, zombie.getPosX(), 0.1f);
    }

    @Test
    public void testMoveWithRealisticDelta() {
        float initialX = zombie.getPosX();
        zombie.move(0.016f); // 60 FPS
        float expectedX = initialX - (50 * 0.016f);
        assertEquals("Position with realistic delta", expectedX, zombie.getPosX(), 0.01f);
    }

    @Test
    public void testMoveWithZeroDelta() {
        float initialX = zombie.getPosX();
        zombie.move(0.0f);
        assertEquals("Position should not change with zero delta", initialX, zombie.getPosX(), 0.01f);
    }

    @Test
    public void testMoveWithSmallDelta() {
        float initialX = zombie.getPosX();
        zombie.move(0.001f);
        float expectedX = initialX - (50 * 0.001f);
        assertEquals("Position with small delta", expectedX, zombie.getPosX(), 0.01f);
    }

    @Test
    public void testContinuousMovement() {
        float initialX = zombie.getPosX();
        float totalDelta = 0f;
        
        for (int i = 0; i < 60; i++) { // Simuler 1 seconde
            zombie.move(0.016f);
            totalDelta += 0.016f;
        }
        
        float expectedX = initialX - (50 * totalDelta);
        assertEquals("Position after continuous movement", expectedX, zombie.getPosX(), 1.0f);
    }

    // ===== State Tests =====

    @Test
    public void testInitialState() {
        assertEquals("Initial state should be WALKING", Unit.UnitState.WALKING, zombie.getCurrentState());
    }

    @Test
    public void testStateAfterMove() {
        zombie.move(1.0f);
        assertEquals("State should be WALKING after move", Unit.UnitState.WALKING, zombie.getCurrentState());
    }

    @Test
    public void testStateWhenStopped() {
        Unit target = mock(Unit.class);
        when(target.getPosX()).thenReturn(100.0f);
        when(target.getPosY()).thenReturn(200.0f);
        when(target.isDead()).thenReturn(false);
        
        zombie.setTarget(target);
        zombie.move(0.016f);
        
        assertEquals("State should be ATTACKING when target in range", Unit.UnitState.ATTACKING, zombie.getCurrentState());
    }

    // ===== Render Tests =====

    @Test
    public void testRenderDoesNotThrow() {
        zombie.render(mockBatch);
        // Should not throw exception even with null animations
    }

    @Test
    public void testRenderAfterMove() {
        zombie.move(1.0f);
        zombie.render(mockBatch);
        // Should not throw exception
    }

    // ===== Target Selection Tests =====

    @Test
    public void testSelectTargetEmpty() {
        zombie.selectTarget(enemies);
        assertNull("Target should be null with no enemies", zombie.getTarget());
    }

    @Test
    public void testSelectTargetWithEnemy() {
        Unit enemy = mock(Unit.class);
        // Zombie à (100, 200), range = 50
        // Placer l'ennemi à portée : distance doit être <= 50
        when(enemy.getPosX()).thenReturn(130.0f); // Distance X = 30
        when(enemy.getPosY()).thenReturn(200.0f); // Distance Y = 0
        // Distance totale = sqrt(30² + 0²) = 30 < 50 ✓
        when(enemy.isDead()).thenReturn(false);
        enemies.add(enemy);
        
        zombie.selectTarget(enemies);
        assertNotNull("Target should be selected when enemy in range", zombie.getTarget());
        assertEquals("Should select the close enemy", enemy, zombie.getTarget());
    }
    
    @Test
    public void testSelectTargetOutOfRange() {
        Unit enemy = mock(Unit.class);
        // Placer l'ennemi HORS de portée
        when(enemy.getPosX()).thenReturn(500.0f); // Distance X = 400 > 50
        when(enemy.getPosY()).thenReturn(200.0f);
        when(enemy.isDead()).thenReturn(false);
        enemies.add(enemy);
        
        zombie.selectTarget(enemies);
        assertNull("Target should not be selected when enemy out of range", zombie.getTarget());
    }
    
    @Test
    public void testSelectTargetClosest() {
        // Ennemi proche (à portée)
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(120.0f); // Distance = 20
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        
        // Ennemi plus loin (hors portée)
        Unit farEnemy = mock(Unit.class);
        when(farEnemy.getPosX()).thenReturn(200.0f); // Distance = 100
        when(farEnemy.getPosY()).thenReturn(200.0f);
        when(farEnemy.isDead()).thenReturn(false);
        
        enemies.add(farEnemy);
        enemies.add(closeEnemy);
        
        zombie.selectTarget(enemies);
        
        assertNotNull("Should select a target", zombie.getTarget());
        assertEquals("Should select closest enemy in range", closeEnemy, zombie.getTarget());
    }
    
    @Test
    public void testSelectTargetIgnoresDeadEnemies() {
        Unit deadEnemy = mock(Unit.class);
        when(deadEnemy.getPosX()).thenReturn(120.0f);
        when(deadEnemy.getPosY()).thenReturn(200.0f);
        when(deadEnemy.isDead()).thenReturn(true); // Mort !
        
        Unit aliveEnemy = mock(Unit.class);
        when(aliveEnemy.getPosX()).thenReturn(130.0f);
        when(aliveEnemy.getPosY()).thenReturn(200.0f);
        when(aliveEnemy.isDead()).thenReturn(false);
        
        enemies.add(deadEnemy);
        enemies.add(aliveEnemy);
        
        zombie.selectTarget(enemies);
        
        assertNotNull("Should select alive enemy", zombie.getTarget());
        assertEquals("Should ignore dead enemy", aliveEnemy, zombie.getTarget());
    }
    
    @Test
    public void testSelectTargetAtExactRange() {
        Unit enemy = mock(Unit.class);
        // Placer l'ennemi exactement à la limite de portée (50)
        when(enemy.getPosX()).thenReturn(150.0f);
        when(enemy.getPosY()).thenReturn(200.0f);
        when(enemy.isDead()).thenReturn(false);
        enemies.add(enemy);
        
        zombie.selectTarget(enemies);
        
        // Devrait être sélectionné si range <= 50
        assertNotNull("Target at exact range should be selected", zombie.getTarget());
    }

    // ===== Cooldown Tests =====

    @Test
    public void testUpdateCooldown() {
        zombie.setCooldown(5);
        zombie.updateCooldown(1.0f);
        assertEquals("Cooldown should decrease", 4.0f, zombie.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testUpdateCooldownMultipleTimes() {
        zombie.setCooldown(10);
        for (int i = 0; i < 5; i++) {
            zombie.updateCooldown(1.0f);
        }
        assertEquals("Cooldown after updates", 5.0f, zombie.getAttackCooldown(), 0.01f);
    }

    // ===== Integration Tests =====

    @Test
    public void testFullGameLoop() {
        for (int i = 0; i < 10; i++) {
            zombie.move(0.016f);
            zombie.updateCooldown(0.016f);
            zombie.render(mockBatch);
        }
        
        assertTrue("Should have moved left", zombie.getPosX() < 100);
    }

    @Test
    public void testZombieInheritance() {
        assertTrue("Zombie should be instance of Unit", zombie instanceof Unit);
    }

    @Test
    public void testGetSprite() {
        assertNotNull("Sprite should not be null", zombie.getSprite());
    }

    @Test
    public void testGetTexture() {
        assertNotNull("Texture should not be null", zombie.getTexture());
    }
}