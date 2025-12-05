package com.main.entities.player;

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
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.entities.Unit;
import com.main.entities.player.Hero.Direction;
import com.main.map.Base;
import com.main.map.WarMap;
import com.main.weapons.Pistol;

public class HeroTest {

    private static Application application;
    private Hero hero;
    private List<Unit> enemies;
    
    @Mock
    private WarMap mockMap;
    
    @Mock
    private Base mockBase;

    @Mock
    private Unit mockTarget2;

    @Mock
    private Unit mockTarget;
    
    @Mock
    private SpriteBatch mockBatch;

    @Mock
    private Sound mockSound;

    @Mock
    private GL20 mockGL;

    // Classe helper pour tester les collisions
    private class TestUnit extends Unit {
        public TestUnit(float x, float y) {
            super(null, x, y);
            this.health = 100;
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
        
        when(mockMap.isCollisionRect(anyFloat(), anyFloat(), anyFloat(), anyFloat())).thenReturn(false);
        
        hero = new Hero(100, 200, mockMap, mockBase);
        hero.setWeapon(new Pistol());
        
        enemies = new ArrayList<>();
        
        when(mockTarget.getPosX()).thenReturn(500.0f);
        when(mockTarget.getPosY()).thenReturn(200.0f);
        when(mockTarget.isDead()).thenReturn(false);
    }

    // ===== Constructor Tests =====

    @Test
    public void testConstructorInitializesHeroCorrectly() {
        assertNotNull("Hero should not be null", hero);
        assertEquals("Initial X position should be 100", 100, hero.getPosX(), 0.01f);
        assertEquals("Initial Y position should be 200", 200, hero.getPosY(), 0.01f);
        assertEquals("Hero health should be 500", 500, hero.getHealth());
        assertNotNull("Hero should have a weapon", hero.weapon);
        assertEquals("Hero speed should be 8", 5, hero.getSpeed(), 0.01f);
    }

    @Test
    public void testConstructorInitializesDefaultWeapon() {
        Hero newHero = new Hero(0, 0, mockMap, mockBase);
        assertNotNull("Default weapon should be Pistol", newHero.weapon);
        assertTrue("Default weapon should be Pistol", newHero.weapon instanceof Pistol);
    }

    @Test
    public void testConstructorInitializesGold() {
        assertEquals("Hero should start with 100 gold", 100, hero.getGold());
    }

    @Test
    public void testConstructorInitializesMaxHealth() {
        assertEquals("Max health should be 500", 500, hero.getMaxHealth());
    }

    // ===== Movement Up Tests =====

    @Test
    public void testMoveUpIncreasesYPosition() {
        float initialY = hero.getPosY();
        hero.moveUp(0.016f, 1000, enemies);
        assertTrue("Y position should increase when moving up", hero.getPosY() > initialY);
    }

    @Test
    public void testMoveUpWithCollisionDoesNotMove() {
        when(mockMap.isCollisionRect(anyFloat(), anyFloat(), anyFloat(), anyFloat())).thenReturn(true);
        float initialY = hero.getPosY();
        hero.moveUp(0.016f, 1000, enemies);
        assertEquals("Y position should not change with collision", initialY, hero.getPosY(), 0.01f);
    }

    @Test
    public void testMoveUpRespectsBoundary() {
        hero.setSpritePosY(995);
        hero.moveUp(0.016f, 1000, enemies);
        assertTrue("Hero should not exceed map height", hero.getPosY() + hero.getHeight() <= 1000);
    }

    @Test
    public void testMoveUpWithEnemyCollision() {
        Unit closeEnemy = new TestUnit(100, 220); // Proche du héros
        enemies.add(closeEnemy);
        
        float initialY = hero.getPosY();
        hero.moveUp(0.016f, 1000, enemies);
        
        // Devrait être bloqué ou se déplacer selon la distance
        assertTrue("Y position should be handled correctly", hero.getPosY() >= initialY);
    }

    @Test
    public void testMoveUpCalculation() {
        float initialY = hero.getPosY();
        hero.moveUp(1.0f, 1000, enemies);
        // Movement = speed * delta * 60 = 8 * 1.0 * 60 = 480
        float expectedY = initialY + 300;
        assertEquals("Position should increase correctly", expectedY, hero.getPosY(), 1.0f);
    }

    // ===== Movement Down Tests =====

