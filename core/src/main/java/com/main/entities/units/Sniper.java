package com.main.entities.units;

import com.main.entities.Unit;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.map.Base;

/**
 * Represents a Sniper unit in the game, a specialized subclass of
 * {@link Soldier}.
 * <p>
 * The Sniper features high attack damage, long range, and fast attack speed,
 * but reduced health.
 * It uses custom animation frames for walking, attacking, and idle states, and
 * interacts with bases and other entities.
 */
public class Sniper extends Soldier {

    /**
     * The gold cost required to deploy a Sniper unit.
     */
    public static final int COST = 30;

    /**
     * Constructs a Sniper unit with specified position and allied base.
     * Initializes stats, range, and loads all relevant animations for walking,
     * attacking, and idle states.
     *
     * @param posX     Initial X position of the Sniper unit.
     * @param posY     Initial Y position of the Sniper unit.
     * @param allyBase The allied base associated with this unit.
     */
    public Sniper(int posX, int posY, Base allyBase) {
        super("Sniper/Walk1.png", posX, posY, allyBase);

        /**
         * Sets the health, attack damage, attack speed, movement speed, and range for
         * the Sniper unit.
         * These values are calculated based on base stats and reflect the Sniper's
         * unique gameplay role.
         */
        this.health = (int) (0.5f * Unit.HP_BASE);
        this.attackDamage = 1.5f * Unit.DAMAGE_BASE;
        this.attackSpeed = 2f * Unit.ATTACK_SPEED_BASE;
        this.speed = 30;
        this.range = 150;

        /**
         * Loads the walk animation frames for the Sniper unit and sets the animation to
         * loop.
         */
        TextureRegion[] walkFrames = loadFrames("Sniper/Walk%d.png", 8);
        this.walkAnimation = new Animation<>(FRAME_DURATION, walkFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        /**
         * Loads the attack animation frames for the Sniper unit and sets the animation
         * to play normally.
         */
        TextureRegion[] attackFrames = loadFrames("Sniper/Attack%d.png", 7);
        this.attackAnimation = new Animation<>(0.1f, attackFrames);
        attackAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        /**
         * Loads the reload (between-shots) animation frames and the stationary
         * idle frames. "Sniper/Idle%d.png" is used as the reload animation, while
         * "Sniper/Idlee%d.png" is used for when the unit is stopped/blocking.
         */
        TextureRegion[] reloadFrames = loadFrames("Sniper/Idle%d.png", 8);
        this.reloadFramer = new Animation<>(FRAME_DURATION, reloadFrames);
        this.reloadFramer.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] idleStoppedFrames = loadFrames("Sniper/Idlee%d.png", 7);
        this.idleFramer = new Animation<>(FRAME_DURATION, idleStoppedFrames);
        this.idleFramer.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] deadFrames = loadFrames("Sniper/Idlee%d.png", 7);
        this.deadFramer = new Animation<>(FRAME_DURATION, deadFrames);
        this.deadFramer.setPlayMode(Animation.PlayMode.NORMAL);
    }
}
