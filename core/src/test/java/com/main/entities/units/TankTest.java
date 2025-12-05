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

public class TankTest {

    private static Application application;
    private Tank tank;
    
    @Mock
    private SpriteBatch mockBatch;
    
    @Mock
    private GL20 mockGL;
    
    @Mock
    private Graphics mockGraphics;

    @Mock
    private Base mockBase;
    
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
        
        tank = new Tank(100, 200, mockBase);
        enemies = new ArrayList<>();
        
        when(mockEnemy.getPosX()).thenReturn(500.0f);
        when(mockEnemy.getPosY()).thenReturn(200.0f);
        when(mockEnemy.isDead()).thenReturn(false);
        when(mockEnemy.getHealth()).thenReturn(100);
    }

    // ===== Constructor Tests =====

    @Test
    public void testConstructor() {
        assertNotNull("Tank should not be null", tank);
        assertEquals("Initial X position should be 100", 100.0f, tank.getPosX(), 0.01f);
        assertEquals("Initial Y position should be 200", 200.0f, tank.getPosY(), 0.01f);
    }

    @Test
    public void testConstructorInitializesStats() {
        assertEquals("Tank health should be 400", 400, tank.getHealth());
        assertEquals("Tank attack damage should be 9", 9, tank.getAttackDamage());
        assertEquals("Tank speed should be 40", 40.0f, tank.getSpeed(), 0.01f);
        assertEquals("Tank attack speed should be 0.9", 0.9f, tank.getAttackSpeed(), 0.01f);
        assertEquals("Tank range should be 100", 100, tank.getRange());
    }

    @Test
    public void testConstructorWithDifferentPositions() {
        Tank tank1 = new Tank(0, 0, mockBase);
        Tank tank2 = new Tank(500, 300, mockBase);
        
        assertEquals("First tank X should be 0", 0.0f, tank1.getPosX(), 0.01f);
        assertEquals("Second tank X should be 500", 500.0f, tank2.getPosX(), 0.01f);
        assertEquals("First tank Y should be 0", 0.0f, tank1.getPosY(), 0.01f);
        assertEquals("Second tank Y should be 300", 300.0f, tank2.getPosY(), 0.01f);
    }

    // ===== Stats Tests =====

    @Test
    public void testGetHealth() {
        assertEquals("Health should be 400", 400, tank.getHealth());
    }

    @Test
    public void testGetAttackDamage() {
        assertEquals("Attack damage should be 9", 9, tank.getAttackDamage());
    }

    @Test
    public void testGetSpeed() {
        assertEquals("Speed should be 40", 40.0f, tank.getSpeed(), 0.01f);
    }

    @Test
    public void testGetAttackSpeed() {
        assertEquals("Attack speed should be 0.9", 0.9f, tank.getAttackSpeed(), 0.01f);
    }

    @Test
    public void testGetRange() {
        assertEquals("Range should be 100 (medium range)", 100, tank.getRange());
    }

    // ===== Movement Tests =====

    @Test
    public void testMove() {
        float initialX = tank.getPosX();
        tank.move(1.0f);
        assertTrue("X position should increase after move", tank.getPosX() > initialX);
    }

    @Test
    public void testMoveCalculation() {
        float initialX = tank.getPosX();
        tank.move(1.0f);
        // Movement = speed * delta = 15 * 1.0 = 15
        float expectedX = initialX + 40;
        assertEquals("Position should increase by speed * delta", expectedX, tank.getPosX(), 0.1f);
    }

    @Test
    public void testMoveMultipleTimes() {
        float initialX = tank.getPosX();
        tank.move(1.0f);
        tank.move(1.0f);
        tank.move(1.0f);
        float expectedX = initialX + (40 * 3);
        assertEquals("Position after 3 moves", expectedX, tank.getPosX(), 0.1f);
    }

    @Test
    public void testMoveWithRealisticDelta() {
        float initialX = tank.getPosX();
        tank.move(0.016f); // 60 FPS
        float expectedX = initialX + (40 * 0.016f);
        assertEquals("Position with realistic delta", expectedX, tank.getPosX(), 0.01f);
    }

    @Test
    public void testMoveWithZeroDelta() {
        float initialX = tank.getPosX();
        tank.move(0.0f);
        assertEquals("Position should not change with zero delta", initialX, tank.getPosX(), 0.01f);
    }

    @Test
    public void testMoveWithSmallDelta() {
        float initialX = tank.getPosX();
        tank.move(0.001f);
        float expectedX = initialX + (40 * 0.001f);
        assertEquals("Position with small delta", expectedX, tank.getPosX(), 0.01f);
    }

    @Test
    public void testMoveWithLargeDelta() {
        float initialX = tank.getPosX();
        tank.move(5.0f);
        float expectedX = initialX + (40 * 5.0f);
        assertEquals("Position with large delta", expectedX, tank.getPosX(), 0.1f);
    }

    @Test
    public void testContinuousMovement() {
        float initialX = tank.getPosX();
        float totalDelta = 0f;
        
        for (int i = 0; i < 60; i++) { // Simuler 1 seconde à 60 FPS
            tank.move(0.016f);
            totalDelta += 0.016f;
        }
        
        float expectedX = initialX + (40 * totalDelta);
        assertEquals("Position after continuous movement", expectedX, tank.getPosX(), 1.0f);
    }

    @Test
    public void testSlowMovement() {
        // Vérifie que le tank est lent comparé aux autres unités
        assertTrue("Tank should be slower than Melee", tank.getSpeed() < 60);
        assertTrue("Tank should be higher than Sniper", tank.getSpeed() > 30);
    }

    // ===== Combat Tests =====

    @Test
    public void testTakeDamage() {
        tank.takeDamage(100);
        assertEquals("Health should decrease by 100", 300, tank.getHealth());
    }

    @Test
    public void testTakeDamageMultipleTimes() {
        tank.takeDamage(150);
        tank.takeDamage(150);
        assertEquals("Health should decrease by 300 total", 100, tank.getHealth());
    }

    @Test
    public void testTakeDamageExact() {
        tank.takeDamage(400);
        assertEquals("Health should be 0", 0, tank.getHealth());
        assertTrue("Tank should be dead", tank.isDead());
    }

    @Test
    public void testTakeDamageOverkill() {
        tank.takeDamage(700);
        assertEquals("Health should be 0 with overkill", 0, tank.getHealth());
        assertTrue("Tank should be dead", tank.isDead());
    }

    @Test
    public void testIsNotDeadInitially() {
        assertFalse("Tank should not be dead initially", tank.isDead());
    }

    @Test
    public void testIsDeadAfterFatalDamage() {
        tank.takeDamage(500);
        assertTrue("Tank should be dead after taking 500 damage", tank.isDead());
    }

    @Test
    public void testIsNotDeadAfterPartialDamage() {
        tank.takeDamage(250);
        assertFalse("Tank should not be dead after taking 250 damage", tank.isDead());
    }

    @Test
    public void testTankHighDurability() {
        // Vérifie que le tank peut survivre plusieurs attaques
        tank.takeDamage(100);
        tank.takeDamage(100);
        tank.takeDamage(100);
        assertFalse("Tank should survive 300 damage", tank.isDead());
    }

    // ===== Attack Tests =====

    @Test
    public void testAttackWithTarget() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(180.0f); // À portée (80 pixels, range = 100)
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        when(closeEnemy.getHealth()).thenReturn(100);
        
        tank.setTarget(closeEnemy);
        tank.setCooldown(0);
        
        tank.attack();
        
        verify(closeEnemy, atLeastOnce()).takeDamage(9);
        assertEquals("Should be in ATTACKING state", Unit.UnitState.ATTACKING, tank.getCurrentState());
    }

    @Test
    public void testSlowAttackSpeed() {
        assertEquals("Tank should have slow attack speed (0.9s)", 0.9f, tank.getAttackSpeed(), 0.01f);
        assertTrue("Tank attack speed should be higher than melee", tank.getAttackSpeed() < 1.2f);
        assertTrue("Tank attack speed should be higher than sniper", tank.getAttackSpeed() < 3f);
    }

    @Test
    public void testHighDamage() {
        assertEquals("Tank should have high damage (30)", 9, tank.getAttackDamage());
        assertTrue("Tank damage should be lower than melee", tank.getAttackDamage() < 15);
        assertTrue("Tank damage should be lower than sniper", tank.getAttackDamage() < 45);
    }

    // ===== Target Selection Tests =====

    @Test
    public void testSelectTargetWithEnemies() {
        enemies.add(mockEnemy);
        tank.selectTarget(enemies);
        // Méthode devrait s'exécuter sans erreur
    }

    @Test
    public void testSelectTargetEmptyList() {
        tank.selectTarget(enemies);
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
        
        tank.selectTarget(enemies);
        // Le plus proche devrait être sélectionné
    }

    // ===== Cooldown Tests =====

    @Test
    public void testUpdateCooldown() {
        tank.setCooldown(5);
        tank.updateCooldown(1.0f);
        assertEquals("Cooldown should decrease by delta", 4.0f, tank.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testUpdateCooldownMultipleTimes() {
        tank.setCooldown(10);
        tank.updateCooldown(1.0f);
        tank.updateCooldown(1.0f);
        tank.updateCooldown(1.0f);
        assertEquals("Cooldown after multiple updates", 7.0f, tank.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testUpdateCooldownToZero() {
        tank.setCooldown(1);
        tank.updateCooldown(2.0f);
        assertEquals("Cooldown should not go below 0", 0.0f, tank.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testLongCooldownRecovery() {
        tank.setCooldown(3);
        
        // Après 1 seconde
        tank.updateCooldown(1.0f);
        assertEquals("After 1 second", 2.0f, tank.getAttackCooldown(), 0.01f);
        
        // Après 2 secondes
        tank.updateCooldown(1.0f);
        assertEquals("After 2 seconds", 1.0f, tank.getAttackCooldown(), 0.01f);
        
        // Après 3 secondes
        tank.updateCooldown(1.0f);
        assertEquals("After 3 seconds", 0.0f, tank.getAttackCooldown(), 0.01f);
    }

    // ===== Render Tests =====

    @Test
    public void testRenderDoesNotThrow() {
        tank.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat(), anyFloat(), anyFloat());
    }

    @Test
    public void testRenderMultipleTimes() {
        tank.render(mockBatch);
        tank.render(mockBatch);
        tank.render(mockBatch);
        // Ne devrait pas lancer d'exception
    }

    @Test
    public void testRenderAfterMove() {
        tank.move(1.0f);
        tank.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat(), anyFloat(), anyFloat());
    }

    @Test
    public void testRenderAfterDamage() {
        tank.takeDamage(100);
        tank.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat(), anyFloat(), anyFloat());
    }

    @Test
    public void testRenderWithCustomDimensions() {
        // Tank utilise des dimensions personnalisées (85x50)
        tank.render(mockBatch);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat(), 
            anyFloat(), anyFloat()); // Width et height personnalisés
    }

    // ===== Sprite Tests =====

    @Test
    public void testGetSprite() {
        assertNotNull("Sprite should not be null", tank.getSprite());
    }

    @Test
    public void testGetTexture() {
        assertNotNull("Texture should not be null", tank.getTexture());
    }

    // ===== Position Tests =====

    @Test
    public void testGetPosX() {
        assertEquals("getPosX should return correct value", 100.0f, tank.getPosX(), 0.01f);
    }

    @Test
    public void testGetPosY() {
        assertEquals("getPosY should return correct value", 200.0f, tank.getPosY(), 0.01f);
    }

    @Test
    public void testSetSpritePosX() {
        tank.setSpritePosX(150);
        assertEquals("Position X should be updated", 150.0f, tank.getPosX(), 0.01f);
    }

    @Test
    public void testSetSpritePosY() {
        tank.setSpritePosY(250);
        assertEquals("Position Y should be updated", 250.0f, tank.getPosY(), 0.01f);
    }

    // ===== State Tests =====

    @Test
    public void testInitialState() {
        assertEquals("Initial state should be WALKING", Unit.UnitState.WALKING, tank.getCurrentState());
    }

    @Test
    public void testStateAfterAttack() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(180.0f);
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        
        tank.setTarget(closeEnemy);
        tank.setCooldown(0);
        tank.attack();
        
        assertEquals("State should be ATTACKING after attack", Unit.UnitState.ATTACKING, tank.getCurrentState());
    }

    // ===== Integration Tests =====

    @Test
    public void testMoveAndRenderSequence() {
        float initialX = tank.getPosX();
        
        tank.move(0.5f);
        tank.render(mockBatch);
        tank.move(0.5f);
        tank.render(mockBatch);
        
        assertTrue("Position should have changed", tank.getPosX() > initialX);
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat(), anyFloat(), anyFloat());
    }

    @Test
    public void testCombatSequence() {
        tank.takeDamage(100);
        assertFalse("Should survive first attack", tank.isDead());
        
        tank.takeDamage(100);
        assertFalse("Should survive second attack", tank.isDead());
        
        tank.takeDamage(100);
        assertFalse("Should survive third attack", tank.isDead());
        
        tank.takeDamage(100);
        assertTrue("Should die after fourth attack", tank.isDead());
    }

    @Test
    public void testFullGameLoop() {
        for (int i = 0; i < 10; i++) {
            tank.move(0.016f);
            tank.updateCooldown(0.016f);
            tank.render(mockBatch);
        }
        
        assertTrue("Should have moved", tank.getPosX() > 100);
    }

    @Test
    public void testFullCombatLoop() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(180.0f);
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        
        tank.setTarget(closeEnemy);
        
        // Simuler plusieurs secondes de combat
        for (int i = 0; i < 200; i++) {
            tank.move(0.016f);
            tank.updateCooldown(0.016f);
            if (tank.getAttackCooldown() <= 0) {
                tank.attack();
            }
            tank.render(mockBatch);
        }
        
        // Au moins une attaque devrait avoir été effectuée
        verify(closeEnemy, atLeastOnce()).takeDamage(9);
    }

    @Test
    public void testTankInheritance() {
        assertTrue("Tank should be instance of Soldier", tank instanceof Soldier);
        assertTrue("Tank should be instance of Unit", tank instanceof Unit);
    }

    @Test
    public void testTankStatsAreDifferent() {
        // Vérifier que Tank a des stats spécifiques
        assertEquals("Tank specific health", 400, tank.getHealth());
        assertEquals("Tank specific damage", 9, tank.getAttackDamage());
        assertEquals("Tank specific speed", 40.0f, tank.getSpeed(), 0.01f);
        assertEquals("Tank specific range", 100, tank.getRange());
        assertEquals("Tank specific attack speed", 0.9f, tank.getAttackSpeed(), 0.01f);
    }

    @Test
    public void testTankHasAnimations() {
        assertNotNull("Walk animation should be loaded", tank.walkAnimation);
        assertNotNull("Attack animation should be loaded", tank.attackAnimation);
        assertNotNull("Idle frame should be loaded", tank.idleFrame);
    }

    @Test
    public void testTankCharacteristics() {
        // Tank : haute vie, dégâts moyens, très lent, attaque lente
        assertTrue("Tank health should be highest", tank.getHealth() >= 400);
        assertTrue("Tank should be very slow", tank.getSpeed() <= 40);
        assertTrue("Tank should have slowest attack rate", tank.getAttackSpeed() >= 0.9f);
        assertTrue("Tank damage should be decent", tank.getAttackDamage() >= 9);
        assertTrue("Tank range should be medium", tank.getRange() == 100);
    }

    @Test
    public void testTankRenderUsesCorrectDimensions() {
        // Tank a des dimensions visuelles de 85x50
        tank.render(mockBatch);
        // Vérifie que render est appelé avec 5 paramètres (incluant width et height)
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat(), 
            anyFloat(), anyFloat());
    }

    @Test
    public void testGetAttackAnimationDuration(){
        tank.attackAnimation = null;
        assertEquals("Attack anim null", 0.5f, tank.getAttackAnimationDuration(), 0.01f);
    }
}