    @Test
    public void testMoveDownDecreasesYPosition() {
        float initialY = hero.getPosY();
        hero.moveDown(0.016f, enemies);
        assertTrue("Y position should decrease when moving down", hero.getPosY() < initialY);
    }

    @Test
    public void testMoveDownWithCollisionDoesNotMove() {
        when(mockMap.isCollisionRect(anyFloat(), anyFloat(), anyFloat(), anyFloat())).thenReturn(true);
        float initialY = hero.getPosY();
        hero.moveDown(0.016f, enemies);
        assertEquals("Y position should not change with collision", initialY, hero.getPosY(), 0.01f);
    }

    @Test
    public void testMoveDownRespectsBoundary() {
        hero.setSpritePosY(5);
        hero.moveDown(0.016f, enemies);
        assertTrue("Hero Y should not go below 0", hero.getPosY() >= 0);
    }

    @Test
    public void testMoveDownWithEnemyCollision() {
        Unit closeEnemy = new TestUnit(100, 180); // Proche du héros
        enemies.add(closeEnemy);
        
        float initialY = hero.getPosY();
        hero.moveDown(0.016f, enemies);
        
        assertTrue("Y position should be handled correctly", hero.getPosY() <= initialY);
    }

    // ===== Movement Left Tests =====

    @Test
    public void testMoveLeftDecreasesXPosition() {
        float initialX = hero.getPosX();
        hero.moveLeft(0.016f, enemies);
        assertTrue("X position should decrease when moving left", hero.getPosX() < initialX);
    }

    @Test
    public void testMoveLeftWithCollisionDoesNotMove() {
        when(mockMap.isCollisionRect(anyFloat(), anyFloat(), anyFloat(), anyFloat())).thenReturn(true);
        float initialX = hero.getPosX();
        hero.moveLeft(0.016f, enemies);
        assertEquals("X position should not change with collision", initialX, hero.getPosX(), 0.01f);
    }

    @Test
    public void testMoveLeftRespectsBoundary() {
        hero.setSpritePosX(5);
        hero.moveLeft(0.016f, enemies);
        assertTrue("Hero X should not go below 0", hero.getPosX() >= 0);
    }

    @Test
    public void testMoveLeftWithEnemyCollision() {
        Unit closeEnemy = new TestUnit(80, 200); // Proche du héros
        enemies.add(closeEnemy);
        
        float initialX = hero.getPosX();
        hero.moveLeft(0.016f, enemies);
        
        assertTrue("X position should be handled correctly", hero.getPosX() <= initialX);
    }

    // ===== Movement Right Tests =====

    @Test
    public void testMoveRightIncreasesXPosition() {
        float initialX = hero.getPosX();
        hero.moveRight(0.016f, 1000, enemies);
        assertTrue("X position should increase when moving right", hero.getPosX() > initialX);
    }

    @Test
    public void testMoveRightWithCollisionDoesNotMove() {
        when(mockMap.isCollisionRect(anyFloat(), anyFloat(), anyFloat(), anyFloat())).thenReturn(true);
        float initialX = hero.getPosX();
        hero.moveRight(0.016f, 1000, enemies);
        assertEquals("X position should not change with collision", initialX, hero.getPosX(), 0.01f);
    }

    @Test
    public void testMoveRightRespectsBoundary() {
        hero.setSpritePosX(965);
        hero.moveRight(0.016f, 1000, enemies);
        assertTrue("Hero should not exceed map width", hero.getPosX() + hero.getWidth() <= 1000);
    }

    @Test
    public void testMoveRightWithEnemyCollision() {
        Unit closeEnemy = new TestUnit(120, 200); // Proche du héros
        enemies.add(closeEnemy);
        
        float initialX = hero.getPosX();
        hero.moveRight(0.016f, 1000, enemies);
        
        assertTrue("X position should be handled correctly", hero.getPosX() >= initialX);
    }

    // ===== Update Tests =====

    @Test
    public void testUpdateDoesNotThrowException() {
        hero.update(0.016f, 1920, 1080, enemies);
        assertNotNull("Hero should still exist after update", hero);
    }

    @Test
    public void testUpdateWithNoInput() {
        float initialX = hero.getPosX();
        float initialY = hero.getPosY();
        
        hero.update(0.016f, 1920, 1080, enemies);
        
        // Sans input clavier, le héros ne devrait pas bouger
        assertEquals("X should not change without input", initialX, hero.getPosX(), 0.01f);
        assertEquals("Y should not change without input", initialY, hero.getPosY(), 0.01f);
    }

