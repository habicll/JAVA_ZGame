package com.main.weapons;

/**
 * Represents an assault rifle weapon in the game.
 * <p>
 * Provides moderate damage, medium range, fast attack speed, and high ammunition capacity. Used by units for rapid fire.
 */
public class AssaultRifle extends Weapon {

    /**
     * Constructs an AssaultRifle with predefined stats (damage, range, attack speed, ammunition).
     * Calls the superclass constructor with assault rifle parameters.
     */
    public AssaultRifle() {
        super(30, 200, 0.7f, 30);
        this.reloadSound = com.badlogic.gdx.Gdx.audio.newSound(com.badlogic.gdx.Gdx.files.internal("sounds/ReloadAR.mp3"));
        this.reloadTimer = 2f;
    }
}