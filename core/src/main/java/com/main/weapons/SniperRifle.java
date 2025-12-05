package com.main.weapons;

/**
 * Represents a sniper rifle weapon in the game.
 * <p>
 * Provides high damage, long range, slow attack speed, and limited ammunition. Used by units for precision attacks.
 */
public class SniperRifle extends Weapon {

    /**
     * Constructs a SniperRifle with predefined stats (damage, range, attack speed, ammunition).
     * Calls the superclass constructor with sniper rifle parameters.
     */
    public SniperRifle() {
        super(150, 450, 3f, 5);
        this.reloadSound = com.badlogic.gdx.Gdx.audio.newSound(com.badlogic.gdx.Gdx.files.internal("sounds/ReloadSniper.mp3"));
        this.reloadTimer = 2.5f;
    }
}