    // ===== Move Override Tests =====

    @Test
    public void testMoveMethodDoesNothing() {
        float initialX = hero.getPosX();
        float initialY = hero.getPosY();
        hero.move(0.016f);
        assertEquals("X should not change with move()", initialX, hero.getPosX(), 0.01f);
        assertEquals("Y should not change with move()", initialY, hero.getPosY(), 0.01f);
    }

    // ===== Weapon Tests =====

    @Test
    public void testSetWeapon() {
        Pistol pistol = new Pistol();
        hero.setWeapon(pistol);
        assertEquals("Weapon should be set", pistol, hero.weapon);
    }

    @Test
    public void testSetWeaponWithNull() {
        Pistol pistol = new Pistol();
        hero.setWeapon(pistol);
        hero.setWeapon(null);
        assertEquals("Weapon should remain unchanged with null", pistol, hero.weapon);
    }

    @Test
    public void testHeroWeaponIsNotNull() {
        assertNotNull("Hero weapon should not be null", hero.weapon);
    }

    // ===== Attack Tests =====

    @Test
    public void testAttackWithNullTargetDoesNothing() {
        hero.attack();
        // Pas d'exception = succès
    }

    @Test
    public void testAttackWithDeadTargetDoesNothing() {
        when(mockTarget.isDead()).thenReturn(true);
        hero.setTarget(mockTarget);
        hero.attack();
        verify(mockTarget, never()).takeDamage(anyInt());
    }

    @Test
    public void testAttackWithLiveTargetDealsDamage() {
        when(mockTarget.getPosX()).thenReturn(120.0f);
        when(mockTarget.getPosY()).thenReturn(200.0f);
        when(mockTarget.isDead()).thenReturn(false);
        hero.setTarget(mockTarget);
        hero.setCooldown(0);
        
        int initialMunitions = hero.weapon.getMunitions();
        assertTrue("Weapon should have munitions", initialMunitions > 0);
        
        hero.attack();
        
        verify(mockTarget, times(1)).takeDamage(anyInt());
    }

    @Test
    public void testAttackRespectsAttackCooldown() {
        hero.setTarget(mockTarget);
        hero.setCooldown(5);
        
        hero.attack();
        
        verify(mockTarget, never()).takeDamage(anyInt());
    }

    @Test
    public void testWeaponReloadWhenNoMunitions() {
        hero.setTarget(mockTarget);
        hero.setCooldown(0);
        
        // Vider les munitions
        while (hero.weapon.getMunitions() > 0) {
            hero.weapon.attack();
        }
        
        assertEquals("Weapon should have 0 munitions", 0, hero.weapon.getMunitions());
        
        hero.attack();
        
        verify(mockTarget, never()).takeDamage(anyInt());
    }

    @Test
    public void testAttackReducesWeaponMunitions() {
        when(mockTarget.getPosX()).thenReturn(120.0f);
        when(mockTarget.getPosY()).thenReturn(200.0f);
        when(mockTarget.isDead()).thenReturn(false);
        hero.setTarget(mockTarget);
        hero.setCooldown(0);
        
        int initialMunitions = hero.weapon.getMunitions();
        // hero.weapon.reload();
        hero.attack();
        
        assertTrue("Munitions should decrease after attack", hero.weapon.getMunitions() < initialMunitions);
        assertTrue("Cooldown should be set after attack", hero.getAttackCooldown() > 0);
    }

    @Test
    public void testMultipleAttacksDepleteMunitions() {
        when(mockTarget.getPosX()).thenReturn(120.0f);
        when(mockTarget.getPosY()).thenReturn(200.0f);
        when(mockTarget.isDead()).thenReturn(false);
        hero.setTarget(mockTarget);
        
        int attackCount = 0;
        int initialMunitions = hero.weapon.getMunitions();
        
        while (hero.weapon.getMunitions() > 0 && attackCount < initialMunitions) {
            hero.setCooldown(0);
            hero.attack();
            attackCount++;
        }
        
        assertTrue("Should have attacked multiple times", attackCount > 0);
        assertEquals("Should have no munitions left", 0, hero.weapon.getMunitions());
    }

    @Test
    public void testAttackSetsCooldownAfterAttack() {
        when(mockTarget.getPosX()).thenReturn(120.0f);
        when(mockTarget.getPosY()).thenReturn(200.0f);
        when(mockTarget.isDead()).thenReturn(false);
        hero.setTarget(mockTarget);
        hero.setCooldown(0);
        
        hero.attack();
        
        assertEquals("Cooldown should be set to weapon attack speed", 
                     hero.weapon.getAttackSpeed(), 
                     hero.getAttackCooldown(), 0.01f);
    }

