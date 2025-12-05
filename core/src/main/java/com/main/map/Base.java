package com.main.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Rectangle;
import com.main.GameScreen;
import com.main.entities.Unit;
import com.main.entities.enemies.CZombie;
import com.main.entities.enemies.FZombie;
import com.main.entities.enemies.WZombie;
import com.main.entities.player.Hero;
import com.main.entities.units.Melee;
import com.main.entities.units.Sniper;
import com.main.entities.units.Tank;
import com.main.utils.Position;

/**
 * Represents a base in the game, either player or enemy.
 * <p>
 * Manages unit spawning, health, position, attack power, collision box, and
 * lane organization.
 * Handles logic for buying units, spawning units, updating unit states, and
 * interactions with the map and other entities.
 * Player bases spawn soldiers, enemy bases spawn zombies. Each base tracks
 * units per lane and manages their updates and interactions.
 */
public class Base {

    /**
     * Enum representing the types of units that can be spawned by a base.
     * Used to determine spawning logic and unit costs.
     */
    /**
     * Types of units that can be spawned by a base.
     */
    public enum Type {
        /** Melee unit. */
        MELEE,
        /** Tank unit. */
        TANK,
        /** Sniper unit. */
        SNIPER,
        /** Woman unit. */
        WOMAN,
        /** Crawl unit. */
        CRAWL,
        /** Fast unit. */
        FAST
    }

    /**
     * Current health of the base. When it reaches zero, the base is destroyed.
     */
    private int health = 1000;
    /**
     * Position of the base on the map (X, Y coordinates).
     */
    private final Position position;
    /**
     * Attack power of the base, used for damage calculations.
     */
    private final int attackPower = 50;
    /**
     * Timer tracking the time since the last unit spawn.
     */
    private float lastSpawn;
    /**
     * List of all units currently managed by this base.
     */
    private List<Unit> units;
    /**
     * List of units organized per lane (3 lanes per base).
     */
    private List<List<Unit>> unitsPerLane;
    /**
     * Random number generator for spawning logic.
     */
    private final Random random;
    /**
     * Flag indicating if this base is the player base (true) or enemy base (false).
     * Player bases spawn soldiers, enemy bases spawn zombies.
     */
    private final boolean isPlayerBase;
    /**
     * Name of the base, used for debugging and logging.
     */
    private final String name;
    /**
     * Collision box (hitbox) of the base, spanning 3 tiles wide and the full map
     * height.
     */
    private Rectangle collisionBox;
    /**
     * Size of a tile in the map (pixels).
     */
    private static final int TILE_SIZE = 16;
    /**
     * Scale factor for the map, used to calculate positions and sizes.
     */
    private static final float SCALE = 2.0f;
    /**
     * Array of Y coordinates for unit spawn points (top, middle, bottom lanes).
     */
    private final int[] spawnPointsY;
    /**
     * Reference to the hero associated with this base (player base only).
     */
    private Hero player = null;

    private float deathTimer = 0f;
    private static final float DEATH_ANIM_DURATION = 1.2f;

    /**
     * Constructs a new Base instance with specified position, type, and map height.
     * Initializes lanes, spawn points, collision box, and other attributes.
     *
     * @param posX         X position of the base on the map
     * @param posY         Y position of the base on the map
     * @param isPlayerBase True if this is the player base, false for enemy base
     * @param mapHeight    Height of the map (used for hitbox and spawn
     *                     calculations)
     */
    public Base(int posX, int posY, boolean isPlayerBase, int mapHeight) {
        this.position = new Position(posX, posY);
        lastSpawn = 0.0f;
        this.units = new ArrayList<>();
        this.unitsPerLane = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            this.unitsPerLane.add(new ArrayList<>());
        }
        random = new Random();
        this.isPlayerBase = isPlayerBase;
        this.name = isPlayerBase ? "PLAYER BASE" : "ENEMY BASE";
        this.spawnPointsY = new int[3];
        this.spawnPointsY[2] = mapHeight / 4 - (int) (TILE_SIZE * SCALE); // Haut (descendu d'une tuile)
        this.spawnPointsY[1] = mapHeight / 2; // Milieu
        this.spawnPointsY[0] = (3 * mapHeight) / 4 + (int) (TILE_SIZE * SCALE); // Bas (remonté d'une tuile)

        // Créer la hitbox : 3 tuiles de large * TILE_SIZE * SCALE, hauteur totale de la
        // map
        float boxWidth = 3 * TILE_SIZE * SCALE; // 3 tuiles * 16px * 2 = 96px
        float boxHeight = mapHeight; // Toute la hauteur de la map

