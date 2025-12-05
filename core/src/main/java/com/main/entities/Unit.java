package com.main.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.main.entities.player.Hero;
import com.main.map.Base;

/**
 * Abstract base class for all units in the game (player, enemy, etc).
 * Handles position, health, attack, movement, and rendering logic.
 */
public abstract class Unit {
    /**
     * Lane index for lane-based movement and targeting.
     */
    private int lane;
    /**
     * Base health value for global unit balancing.
     */
    protected static final int HP_BASE = 200;
    /**
     * Base attack damage for global unit balancing.
     */
    protected static final float DAMAGE_BASE = 30f;
    /**
     * Base attack speed (seconds between attacks) for global unit balancing.
     */
    protected static final float ATTACK_SPEED_BASE = 1.5f;

    /**
     * Unit states for animation and behavior control.
     */
    public enum UnitState {
        /**
         * Unit is standing still.
         */
        IDLE,
        /**
         * Unit is moving towards its target.
         */
        WALKING,
        /**
         * Unit is performing an attack animation.
         */
        ATTACKING,
        DYING
    }

    /** X position of the unit. */
    protected float posX;
    /** Y position of the unit. */
    protected float posY;
    /** Sprite used for rendering the unit. */
    protected Sprite sprite;
    /** Current health of the unit. */
    protected int health;
    /** Attack damage value. */
    protected float attackDamage;
    /**
     * Time in seconds between attacks (attack speed).
     */
    protected float attackSpeed;
    /** Movement speed of the unit. */
    protected float speed;
    /** Current target of the unit. */
    protected Unit target;
    /**
     * Reference to the enemy base for attack logic.
     */
    protected Base targetBase;
    protected Base allyBase;
    protected int range;
    /**
     * Current attack cooldown in seconds.
     */
    protected float attackCooldown = 0f;
    protected Texture texture;
    protected float width, height;

    /**
     * Index of the unit within its lane (used for collision and movement).
     */
    private int index;

    // State management
    protected UnitState currentState = UnitState.WALKING;
    /**
     * Tracks attack animation progress in seconds.
     */
    protected float attackAnimationTimer = 0f;
    /**
     * Duration of attack animation in seconds.
     */
    protected static final float ATTACK_ANIMATION_DURATION = 0.5f;
    // Shared animation time counter for subclasses to use when rendering
    /**
     * Shared animation time counter for rendering and animation updates.
     */
    protected float stateTime = 0f;
    /**
     * Default range for attacking a base (pixels).
     */
    protected static final int BASE_ATTACK_RANGE = 100;

    /**
     * Hook for subclasses to override the duration of their attack animation.
     * Default implementation returns the global ATTACK_ANIMATION_DURATION.
     *
     * @return Duration of the attack animation in seconds
     */
    public float getAttackAnimationDuration() {
        return ATTACK_ANIMATION_DURATION;
    }

    public Unit(String filePath, float posX, float posY) {
        this.posX = posX;
        this.posY = posY;

        // Handle null texture for testing purposes
        if (filePath != null) {
            this.texture = new Texture(filePath);
            this.sprite = new Sprite(texture);
        } else {
            this.texture = null;
            this.sprite = null;
        }

        if (this.sprite != null) {
            this.sprite.setPosition(posX, posY);
            this.sprite.setSize(32, 48); // Visual size of the sprite
        }
        this.width = 32; // Hitbox width
        this.height = 48; // Hitbox height
    }

    public float getPosX() {
        return posX;
    }

    public int getLane(){
        return lane;
    }

    public float getSpeed() {
        return speed;
    }