    @Test
    public void testCannotAttackTwiceWithoutResettingCooldown() {
        when(mockTarget.getPosX()).thenReturn(120.0f);
        when(mockTarget.getPosY()).thenReturn(200.0f);
        when(mockTarget.isDead()).thenReturn(false);
        hero.setTarget(mockTarget);
        hero.setCooldown(0);
        
        int initialMunitions = hero.weapon.getMunitions();
        
        hero.attack();
        assertEquals("Munitions should decrease by 1", initialMunitions - 1, hero.weapon.getMunitions());
        
        hero.attack();
        assertEquals("Munitions should NOT decrease again due to cooldown", 
                     initialMunitions - 1, 
                     hero.weapon.getMunitions());
        
        verify(mockTarget, times(1)).takeDamage(anyInt());
    }

    // ===== Health System Tests =====

    @Test
    public void testGetCurrentHealth() {
        assertEquals("Current health should be 500", 500, hero.getCurrentHealth());
    }

    @Test
    public void testGetMaxHealth() {
        assertEquals("Max health should be 500", 500, hero.getMaxHealth());
    }

    @Test
    public void testSetMaxHealth() {
        hero.setMaxHealth(600);
        assertEquals("Max health should be updated", 600, hero.getMaxHealth());
    }

    @Test
    public void testSetMaxHealthLowerThanCurrent() {
        hero.takeDamage(100); // Health = 400
        hero.setMaxHealth(300);
        assertEquals("Max health should be 300", 300, hero.getMaxHealth());
        assertEquals("Current health should be capped at 300", 300, hero.getCurrentHealth());
    }

    @Test
    public void testHeal() {
        hero.takeDamage(200); // Health = 300
        hero.heal(100);
        assertEquals("Health should be 400 after healing", 400, hero.getCurrentHealth());
    }

    @Test
    public void testHealAboveMax() {
        hero.takeDamage(100); // Health = 400
        hero.heal(200);
        assertEquals("Health should be capped at max (500)", 500, hero.getCurrentHealth());
    }

    @Test
    public void testHealAtFullHealth() {
        hero.heal(50);
        assertEquals("Health should remain at max", 500, hero.getCurrentHealth());
    }

    // ===== Gold System Tests =====

    @Test
    public void testGetGold() {
        assertEquals("Should start with 100 gold", 100, hero.getGold());
    }

    @Test
    public void testSetGold() {
        hero.setGold(150);
        assertEquals("Gold should be 150", 150, hero.getGold());
    }

    @Test
    public void testSetGoldNegative() {
        hero.setGold(-50);
        assertEquals("Gold should not go negative", 0, hero.getGold());
    }

    @Test
    public void testAddGold() {
        hero.addGold(50);
        assertEquals("Gold should be 150", 150, hero.getGold());
    }

    @Test
    public void testAddGoldMultipleTimes() {
        hero.addGold(25);
        hero.addGold(25);
        hero.addGold(50);
        assertEquals("Gold should be 200", 200, hero.getGold());
    }

    @Test
    public void testRemoveGold() {
        boolean result = hero.removeGold(30);
        assertTrue("Should successfully remove gold", result);
        assertEquals("Gold should be 70", 70, hero.getGold());
    }

    @Test
    public void testRemoveGoldInsufficientFunds() {
        boolean result = hero.removeGold(200);
        assertFalse("Should fail to remove gold", result);
        assertEquals("Gold should remain 120", 100, hero.getGold());
    }

    @Test
    public void testRemoveGoldExact() {
        boolean result = hero.removeGold(50);
        assertTrue("Should successfully remove exact amount", result);
        assertEquals("Gold should be 50", 50, hero.getGold());
    }

    // ===== Render Tests =====

    @Test
    public void testRenderDoesNotThrow() {
        hero.render(mockBatch);
        // Pas d'exception = succès
    }

    @Test
    public void testGetSpriteReturnsValidSprite() {
        assertNotNull("Sprite should not be null", hero.getSprite());
    }

    // ===== Getters Tests =====

