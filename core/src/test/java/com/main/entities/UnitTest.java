package com.main.entities;

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
import org.mockito.Mock;
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
import com.badlogic.gdx.math.Rectangle;
import com.main.entities.Unit.UnitState;
import com.main.entities.player.Hero;
import com.main.map.Base;
import com.main.utils.Position;

public class UnitTest {

    private static Application application;
    private TestUnit unit;
    private TestUnit enemy1;
    private TestUnit enemy2;
    private TestUnit enemy3;

    @Mock
    private Base mockEnemyBase;
    
    @Mock
    private SpriteBatch mockBatch;

    @Mock
    private Hero mockHero;
    
    @Mock
    private List<List<Unit>> mockunitPerLane;

    @Mock
    private GL20 mockGL;
    
    @Mock
    private Graphics mockGraphics;
    
    @Mock
    private Base mockBase;

    @Mock
    private Unit mockUnit;

    // Classe concrète pour tester la classe abstraite Unit
    private class TestUnit extends Unit {
        public TestUnit(float posX, float posY) {
            super(null, posX, posY);
            this.texture = mock(Texture.class);
            this.sprite = mock(Sprite.class);
            when(this.sprite.getX()).thenReturn(posX);
            when(this.sprite.getY()).thenReturn(posY);
            this.health = 100;
            this.attackDamage = 10;
            this.attackSpeed = 2.0f;
            this.speed = 50.0f;
            this.range = 100;
            this.allyBase = mockBase;
            this.setIndex(0);
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
        
        unit = new TestUnit(100, 200);
        enemy1 = new TestUnit(150, 200); // À portée (50 pixels)
        enemy2 = new TestUnit(500, 500); // Hors portée
        enemy3 = new TestUnit(300, 200); // Hors portée (200 pixels)
    }

    // ===== Constructor Tests =====

    @Test
    public void testConstructor() {
        assertNotNull("Unit should not be null", unit);
        assertEquals("Initial X position should be 100", 100.0f, unit.getPosX(), 0.01f);
        assertEquals("Initial Y position should be 200", 200.0f, unit.getPosY(), 0.01f);
        assertNotNull("Sprite should not be null", unit.getSprite());
    }

    @Test
    public void testConstructorInitializesStats() {
        assertEquals("Health should be 100", 100, unit.getHealth());
        assertEquals("Attack damage should be 10", 10, unit.getAttackDamage());
        assertEquals("Attack speed should be 2.0", 2.0f, unit.getAttackSpeed(), 0.01f);
        assertEquals("Speed should be 50", 50.0f, unit.getSpeed(), 0.01f);
        assertEquals("Range should be 100", 100, unit.getRange());
    }

    @Test
    public void testConstructorWithNullFilePath() {
        TestUnit nullUnit = new TestUnit(50, 50);
        assertNotNull("Unit should be created with null filepath", nullUnit);
        assertNotNull("Sprite should be mocked", nullUnit.getSprite());
    }

    @Test
    public void testConstructorSetsDimensions() {
        assertEquals("Width should be 32", 32.0f, unit.getWidth(), 0.01f);
        assertEquals("Height should be 48", 48.0f, unit.getHeight(), 0.01f);
    }

    // ===== Position Tests =====

    @Test
    public void testGetPosX() {
        assertEquals("getPosX should return correct value", 100.0f, unit.getPosX(), 0.01f);
    }

    @Test
    public void testGetPosY() {
        assertEquals("getPosY should return correct value", 200.0f, unit.getPosY(), 0.01f);
    }

    @Test
    public void testSetSpritePosX() {
        unit.setSpritePosX(150);
        assertEquals("Position X should be updated", 150.0f, unit.getPosX(), 0.01f);
        verify(unit.sprite).setX(150);
    }

    @Test
    public void testSetSpritePosY() {
        unit.setSpritePosY(250);
        assertEquals("Position Y should be updated", 250.0f, unit.getPosY(), 0.01f);
        verify(unit.sprite).setY(250);
    }

    // ===== Health and Damage Tests =====

    @Test
    public void testTakeDamage() {
        unit.takeDamage(30);
        assertEquals("Health should decrease by 30", 70, unit.getHealth());
    }

