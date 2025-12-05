package com.main.entities.enemies;

import com.main.entities.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.map.Base;

/**
 * Represents a crawling zombie enemy unit.
 */
public class CZombie extends Zombie {

    /**
     * Constructs a new CZombie instance with specific stats and animations for the "crawl" zombie type.
     * @param posX Initial X position
     * @param posY Initial Y position
     * @param allyBase Reference to the allied base
     */
    public CZombie(int posX, int posY, Base allyBase) {
        super("zombie/crawl/Walk1.png", posX, posY, allyBase);
        this.health = (int) (0.7f * Unit.HP_BASE); // Sets health to 70% of base unit
        this.speed = 60; // Movement speed for this zombie type
        this.attackDamage = 1.3f * Unit.DAMAGE_BASE; // Attack damage for this zombie type
        this.attackSpeed = 0.5f * Unit.ATTACK_SPEED_BASE; // Attack speed for this zombie type
        this.range = 50; // Attack range in pixels

        /**
         * Loads and sets the walking animation for the zombie.
         */
        TextureRegion[] leftFrames = loadFrames("zombie/crawl/Walk%d.png", 10);
        walkLeft = new Animation<>(FRAME_DURATION, leftFrames);
        walkLeft.setPlayMode(Animation.PlayMode.LOOP);

        /**
         * Loads and sets the attack animation for the zombie.
         */
        TextureRegion[] attackTex = loadFrames("zombie/crawl/Attack%d.png", 12);
        attackAnimation = new Animation<>(FRAME_DURATION, attackTex);
        attackAnimation.setPlayMode(Animation.PlayMode.LOOP);
        /**
         * Load idle animation frames for when the zombie is stationary/blocking.
         */
        TextureRegion[] idleFrames = loadFrames("zombie/crawl/Idle%d.png", 9);
        this.idleFramer = new Animation<>(FRAME_DURATION, idleFrames);
        this.idleFramer.setPlayMode(Animation.PlayMode.LOOP);
        this.idleFrame = idleFrames[0];
    }
}
