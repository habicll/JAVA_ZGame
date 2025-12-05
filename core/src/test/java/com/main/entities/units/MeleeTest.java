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

public class MeleeTest {

    private static Application application;
    private Melee melee;
    
    @Mock
    private SpriteBatch mockBatch;
    
    @Mock
    private GL20 mockGL;
    
    @Mock
    private Graphics mockGraphics;
    
    @Mock
    private Unit mockEnemy;
    
    @Mock
    private Base mockBase;
    
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
        
        melee = new Melee(100, 200, mockBase);
        enemies = new ArrayList<>();
        
        when(mockEnemy.getPosX()).thenReturn(500.0f);
        when(mockEnemy.getPosY()).thenReturn(200.0f);
        when(mockEnemy.isDead()).thenReturn(false);
        when(mockEnemy.getHealth()).thenReturn(100);
    }

    // ===== Constructor Tests =====

    @Test
    public void testConstructor() {
        assertNotNull("Melee should not be null", melee);
        assertEquals("Initial X position should be 100", 100.0f, melee.getPosX(), 0.01f);
        assertEquals("Initial Y position should be 200", 200.0f, melee.getPosY(), 0.01f);
    }

    @Test
    public void testConstructorInitializesStats() {
        assertEquals("Melee health should be 140", 140, melee.getHealth());
        assertEquals("Melee attack damage should be 15", 15, melee.getAttackDamage());
        assertEquals("Melee speed should be 60", 60.0f, melee.getSpeed(), 0.01f);
        assertEquals("Melee attack speed should be 1.2", 1.2f, melee.getAttackSpeed(), 0.01f);
        assertEquals("Melee range should be 50", 50, melee.getRange());
    }

    @Test
    public void testConstructorWithDifferentPositions() {
        Melee melee1 = new Melee(0, 0, mockBase);
        Melee melee2 = new Melee(500, 300, mockBase);
        
        assertEquals("First melee X should be 0", 0.0f, melee1.getPosX(), 0.01f);
        assertEquals("Second melee X should be 500", 500.0f, melee2.getPosX(), 0.01f);
        assertEquals("First melee Y should be 0", 0.0f, melee1.getPosY(), 0.01f);
        assertEquals("Second melee Y should be 300", 300.0f, melee2.getPosY(), 0.01f);
    }

    // ===== Stats Tests =====

    @Test
    public void testGetHealth() {
        assertEquals("Health should be 140", 140, melee.getHealth());
    }

    @Test
    public void testGetAttackDamage() {
        assertEquals("Attack damage should be 15", 15, melee.getAttackDamage());
    }

    @Test
    public void testGetSpeed() {
        assertEquals("Speed should be 60", 60.0f, melee.getSpeed(), 0.01f);
    }

    @Test
    public void testGetAttackSpeed() {
        assertEquals("Attack speed should be 1.2", 1.2f, melee.getAttackSpeed(), 0.01f);
    }

    @Test
    public void testGetRange() {
        assertEquals("Range should be 50 (melee range)", 50, melee.getRange());
    }

    // ===== Movement Tests =====

    @Test
    public void testMove() {
        float initialX = melee.getPosX();
        melee.move(1.0f);
        assertTrue("X position should increase after move", melee.getPosX() > initialX);
    }

    @Test
    public void testMoveCalculation() {
        float initialX = melee.getPosX();
        melee.move(1.0f);
        // Movement = speed * delta = 100 * 1.0 = 100
        float expectedX = initialX + 60;
        assertEquals("Position should increase by speed * delta", expectedX, melee.getPosX(), 0.1f);
    }

    @Test
    public void testMoveMultipleTimes() {
        float initialX = melee.getPosX();
        melee.move(1.0f);
        melee.move(1.0f);
        melee.move(1.0f);
        float expectedX = initialX + (60 * 3);
        assertEquals("Position after 3 moves", expectedX, melee.getPosX(), 0.1f);
    }

    @Test
    public void testMoveWithRealisticDelta() {
        float initialX = melee.getPosX();
        melee.move(0.016f); // 60 FPS
        float expectedX = initialX + (60 * 0.016f);
        assertEquals("Position with realistic delta", expectedX, melee.getPosX(), 0.01f);
    }

    @Test
    public void testMoveWithZeroDelta() {
        float initialX = melee.getPosX();
        melee.move(0.0f);
        assertEquals("Position should not change with zero delta", initialX, melee.getPosX(), 0.01f);
    }

    @Test
    public void testMoveWithSmallDelta() {
        float initialX = melee.getPosX();
        melee.move(0.001f);
        float expectedX = initialX + (60 * 0.001f);
        assertEquals("Position with small delta", expectedX, melee.getPosX(), 0.01f);
    }

    @Test
    public void testMoveWithLargeDelta() {
        float initialX = melee.getPosX();
        melee.move(5.0f);
        float expectedX = initialX + (60 * 5.0f);
        assertEquals("Position with large delta", expectedX, melee.getPosX(), 0.1f);
    }

    @Test
    public void testContinuousMovement() {
        float initialX = melee.getPosX();
        float totalDelta = 0f;
        
        for (int i = 0; i < 60; i++) { // Simuler 1 seconde à 60 FPS
            melee.move(0.016f);
            totalDelta += 0.016f;
        }
        
        float expectedX = initialX + (60 * totalDelta);
        assertEquals("Position after continuous movement", expectedX, melee.getPosX(), 1.0f);
    }

    // ===== Combat Tests =====

    @Test
    public void testTakeDamage() {
        melee.takeDamage(50);
        assertEquals("Health should decrease by 50", 90, melee.getHealth());
    }

    @Test
    public void testTakeDamageMultipleTimes() {
        melee.takeDamage(30);
        melee.takeDamage(40);
        assertEquals("Health should decrease by 70 total", 70, melee.getHealth());
    }

    @Test
    public void testTakeDamageExact() {
        melee.takeDamage(140);
        assertEquals("Health should be 0", 0, melee.getHealth());
        assertTrue("Melee should be dead", melee.isDead());
    }

    @Test
    public void testTakeDamageOverkill() {
        melee.takeDamage(300);
        assertEquals("Health should be 0 with overkill", 0, melee.getHealth());
        assertTrue("Melee should be dead", melee.isDead());
    }

    @Test
    public void testIsNotDeadInitially() {
        assertFalse("Melee should not be dead initially", melee.isDead());
    }

    @Test
    public void testIsDeadAfterFatalDamage() {
        melee.takeDamage(200);
        assertTrue("Melee should be dead after taking 200 damage", melee.isDead());
    }

    @Test
    public void testIsNotDeadAfterPartialDamage() {
        melee.takeDamage(100);
        assertFalse("Melee should not be dead after taking 100 damage", melee.isDead());
    }

    // ===== Attack Tests =====

    @Test
    public void testAttackWithTarget() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(120.0f);
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        when(closeEnemy.getHealth()).thenReturn(100);
        
        melee.setTarget(closeEnemy);
        melee.setCooldown(0);
        
        melee.attack();
        
        verify(closeEnemy, atLeastOnce()).takeDamage(15);
        assertEquals("Should be in ATTACKING state", Unit.UnitState.ATTACKING, melee.getCurrentState());
    }

    @Test
    public void testAttackWithoutTarget() {
        melee.setTarget(null);
        melee.setCooldown(0);
        
        melee.attack();
        
        // Ne devrait pas changer d'état sans cible
        assertEquals("Should remain in current state", Unit.UnitState.WALKING, melee.getCurrentState());
    }

    @Test
    public void testAttackOnCooldown() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(120.0f);
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        when(closeEnemy.getHealth()).thenReturn(100);
        
        melee.setTarget(closeEnemy);
        melee.setCooldown(5);
        
        melee.attack();
        
        // Ne devrait pas attaquer si en cooldown
        verify(closeEnemy, never()).takeDamage(20);
    }

    // ===== Target Selection Tests =====

    @Test
    public void testSelectTargetWithEnemies() {
        enemies.add(mockEnemy);
        melee.selectTarget(enemies);
        // Méthode devrait s'exécuter sans erreur
    }

    @Test
    public void testSelectTargetEmptyList() {
        melee.selectTarget(enemies);
        // Méthode devrait s'exécuter sans erreur avec liste vide
    }

    @Test
    public void testSelectTargetMultipleEnemies() {
        Unit enemy1 = mock(Unit.class);
        Unit enemy2 = mock(Unit.class);
        when(enemy1.getPosX()).thenReturn(150.0f);
        when(enemy1.getPosY()).thenReturn(200.0f);
        when(enemy1.isDead()).thenReturn(false);
        when(enemy2.getPosX()).thenReturn(200.0f);
        when(enemy2.getPosY()).thenReturn(200.0f);
        when(enemy2.isDead()).thenReturn(false);
        
        enemies.add(enemy1);
        enemies.add(enemy2);
        
        melee.selectTarget(enemies);
        // Le plus proche devrait être sélectionné
    }

    // ===== Cooldown Tests =====

    @Test
    public void testUpdateCooldown() {
        melee.setCooldown(5);
        melee.updateCooldown(1.0f);
        assertEquals("Cooldown should decrease by delta", 4.0f, melee.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testUpdateCooldownMultipleTimes() {
        melee.setCooldown(10);
        melee.updateCooldown(1.0f);
        melee.updateCooldown(1.0f);
        melee.updateCooldown(1.0f);
        assertEquals("Cooldown after multiple updates", 7.0f, melee.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testUpdateCooldownToZero() {
        melee.setCooldown(1);
        melee.updateCooldown(2.0f);
        assertEquals("Cooldown should not go below 0", 0.0f, melee.getAttackCooldown(), 0.01f);
    }

    // ===== Render Tests =====

    @Test
    public void testRenderDoesNotThrow() {
        melee.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    @Test
    public void testRenderMultipleTimes() {
        melee.render(mockBatch);
        melee.render(mockBatch);
        melee.render(mockBatch);
        // Ne devrait pas lancer d'exception
    }

    @Test
    public void testRenderAfterMove() {
        melee.move(1.0f);
        melee.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    @Test
    public void testRenderAfterDamage() {
        melee.takeDamage(50);
        melee.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    @Test
    public void testRenderDuringAttack() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(120.0f);
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        
        melee.setTarget(closeEnemy);
        melee.setCooldown(0);
        melee.attack();
        
        melee.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    // ===== Sprite Tests =====

    @Test
    public void testGetSprite() {
        assertNotNull("Sprite should not be null", melee.getSprite());
    }

    @Test
    public void testGetTexture() {
        assertNotNull("Texture should not be null", melee.getTexture());
    }

    // ===== Position Tests =====

    @Test
    public void testGetPosX() {
        assertEquals("getPosX should return correct value", 100.0f, melee.getPosX(), 0.01f);
    }

    @Test
    public void testGetPosY() {
        assertEquals("getPosY should return correct value", 200.0f, melee.getPosY(), 0.01f);
    }

    @Test
    public void testSetSpritePosX() {
        melee.setSpritePosX(150);
        assertEquals("Position X should be updated", 150.0f, melee.getPosX(), 0.01f);
    }

    @Test
    public void testSetSpritePosY() {
        melee.setSpritePosY(250);
        assertEquals("Position Y should be updated", 250.0f, melee.getPosY(), 0.01f);
    }

    @Test
    public void testGetWidth() {
        assertEquals("Width should be 32", 32.0f, melee.getWidth(), 0.01f);
    }

    @Test
    public void testGetHeight() {
        assertEquals("Height should be 48", 48.0f, melee.getHeight(), 0.01f);
    }

    // ===== State Tests =====

    @Test
    public void testInitialState() {
        assertEquals("Initial state should be WALKING", Unit.UnitState.WALKING, melee.getCurrentState());
    }

    @Test
    public void testStateAfterAttack() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(120.0f);
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        
        melee.setTarget(closeEnemy);
        melee.setCooldown(0);
        melee.attack();
        
        assertEquals("State should be ATTACKING after attack", Unit.UnitState.ATTACKING, melee.getCurrentState());
    }

    // ===== Integration Tests =====

    @Test
    public void testMoveAndRenderSequence() {
        float initialX = melee.getPosX();
        
        melee.move(0.5f);
        melee.render(mockBatch);
        melee.move(0.5f);
        melee.render(mockBatch);
        
        assertTrue("Position should have changed", melee.getPosX() > initialX);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    @Test
    public void testCombatSequence() {
        melee.takeDamage(50);
        assertFalse("Should survive first attack", melee.isDead());
        
        melee.takeDamage(50);
        assertFalse("Should survive second attack", melee.isDead());
        
        melee.takeDamage(50);
        assertTrue("Should die after third attack", melee.isDead());
    }

    @Test
    public void testFullGameLoop() {
        for (int i = 0; i < 10; i++) {
            melee.move(0.016f);
            melee.updateCooldown(0.016f);
            melee.render(mockBatch);
        }
        
        assertTrue("Should have moved", melee.getPosX() > 100);
    }

    @Test
    public void testFullCombatLoop() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(120.0f);
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        
        melee.setTarget(closeEnemy);
        
        for (int i = 0; i < 100; i++) {
            melee.move(0.016f);
            melee.updateCooldown(0.016f);
            if (melee.getAttackCooldown() <= 0) {
                melee.attack();
            }
            melee.render(mockBatch);
        }
        
        // Au moins une attaque devrait avoir été effectuée
        verify(closeEnemy, atLeastOnce()).takeDamage(15);
    }

    @Test
    public void testMeleeInheritance() {
        assertTrue("Melee should be instance of Soldier", melee instanceof Soldier);
        assertTrue("Melee should be instance of Unit", melee instanceof Unit);
    }

    @Test
    public void testMeleeStatsAreDifferent() {
        // Vérifier que Melee a des stats spécifiques
        assertEquals("Melee specific health", 140, melee.getHealth());
        assertEquals("Melee specific damage", 15, melee.getAttackDamage());
        assertEquals("Melee specific speed", 60.0f, melee.getSpeed(), 0.01f);
        assertEquals("Melee specific range", 50, melee.getRange()); // Courte portée
    }

    @Test
    public void testMeleeHasAnimations() {
        assertNotNull("Walk animation should be loaded", melee.walkAnimation);
        assertNotNull("Attack animation should be loaded", melee.attackAnimation);
        assertNotNull("Idle frame should be loaded", melee.idleFrame);
    }

    @Test
    public void testGetAttackAnimationDuration(){
        melee.attackAnimation = null;
        assertEquals("Attack anim null", 0.5f, melee.getAttackAnimationDuration(), 0.01f);
    }
}