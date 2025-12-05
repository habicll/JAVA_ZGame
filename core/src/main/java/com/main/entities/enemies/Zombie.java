package com.main.entities.enemies;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.entities.Unit;
import com.main.map.Base;

/**
 * Represents a basic zombie enemy unit in the game.
 * <p>
 * Inherits from {@link Unit} and provides zombie-specific behaviors such as
 * movement, attack logic, and animation handling.
 * Zombies interact with the player and allied base, using a state machine for
 * movement and combat.
 */
public class Zombie extends Unit {

    /**
     * Animation for walking left. Shared by all zombie types.
     */
    protected Animation<TextureRegion> walkLeft;

    /**
     * Animation for attacking. Shared by all zombie types.
     */
    protected Animation<TextureRegion> attackAnimation;

    /**
     * Static frame used for attack pose.
     */
    protected TextureRegion attackFrame;

    /**
     * Static frame used for idle pose.
     */
    protected TextureRegion idleFrame;

    /**
     * Animation for idle (stationary) pose.
     */
    protected Animation<TextureRegion> idleFramer;

    /**
     * Optional animation used while waiting between attacks (cooldown).
     */
    protected Animation<TextureRegion> reloadFramer;

    /**
     * Indicates if the zombie is currently moving.
     */
    protected boolean moving = false;

    /**
     * List of loaded textures for animation management and resource cleanup.
     */
    protected List<Texture> loadedTextures = new ArrayList<>();

    /**
     * Duration of each animation frame in seconds.
     */
    protected final float FRAME_DURATION = 0.2f;

    /**
     * Constructs a new Zombie unit with the specified sprite, position, and allied
     * base.
     *
     * @param filePath Path to the sprite file for this zombie
     * @param posX     Initial X position
     * @param posY     Initial Y position
     * @param allyBase Reference to the allied base associated with this unit
     */
    public Zombie(String filePath, float posX, float posY, Base allyBase) {
        super(filePath, posX, posY);
        this.allyBase = allyBase;
    }

    /**
     * Updates the zombie's movement and attack logic for the current frame.
     * <p>
     * Handles state transitions, attack cooldowns, animation timing, and collision
     * checks.
     * If a target is in range, initiates an attack and updates animation state.
     *
     * @param delta Time elapsed since the last frame (in seconds)
     */
    @Override
    public void move(float delta) {
        this.moving = false; // Reset moving state

        // Update attack animation timer
        if (attackAnimationTimer > 0) {
            attackAnimationTimer -= delta;
            // advance shared animation time so attack animations progress
            this.stateTime += delta;
            if (attackAnimationTimer > 0) {
                return;
            }
        }

        // Vérifie si une cible est à portée, si oui, attaque (utilise Unit.attack()
        // pour gérer cooldowns/timers)

        // If there's a unit target and it's in range, attack
        if (target != null && !target.isDead()) {
            double distance = Math
                    .sqrt(Math.pow(this.posX - target.getPosX(), 2) + Math.pow(this.posY - target.getPosY(), 2));
            if (distance <= this.range) {
                // Use the shared attack logic so damage, cooldown and attack animation timer
                // are applied
                attack();
                // stateTime reset is handled by Unit.attack(); ensure we advance during this
                // frame
                this.stateTime += delta;
                return;
            }
        }

        // Only move and animate if not in combat

        // If should stop (eg base in range or attack animation), idle
        if (shouldStopMoving(delta, -1)) {
            currentState = UnitState.IDLE;
            this.stateTime += delta;
            // Attaque la cible si elle est à portée
            if (target != null && !target.isDead()) {
                attack();
            }
            return;
        }

        // Default: move left (zombies direction) with collision check
        currentState = UnitState.WALKING;
        float newX = calculateNewPositionX(delta, -1); // -1 for left movement
        this.setSpritePosX(newX);
        this.moving = true;
        this.stateTime += delta;
    }

    /**
     * Renders the zombie on the screen using the current animation frame and state.
     * <p>
     * Selects the appropriate animation or static frame based on the zombie's state
     * (attacking, idle, walking).
     *
     * @param batch SpriteBatch used for drawing the zombie
     */
    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;
        if (currentState == UnitState.WALKING && walkLeft != null) {
            currentFrame = walkLeft.getKeyFrame(stateTime, true);
        } else if (currentState == UnitState.ATTACKING && attackAnimation != null) {
            // prefer explicit attack animation; otherwise fallback to reloadFramer
            currentFrame = attackAnimation.getKeyFrame(stateTime, false);
        } else if (currentState == UnitState.IDLE) {
            if (attackCooldown > 0 && reloadFramer != null) {
                currentFrame = reloadFramer.getKeyFrame(stateTime, true);
            } else if (idleFramer != null) {
                currentFrame = idleFramer.getKeyFrame(stateTime, true);
            } else {
                currentFrame = idleFrame;
            }
        } else {
            if (idleFramer != null) {
                currentFrame = idleFramer.getKeyFrame(stateTime, true);
            } else {
                currentFrame = idleFrame;
            }
        }

        batch.draw(currentFrame, posX, posY);
    }

    /**
     * Returns the duration of the attack animation for this zombie.
     * <p>
     * Can be overridden by subclasses for custom animation timing.
     *
     * @return Duration of the attack animation in seconds
     */
    @Override
    public float getAttackAnimationDuration() {
        if (attackAnimation != null) {
            return attackAnimation.getAnimationDuration();
        }
        return super.getAttackAnimationDuration();
    }

    /**
     * Loads an array of animation frames from file names matching a pattern.
     * <p>
     * Used for initializing walking and attack animations.
     *
     * @param pattern Filename pattern with a %d placeholder for frame number
     * @param count   Number of frames to load
     * @return Array of loaded TextureRegion frames
     */
    protected TextureRegion[] loadFrames(String pattern, int count) {
        TextureRegion[] frames = new TextureRegion[count];
        for (int i = 0; i < count; i++) {
            Texture tex = new Texture(Gdx.files.internal(String.format(pattern, i + 1)));
            loadedTextures.add(tex);
            frames[i] = new TextureRegion(tex);
        }
        return frames;
    }

    /**
     * Disposes of all loaded textures and resources used by this zombie.
     * <p>
     * Ensures proper cleanup to prevent memory leaks. Calls super.dispose() for
     * base cleanup.
     */
    @Override
    public void dispose() {
        super.dispose();
        // Dispose all loaded textures
        for (Texture tex : loadedTextures) {
            if (tex != null) {
                tex.dispose();
            }
        }
        loadedTextures.clear();
    }
}
