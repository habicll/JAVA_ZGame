package com.main.entities.enemies;

import com.main.entities.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.map.Base;

/**
 * Represents a female zombie enemy unit in the game.
 * <p>
 * Inherits from {@link Zombie} and customizes stats, animations, and attack
 * behavior for the "women" zombie type.
 */
public class WZombie extends Zombie {

    /**
     * Constructs a new WZombie instance with specific stats and animations for the
     * "women" zombie type.
     *
     * @param posX     Initial X position
     * @param posY     Initial Y position
     * @param allyBase Reference to the allied base
     */
    public WZombie(int posX, int posY, Base allyBase) {
        super("zombie/women/Walk1.png", posX, posY, allyBase);
        this.health = (int) (0.6f * Unit.HP_BASE);
        this.speed = 60;
        this.attackDamage = 0.9f * Unit.DAMAGE_BASE;
        this.attackSpeed = 1.2f * Unit.ATTACK_SPEED_BASE;
        this.range = 50;

        // Load walk animation
        TextureRegion[] leftFrames = loadFrames("zombie/women/Walk%d.png", 7);
        walkLeft = new Animation<>(FRAME_DURATION, leftFrames);
        walkLeft.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] attackTex = loadFrames("zombie/women/Attack%d.png", 20);
        attackAnimation = new Animation<>(0.1f, attackTex);
        // Attack should play once when triggered
        attackAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        TextureRegion[] idleFrames = loadFrames("zombie/women/Idle%d.png", 8);
        this.idleFramer = new Animation<>(FRAME_DURATION, idleFrames);
        this.idleFramer.setPlayMode(Animation.PlayMode.LOOP);
        this.idleFrame = idleFrames[0];
    }
}