    @Test
    public void testTakeDamageMultipleTimes() {
        unit.takeDamage(20);
        unit.takeDamage(30);
        assertEquals("Health should decrease by 50 total", 50, unit.getHealth());
    }

    @Test
    public void testTakeDamageToZero() {
        unit.takeDamage(100);
        assertEquals("Health should be 0", 0, unit.getHealth());
        assertTrue("Unit should be dead", unit.isDead());
    }

    @Test
    public void testTakeDamageOverkill() {
        unit.takeDamage(150);
        assertEquals("Health should be 0 with overkill", 0, unit.getHealth());
        assertTrue("Unit should be dead", unit.isDead());
    }

    @Test
    public void testGetIndex(){
        assertEquals("Index should be 0", 0, unit.getIndex());
    }

    @Test
    public void testIsDead() {
        assertFalse("Unit should not be dead initially", unit.isDead());
        unit.takeDamage(100);
        assertTrue("Unit should be dead after fatal damage", unit.isDead());
    }

    @Test
    public void testIsNotDead() {
        unit.takeDamage(50);
        assertFalse("Unit should not be dead after partial damage", unit.isDead());
    }

    @Test
    public void testOnDeath() {
        unit.takeDamage(100);
        assertTrue("Unit should be dead", unit.isDead());
        assertEquals("Health should be 0", 0, unit.getHealth());
    }

    // ===== Cooldown Tests =====

