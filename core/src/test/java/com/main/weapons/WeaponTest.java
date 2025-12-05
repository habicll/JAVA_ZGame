package com.main.weapons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.badlogic.gdx.audio.Sound;

public class WeaponTest {

    @Mock
    private Sound mockSound;

    // Classe concrète pour tester la classe abstraite Weapon
    private class TestWeapon extends Weapon {
        public TestWeapon(int damage, int range, int as, int maxMun) {
            super(damage, range, as, maxMun);
            
        }
    }

    private TestWeapon weapon;

    @Before
    public void setUp() {
        weapon = new TestWeapon(50, 200, 10, 30);
        weapon.setReloadSound(mockSound);
    }

    @Test
    public void testConstructor() {
        assertNotNull(weapon);
        assertEquals(50, weapon.getDamage());
        assertEquals(200, weapon.getRange());
        assertEquals(10, weapon.getAttackSpeed(), 0.01f);
        assertEquals(30, weapon.getMaxMunitions());
        assertEquals(30, weapon.getMunitions());
    }

    @Test
    public void testGetDamage() {
        assertEquals(50, weapon.getDamage());
    }

    @Test
    public void testGetRange() {
        assertEquals(200, weapon.getRange());
    }

    @Test
    public void testGetAttackSpeed() {
        assertEquals(10, weapon.getAttackSpeed(), 0.01f);
    }

    @Test
    public void testGetMunitions() {
        assertEquals(30, weapon.getMunitions());
    }

    @Test
    public void testGetMaxMunitions() {
        assertEquals(30, weapon.getMaxMunitions());
    }

    @Test
    public void testAttack() {
        assertEquals(30, weapon.getMunitions());
        weapon.attack();
        assertEquals(29, weapon.getMunitions());
    }

    @Test
    public void testAttackMultipleTimes() {
        weapon.attack();
        weapon.attack();
        weapon.attack();
        assertEquals(27, weapon.getMunitions());
    }

    @Test
    public void testAttackUntilEmpty() {
        for (int i = 0; i < 30; i++) {
            weapon.attack();
        }
        assertEquals(0, weapon.getMunitions());
    }

    @Test
    public void testAttackWhenEmpty() {
        for (int i = 0; i < 30; i++) {
            weapon.attack();
        }
        weapon.attack();
        assertEquals(0, weapon.getMunitions());
    }

    @Test
    public void testReload() {
        weapon.attack();
        weapon.attack();
        assertEquals(28, weapon.getMunitions());
        
        weapon.reload();
        assertEquals(30, weapon.getMunitions());
    }

    @Test
    public void testReloadWhenEmpty() {
        for (int i = 0; i < 30; i++) {
            weapon.attack();
        }
        assertEquals(0, weapon.getMunitions());
        
        weapon.reload();
        assertEquals(30, weapon.getMunitions());
    }

    @Test
    public void testReloadWhenFull() {
        assertEquals(30, weapon.getMunitions());
        weapon.reload();
        assertEquals(30, weapon.getMunitions());
    }
}