    @Test
    public void testGettersReturnCorrectValues() {
        assertEquals("getPosX should return correct value", 100, hero.getPosX(), 0.01f);
        assertEquals("getPosY should return correct value", 200, hero.getPosY(), 0.01f);
        assertEquals("getSpeed should return correct value", 5, hero.getSpeed(), 0.01f);
        assertEquals("getHealth should return correct value", 500, hero.getHealth());
        assertEquals("getAttackSpeed should return correct value", 1, hero.getAttackSpeed(), 0.01f);
    }

    @Test
    public void testHeroHasCorrectInitialHealth() {
        assertEquals("Hero should start with 500 health", 500, hero.getHealth());
    }

    @Test
    public void testHeroHasCorrectSpeed() {
        assertEquals("Hero speed should be 8", 5, hero.getSpeed(), 0.01f);
    }

    @Test
    public void testHeroHasCorrectAttackSpeed() {
        assertEquals("Hero attack speed should be 1", 1.0f, hero.getAttackSpeed(), 0.01f);
    }

    // ===== Integration Tests =====

    @Test
    public void testFullCombatSequence() {
        when(mockTarget.getPosX()).thenReturn(120.0f);
        when(mockTarget.getPosY()).thenReturn(200.0f);
        when(mockTarget.isDead()).thenReturn(false);
        hero.setTarget(mockTarget);
        hero.setCooldown(0);
        
        // Première attaque
        hero.attack();
        verify(mockTarget, times(1)).takeDamage(anyInt());
        
        // Attaque en cooldown
        hero.attack();
        verify(mockTarget, times(1)).takeDamage(anyInt()); // Toujours 1
        
        // Après cooldown
        hero.setCooldown(0);
        hero.attack();
        verify(mockTarget, times(2)).takeDamage(anyInt());
    }

    @Test
    public void testGoldEconomy() {
        assertEquals("Start with 100 gold", 100, hero.getGold());
        
        hero.addGold(100); // Gain de l'or
        assertEquals("Should have 100 gold", 200, hero.getGold());
        
        assertTrue("Should purchase for 80", hero.removeGold(80));
        assertEquals("Should have 120 gold left", 120, hero.getGold());
        
        assertFalse("Should fail to purchase for 200", hero.removeGold(200));
        assertEquals("Should still have 120 gold", 120, hero.getGold());
    }

    @Test
    public void testHealthManagement() {
        assertEquals("Start with full health", 500, hero.getCurrentHealth());
        
        hero.takeDamage(200);
        assertEquals("Health after damage", 300, hero.getCurrentHealth());
        
        hero.heal(100);
        assertEquals("Health after heal", 400, hero.getCurrentHealth());
        
        hero.heal(200);
        assertEquals("Health capped at max", 500, hero.getCurrentHealth());
    }

    @Test
    public void testGetCurrentAttackAnimationDurationRight(){
        when(mockTarget.getPosX()).thenReturn(150.0f);
        when(mockTarget.getPosY()).thenReturn(200.0f);
        hero.moveRight(0.016f, 1000, enemies);
        hero.setTarget(mockTarget);
        hero.setCooldown(0);
        hero.attack();
        assertEquals("No clue what's expected",hero.AttackRight.getAnimationDuration(), hero.getCurrentAttackAnimationDuration(),  0.01f);
    }

    @Test
    public void testGetCurrentAttackAnimationDurationLeft(){
        when(mockTarget.getPosX()).thenReturn(80.0f);
        when(mockTarget.getPosY()).thenReturn(200.0f);
        hero.moveLeft(0.016f, enemies);
        hero.setTarget(mockTarget);
        hero.setCooldown(0);
        hero.attack();
        assertEquals("No clue what's expected", hero.AttackLeft.getAnimationDuration(), hero.getCurrentAttackAnimationDuration(),  0.01f);
    }

    @Test
    public void testGetCurrentAttackAnimationDurationDown(){
        when(mockTarget.getPosX()).thenReturn(100.0f);
        when(mockTarget.getPosY()).thenReturn(180.0f);
        hero.moveDown(0.016f, enemies);
        hero.setTarget(mockTarget);
        hero.setCooldown(0);
        hero.attack();
        assertEquals("No clue what's expected", hero.AttackDown.getAnimationDuration(), hero.getCurrentAttackAnimationDuration(),  0.01f);
    }
    @Test
    public void testGetCurrentAttackAnimationDurationUp(){
        when(mockTarget.getPosX()).thenReturn(100.0f);
        when(mockTarget.getPosY()).thenReturn(220.0f);
        hero.moveUp(0.016f, 1000, enemies);
        hero.setTarget(mockTarget);
        hero.setCooldown(0);
        hero.attack();
        assertEquals("No clue what's expected", hero.AttackUp.getAnimationDuration(), hero.getCurrentAttackAnimationDuration(),  0.01f);
    }

