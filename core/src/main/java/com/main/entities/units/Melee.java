package com.main.entities.units;

import com.main.entities.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.map.Base;

/**
 * Represents a Melee unit in the game, a subclass of {@link Soldier}
 * specialized for close combat.
 * <p>
 * The Melee unit features increased health, reduced attack damage, and short
 * range.
 * It uses custom animation frames for walking, attacking, and idle states, and
 * interacts with bases and other entities.
 */
public class Melee extends Soldier {

    /**
     * The gold cost required to deploy a Melee unit.
     */
    public static final int COST = 20;

    /**
     * Constructs a Melee unit with specified position and allied base.
     * Initializes stats, range, and loads all relevant animations for walking,
     * attacking, and idle states.
     *
     * @param posX     Initial X position of the Melee unit.
     * @param posY     Initial Y position of the Melee unit.
     * @param allyBase The allied base associated with this unit.
     */
    public Melee(float posX, float posY, Base allyBase) {
        super("Melee/Walk1.png", posX, posY, allyBase);

        /**
         * Sets the health, attack damage, attack speed, movement speed, and range for
         * the Melee unit.
         * These values are calculated based on base stats and reflect the Melee's
         * unique gameplay role.
         */
        this.health = (int) (0.7f * Unit.HP_BASE);
        this.attackDamage = 0.5f * Unit.DAMAGE_BASE;
        this.attackSpeed = 0.8f * Unit.ATTACK_SPEED_BASE;
        this.speed = 60;
        this.range = 50;

        /**
         * Loads the walk animation frames for the Melee unit and sets the animation to
         * loop.
         */
        TextureRegion[] walkFrames = loadFrames("Melee/Walk%d.png", 8);
        this.walkAnimation = new Animation<>(FRAME_DURATION, walkFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        /**
         * Loads the attack animation frames for the Melee unit and sets the animation
         * to play normally.
         */
        TextureRegion[] attackFrames = loadFrames("Melee/Attack_%d.png", 10);
        this.attackAnimation = new Animation<>(0.15f, attackFrames);
        attackAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        /**
         * Loads the idle animation frames for the Melee unit.
         * Prefer explicit idle sprites if present: `Melee/Idle.png` then
         * `Melee/Idle1..5.png`.
         * Falls back to the first walk frame if any load fails.
         */
        // frame 0 may be named "Idle.png"
        // frames 1..5

        TextureRegion[] idleFrames = loadFrames("Melee/Idle%d.png", 5);
        this.idleFramer = new Animation<>(0.15f, idleFrames);
        idleFramer.setPlayMode(Animation.PlayMode.LOOP);

    }

    /**
     * Returns the duration of the attack animation for the Melee unit.
     * Uses the custom attack animation if available, otherwise falls back to the
     * base duration.
     *
     * @return Duration of the attack animation in seconds.
     */
    @Override
    public float getAttackAnimationDuration() {
        if (this.attackAnimation != null) {
            return this.attackAnimation.getAnimationDuration();
        }
        return super.getAttackAnimationDuration();
    }
}
