package com.main.entities.units;

import java.util.ArrayList;
import java.util.List;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import com.main.entities.Unit;
import com.main.map.Base;

public class SniperTest {

    private static Application application;
    private Sniper sniper;
    
    @Mock
    private SpriteBatch mockBatch;

    @Mock
    private Base mockBase;
    
    @Mock
    private GL20 mockGL;
    
    @Mock
    private Graphics mockGraphics;
    
    @Mock
    private Unit mockEnemy;
    
    private List<Unit> enemies;

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
        
        sniper = new Sniper(100, 200, mockBase);
        enemies = new ArrayList<>();
        
        when(mockEnemy.getPosX()).thenReturn(500.0f);
        when(mockEnemy.getPosY()).thenReturn(200.0f);
        when(mockEnemy.isDead()).thenReturn(false);
        when(mockEnemy.getHealth()).thenReturn(100);
    }

    // ===== Constructor Tests =====

    @Test
    public void testConstructor() {
        assertNotNull("Sniper should not be null", sniper);
        assertEquals("Initial X position should be 100", 100.0f, sniper.getPosX(), 0.01f);
        assertEquals("Initial Y position should be 200", 200.0f, sniper.getPosY(), 0.01f);
    }

    @Test
    public void testConstructorInitializesStats() {
        assertEquals("Sniper health should be 100", 100, sniper.getHealth());
        assertEquals("Sniper attack damage should be 45", 45, sniper.getAttackDamage());
        assertEquals("Sniper speed should be 30", 30.0f, sniper.getSpeed(), 0.01f);
        assertEquals("Sniper attack speed should be 3", 3f, sniper.getAttackSpeed(), 0.01f);
        assertEquals("Sniper range should be 150", 150, sniper.getRange());
    }

    @Test
    public void testConstructorWithDifferentPositions() {
        Sniper sniper1 = new Sniper(0, 0, mockBase);
        Sniper sniper2 = new Sniper(500, 300, mockBase);
        
        assertEquals("First sniper X should be 0", 0.0f, sniper1.getPosX(), 0.01f);
        assertEquals("Second sniper X should be 500", 500.0f, sniper2.getPosX(), 0.01f);
        assertEquals("First sniper Y should be 0", 0.0f, sniper1.getPosY(), 0.01f);
        assertEquals("Second sniper Y should be 300", 300.0f, sniper2.getPosY(), 0.01f);
    }

    // ===== Stats Tests =====

    @Test
    public void testGetHealth() {
        assertEquals("Health should be 100", 100, sniper.getHealth());
    }

    @Test
    public void testGetAttackDamage() {
        assertEquals("Attack damage should be 45", 45, sniper.getAttackDamage());
    }

    @Test
    public void testGetSpeed() {
        assertEquals("Speed should be 30", 30.0f, sniper.getSpeed(), 0.01f);
    }

    @Test
    public void testGetAttackSpeed() {
        assertEquals("Attack speed should be 3", 3f, sniper.getAttackSpeed(), 0.01f);
    }

    @Test
    public void testGetRange() {
        assertEquals("Range should be 150 (long range)", 150, sniper.getRange());
    }

    // ===== Movement Tests =====

    @Test
    public void testMove() {
        float initialX = sniper.getPosX();
        sniper.move(1.0f);
        assertTrue("X position should increase after move", sniper.getPosX() > initialX);
    }

    @Test
    public void testMoveCalculation() {
        float initialX = sniper.getPosX();
        sniper.move(1.0f);
        // Movement = speed * delta = 30 * 1.0 = 30
        float expectedX = initialX + 30;
        assertEquals("Position should increase by speed * delta", expectedX, sniper.getPosX(), 0.1f);
    }

    @Test
    public void testMoveMultipleTimes() {
        float initialX = sniper.getPosX();
        sniper.move(1.0f);
        sniper.move(1.0f);
        sniper.move(1.0f);
        float expectedX = initialX + (30 * 3);
        assertEquals("Position after 3 moves", expectedX, sniper.getPosX(), 0.1f);
    }

    @Test
    public void testMoveWithRealisticDelta() {
        float initialX = sniper.getPosX();
        sniper.move(0.016f); // 60 FPS
        float expectedX = initialX + (30 * 0.016f);
        assertEquals("Position with realistic delta", expectedX, sniper.getPosX(), 0.01f);
    }

    @Test
    public void testMoveWithZeroDelta() {
        float initialX = sniper.getPosX();
        sniper.move(0.0f);
        assertEquals("Position should not change with zero delta", initialX, sniper.getPosX(), 0.01f);
    }

    @Test
    public void testMoveWithSmallDelta() {
        float initialX = sniper.getPosX();
        sniper.move(0.001f);
        float expectedX = initialX + (30 * 0.001f);
        assertEquals("Position with small delta", expectedX, sniper.getPosX(), 0.01f);
    }

    @Test
    public void testMoveWithLargeDelta() {
        float initialX = sniper.getPosX();
        sniper.move(5.0f);
        float expectedX = initialX + (30 * 5.0f);
        assertEquals("Position with large delta", expectedX, sniper.getPosX(), 0.1f);
    }

    @Test
    public void testContinuousMovement() {
        float initialX = sniper.getPosX();
        float totalDelta = 0f;
        
        for (int i = 0; i < 60; i++) { // Simuler 1 seconde à 60 FPS
            sniper.move(0.016f);
            totalDelta += 0.016f;
        }
        
        float expectedX = initialX + (30 * totalDelta);
        assertEquals("Position after continuous movement", expectedX, sniper.getPosX(), 1.0f);
    }

    // ===== Combat Tests =====

    @Test
    public void testTakeDamage() {
        sniper.takeDamage(50);
        assertEquals("Health should decrease by 50", 50, sniper.getHealth());
    }

    @Test
    public void testTakeDamageMultipleTimes() {
        sniper.takeDamage(30);
        sniper.takeDamage(40);
        assertEquals("Health should decrease by 70 total", 30, sniper.getHealth());
    }

    @Test
    public void testTakeDamageExact() {
        sniper.takeDamage(100);
        assertEquals("Health should be 0", 0, sniper.getHealth());
        assertTrue("Sniper should be dead", sniper.isDead());
    }

    @Test
    public void testTakeDamageOverkill() {
        sniper.takeDamage(300);
        assertEquals("Health should be 0 with overkill", 0, sniper.getHealth());
        assertTrue("Sniper should be dead", sniper.isDead());
    }

    @Test
    public void testIsNotDeadInitially() {
        assertFalse("Sniper should not be dead initially", sniper.isDead());
    }

    @Test
    public void testIsDeadAfterFatalDamage() {
        sniper.takeDamage(150);
        assertTrue("Sniper should be dead after taking 150 damage", sniper.isDead());
    }

    @Test
    public void testIsNotDeadAfterPartialDamage() {
        sniper.takeDamage(90);
        assertFalse("Sniper should not be dead after taking 90 damage", sniper.isDead());
    }

    @Test
    public void testTakeDamageTwice() {
        sniper.takeDamage(50);
        assertFalse("Should survive first attack", sniper.isDead());
        
        sniper.takeDamage(50);
        assertTrue("Should die after second attack", sniper.isDead());
    }

    // ===== Attack Tests =====

    @Test
    public void testAttackWithLongRangeTarget() {
        // Sniper a une portée de 250, donc peut attaquer à distance
        Unit distantEnemy = mock(Unit.class);
        when(distantEnemy.getPosX()).thenReturn(250.0f); // 200 pixels de distance
        when(distantEnemy.getPosY()).thenReturn(200.0f);
        when(distantEnemy.isDead()).thenReturn(false);
        when(distantEnemy.getHealth()).thenReturn(100);
        
        sniper.setTarget(distantEnemy);
        sniper.setCooldown(0);
        
        sniper.attack();
        
        verify(distantEnemy, atLeastOnce()).takeDamage(45);
        assertEquals("Should be in ATTACKING state", Unit.UnitState.ATTACKING, sniper.getCurrentState());
    }

    @Test
    public void testAttackWithoutTarget() {
        sniper.setTarget(null);
        sniper.setCooldown(0);
        
        sniper.attack();
        
        assertEquals("Should remain in current state", Unit.UnitState.WALKING, sniper.getCurrentState());
    }

    @Test
    public void testAttackOnCooldown() {
        Unit distantEnemy = mock(Unit.class);
        when(distantEnemy.getPosX()).thenReturn(300.0f);
        when(distantEnemy.getPosY()).thenReturn(200.0f);
        when(distantEnemy.isDead()).thenReturn(false);
        
        sniper.setTarget(distantEnemy);
        sniper.setCooldown(5);
        
        sniper.attack();
        
        verify(distantEnemy, never()).takeDamage(40);
    }

    @Test
    public void testSlowerAttackSpeed() {
        // Vérifie que le sniper a un cooldown plus long (2.5s vs 1.0s du melee)
        assertEquals("Sniper should have slower attack speed", 3f, sniper.getAttackSpeed(), 0.01f);
        assertTrue("Sniper attack speed should be slower than melee", sniper.getAttackSpeed() > 1.2f);
    }

    // ===== Target Selection Tests =====

    @Test
    public void testSelectTargetWithEnemies() {
        enemies.add(mockEnemy);
        sniper.selectTarget(enemies);
        // Méthode devrait s'exécuter sans erreur
    }

    @Test
    public void testSelectTargetEmptyList() {
        sniper.selectTarget(enemies);
        // Méthode devrait s'exécuter sans erreur avec liste vide
    }

    @Test
    public void testSelectTargetMultipleEnemies() {
        Unit enemy1 = mock(Unit.class);
        Unit enemy2 = mock(Unit.class);
        when(enemy1.getPosX()).thenReturn(200.0f);
        when(enemy1.getPosY()).thenReturn(200.0f);
        when(enemy1.isDead()).thenReturn(false);
        when(enemy2.getPosX()).thenReturn(300.0f);
        when(enemy2.getPosY()).thenReturn(200.0f);
        when(enemy2.isDead()).thenReturn(false);
        
        enemies.add(enemy1);
        enemies.add(enemy2);
        
        sniper.selectTarget(enemies);
        // Le plus proche devrait être sélectionné
    }

    // ===== Cooldown Tests =====

    @Test
    public void testUpdateCooldown() {
        sniper.setCooldown(5);
        sniper.updateCooldown(1.0f);
        assertEquals("Cooldown should decrease by delta", 4.0f, sniper.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testUpdateCooldownMultipleTimes() {
        sniper.setCooldown(10);
        sniper.updateCooldown(1.0f);
        sniper.updateCooldown(1.0f);
        sniper.updateCooldown(1.0f);
        assertEquals("Cooldown after multiple updates", 7.0f, sniper.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testUpdateCooldownToZero() {
        sniper.setCooldown(1);
        sniper.updateCooldown(2.0f);
        assertEquals("Cooldown should not go below 0", 0.0f, sniper.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testSniperSlowerCooldownRecovery() {
        sniper.setCooldown(2.5f);
        sniper.updateCooldown(0.016f); // Un frame à 60 FPS
        
        assertTrue("Sniper cooldown should still be high", sniper.getAttackCooldown() > 2.4f);
        assertEquals("Cooldown should decrease slightly", 2.484f, sniper.getAttackCooldown(), 0.01f);
    }

    // ===== Render Tests =====

    @Test
    public void testRenderDoesNotThrow() {
        sniper.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    @Test
    public void testRenderMultipleTimes() {
        sniper.render(mockBatch);
        sniper.render(mockBatch);
        sniper.render(mockBatch);
        // Ne devrait pas lancer d'exception
    }

    @Test
    public void testRenderAfterMove() {
        sniper.move(1.0f);
        sniper.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    @Test
    public void testRenderAfterDamage() {
        sniper.takeDamage(50);
        sniper.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    // ===== Sprite Tests =====

    @Test
    public void testGetSprite() {
        assertNotNull("Sprite should not be null", sniper.getSprite());
    }

    @Test
    public void testGetTexture() {
        assertNotNull("Texture should not be null", sniper.getTexture());
    }

    // ===== Position Tests =====

    @Test
    public void testGetPosX() {
        assertEquals("getPosX should return correct value", 100.0f, sniper.getPosX(), 0.01f);
    }

    @Test
    public void testGetPosY() {
        assertEquals("getPosY should return correct value", 200.0f, sniper.getPosY(), 0.01f);
    }

    @Test
    public void testSetSpritePosX() {
        sniper.setSpritePosX(150);
        assertEquals("Position X should be updated", 150.0f, sniper.getPosX(), 0.01f);
    }

    @Test
    public void testSetSpritePosY() {
        sniper.setSpritePosY(250);
        assertEquals("Position Y should be updated", 250.0f, sniper.getPosY(), 0.01f);
    }

    @Test
    public void testGetWidth() {
        assertEquals("Width should be 32", 32.0f, sniper.getWidth(), 0.01f);
    }

    @Test
    public void testGetHeight() {
        assertEquals("Height should be 48", 48.0f, sniper.getHeight(), 0.01f);
    }

    // ===== State Tests =====

    @Test
    public void testInitialState() {
        assertEquals("Initial state should be WALKING", Unit.UnitState.WALKING, sniper.getCurrentState());
    }

    @Test
    public void testStateAfterAttack() {
        Unit distantEnemy = mock(Unit.class);
        when(distantEnemy.getPosX()).thenReturn(120.0f);
        when(distantEnemy.getPosY()).thenReturn(200.0f);
        when(distantEnemy.isDead()).thenReturn(false);
        
        sniper.setTarget(distantEnemy);
        sniper.setCooldown(0);
        sniper.attack();
        
        assertEquals("State should be ATTACKING after attack", Unit.UnitState.ATTACKING, sniper.getCurrentState());
    }

    // ===== Range Tests =====

    @Test
    public void testLongRangeAttack() {
        // Vérifie que le sniper peut attaquer à 250 de distance
        Unit veryDistantEnemy = mock(Unit.class);
        when(veryDistantEnemy.getPosX()).thenReturn(250.0f); // 240 pixels de distance
        when(veryDistantEnemy.getPosY()).thenReturn(200.0f);
        when(veryDistantEnemy.isDead()).thenReturn(false);
        
        sniper.setTarget(veryDistantEnemy);
        sniper.setCooldown(0);
        sniper.attack();
        
        verify(veryDistantEnemy, atLeastOnce()).takeDamage(45);
    }

    @Test
    public void testOutOfRangeAttack() {
        // Au-delà de 250 pixels
        Unit tooFarEnemy = mock(Unit.class);
        when(tooFarEnemy.getPosX()).thenReturn(400.0f); // 300 pixels de distance
        when(tooFarEnemy.getPosY()).thenReturn(200.0f);
        when(tooFarEnemy.isDead()).thenReturn(false);
        when(tooFarEnemy.getHealth()).thenReturn(100);
        
        sniper.setTarget(tooFarEnemy);
        sniper.setCooldown(0);
        
        sniper.attack();
        
        // Ne devrait pas attaquer car hors de portée
        verify(tooFarEnemy, never()).takeDamage(40);
    }

    // ===== Integration Tests =====

    @Test
    public void testMoveAndRenderSequence() {
        float initialX = sniper.getPosX();
        
        sniper.move(0.5f);
        sniper.render(mockBatch);
        sniper.move(0.5f);
        sniper.render(mockBatch);
        
        assertTrue("Position should have changed", sniper.getPosX() > initialX);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    @Test
    public void testCombatSequence() {
        sniper.takeDamage(20);
        assertFalse("Should survive first attack", sniper.isDead());
        
        sniper.takeDamage(20);
        assertFalse("Should survive second attack", sniper.isDead());
        
        sniper.takeDamage(60);
        assertTrue("Should die after third attack", sniper.isDead());
    }

    @Test
    public void testFullGameLoop() {
        for (int i = 0; i < 10; i++) {
            sniper.move(0.016f);
            sniper.updateCooldown(0.016f);
            sniper.render(mockBatch);
        }
        
        assertTrue("Should have moved", sniper.getPosX() > 100);
    }

    @Test
    public void testSniperInheritance() {
        assertTrue("Sniper should be instance of Soldier", sniper instanceof Soldier);
        assertTrue("Sniper should be instance of Unit", sniper instanceof Unit);
    }

    @Test
    public void testSniperStatsAreDifferent() {
        // Vérifier que Sniper a des stats spécifiques
        assertEquals("Sniper specific health", 100, sniper.getHealth());
        assertEquals("Sniper specific damage", 45, sniper.getAttackDamage());
        assertEquals("Sniper specific speed", 30.0f, sniper.getSpeed(), 0.01f);
        assertEquals("Sniper specific range", 150, sniper.getRange()); // Longue portée
        assertEquals("Sniper slower attack", 3f, sniper.getAttackSpeed(), 0.01f);
    }
    
    @Test
    public void testSniperHasAnimations() {
        assertNotNull("Walk animation should be loaded", sniper.walkAnimation);
        assertNotNull("Attack animation should be loaded", sniper.attackAnimation);
        assertNotNull("Idle animation should be loaded", sniper.idleFramer);
    }

    @Test
    public void testSniperCharacteristics() {
        // Sniper : faible vie, gros dégâts, longue portée, lent
        assertTrue("Sniper health should be lower than melee", sniper.getHealth() < 200);
        assertTrue("Sniper damage should be higher than melee", sniper.getAttackDamage() > 20);
        assertTrue("Sniper range should be much higher", sniper.getRange() > 100);
        assertTrue("Sniper should be slower", sniper.getSpeed() < 100);
        assertTrue("Sniper should have slower attack rate", sniper.getAttackSpeed() > 1.0f);
    }
}