    @Test
    public void testSetGetDirection(){
        hero.setDirection(Direction.RIGHT);
        assertEquals("Direction should be set", Direction.RIGHT, hero.getDirection());
    }

    @Test
    public void testSetGetShootSound(){
        hero.setShootSound(mockSound);
        assertEquals("ShootSound should be set", mockSound, hero.getShootSound());
    }

    @Test
    public void testHeroAttackNoMunitions(){
        hero.setTarget(mockTarget);
        when(mockTarget.getPosX()).thenReturn(120f);
        when(mockTarget.getPosY()).thenReturn(200f);
        hero.setShootSound(mockSound);
        hero.getWeapon().setMunition(0);
        hero.attack();
        assertTrue("Weapon should be reloaded", hero.getWeapon().getMunitions() > 0);
    }

    @Test
    public void testHeroAttackSound(){
        hero.setTarget(mockTarget);
        when(mockTarget.getPosX()).thenReturn(120f);
        when(mockTarget.getPosY()).thenReturn(200f);
        hero.setShootSound(mockSound);
        hero.attack();
        verify(mockSound).play(0.7f);
    }

    @Test
    public void testFindClosestEnemyInRange(){
        List<Unit> tmp = new ArrayList<>();
        when(mockTarget.getPosX()).thenReturn(120f);
        when(mockTarget.getPosY()).thenReturn(200f);
        when(mockTarget2.getPosX()).thenReturn(130f);
        when(mockTarget2.getPosY()).thenReturn(200f);
        tmp.add(mockTarget);
        tmp.add(mockTarget2);
        assertEquals("Should return mockTarget", mockTarget, hero.findClosestEnemyInRange(tmp));
    }

    @Test
    public void testFindClosestEnemyInRangeWithDeadEnemy(){
        List<Unit> tmp = new ArrayList<>();
        when(mockTarget.getPosX()).thenReturn(120f);
        when(mockTarget.getPosY()).thenReturn(200f);
        when(mockTarget.isDead()).thenReturn(true);
        when(mockTarget2.getPosX()).thenReturn(130f);
        when(mockTarget2.getPosY()).thenReturn(200f);
        tmp.add(mockTarget);
        tmp.add(mockTarget2);
        assertEquals("Should return mockTarget2", mockTarget2, hero.findClosestEnemyInRange(tmp));
    }

    @Test
    public void testFindClosestSoldier(){
        List<Unit> tmp = new ArrayList<>();
        when(mockTarget.getPosX()).thenReturn(120f);
        when(mockTarget.getPosY()).thenReturn(200f);
        when(mockTarget2.getPosX()).thenReturn(130f);
        when(mockTarget2.getPosY()).thenReturn(200f);
        tmp.add(mockTarget);
        tmp.add(mockTarget2);
        assertEquals("Should return mockTarget", mockTarget, hero.findClosestSoldier(tmp));
    }

    @Test
    public void testFindClosestSoldierWithDeadSoldier(){
        List<Unit> tmp = new ArrayList<>();
        when(mockTarget.getPosX()).thenReturn(120f);
        when(mockTarget.getPosY()).thenReturn(200f);
        when(mockTarget.isDead()).thenReturn(true);
        when(mockTarget2.getPosX()).thenReturn(130f);
        when(mockTarget2.getPosY()).thenReturn(200f);
        tmp.add(mockTarget);
        tmp.add(mockTarget2);
        assertEquals("Should return mockTarget2", mockTarget2, hero.findClosestSoldier(tmp));
    }

    @Test
    public void testCheckSoldierCollision(){
        when(mockTarget.getPosX()).thenReturn(110f);
        when(mockTarget.getPosY()).thenReturn(200f);
        assertTrue("Should return true (collision)", hero.checkHeroSoldierCollisions(110f, 200f, mockTarget));
    }

     @Test
    public void testCheckSoldierCollisionNoCollision(){
        when(mockTarget.getPosX()).thenReturn(210f);
        when(mockTarget.getPosY()).thenReturn(200f);
        assertFalse("Should return false (no collision)", hero.checkHeroSoldierCollisions(110f, 200f, mockTarget));
    }
}