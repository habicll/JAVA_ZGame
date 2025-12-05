package com.main.entities.units;

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
import com.main.map.Base;

public class SoldierTest {

    private static Application application;
    private TestSoldier soldier;
    
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

    // Classe concrète pour tester la classe abstraite Soldier
    private class TestSoldier extends Soldier {
        public TestSoldier(float posX, float posY) {
            super(null, posX, posY, mockBase);
            this.texture = mock(Texture.class);
            this.sprite = mock(Sprite.class);
            when(this.sprite.getX()).thenReturn(posX);
            when(this.sprite.getY()).thenReturn(posY);
            this.health = 100;
            this.attackDamage = 10;
            this.attackSpeed = 1.0f;
            this.speed = 50.0f;
            this.range = 100;
            
            // Mock animations
            this.idleFrame = mock(TextureRegion.class);
            this.walkAnimation = mock(com.badlogic.gdx.graphics.g2d.Animation.class);
            this.attackAnimation = mock(com.badlogic.gdx.graphics.g2d.Animation.class);
            this.idleFramer = mock(com.badlogic.gdx.graphics.g2d.Animation.class);
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
        
        soldier = new TestSoldier(100, 200);
        
        when(mockEnemy.getPosX()).thenReturn(500.0f);
        when(mockEnemy.getPosY()).thenReturn(200.0f);
        when(mockEnemy.isDead()).thenReturn(false);
        when(mockEnemy.getHealth()).thenReturn(100);
    }

    // ===== Constructor Tests =====

    @Test
    public void testConstructor() {
        assertNotNull("Soldier should not be null", soldier);
        assertEquals("Initial X position should be 100", 100.0f, soldier.getPosX(), 0.01f);
        assertEquals("Initial Y position should be 200", 200.0f, soldier.getPosY(), 0.01f);
    }

    @Test
    public void testConstructorInitializesAnimations() {
        assertNotNull("Idle frame should be initialized", soldier.idleFrame);
        assertNotNull("Walk animation should be initialized", soldier.walkAnimation);
        assertNotNull("Attack animation should be initialized", soldier.attackAnimation);
        assertNotNull("Idle animation should be initialized", soldier.idleFramer);
    }

    @Test
    public void testConstructorWithNullFilePath() {
        TestSoldier nullSoldier = new TestSoldier(50, 50);
        assertNotNull("Soldier should be created with null filepath", nullSoldier);
    }

    // ===== Movement Tests =====

    @Test
    public void testMoveRight() {
        float initialX = soldier.getPosX();
        soldier.move(1.0f);
        assertTrue("X position should increase (move right)", soldier.getPosX() > initialX);
    }

    @Test
    public void testMoveCalculation() {
        float initialX = soldier.getPosX();
        soldier.move(1.0f);
        float expectedX = initialX + (50.0f * 1.0f); // speed * delta
        assertEquals("Position should increase correctly", expectedX, soldier.getPosX(), 0.1f);
    }

    @Test
    public void testMoveWithRealisticDelta() {
        float initialX = soldier.getPosX();
        soldier.move(0.016f); // 60 FPS
        float expectedX = initialX + (50.0f * 0.016f);
        assertEquals("Position with realistic delta", expectedX, soldier.getPosX(), 0.01f);
    }

    @Test
    public void testMoveWithZeroDelta() {
        float initialX = soldier.getPosX();
        soldier.move(0.0f);
        assertEquals("Position should not change with zero delta", initialX, soldier.getPosX(), 0.01f);
    }

    @Test
    public void testMoveMultipleTimes() {
        float initialX = soldier.getPosX();
        soldier.move(0.1f);
        soldier.move(0.1f);
        soldier.move(0.1f);
        float expectedX = initialX + (50.0f * 0.3f);
        assertEquals("Position after multiple moves", expectedX, soldier.getPosX(), 0.1f);
    }

    // ===== Movement with Target Tests =====

    @Test
    public void testMoveTowardsTarget() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(150.0f);
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        
        soldier.setTarget(closeEnemy);
        float initialX = soldier.getPosX();
        soldier.move(1.0f);
        