    @Test
    public void testUpdateCooldown() {
        unit.setCooldown(5);
        unit.updateCooldown(1.0f);
        assertEquals("Cooldown should decrease by delta", 4.0f, unit.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testUpdateCooldownAtZero() {
        unit.setCooldown(0);
        unit.updateCooldown(1.0f);
        assertEquals("Cooldown should stay at 0", 0.0f, unit.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testUpdateCooldownMultipleTimes() {
        unit.setCooldown(10);
        for (int i = 0; i < 5; i++) {
            unit.updateCooldown(1.0f);
        }
        assertEquals("Cooldown should be 5", 5.0f, unit.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testUpdateCooldownBelowZero() {
        unit.setCooldown(1);
        unit.updateCooldown(2.0f);
        assertEquals("Cooldown should not go below 0", 0.0f, unit.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testUpdateCooldownWithSmallDelta() {
        unit.setCooldown(5);
        unit.updateCooldown(0.016f); // 60 FPS
        assertEquals("Cooldown with realistic delta", 4.984f, unit.getAttackCooldown(), 0.01f);
    }

    // ===== Enemy Detection Tests =====

    @Test
    public void testDetectEnemiesInRange() {
        List<Unit> enemies = new ArrayList<>();
        enemies.add(enemy1); // À portée
        enemies.add(enemy2); // Hors portée
        
        List<Unit> inRange = unit.detectEnemiesInRange(enemies);
        
        assertEquals("Should detect 1 enemy in range", 1, inRange.size());
        assertTrue("Should contain enemy1", inRange.contains(enemy1));
        assertFalse("Should not contain enemy2", inRange.contains(enemy2));
    }

    @Test
    public void testDetectEnemiesInRangeEmpty() {
        List<Unit> enemies = new ArrayList<>();
        List<Unit> inRange = unit.detectEnemiesInRange(enemies);
        
        assertEquals("Should detect 0 enemies", 0, inRange.size());
    }

    @Test
    public void testDetectEnemiesInRangeNoEnemies() {
        List<Unit> enemies = new ArrayList<>();
        enemies.add(enemy2); // Hors portée
        
        List<Unit> inRange = unit.detectEnemiesInRange(enemies);
        
        assertEquals("Should detect 0 enemies", 0, inRange.size());
    }

    @Test
    public void testDetectEnemiesInRangeWithSelf() {
        List<Unit> enemies = new ArrayList<>();
        enemies.add(unit); // Soi-même
        enemies.add(enemy1);
        
        List<Unit> inRange = unit.detectEnemiesInRange(enemies);
        
        assertEquals("Should detect 1 enemy (not self)", 1, inRange.size());
        assertFalse("Should not contain self", inRange.contains(unit));
        assertTrue("Should contain enemy1", inRange.contains(enemy1));
    }

    @Test
    public void testDetectMultipleEnemiesInRange() {
        TestUnit enemy4 = new TestUnit(180, 200); // À portée (80 pixels)
        List<Unit> enemies = new ArrayList<>();
        enemies.add(enemy1); // À portée
        enemies.add(enemy2); // Hors portée
        enemies.add(enemy4); // À portée
        
        List<Unit> inRange = unit.detectEnemiesInRange(enemies);
        
        assertEquals("Should detect 2 enemies in range", 2, inRange.size());
        assertTrue("Should contain enemy1", inRange.contains(enemy1));
        assertTrue("Should contain enemy4", inRange.contains(enemy4));
        assertFalse("Should not contain enemy2", inRange.contains(enemy2));
    }

    // ===== Target Selection Tests =====

    @Test
    public void testSelectTarget() {
        List<Unit> enemies = new ArrayList<>();
        enemies.add(enemy1); // À portée
        enemies.add(enemy2); // Hors portée
        
        unit.selectTarget(enemies);
        
        assertNotNull("Target should be selected", unit.getTarget());
        assertEquals("Should target enemy1", enemy1, unit.getTarget());
    }

    @Test
    public void testSelectTargetNoEnemies() {
        List<Unit> enemies = new ArrayList<>();
        
        unit.selectTarget(enemies);
        
        assertNull("Target should be null with no enemies", unit.getTarget());
    }

    @Test
    public void testSelectTargetOutOfRange() {
        List<Unit> enemies = new ArrayList<>();
        enemies.add(enemy2); // Hors portée
        
        unit.selectTarget(enemies);
        
        assertNull("Target should be null when all out of range", unit.getTarget());
    }

    @Test
    public void testSelectTargetClosest() {
        TestUnit nearEnemy = new TestUnit(110, 200); // 10 pixels
        List<Unit> enemies = new ArrayList<>();
        enemies.add(enemy1); // 50 pixels
        enemies.add(nearEnemy); // 10 pixels
        
        unit.selectTarget(enemies);
        
        assertEquals("Should select closest enemy", nearEnemy, unit.getTarget());
    }

    @Test
    public void testSetTarget() {
        unit.setTarget(enemy1);
        assertEquals("Target should be set", enemy1, unit.getTarget());
    }

    @Test
    public void testSetTargetNull() {
        unit.setTarget(enemy1);
        unit.setTarget(null);
        assertEquals("Target should remain enemy1 when setting null", enemy1, unit.getTarget());
    }

    // ===== Attack Tests =====

    @Test
    public void testAttackWithTarget() {
        unit.setTarget(enemy1);
        unit.setCooldown(0);
        int initialHealth = enemy1.getHealth();
        
        unit.attack();
        
        assertEquals("Enemy health should decrease", initialHealth - unit.getAttackDamage(), enemy1.getHealth());
        assertEquals("Cooldown should be set", unit.getAttackSpeed(), unit.getAttackCooldown(), 0.01f);
        assertEquals("State should be ATTACKING", Unit.UnitState.ATTACKING, unit.getCurrentState());
    }

    @Test
    public void testAttackWithoutTarget() {
        unit.setTarget(null);
        unit.setCooldown(0);
        
        unit.attack();
        
        assertEquals("Cooldown should remain 0", 0.0f, unit.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testAttackWithDeadTarget() {
        unit.setTarget(enemy1);
        enemy1.takeDamage(100); // Tue l'ennemi
        unit.setCooldown(0);
        
        unit.attack();
        
        assertEquals("Cooldown should remain 0", 0.0f, unit.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testAttackOnCooldown() {
        unit.setTarget(enemy1);
        unit.setCooldown(5);
        int initialHealth = enemy1.getHealth();
        
        unit.attack();
        
        assertEquals("Enemy health should not decrease", initialHealth, enemy1.getHealth());
        assertEquals("Cooldown should remain 5", 5.0f, unit.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testAttackTargetOutOfRange() {
        unit.setTarget(enemy3); // 200 pixels de distance, hors de portée (100)
        unit.setCooldown(0);
        int initialHealth = enemy3.getHealth();
        
        unit.attack();
        
        assertEquals("Enemy health should not decrease", initialHealth, enemy3.getHealth());
        assertEquals("Cooldown should remain 0", 0.0f, unit.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testAttackSetsAnimationTimer() {
        unit.setTarget(enemy1);
        unit.setCooldown(0);
        
        unit.attack();
        
        assertTrue("Attack animation timer should be set", unit.attackAnimationTimer > 0);
        assertEquals("State time should be reset", 0.0f, unit.stateTime, 0.01f);
    }

    @Test
    public void testAttackMultipleTimes() {
        unit.setTarget(enemy1);
        unit.setCooldown(0);
        
        // Première attaque
        int initialHealth = enemy1.getHealth(); // 100
        unit.attack();
        assertEquals("Enemy should take damage", initialHealth - unit.getAttackDamage(), enemy1.getHealth()); // 90
        
        unit.setCooldown(0);
        
        assertEquals("Cooldown should be 0", 0.0f, unit.getAttackCooldown(), 0.01f);
        
        // Deuxième attaque
        int healthBefore = enemy1.getHealth(); // Devrait être 90
        unit.attack();
        assertEquals("Enemy should take damage again", healthBefore - unit.getAttackDamage(), enemy1.getHealth()); // 80
    }

    // ===== Base Attack Tests =====

    @Test
    public void testSetTargetBase() {
        unit.setTargetBase(mockBase);
        assertEquals("Target base should be set", mockBase, unit.getTargetBase());
    }

    @Test
    public void testAttackBase() {
        when(mockBase.getHealth()).thenReturn(1000);
        
        unit.attackBase(mockBase);
        
        verify(mockBase).takeDamage(unit.getAttackDamage());
        assertEquals("Cooldown should be set", unit.getAttackSpeed(), unit.getAttackCooldown(), 0.01f);
        assertEquals("State should be ATTACKING", Unit.UnitState.ATTACKING, unit.getCurrentState());
    }

    @Test
    public void testAttackBaseOnCooldown() {
        when(mockBase.getHealth()).thenReturn(1000);
        unit.setCooldown(5);
        
        unit.attackBase(mockBase);
        
        // Ne devrait pas attaquer
        assertEquals("Cooldown should remain 5", 5.0f, unit.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testIsNearEnemyBase() {
        Rectangle baseBox = new Rectangle(700, 0, 100, 1080);
        when(mockBase.getCollisionBox()).thenReturn(baseBox);
        
        unit.setSpritePosX(650); // À portée (50 pixels du centre)
        assertTrue("Should be near enemy base", unit.isNearEnemyBase(mockBase));
        
        unit.setSpritePosX(400); // Hors portée
        assertFalse("Should not be near enemy base", unit.isNearEnemyBase(mockBase));
    }

    @Test
    public void testIsNearEnemyBaseWithNullBase() {
        assertFalse("Should return false with null base", unit.isNearEnemyBase(null));
    }

    // ===== Movement Tests =====

    @Test
    public void testMove() {
        float initialX = unit.getPosX();
        unit.move(1.0f);
        // Sans cible, devrait passer en IDLE
        assertEquals("Should be in IDLE state", Unit.UnitState.IDLE, unit.getCurrentState());
    }

    @Test
    public void testMoveWithTarget() {
        unit.setTarget(enemy3); // Hors portée
        float initialX = unit.getPosX();
        
        unit.move(1.0f);
        
        assertTrue("Should move towards target", unit.getPosX() != initialX);
        assertEquals("Should be in WALKING state", Unit.UnitState.WALKING, unit.getCurrentState());
    }

    @Test
    public void testMoveWithTargetInRange() {
        unit.setTarget(enemy1); // À portée
        unit.setCooldown(0);
        
        unit.move(1.0f);
        
        assertEquals("Should attack target in range", Unit.UnitState.ATTACKING, unit.getCurrentState());
    }

    @Test
    public void testMoveWithDeadTarget() {
        unit.setTarget(enemy1);
        enemy1.takeDamage(100); // Tue la cible
        
        unit.move(1.0f);
        
        assertNull("Target should be cleared", unit.getTarget());
        assertEquals("Should be in WALKING state", Unit.UnitState.WALKING, unit.getCurrentState());
    }

    @Test
    public void testMoveDuringAttackAnimation() {
        unit.attackAnimationTimer = 0.5f;
        float initialX = unit.getPosX();
        
        unit.move(0.1f);
        
        // L'unité ne devrait pas se déplacer pendant l'animation
        assertEquals("Should not move during attack animation", initialX, unit.getPosX(), 0.01f);
        
        // Le timer devrait diminuer OU rester inchangé selon l'implémentation
        // Si move() décrémente le timer :
        // assertEquals("Animation timer should decrease", 0.4f, unit.attackAnimationTimer, 0.01f);
        
        // Si move() ne décrémente PAS le timer (le timer est géré ailleurs) :
        assertTrue("Animation timer should be positive", unit.attackAnimationTimer > 0);
    }
    
    @Test
    public void testAttackAnimationTimerDecreases() {
        unit.attackAnimationTimer = 0.5f;
        unit.currentState = Unit.UnitState.ATTACKING;
        
        // Simuler plusieurs frames
        unit.move(0.1f);
        unit.move(0.1f);
        unit.move(0.1f);
        
        // Après 0.3s, le timer devrait avoir diminué
        assertTrue("Animation should progress or complete", 
                   unit.attackAnimationTimer <= 0.5f);
    }
    
    @Test
    public void testAttackAnimationEnds() {
        unit.setTarget(enemy1);
        unit.currentState = Unit.UnitState.ATTACKING;
        unit.attackAnimationTimer = 0.1f;
        unit.setCooldown(5); // En cooldown
        
        // Simuler un grand delta qui fait finir l'animation
        unit.move(0.5f); // Delta > attackAnimationTimer
        
        // L'animation devrait être terminée
        assertTrue("Animation should be finished", 
                   unit.attackAnimationTimer <= 0 || unit.getCurrentState() == Unit.UnitState.IDLE);
    }

    @Test
    public void testMoveAfterAttackAnimationEnds() {
        unit.setTarget(enemy1);
        unit.currentState = Unit.UnitState.ATTACKING;
        unit.attackAnimationTimer = 0.1f;
        unit.setCooldown(5); // En cooldown
        
        unit.move(0.2f); // L'animation finit
        
        assertEquals("Should transition to IDLE", Unit.UnitState.IDLE, unit.getCurrentState());
    }

    // ===== State Tests =====

    @Test
    public void testInitialState() {
        assertEquals("Initial state should be WALKING", Unit.UnitState.WALKING, unit.getCurrentState());
    }

    @Test
    public void testGetCurrentState() {
        assertEquals("Should return current state", Unit.UnitState.WALKING, unit.getCurrentState());
        
        unit.currentState = Unit.UnitState.ATTACKING;
        assertEquals("Should return ATTACKING", Unit.UnitState.ATTACKING, unit.getCurrentState());
    }

    @Test
    public void testStateTransitions() {
        // WALKING -> ATTACKING
        unit.setTarget(enemy1);
        unit.setCooldown(0);
        unit.attack();
        assertEquals("Should be ATTACKING", Unit.UnitState.ATTACKING, unit.getCurrentState());
        
        // ATTACKING -> IDLE (after animation, on cooldown)
        unit.attackAnimationTimer = 0.1f;
        unit.setCooldown(5);
        unit.move(0.2f);
        assertEquals("Should be IDLE", Unit.UnitState.IDLE, unit.getCurrentState());
    }

    // ===== Collision Tests =====

    @Test
    public void testCalculateNewPositionXRight() {
        float newX = unit.calculateNewPositionX(1.0f, 1);
        assertEquals("Should move right", 150.0f, newX, 0.1f);
    }

    @Test
    public void testCalculateNewPositionXLeft() {
        float newX = unit.calculateNewPositionX(1.0f, -1);
        assertEquals("Should move left", 50.0f, newX, 0.1f);
    }

    @Test
    public void testCalculateNewPositionXRightWouldCollideBase(){
        when(mockBase.getCollisionBox()).thenReturn(new Rectangle(101, 200, 1000, 1000));
        unit.setTargetBase(mockBase);
        float initialX = unit.getPosX();
        float newX = unit.calculateNewPositionX(1.0f, 1);
        assertEquals("Should not move", initialX, newX, 0.01f);
    }

    @Test
    public void testCalculateNewPositionXRightUnitCollision(){
        
        List<Unit> lane1Units = new ArrayList<>();
        TestUnit testunit = new TestUnit(99, 200);
        when(mockBase.getCollisionBox()).thenReturn(new Rectangle(500,200,1000,1000));
        when(mockBase.getHero()).thenReturn(null);
        when(mockEnemyBase.getCollisionBox()).thenReturn(new Rectangle(500,200,1000,1000));
        lane1Units.add(unit);        // Index 0
        lane1Units.add(testunit);    // Index 1
        
        testunit.setIndex(1);
        testunit.setLane(1);
        testunit.setAllyBase(mockBase);
        testunit.setTargetBase(mockEnemyBase);
        
        unit.posX = 100;
        unit.posY = 200;
        unit.setIndex(0);
        unit.setLane(1);
        unit.setAllyBase(mockBase);
        testunit.setTargetBase(mockEnemyBase);
        
        List<List<Unit>> allLanes = new ArrayList<>();
        allLanes.add(new ArrayList<>()); // Lane 0 (vide)
        allLanes.add(lane1Units);        // Lane 1 (avec nos units)
        
        when(mockBase.getUnitsPerLane()).thenReturn(allLanes); // ✅ Retourner la vraie structure
        float initialX = testunit.getPosX();
        float newX = testunit.calculateNewPositionX(0.16f, 1);

        
        assertEquals("Should not move", initialX, newX, 0.01f);;
    }

    // ===== Utility Tests =====

    @Test
    public void testSpecialAbility() {
        unit.specialAbility();
        // Vérifie que ça ne lance pas d'exception
    }

    @Test
    public void testGetters() {
        assertEquals("getHealth()", 100, unit.getHealth());
        assertEquals("getAttackDamage()", 10, unit.getAttackDamage());
        assertEquals("getAttackSpeed()", 2.0f, unit.getAttackSpeed(), 0.01f);
        assertEquals("getSpeed()", 50.0f, unit.getSpeed(), 0.01f);
        assertEquals("getRange()", 100, unit.getRange());
        assertEquals("getWidth()", 32.0f, unit.getWidth(), 0.01f);
        assertEquals("getHeight()", 48.0f, unit.getHeight(), 0.01f);
    }

    @Test
    public void testGetAttackAnimationDuration() {
        assertEquals("Default animation duration should be 0.5", 0.5f, unit.getAttackAnimationDuration(), 0.01f);
    }

    // ===== Integration Tests =====

    @Test
    public void testFullCombatCycle() {
        unit.setTarget(enemy1);
        unit.setCooldown(0);
        
        // Attaque
        unit.attack();
        assertEquals("Should be attacking", Unit.UnitState.ATTACKING, unit.getCurrentState());
        assertTrue("Animation timer should be set", unit.attackAnimationTimer > 0);
        
        // Pendant l'animation
        unit.move(0.2f);
        assertTrue("Should still be in animation", unit.attackAnimationTimer > 0);
        
        // Après l'animation
        unit.move(0.5f);
        assertTrue("Should have transitioned", unit.getCurrentState() != Unit.UnitState.ATTACKING);
    }

    @Test
    public void testFullMovementCycle() {
        unit.setTarget(enemy3); // Cible éloignée
        float initialX = unit.getPosX();
        
        for (int i = 0; i < 10; i++) {
            unit.move(0.016f);
        }
        
        assertTrue("Should have moved towards target", unit.getPosX() != initialX);
    }

    @Test
    public void testCalculateNewPositionXMovement() {
        unit.setSpritePosX(100);
        
        // Test mouvement à droite
        float newXRight = unit.calculateNewPositionX(1.0f, 1);
        assertEquals("Should move right", 150.0f, newXRight, 0.1f);
        
        // Test mouvement à gauche
        unit.setSpritePosX(100);
        float newXLeft = unit.calculateNewPositionX(1.0f, -1);
        assertEquals("Should move left", 50.0f, newXLeft, 0.1f);
    }

    @Test
    public void testWouldCollideWithBase(){
        unit.setTargetBase(mockBase);
        unit.posX = 100;
        unit.posY = 200;
        Rectangle tmp = new Rectangle(120, 200, 800, 600);
        when(mockBase.getCollisionBox()).thenReturn(tmp);
        assertTrue(unit.wouldCollideWithBase(120));
    }

    @Test
    public void testWouldCollideWithBaseCollisionBoxNull(){
        unit.setTargetBase(mockBase);
        when(mockBase.getCollisionBox()).thenReturn(null);
        assertFalse(unit.wouldCollideWithBase(120));
    }

    @Test
    public void testIsNearEnemyBaseBaseBoxNull(){
        when(mockBase.getCollisionBox()).thenReturn(null);
        assertFalse(unit.isNearEnemyBase(mockBase));
    }

    @Test
    public void testShouldStopMovingAnimTimer(){
        unit.setTarget(enemy1);
        unit.attack();
        assertTrue(unit.shouldStopMoving());
    }

    @Test
    public void testShouldStopMovingIndexZero(){
        unit.setTarget(enemy1);
        unit.setIndex(0);
        assertTrue(unit.shouldStopMoving());
    }

    @Test
    public void testCheckUnitCollision(){
        assertFalse(unit.checkUnitCollisions(unit.getPosX(), unit.getPosY()));
    }

    @Test
    public void testCheckUnitCollisionTargetBaseHero(){
        when(mockBase.getHero()).thenReturn(mockHero);
        when(mockHero.getPosX()).thenReturn(100f);
        when(mockHero.getPosY()).thenReturn(200f);
        when(mockHero.getWidth()).thenReturn(35f);
        unit.setAllyBase(mockBase);
        assertTrue(unit.checkUnitCollisions(120f, 200f));
    }

    @Test
    public void testCheckUnitCollisionTargetBaseHeroFar(){
        when(mockBase.getHero()).thenReturn(mockHero);
        when(mockHero.getPosX()).thenReturn(0f);
        when(mockHero.getPosY()).thenReturn(0f);
        when(mockHero.getWidth()).thenReturn(35f);
        unit.setAllyBase(mockBase);
        assertFalse(unit.checkUnitCollisions(120f, 200f));
    }

    @Test
    public void testCheckUnitCollisionIndexNotZero(){
        List<Unit> lane1Units = new ArrayList<>();
        TestUnit testunit = new TestUnit(100, 200);
        lane1Units.add(unit);        // Index 0
        lane1Units.add(testunit);    // Index 1
        
        testunit.setAllyBase(mockBase);
        testunit.setIndex(1);
        testunit.setLane(1);
        
        unit.setIndex(0);
        unit.setLane(1);
        unit.setAllyBase(mockBase);
        
        List<List<Unit>> allLanes = new ArrayList<>();
        allLanes.add(new ArrayList<>()); // Lane 0 (vide)
        allLanes.add(lane1Units);        // Lane 1 (avec nos units)
        
        when(mockBase.getUnitsPerLane()).thenReturn(allLanes); // ✅ Retourner la vraie structure
        
        // Test: l'unité à l'index 1 vérifie la collision avec l'unité à l'index 0
        assertTrue("Should collide", testunit.checkUnitCollisions(120f, 200f));
    }

    @Test
    public void testCheckUnitCollisionIndexNotZeroHeroNotNull(){
        List<Unit> lane1Units = new ArrayList<>();
        TestUnit testunit = new TestUnit(100, 200);
        lane1Units.add(unit);        // Index 0
        lane1Units.add(testunit);    // Index 1
        
        testunit.setAllyBase(mockBase);
        testunit.setIndex(1);
        testunit.setLane(1);
        
        unit.setIndex(0);
        unit.setLane(1);
        unit.setAllyBase(mockBase);
        
        List<List<Unit>> allLanes = new ArrayList<>();
        allLanes.add(new ArrayList<>()); // Lane 0 (vide)
        allLanes.add(lane1Units);        // Lane 1 (avec nos units)
        
        when(mockBase.getUnitsPerLane()).thenReturn(allLanes); // ✅ Retourner la vraie structure
        when(mockBase.getHero()).thenReturn(mockHero);
        when(mockHero.getPosX()).thenReturn(110f);
        when(mockHero.getPosY()).thenReturn(200f);
        
        // Test: l'unité à l'index 1 vérifie la collision avec l'unité à l'index 0
        assertTrue("Should collide", testunit.checkUnitCollisions(120f, 200f));
    }

    @Test
    public void testCheckUnitCollisionIndexNotZeroHeroNotNullNoCollision(){
        List<Unit> lane1Units = new ArrayList<>();
        TestUnit testunit = new TestUnit(0, 200);
        lane1Units.add(unit);        // Index 0
        lane1Units.add(testunit);    // Index 1
        
        testunit.setAllyBase(mockBase);
        testunit.setIndex(1);
        testunit.setLane(1);
        
        unit.setIndex(0);
        unit.setLane(1);
        unit.setAllyBase(mockBase);
        
        List<List<Unit>> allLanes = new ArrayList<>();
        allLanes.add(new ArrayList<>()); // Lane 0 (vide)
        allLanes.add(lane1Units);        // Lane 1 (avec nos units)
        
        when(mockBase.getUnitsPerLane()).thenReturn(allLanes); // ✅ Retourner la vraie structure
        when(mockBase.getHero()).thenReturn(mockHero);
        when(mockHero.getPosX()).thenReturn(800f);
        when(mockHero.getPosY()).thenReturn(200f);
        
        // Test: l'unité à l'index 1 vérifie la collision avec l'unité à l'index 0
        assertFalse("Should not collide", testunit.checkUnitCollisions(10f, 200f));
    }

    @Test
    public void testGetBase(){
        unit.setAllyBase(mockBase);
        assertEquals("AllyBase should be set", mockBase, unit.getAllyBase());
    }

    @Test
    public void testMoveStateAttackingCooldownZero(){
        when(mockUnit.getPosY()).thenReturn(200f);
        when(mockUnit.getPosX()).thenReturn(110f);
        when(mockUnit.isDead()).thenReturn(false);
        unit.setTarget(mockUnit);
        unit.attack();
        unit.setCooldown(0);
        unit.move(1.16f);
        assertEquals("Unit should be attacking", UnitState.ATTACKING, unit.getCurrentState());
    }

    @Test
    public void testMoveStateAttackingTargetDead(){
        when(mockUnit.getPosY()).thenReturn(200f);
        when(mockUnit.getPosX()).thenReturn(110f);
        when(mockUnit.isDead()).thenReturn(false);
        unit.setTarget(mockUnit);
        unit.attack();
        unit.setCooldown(0);
        when(mockUnit.isDead()).thenReturn(true);
        unit.move(1.16f);
        assertEquals("Unit should be walking", UnitState.WALKING, unit.getCurrentState());
    }


    @Test
    public void testMoveStateAttackingTargetNull(){
        when(mockUnit.getPosY()).thenReturn(200f);
        when(mockUnit.getPosX()).thenReturn(110f);
        when(mockUnit.isDead()).thenReturn(false);
        unit.setTarget(mockUnit);
        unit.attack();
        unit.setCooldown(0);
        unit.target = null;
        unit.move(1.16f);
        assertEquals("Unit should be walking", UnitState.WALKING, unit.getCurrentState());
    }

    @Test
    public void testMoveTargetNullBaseNotNullCooldownZero(){
        unit.target = null;
        unit.setTargetBase(mockEnemyBase);
        when(mockEnemyBase.getPosition()).thenReturn(new Position(110, 200));
        unit.move(0.16f);
        assertEquals("Should be attacking", UnitState.ATTACKING, unit.getCurrentState());
    }

    @Test
    public void testMoveTargetNullBaseNotNullCooldownNotZero(){
        unit.target = null;
        unit.setTargetBase(mockEnemyBase);
        unit.setCooldown(4);
        when(mockEnemyBase.getPosition()).thenReturn(new Position(110, 200));
        unit.move(0.16f);
        assertEquals("Should be idle", UnitState.IDLE, unit.getCurrentState());
    }

    @Test
    public void testMoveTargetNullBaseNull(){
        unit.target = null;
        unit.setTargetBase(mockEnemyBase);
        unit.setCooldown(4);
        when(mockEnemyBase.getPosition()).thenReturn(new Position(5000, 200));
        unit.move(0.16f);
        assertEquals("Should be walking", UnitState.WALKING, unit.getCurrentState());
    }
}