        // Position : pour la base joueur (gauche), pour la base ennemie (droite -
        // largeur hitbox)
        float boxX = isPlayerBase ? posX : (posX - boxWidth + 65); // Décalage léger à droite pour la base zombie
        float boxY = 0; // Du bas de la map

        this.collisionBox = new Rectangle(boxX, boxY, boxWidth, boxHeight);
    }

    /**
     * Returns the current health of the base.
     *
     * @return Current health value
     */
    public int getHealth() {
        return health;
    }

    /**
     * Returns the position of the base on the map.
     *
     * @return Position object containing X and Y coordinates
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the collision box (hitbox) for the base.
     *
     * @param collisionBox Rectangle representing the new hitbox
     */
    public void setCollisionBox(Rectangle collisionBox) {
        this.collisionBox = collisionBox;
    }

    /**
     * Sets the list of units per lane for this base.
     *
     * @param unitsPerLane List of lists of units, one per lane
     */
    public void setUnitsPerLane(List<List<Unit>> unitsPerLane) {
        this.unitsPerLane = unitsPerLane;
    }

    /**
     * Returns the X position of the base on the map.
     *
     * @return X coordinate
     */
    public float getPosX() {
        return position.getPosX();
    }

    /**
     * Returns the attack power of the base.
     *
     * @return Attack power value
     */
    public int getAttackPower() {
        return attackPower;
    }

    /**
     * Sets the hero associated with this base (player base only).
     *
     * @param player Hero instance to associate
     */
    public void setHero(Hero player) {
        this.player = player;
    }

    /**
     * Returns the hero associated with this base (player base only).
     *
     * @return Hero instance or null if not set
     */
    public Hero getHero() {
        return player;
    }

    /**
     * Reduces the base's health by the specified damage amount.
     * Ensures health does not drop below zero. Logs the damage event.
     *
     * @param damage Amount of damage to apply
     */
    public void takeDamage(int damage) {
        int oldHealth = this.health;
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    /**
     * Returns the list of units organized per lane.
     *
     * @return List of lists of units per lane
     */
    public List<List<Unit>> getUnitsPerLane() {
        return unitsPerLane;
    }

    /**
     * Checks if the base is destroyed (health is zero or less).
     *
     * @return True if destroyed, false otherwise
     */
    public boolean isDestroyed() {
        return this.health <= 0;
    }

    /**
     * Returns the name of the base (for debugging/logging).
     *
     * @return Name string
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the collision box (hitbox) of the base.
     *
     * @return Rectangle representing the hitbox
     */
    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    /**
     * Adds a unit to the base's unit list if not null.
     *
     * @param unit Unit to add
     */
    public void addUnit(Unit unit) {
        if (unit != null) {
            this.units.add(unit);
        }
    }

    /**
     * Returns the list of all units managed by this base.
     *
     * @return List of units
     */
    public List<Unit> getUnits() {
        return this.units;
    }

    /**
     * Buys and spawns a unit of the specified type for the player base, if the hero
     * has enough gold.
     * Deducts gold, creates the unit, and adds it to the appropriate lane.
     *
     * @param unitType   Type of unit to buy (MELEE, TANK, SNIPER)
     * @param spawnIndex Index of the lane to spawn the unit (0: bottom, 1: middle,
     *                   2: top)
     * @param hero       Hero instance for gold deduction and ownership
     * @return The created Unit if successful, null otherwise
     */
    public Unit buyUnit(Type unitType, int spawnIndex, Hero hero) {
        int spawnY = spawnPointsY[spawnIndex];
        switch (unitType) {
            case MELEE:
                if (hero.getGold() >= Melee.COST) {
                    hero.removeGold(Melee.COST);
                    Unit melee = new Melee(100, spawnY, this);
                    this.unitsPerLane.get(spawnIndex).add(melee);
                    melee.setLane(spawnIndex);
                    melee.setIndex(this.unitsPerLane.get(spawnIndex).size() - 1);
                    return melee;
                }
                break;
            case TANK:
                if (hero.getGold() >= Tank.COST) {
                    hero.removeGold(Tank.COST);
                    Unit tank = new Tank(100, spawnY, this);
                    this.unitsPerLane.get(spawnIndex).add(tank);
                    tank.setLane(spawnIndex);
                    tank.setIndex(this.unitsPerLane.get(spawnIndex).size() - 1);
                    return tank;
                }
                break;
            case SNIPER:
                if (hero.getGold() >= Sniper.COST) {
                    hero.removeGold(Sniper.COST);
                    Unit sniper = new Sniper(100, spawnY, this);
                    this.unitsPerLane.get(spawnIndex).add(sniper);
                    sniper.setLane(spawnIndex);
                    sniper.setIndex(this.unitsPerLane.get(spawnIndex).size() - 1);
                    return sniper;
                }
                break;
            default:
                break;
        }
        return null;
    }

    /**
     * Spawns a new unit for the base if the spawn timer has elapsed.
     * Player bases spawn soldiers, enemy bases spawn zombies. Units are added to
     * the correct lane and tracked.
     *
     * @param screen GameScreen instance for map width and context
     * @param delta  Time elapsed since last update (seconds)
     * @return The spawned Unit if successful, null otherwise
     */
    public Unit spawnUnit(GameScreen screen, float delta) {
        if (lastSpawn >= 5.0f) {
            lastSpawn = 0.0f;

            if (!isPlayerBase) {
                // Spawn zombies (right side)
                Type[] zombieTypes = { Type.WOMAN, Type.CRAWL, Type.FAST };
                Type type = zombieTypes[random.nextInt(zombieTypes.length)];
                int rand = random.nextInt(3);
                int lane = spawnPointsY[rand];
                switch (type) {
                    case WOMAN:
                        Unit wzombie = new WZombie(screen.getMapWidth(), lane, this);
                        this.unitsPerLane.get(rand).add(wzombie);
                        wzombie.setLane(rand);
                        wzombie.setIndex(this.unitsPerLane.get(rand).size() - 1);
                        this.units.add(wzombie);
                        return wzombie;
                    case CRAWL:
                        Unit czombie = new CZombie(screen.getMapWidth(), lane, this);
                        this.unitsPerLane.get(rand).add(czombie);
                        czombie.setLane(rand);
                        czombie.setIndex(this.unitsPerLane.get(rand).size() - 1);
                        this.units.add(czombie);
                        return czombie;
                    case FAST:
                        Unit fzombie = new FZombie(screen.getMapWidth(), lane, this);
                        this.unitsPerLane.get(rand).add(fzombie);
                        fzombie.setLane(rand);
                        fzombie.setIndex(this.unitsPerLane.get(rand).size() - 1);
                        this.units.add(fzombie);
                        return fzombie;
                    default:
                        return null;
                }
            }
        }
        lastSpawn += delta;
        return null;
    }

    /**
     * Updates the index of each unit in every lane to maintain correct ordering.
     * Used after adding or removing units from lanes.
     */
    private void updateIndexes() {
        for (List<Unit> list : unitsPerLane) {
            int newIndex = 0;
            for (Unit unit : list) {
                unit.setIndex(newIndex++);
            }
        }
    }

    /**
     * Updates all units managed by this base.
     * Removes dead units, updates indexes, sets targets, updates cooldowns, and
     * handles movement and attack logic.
     * Units attack enemy base if no target is available and they are near the base.
     *
     * @param delta     Time elapsed since last update (seconds)
     * @param enemies   List of enemy units to attack
     * @param enemyBase Enemy base to attack if no enemies are present
     * @param hero      Hero instance to include as a target (if present)
     */
    public void updateUnits(float delta, List<Unit> enemies, Base enemyBase, Hero hero) {
        // Remove dead units
        deathTimer += delta;
        if (deathTimer >= DEATH_ANIM_DURATION) {
            units.removeIf(Unit::isDead);
            deathTimer = 0f;
        }
        for (List<Unit> list : unitsPerLane) {
            list.removeIf(Unit::isDead);
        }
        updateIndexes();

        // Update each unit
        for (

        Unit unit : units) {
            // Set enemy base as target
            unit.setTargetBase(enemyBase);

            // Filter only live enemies
            List<Unit> liveEnemies = new ArrayList<>();
            if (enemies != null) {
                for (Unit enemy : enemies) {
                    if (!enemy.isDead()) {
                        liveEnemies.add(enemy);
                    }
                }
            }
            if (hero != null)
                liveEnemies.add(hero);

            // Determine target and update cooldown BEFORE moving so move(delta) sees the
            // correct state
            unit.selectTarget(liveEnemies);
            unit.updateCooldown(delta);

            // If no target and near enemy base, attack the base
            if (unit.getTarget() == null && unit.isNearEnemyBase(enemyBase)) {
                unit.attackBase(enemyBase);
            }

            // Move handles attack triggering and animation timing internally
            unit.move(delta);
        }
    }
}