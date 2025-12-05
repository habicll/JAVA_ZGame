package com.main.weapons;

import com.badlogic.gdx.audio.Sound;
import com.utils.AudioSettings;

/**
 * Abstract base class for all weapons in the game.
 * <p>
 * Defines shared attributes and behaviors for weapon types, including damage, range, attack speed, and ammunition management.
 * Weapons interact with units to provide attack logic and resource management.
 */
public abstract class Weapon {
    /**
     * Damage dealt by the weapon per attack.
     */
    protected int damage;

    /**
     * Maximum range of the weapon (pixels).
     */
    protected int range;

    /**
     * Time in seconds between attacks (attack cooldown).
     */
    protected float attackSpeed;

    /**
     * Current ammunition available for the weapon.
     */
    protected int munitions;

    /**
     * Maximum ammunition capacity for the weapon.
     */
    protected int maxMunition;

    /**
     * Reload sound
     */
    protected Sound reloadSound;

    /**
     * Reload timer
     */
    protected float reloadTimer;

    /**
     * Constructs a Weapon with specified damage, range, attack speed, and maximum ammunition.
     * Initializes ammunition to maximum capacity.
     *
     * @param damage    Damage per attack
     * @param range     Maximum range in pixels
     * @param as        Attack speed (seconds between attacks)
     * @param maxMun    Maximum ammunition capacity
     */
    protected Weapon(int damage, int range, float as, int maxMun){
        this.damage = damage;
        this.range = range;
        this.attackSpeed = as;
        this.maxMunition = maxMun;
        this.munitions = maxMun;
    }

    /**
     * Reloads the weapon, restoring ammunition to maximum capacity.
     */
    public void reload(){
        this.playReloadSound();
        this.munitions = this.maxMunition;
    }

    /**
     * Performs an attack, consuming one ammunition if available.
     * Does not attack if ammunition is depleted.
     */
    public void attack(){
        if (this.munitions > 0)
            this.munitions--;
    }

    /**
     * Returns the damage dealt by the weapon per attack.
     *
     * @return Damage value
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Returns the maximum range of the weapon in pixels.
     *
     * @return Range value
     */
    public int getRange() {
        return range;
    }

    /**
     * Returns the attack speed (seconds between attacks).
     *
     * @return Attack speed value
     */
    public float getAttackSpeed() {
        return attackSpeed;
    }

    /**
     * Returns the current ammunition available.
     *
     * @return Current ammunition count
     */
    public int getMunitions() {
        return munitions;
    }

    /**
     * Return the reload time of the weapon.
     * 
     * @return Reload time
     */
    public float getReloadTimer() {
        return reloadTimer;
    }

    /**
     * Returns the maximum ammunition capacity for the weapon.
     *
     * @return Maximum ammunition count
     */
    public int getMaxMunitions(){
        return maxMunition;
    }

    public void setMunition(int mun){
        this.munitions = mun;
    }

    public void playReloadSound(){
        if (this.reloadSound != null && AudioSettings.isSoundEnabled())
            this.reloadSound.play(1.0f);
    }
}