    public float getWidth() {
        return width;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public float getHeight() {
        return height;
    }

    public float getPosY() {
        return posY;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public int getHealth() {
        return this.health;
    }

    public int getAttackDamage() {
        return (int)attackDamage;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public Unit getTarget() {
        return target;
    }

    public String getType(){
        return this.getClass().getSimpleName();
    }

    public int getRange() {
        return range;
    }

    public float getAttackCooldown() {
        return attackCooldown;
    }

    public Texture getTexture() {
        return texture;
    }


    public float getAttackAnimationTimer() {
        return attackAnimationTimer;
    }

    public void setCooldown(float cd) {
        this.attackCooldown = cd;
    }

    public void setSpritePosX(float posX) {
        this.sprite.setX(posX);
        this.posX = posX;
    }

    public void setSpritePosY(float posY) {
        this.sprite.setY(posY);
        this.posY = posY;
    }

    public void setLane(int lane){
        this.lane = lane;
    }

    public void setAllyBase(Base allyBase){
        this.allyBase = allyBase;
    }
    
    public Base getAllyBase(){
        return this.allyBase;
    }

    @lombok.Generated
    /**
     * Renders the unit's sprite using the provided SpriteBatch.
     *
     * @param batch SpriteBatch used for rendering.
     */
    public void render(SpriteBatch batch) {
        batch.draw(this.texture, posX, posY);
    }

    /**
     * Releases resources used by the unit (such as texture).
     */
    public void dispose() {
        texture.dispose();
    }

    /**
     * Checks if moving to newX would cause a collision with the enemy base's hitbox.
     *
     * @param newX Proposed new X position.
     * @return True if collision would occur, false otherwise.
     */
    protected boolean wouldCollideWithBase(float newX) {
        if (targetBase == null || targetBase.getCollisionBox() == null) {
            return false;
        }

        // Créer un rectangle temporaire pour la nouvelle position
        Rectangle unitRect = new Rectangle(newX, this.posY, this.width, this.height);

        // Vérifier la collision avec la hitbox de la base
        boolean collides = unitRect.overlaps(targetBase.getCollisionBox());

        return collides;
    }

    /**
     * Calculates the Euclidean distance between this unit and another unit.
     *
     * @param other The other unit to measure distance to.
     * @return Distance in pixels.
     */
    protected double calculateDistance(Unit other) {
        float dx = this.posX - other.getPosX();
        float dy = this.posY - other.getPosY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Detects all enemy units within attack range.
     *
     * @param enemies List of enemy units to check.
     * @return List of units within attack range.
     */
    protected List<Unit> detectEnemiesInRange(List<Unit> enemies) {
        List<Unit> inRange = new ArrayList<>();
        for (Unit unit : enemies) {
            if (unit != this && !unit.isDead()) {
                double distance = calculateDistance(unit);
                if ((int)distance <= this.range) {
                    inRange.add(unit);
                }
            }
        }
        return inRange;
    }

    /**
     * Finds the closest enemy unit in the provided list.
     *
     * @param enemies List of enemy units.
     * @return Closest enemy unit, or null if none found.
     */
    protected Unit findClosestEnemy(List<Unit> enemies) {
        Unit closest = null;
        double minDistance = Double.MAX_VALUE;
        for (Unit enemy : enemies) {
            double distance = calculateDistance(enemy);
            if (distance < minDistance && ((!enemy.isDead() && enemy.getLane() == this.lane) || enemy instanceof Hero)) {
                minDistance = distance;
                closest = enemy;
            }
        }
        return closest;
    }

    /**
     * Automatically selects the closest target from available enemies.
     * If no enemy units are available, targets the enemy base.
     *
     * @param enemies List of enemy units.
     */
    public void selectTarget(List<Unit> enemies) {
        List<Unit> inRange = detectEnemiesInRange(enemies);
        if (!inRange.isEmpty()) {
            Unit newTarget = findClosestEnemy(inRange);
            this.target = newTarget;
        } else {
            // No enemy units available, target enemy base if set
            this.target = null;
        }
    }

    public void setTargetBase(Base enemyBase) {
        this.targetBase = enemyBase;
    }

    public Base getTargetBase() {
        return targetBase;
    }

    public void setTarget(Unit target) {
        if (target != null) {
            this.target = target;
        }
    }

    /**
     * Inflicts damage to this unit and triggers death logic if health reaches zero.
     *
     * @param damage Amount of damage to apply.
     */
    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.health = 0;
        }
    }

    /**
     * Updates the attack cooldown timer.
     *
     * @param delta Time elapsed since last update (seconds).
     */
    public void updateCooldown(float delta) {
        if (attackCooldown > 0) {
            attackCooldown -= delta; // Decrement by time in seconds
            if (attackCooldown < 0) {
                attackCooldown = 0;
            }
        }
    }

    /**
     * Called when the unit dies. Subclasses may override to implement death animation or effects.
     * Example: trigger animation, play sound, etc.
     */


    /**
     * Checks if the unit is dead (health is zero or less).
     *
     * @return True if dead, false otherwise.
     */
    public boolean isDead() {
        return this.health <= 0;
    }

    /**
     * Attacks the current target unit if in range and cooldown is ready.
     * Handles attack animation, cooldown, and state changes.
     */
    public void attack() {
        if (target != null && !target.isDead() && attackCooldown <= 0) {
            double distance = calculateDistance(target);
            if (distance <= this.range) {
                target.takeDamage((int)attackDamage);
                attackCooldown = attackSpeed;
                currentState = UnitState.ATTACKING;
                attackAnimationTimer = getAttackAnimationDuration();
                this.stateTime = 0f;
            }
        }
    }


    /**
     * Attacks the specified enemy base if cooldown is ready.
     * Handles base damage, cooldown, and attack animation.
     *
     * @param enemyBase Enemy base to attack.
     */
    public void attackBase(Base enemyBase) {
        if (enemyBase != null && attackCooldown <= 0) {
            enemyBase.takeDamage((int)this.attackDamage);
            attackCooldown = attackSpeed;
            // Use subclass-specific attack animation duration when available
            attackAnimationTimer = getAttackAnimationDuration();
            currentState = UnitState.ATTACKING;
            this.stateTime = 0f;
        }
    }

    /**
     * Checks if the unit is near the specified enemy base (within attack range).
     *
     * @param base Enemy base to check proximity.
     * @return True if near the base, false otherwise.
     */
    public boolean isNearEnemyBase(Base base) {
        if (base == null)
            return false;
        Rectangle baseBox = base.getCollisionBox();
        if (baseBox == null)
            return false;

        // Calculer la distance entre l'unité et la hitbox de la base
        float unitCenterX = this.posX + (this.width / 2);
        float baseCenterX = baseBox.x + (baseBox.width / 2);
        float distance = Math.abs(unitCenterX - baseCenterX);

        return distance <= BASE_ATTACK_RANGE;
    }

    /**
     * Checks if the unit should stop moving (target in range, attacking, near base,
     * or blocked by collision). This version simulates the next movement step and
     * treats inability to advance as "should stop" so the unit won't play walking
     * animation while stuck.
     *
     * @param delta     Time elapsed since last update (seconds)
     * @param direction Movement direction (1 = right, -1 = left)
     * @return True if the unit should stop moving, false otherwise.
     */
    protected boolean shouldStopMoving(float delta, int direction) {
        // Stop if attacking
        if (attackAnimationTimer > 0) {
            return true;
        }

        // If moving would result in collision (can't advance), treat as stopped
        float newX = calculateNewPositionX(delta, direction);
        if (newX == this.posX) {
            return true;
        }

        // Stop if unit in front is attacking and target not in range
        if (this.index != 0 && target != null && !target.isDead()
                && calculateDistance(target) >= this.range
                && this.allyBase.getUnitsPerLane().get(this.lane).get(this.index - 1).currentState == UnitState.ATTACKING) {
            return true;
        }

        // Stop if unit target in range
        if (target != null && !target.isDead()) {
            double distance = calculateDistance(target);
            if (distance <= this.range) {
                return true;
            }
        }

        // Stop if base target in range
        if (target == null && isNearEnemyBase(targetBase)) {
            return true;
        }

        return false;
    }

    /**
     * Checks for collisions with other units or the hero when moving to a new position.
     * Handles lane-based collision logic and hero proximity.
     *
     * @param newX Proposed new X position.
     * @param newY Proposed new Y position.
     * @return True if a collision would occur, false otherwise.
     */
    protected boolean checkUnitCollisions(float newX, float newY) {

        float distance = 0;
        float distanceHero = 0;
        if (this.getIndex() == 0){
            if (this.allyBase.getHero() != null){
                float dxH = newX - this.allyBase.getHero().getPosX();
                float dyH = newY - this.allyBase.getHero().getPosY();
                distanceHero = (float) Math.sqrt(dxH * dxH + dyH * dyH);
                if (distanceHero <= this.allyBase.getHero().getWidth()) {
                    return true;
                }
            }
            return false;
        }
        if (this.getIndex() != 0){
            float dx = newX - this.allyBase.getUnitsPerLane().get(this.getLane()).get(this.getIndex() - 1).getPosX();
            float dy = newY - this.allyBase.getUnitsPerLane().get(this.getLane()).get(this.getIndex() - 1).getPosY();
            distance = (float) Math.sqrt(dx * dx + dy * dy);
        }

        if (this.allyBase.getHero() != null){
            float dxH = newX - this.allyBase.getHero().getPosX();
            float dyH = newY - this.allyBase.getHero().getPosY();
            distanceHero = (float) Math.sqrt(dxH * dxH + dyH * dyH);
            distance = Math.min(distance, distanceHero);
        }

        if (distance <= this.getWidth()) {
            return true;
        }
        return false;
    }

    /**
     * Calculates the new X position after movement, considering direction and collision.
     *
     * @param delta     Time elapsed since last frame (seconds).
     * @param direction Movement direction (1 = right, -1 = left).
     * @return New X position, or current position if collision occurs.
     */
    protected float calculateNewPositionX(float delta, int direction) {
        float newX = this.posX + (this.speed * delta * direction);

        // Check collision with enemy base hitbox
        if (wouldCollideWithBase(newX) || checkUnitCollisions(newX, posY)) {
            return this.posX; // Stay in place
        }
        return newX;
    }

    /**
     * Executes the unit's special ability. Subclasses should override to implement specific logic.
     */
    public void specialAbility() {
        // Implement special ability logic here
    }

    /**
     * Moves the unit for the current frame. Default implementation moves right.
     * Handles attack animation, cooldown, state changes, and movement logic.
     * Subclasses may override to provide specific movement behavior.
     *
     * @param delta Time elapsed since last frame (seconds).
     */
    public void move(float delta) {
        if (currentState == UnitState.ATTACKING) {
            attackAnimationTimer -= delta;
            // advance shared animation timer so attack animations progress when
            // using default move implementation
            this.stateTime += delta;
            if (attackAnimationTimer > 0) {
                return;
            }
            if (target != null && !target.isDead()) {
                if (attackCooldown <= 0) {
                    attack();
                }
                else {
                    currentState = UnitState.IDLE;
                }
            }
            else {
                // Cible morte → repasser à WALKING
                currentState = UnitState.WALKING;
                target = null;
            }
            return;
        }

        if (target != null) {
            if (target.isDead()) {
                target = null;
                currentState = UnitState.WALKING;
                return;
            }

            double distance = calculateDistance(target);

            if (distance <= this.range) {
                attack();
                return;
            } else {
                currentState = UnitState.WALKING;
                float direction = (target.getPosX() > posX) ? 1 : -1;
                setSpritePosX(posX + direction * speed * delta);
                return;
            }
        }
        // If no unit target, handle enemy base: attack when in range, otherwise move toward it
        if (targetBase != null) {
            float baseX = targetBase.getPosition().getPosX();
            float baseY = targetBase.getPosition().getPosY();
            double distanceToBase = Math.sqrt(Math.pow(baseX - this.posX, 2) + Math.pow(baseY - this.posY, 2));
            if (distanceToBase <= BASE_ATTACK_RANGE) {
                // In range of base: attempt an attack if cooldown ready
                if (attackCooldown <= 0) {
                    // Use dedicated base attack helper so we apply base damage and animation
                    attackBase(targetBase);
                    this.stateTime = 0f;
                    return;
                } else {
                    currentState = UnitState.IDLE;
                    this.stateTime += delta;
                    return;
                }
            } else {
                // Not yet in base range: walk towards the base
                currentState = UnitState.WALKING;
                int direction = (targetBase.getPosition().getPosX() > posX) ? 1 : -1;
                setSpritePosX(calculateNewPositionX(delta, direction));
                this.stateTime += delta;
                return;
            }
        }

        currentState = UnitState.IDLE;
        // advance idle animation timer for units using default move()
        this.stateTime += delta;
    }

    /**
     * Returns the current state of the unit (for animation and logic).
     *
     * @return Current UnitState.
     */
    public UnitState getCurrentState() {
        return currentState;
    }
}