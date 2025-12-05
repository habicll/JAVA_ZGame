package com.main.entities.enemies;

import com.main.entities.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.map.Base;

/**
 * Represents a normal zombie enemy unit.
 */
public class FZombie extends Zombie {

    /**
     * Constructs a new FZombie instance with specific stats and animations for the
     * "normal" zombie type.
     *
     * @param posX     Initial X position
     * @param posY     Initial Y position
     * @param allyBase Reference to the allied base
     */
    public FZombie(int posX, int posY, Base allyBase) {
        /**
         * Constructs a new FZombie instance with specific stats and animations for the
         * "normal" zombie type.
         *
         * @param posX     Initial X position
         * @param posY     Initial Y position
         * @param allyBase Reference to the allied base
         */
        super("zombie/normal/Walk1.png", posX, posY, allyBase);
        this.health = (int) (2.0f * Unit.HP_BASE); // Sets health to 200% of base unit
        this.speed = 30; // Movement speed for this zombie type
        this.attackDamage = 0.6f * Unit.DAMAGE_BASE; // Attack damage for this zombie type
        this.attackSpeed = 0.8f * Unit.ATTACK_SPEED_BASE; // Attack speed for this zombie type
        this.range = 50; // Attack range in pixels

        /**
         * Loads and sets the walking animation for the zombie.
         */
        TextureRegion[] leftFrames = loadFrames("zombie/normal/Walk%d.png", 10);
        walkLeft = new Animation<>(FRAME_DURATION, leftFrames);
        walkLeft.setPlayMode(Animation.PlayMode.LOOP);

        /**
         * Loads and sets the attack animation for the zombie.
         */
        TextureRegion[] attackTex = loadFrames("zombie/normal/Attack%d.png", 4);
        attackAnimation = new Animation<>(FRAME_DURATION, attackTex);
        attackAnimation.setPlayMode(Animation.PlayMode.LOOP);
        /**
         * Loads and sets the idle animation for the zombie when stationary.
         */
        TextureRegion[] idleFrames = loadFrames("zombie/normal/Idle%d.png", 6);
        this.idleFramer = new Animation<>(FRAME_DURATION, idleFrames);
        this.idleFramer.setPlayMode(Animation.PlayMode.LOOP);
        this.idleFrame = idleFrames[0];
    }
}
