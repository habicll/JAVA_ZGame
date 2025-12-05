package com.main.weapons;

/**
 * Represents a shotgun weapon in the game.
 * <p>
 * Provides high damage, short range, slow attack speed, and very limited ammunition. Used by units for close-range burst attacks.
 */
public class Shotgun extends Weapon {

    /**
     * Constructs a Shotgun with predefined stats (damage, range, attack speed, ammunition).
     * Calls the superclass constructor with shotgun parameters.
     */
    public Shotgun() {
        super(70, 100, 1.5f, 6);
        this.reloadSound = com.badlogic.gdx.Gdx.audio.newSound(com.badlogic.gdx.Gdx.files.internal("sounds/ReloadShotgun.mp3"));
        this.reloadTimer = 1.6f;
    }
}