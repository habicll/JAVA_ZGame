package com.main.weapons;

/**
 * Represents a submachine gun (SMG) weapon in the game.
 * <p>
 * Provides low damage, medium range, very fast attack speed, and high ammunition capacity. Used by units for close-quarters combat.
 */
public class SMG extends Weapon {

    /**
     * Constructs an SMG with predefined stats (damage, range, attack speed, ammunition).
     * Calls the superclass constructor with SMG parameters.
     */
    public SMG() {
        super(20, 200, 0.5f, 35);
        this.reloadSound = com.badlogic.gdx.Gdx.audio.newSound(com.badlogic.gdx.Gdx.files.internal("sounds/ReloadSMG.mp3"));
        this.reloadTimer = 1.2f;
    }
}