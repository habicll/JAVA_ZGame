package com.main.entities.units;
import com.main.entities.Unit;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.map.Base;

/**
 * Represents a Tank unit in the game, a subclass of Soldier with heavy armor and slow movement.
 * <p>
 * The Tank has increased health, reduced attack damage, slower attack speed, and unique animations.
 * It interacts with bases, lanes, and other units, and uses custom animation frames for walking and attacking.
 */
public class Tank extends Soldier {

    /**
     * The gold cost required to deploy a Tank unit.
     */
    public static final int COST = 40;

    /**
     * Constructs a Tank unit at the specified position and base.
     * Initializes stats and loads animation frames for walking, attacking, and idle states.
     *
     * @param posX Initial X position of the Tank.
     * @param posY Initial Y position of the Tank.
     * @param allyBase Reference to the allied base for lane management.
     */
    public Tank(float posX, float posY, Base allyBase) {
        super("Tank/Ride1.png", posX, posY, allyBase);
        // Set Tank stats based on base values
        this.health = (int)(2.0f * Unit.HP_BASE);
        this.attackDamage = 0.3f * Unit.DAMAGE_BASE;
        this.attackSpeed = 0.6f * Unit.ATTACK_SPEED_BASE;
        this.speed = 40;
        this.range = 100;

        // Load walk animation frames
        TextureRegion[] walkFrames = loadFrames("Tank/Ride%d.png", 2);
        walkAnimation = new Animation<>(FRAME_DURATION, walkFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // Load attack animation frames (muzzle flash effect)
        TextureRegion[] attackFrames = loadFrames("Tank/Attack%d.png", 7);
        attackAnimation = new Animation<>(FRAME_DURATION, attackFrames);
        attackAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        // Set idle frame to first walk frame if no dedicated idle frames
        this.idleFrame = walkFrames[0];
    }

    /**
     * Renders the Tank unit using the current animation frame based on its state.
     * Handles visual offsets and draws the sprite with correct dimensions.
     *
     * @param batch The SpriteBatch used for rendering.
     */
    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;
        switch (getCurrentState()) {
            case ATTACKING:
                currentFrame = attackAnimation.getKeyFrame(stateTime, false);
                break;
            case IDLE:
                currentFrame = idleFrame;
                break;
            case WALKING:
            default:
                currentFrame = walkAnimation.getKeyFrame(stateTime, true);
                break;
        }

        float visualWidth = 85;
        float visualHeight = 50;
        float offsetX = (this.width - visualWidth) / 2;
        float offsetY = 0;
        batch.draw(currentFrame, this.posX + offsetX, this.posY + offsetY, visualWidth, visualHeight);
    }

    /**
     * Returns the duration of the attack animation for the Tank unit.
     * Uses the custom attack animation if available, otherwise falls back to the base duration.
     *
     * @return Duration of the attack animation in seconds.
     */
    @Override
    public float getAttackAnimationDuration() {
        if (attackAnimation != null) {
            return attackAnimation.getAnimationDuration();
        }
        return super.getAttackAnimationDuration();
    }

    /*
     * Example of custom movement logic for Tank (commented out).
     * To implement, override move(float delta) and handle attack animation, cooldown, and movement.
     * Use state machine and collision checks for proper game behavior.
     */
    // ...existing code...
}
