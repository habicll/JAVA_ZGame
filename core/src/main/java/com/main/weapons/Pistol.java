package com.main.weapons;

/**
 * Represents a pistol weapon in the game.
 * <p>
 * Provides low damage, short range, moderate attack speed, and limited ammunition. Used by units for basic attacks.
 */
public class Pistol extends Weapon {

    /**
     * Constructs a Pistol with predefined stats (damage, range, attack speed, ammunition).
     * Calls the superclass constructor with pistol parameters.
     */
    public Pistol() {
        super(15, 150, 1f, 12);
        this.reloadSound = com.badlogic.gdx.Gdx.audio.newSound(com.badlogic.gdx.Gdx.files.internal("sounds/ReloadPistol.mp3"));
        this.reloadTimer = 1.5f;
    }
}