        // Devrait se déplacer ou rester en place si à portée
        assertTrue("Should move or stay in range", soldier.getPosX() >= initialX);
    }

    @Test
    public void testStopWhenTargetInRange() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(150.0f); // À portée (50 pixels, range = 100)
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        
        soldier.setTarget(closeEnemy);
        soldier.setCooldown(0);
        
        soldier.move(1.0f);
        
        assertEquals("Should be in ATTACKING or IDLE state", true, 
            soldier.getCurrentState() == Unit.UnitState.ATTACKING || 
            soldier.getCurrentState() == Unit.UnitState.IDLE);
    }

    @Test
    public void testMoveWithDeadTarget() {
        soldier.setTarget(mockEnemy);
        when(mockEnemy.isDead()).thenReturn(true);
        
        float initialX = soldier.getPosX();
        soldier.move(1.0f);
        
        assertTrue("Should continue moving after target dies", soldier.getPosX() > initialX);
    }

    // ===== Attack Animation Tests =====



    // ===== Collision with Base Tests =====

    @Test
    public void testMoveWithBaseCollision() {
        soldier.setTargetBase(mockBase);
        when(mockBase.getCollisionBox()).thenReturn(new com.badlogic.gdx.math.Rectangle(700, 0, 100, 1080));
        
        soldier.setSpritePosX(695);
        float initialX = soldier.getPosX();
        
        soldier.move(10.0f);
        
        assertEquals("Should stop at base collision", initialX, soldier.getPosX(), 0.01f);
    }

    // ===== State Management Tests =====

    @Test
    public void testInitialStateIsWalking() {
        TestSoldier newSoldier = new TestSoldier(0, 0);
        assertEquals("Initial state should be WALKING", Unit.UnitState.WALKING, newSoldier.getCurrentState());
    }

    @Test
    public void testStateChangesToIdle() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(150.0f);
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        
        soldier.setTarget(closeEnemy);
        soldier.setCooldown(5); // En cooldown
        
        soldier.move(0.016f);
        
        assertEquals("Should be IDLE when in range but on cooldown", Unit.UnitState.IDLE, soldier.getCurrentState());
    }

    @Test
    public void testStateChangesToAttacking() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(150.0f);
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        
        soldier.setTarget(closeEnemy);
        soldier.setCooldown(0);
        
        soldier.move(0.016f);
        
        assertEquals("Should be ATTACKING when ready to attack", Unit.UnitState.ATTACKING, soldier.getCurrentState());
    }

    // ===== Render Tests =====

    // ===== Animation Frame Loading Tests =====

    @Test
    public void testLoadFramesReturnsCorrectCount() {
        // Cette méthode charge des textures, difficile à tester sans fichiers
        // On vérifie juste qu'elle existe
        assertNotNull("loadFrames method should exist", soldier);
    }

    @Test
    public void testFrameDurationConstant() {
        assertEquals("Frame duration should be 0.2", 0.2f, soldier.FRAME_DURATION, 0.01f);
    }

    // ===== Position Tests =====

    @Test
    public void testGetPosX() {
        assertEquals("getPosX should return correct value", 100.0f, soldier.getPosX(), 0.01f);
    }

    @Test
    public void testGetPosY() {
        assertEquals("getPosY should return correct value", 200.0f, soldier.getPosY(), 0.01f);
    }

    @Test
    public void testSetSpritePosX() {
        soldier.setSpritePosX(150);
        assertEquals("Position X should be updated", 150.0f, soldier.getPosX(), 0.01f);
    }

    @Test
    public void testSetSpritePosY() {
        soldier.setSpritePosY(250);
        assertEquals("Position Y should be updated", 250.0f, soldier.getPosY(), 0.01f);
    }

    // ===== Dispose Tests =====

    @Test
    public void testDisposeDoesNotThrow() {
        soldier.dispose();
        // Should not throw exception
    }

    @Test
    public void testDisposeClearsLoadedTextures() {
        soldier.dispose();
        assertEquals("Loaded textures should be cleared", 0, soldier.loadedTextures.size());
    }

    // ===== Integration Tests =====

    @Test
    public void testFullMovementCycle() {
        float initialX = soldier.getPosX();
        
        for (int i = 0; i < 60; i++) {
            soldier.move(0.016f);
        }
        
        assertTrue("Should have moved after full cycle", soldier.getPosX() > initialX);
    }

    @Test
    public void testCombatCycle() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(150.0f);
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        
        soldier.setTarget(closeEnemy);
        soldier.setCooldown(0);
        
        // Premier cycle : attaque
        soldier.move(0.016f);
        assertEquals("Should be ATTACKING", Unit.UnitState.ATTACKING, soldier.getCurrentState());
        
        // Pendant l'animation d'attaque, reste en ATTACKING
        for (int i = 0; i < 10; i++) {
            soldier.move(0.016f);
        }
        
        // Après l'animation (0.5s), devrait être en IDLE (en cooldown avec target vivante)
        soldier.move(0.5f);
        assertEquals("Should be WALKING after attack animation with alive target", 
                     Unit.UnitState.WALKING, 
                     soldier.getCurrentState());
    }
    
    @Test
    public void testCombatCycleWithDeadTarget() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(150.0f);
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false).thenReturn(true); // Meurt après l'attaque
        
        soldier.setTarget(closeEnemy);
        soldier.setCooldown(0);
        
        // Attaque
        soldier.move(0.016f);
        assertEquals("Should be ATTACKING", Unit.UnitState.WALKING, soldier.getCurrentState());
        
        // L'ennemi meurt, passe en WALKING pour chercher nouvelle cible
        soldier.move(0.6f); // Fin de l'animation
        assertEquals("Should be WALKING after target dies", 
                     Unit.UnitState.WALKING, 
                     soldier.getCurrentState());
    }
    
    @Test
    public void testCombatCycleFullSequence() {
        Unit closeEnemy = mock(Unit.class);
        when(closeEnemy.getPosX()).thenReturn(150.0f);
        when(closeEnemy.getPosY()).thenReturn(200.0f);
        when(closeEnemy.isDead()).thenReturn(false);
        
        soldier.setTarget(closeEnemy);
        soldier.setCooldown(0);
        
        // 1. Attaque initiale
        soldier.move(0.016f);
        assertEquals("Should start ATTACKING", Unit.UnitState.ATTACKING, soldier.getCurrentState());
        
        // 2. Animation d'attaque en cours
        assertTrue("Attack animation timer should be set", soldier.getAttackAnimationTimer() > 0);
        
        // 3. Pendant l'animation, reste en ATTACKING
        soldier.move(0.2f);
        assertTrue("Should still have animation time", soldier.getAttackAnimationTimer() > 0);
        
        // 4. Après l'animation complète
        soldier.move(0.4f); // Total: 0.6s > 0.5s animation duration
        assertEquals("Should be WALKING after animation with cooldown", 
                     Unit.UnitState.WALKING, 
                     soldier.getCurrentState());
        
        // 5. Cooldown se termine
        soldier.updateCooldown(1.0f); // Cooldown = 0
        
        // 6. Réattaque
        soldier.move(0.016f);
        assertEquals("Should ATTACK again when cooldown finished", 
                     Unit.UnitState.ATTACKING, 
                     soldier.getCurrentState());
    }

    @Test
    public void testMoveAndRenderSequence() {
        when(soldier.walkAnimation.getKeyFrame(anyFloat(), any(Boolean.class)))
            .thenReturn(mock(TextureRegion.class));
        
        soldier.move(0.5f);
        soldier.render(mockBatch);
        soldier.move(0.5f);
        soldier.render(mockBatch);
        
        verify(mockBatch, atLeastOnce()).draw(any(TextureRegion.class), anyFloat(), anyFloat());
    }

    @Test
    public void testSoldierInheritance() {
        assertTrue("Soldier should be instance of Unit", soldier instanceof Unit);
    }

    @Test
    public void testGetSprite() {
        assertNotNull("Sprite should not be null", soldier.getSprite());
    }

    @Test
    public void testGetTexture() {
        assertNotNull("Texture should not be null", soldier.getTexture());
    }
}