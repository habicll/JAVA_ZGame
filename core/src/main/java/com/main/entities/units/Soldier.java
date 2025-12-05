package com.main.entities.units;

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
 * Represents a Soldier unit in the game, controlled by the player or AI.
 * <p>
 * Soldiers inherit from {@link Unit} and provide specific stats, animations, and behaviors.
 * They interact with bases, lanes, and other entities, using a state machine for movement, attack, and cooldown logic.
 * This class manages animation frames, rendering, and resource disposal for soldier units.
 */
public class Soldier extends Unit {
    /**
     * Animation for walking state, shared by all soldier types.
     */
    protected Animation<TextureRegion> walkAnimation;

    /**
     * Animation for attacking state, shared by all soldier types.
     */
    protected Animation<TextureRegion> attackAnimation;

    /**
     * Texture frame for idle state.
     */
    protected TextureRegion idleFrame;

    /**
     * Animation for idle state, can be used for subtle idle effects.
     */
    protected Animation<TextureRegion> idleFramer;
    
    protected Animation<TextureRegion> deadFramer;

    /**
     * Animation used while reloading / waiting between attacks (cooldown).
     */
    protected Animation<TextureRegion> reloadFramer;

    /**
     * List of loaded textures for animation frames, managed for proper disposal.
     */
    protected List<Texture> loadedTextures = new ArrayList<>();

    /**
     * Duration of each animation frame in seconds.
     */
    protected final float FRAME_DURATION = 0.2f;

    /**
     * Constructs a Soldier unit with the specified sprite, position, and allied base.
     * Initializes the base reference and delegates sprite setup to the superclass.
     *
     * @param filePath Path to the sprite file for this soldier.
     * @param posX Initial X position of the soldier.
     * @param posY Initial Y position of the soldier.
     * @param allyBase The allied base associated with this unit.
     */
    public Soldier(String filePath, float posX, float posY, Base allyBase) {
        super(filePath, posX, posY);
        this.allyBase = allyBase;
    }

    /**
     * Updates the movement and state of the soldier unit based on the game delta time.
     * Handles attack animation timer, target engagement, cooldowns, and collision checks.
     * Implements state transitions for walking, attacking, and idle states using a state machine.
     *
     * @param delta Time elapsed since the last update (in seconds).
     */
    @Override
    public void move(float delta) {
        // Handle attack animation timer and transition back to walking state when finished
        if (attackAnimationTimer > 0) {
            attackAnimationTimer -= delta;
            this.stateTime += delta;
            if (attackAnimationTimer <= 0) {
                currentState = UnitState.WALKING;
                this.stateTime = 0;
            }
            return;
        }

        // Engage target if within range and alive, applying attack logic and cooldowns
        if (target != null && !target.isDead()) {
            double distance = Math.sqrt(Math.pow(this.posX - target.getPosX(), 2) + Math.pow(this.posY - target.getPosY(), 2));
            if (distance <= this.range) {
                // In range: apply attack logic, cooldown, and attack animation timer
                if (attackCooldown <= 0f) {
                    attack();
                    this.stateTime = 0f;
                } else {
                    currentState = UnitState.IDLE;
                    this.stateTime += delta;
                }
                return;
            }
        }

        // Stop movement if attack animation or cooldown prevents further action
        if (shouldStopMoving(delta, 1)) {
            currentState = UnitState.IDLE;
            this.stateTime += delta;
            return;
        }


        if(this.health <= 0){
            currentState = UnitState.DYING;
            this.stateTime += delta;
            return;
        }

        // Default movement: move right (soldier direction) with collision check
        currentState = UnitState.WALKING;
        float newX = calculateNewPositionX(delta, 1);
        this.setSpritePosX(newX);
        this.stateTime += delta;
    }

    /**
     * Renders the soldier unit using the appropriate animation frame based on its current state.
     * Selects frames for walking, attacking, or idle states, and draws the sprite at its current position.
     *
     * @param batch The SpriteBatch used for rendering.
     */
    @lombok.Generated
    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;

        // Select animation frame based on current state
        switch (getCurrentState()) {
            case ATTACKING:
                if (attackAnimation != null) {
                    currentFrame = attackAnimation.getKeyFrame(this.stateTime, false);
                } else if (reloadFramer != null) {
                    currentFrame = reloadFramer.getKeyFrame(this.stateTime, true);
                } else if (idleFramer != null) {
                    currentFrame = idleFramer.getKeyFrame(this.stateTime, true);
                } else {
                    currentFrame = idleFrame;
                }
                break;
            case IDLE:
                // If we're in cooldown between shots, prefer the reload animation
                if (attackCooldown > 0 && reloadFramer != null) {
                    currentFrame = reloadFramer.getKeyFrame(this.stateTime, true);
                } else if (idleFramer != null) {
                    currentFrame = idleFramer.getKeyFrame(this.stateTime, true);
                } else {
                    currentFrame = idleFrame;
                }
                break;
            case DYING:
                if (deadFramer != null) {
                    currentFrame = deadFramer.getKeyFrame(this.stateTime, false);
                } else {
                    currentFrame = idleFrame;
                }
                break;
            case WALKING:
            default:
                if (walkAnimation != null) {
                    currentFrame = walkAnimation.getKeyFrame(this.stateTime, true);
                } else {
                    currentFrame = idleFrame;
                }
                break;
        }

        batch.draw(currentFrame, posX, posY);
    }

    /**
     * Loads animation frames for the soldier unit from a file pattern.
     * Each frame is loaded as a texture and added to the managed texture list for disposal.
     *
     * @param pattern The file path pattern for animation frames (e.g., "soldier_walk_%d.png").
     * @param count The number of frames to load.
     * @return An array of TextureRegion objects representing the loaded frames.
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
     * Disposes of all resources used by the soldier unit, including loaded textures.
     * Ensures proper cleanup to prevent memory leaks.
     */
    @lombok.Generated
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
