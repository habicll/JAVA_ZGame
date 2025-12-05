package com.main.entities.player;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.entities.Unit;
import com.main.entities.units.Sniper;
import com.main.map.Base;
import com.main.map.WarMap;
import com.main.weapons.SniperRifle;
import com.main.weapons.AssaultRifle;
import com.main.weapons.Pistol;
import com.main.weapons.SMG;
import com.main.weapons.Shotgun;
import com.main.weapons.Weapon;
import com.utils.AudioSettings;
import com.ui.Inventory;

/**
 * Represents the main playable hero unit in the game.
 * Inherits from Unit and provides hero-specific stats, abilities, and
 * behaviors.
 */
public class Hero extends Unit {

    /**
     * Represents the possible directions the hero can face or attack.
     */
    protected enum Direction {
        UP, DOWN, LEFT, RIGHT,
        UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT,
        ATTACKUP, ATTACKDOWN, ATTACKLEFT, ATTACKRIGHT, DIE
    }

    @lombok.Generated
    /**
     * Returns the duration of the current attack animation according to the hero's
     * direction.
     *
     * @return Duration of the current attack animation in seconds
     */
    protected float getCurrentAttackAnimationDuration() {
        switch (direction) {
            case ATTACKRIGHT:
                return (AttackRight != null) ? AttackRight.getAnimationDuration() : 0f;
            case ATTACKLEFT:
                return (AttackLeft != null) ? AttackLeft.getAnimationDuration() : 0f;
            case ATTACKUP:
                return (AttackUp != null) ? AttackUp.getAnimationDuration() : 0f;
            case ATTACKDOWN:
                return (AttackDown != null) ? AttackDown.getAnimationDuration() : 0f;
            default:
                return 0f;
        }
    }

    /**
     * Current experience points of the hero.
     */
    protected int xp;

    /**
     * Current level of the hero.
     */
    protected int level;

    /**
     * List of abilities possessed by the hero.
     */
    protected List<Ability> abilities = new ArrayList<>();

    /**
     * Strength attribute of the hero.
     */
    protected int strength;

    /**
     * Dexterity attribute of the hero.
     */
    protected int dexterity;

    /**
     * Agility attribute of the hero.
     */
    protected int agility;

    /**
     * Weapon currently equipped by the hero.
     */
    protected Weapon weapon;

    /**
     * Amount of gold the hero possesses.
     */
    protected int gold = 0;

    /**
     * Maximum health value for the hero.
     */
    protected int maxHealth = 500;

    /**
     * Reference to the game map.
     */
    private WarMap map;

    /**
     * Animation for walking right.
     */
    private Animation<TextureRegion> walkRight;
    /**
     * Animation for walking left.
     */
    private Animation<TextureRegion> walkLeft;
    /**
     * Animation for walking up.
     */
    private Animation<TextureRegion> walkUp;
    /**
     * Animation for walking down.
     */
    private Animation<TextureRegion> walkDown;

    /**
     * Animation for walking up right.
     */
    private Animation<TextureRegion> walkUR;

    /**
     * Animation for walking up left.
     */
    private Animation<TextureRegion> walkUL;

    /**
     * Animation for walking down right.
     */
    private Animation<TextureRegion> walkDR;

    /**
     * Animation for walking down left.
     */
    private Animation<TextureRegion> walkDL;

    private Animation<TextureRegion> die;

    /**
     * Idle textures for each direction (cardinal and diagonal).
     */
    private TextureRegion idle, idleR, idleL, idleU, idleD, idleUR, idleUL, idleDR, idleDL;

    /**
     * Animation for attacking right.
     */
    protected Animation<TextureRegion> AttackRight;
    /**
     * Animation for attacking left.
     */
    protected Animation<TextureRegion> AttackLeft;
    /**
     * Animation for attacking up.
     */
    protected Animation<TextureRegion> AttackUp;
    /**
     * Animation for attacking down.
     */
    protected Animation<TextureRegion> AttackDown;

    /**
     * Indicates if the hero is currently moving.
     */
    private boolean moving = false;

    /**
     * Current direction of the hero.
     */
    protected Direction direction = Direction.DOWN;

    /**
     * Previous non-attack direction to revert to after attack animation.
     */
    private Direction prevDirection = Direction.DOWN;

    /**
     * List of loaded textures for the hero's animations.
     */
    private List<Texture> loadedTextures = new ArrayList<>();

    /**
     * Frame duration for attack animations.
     */
    private final float FRAME_DURATION = 0.12f;

    /**
     * Frame duration for walking animations.
     */
    private final float FRAME_DURATIONW = 0.12f;

    /**
     * Timer for retargeting enemies.
     */
    private float retargetTimer = 0;

    /**
     * Interval for retargeting enemies (in seconds).
     */
    private final float retargetInterval = 0.1f; // 100ms

    /**
     * Time for gaining gold
     */

    private float goldTimer = 0f;

    /**
     * Interval before gaining gold passively
     */
    private final float goldInterval = 3f;

    /**
     * Sound effect played when the hero shoots.
     */
    private Sound shootSound;

    /**
     * Constructs a new Hero instance with initial position, map, and allied base.
     *
     * @param posX     Initial X position
     * @param posY     Initial Y position
     * @param map      Reference to the game map
     * @param allyBase Reference to the allied base
     */
    public Hero(float posX, float posY, WarMap map, Base allyBase) {
        super("sold/Idle.png", posX, posY);
        this.allyBase = allyBase;

        TextureRegion[] rightFrames = loadFrames("sold/RIght%d.png", 8);
        walkRight = new Animation<>(FRAME_DURATION, rightFrames);
        walkRight.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] leftFrames = loadFrames("sold/Left%d.png", 8);
        walkLeft = new Animation<>(FRAME_DURATION, leftFrames);
        walkLeft.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] upFrames = loadFrames("sold/Up%d.png", 8);
        walkUp = new Animation<>(FRAME_DURATIONW, upFrames);
        walkUp.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] downFrames = loadFrames("sold/Down%d.png", 8);
        walkDown = new Animation<>(FRAME_DURATIONW, downFrames);
        walkDown.setPlayMode(Animation.PlayMode.LOOP);

        // Diagonal walk animations (if provided in assets)
        TextureRegion[] urFrames = loadFrames("sold/WalkUR%d.png", 8);
        walkUR = new Animation<>(FRAME_DURATION, urFrames);
        walkUR.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] ulFrames = loadFrames("sold/WalkUL%d.png", 8);
        walkUL = new Animation<>(FRAME_DURATION, ulFrames);
        walkUL.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] drFrames = loadFrames("sold/WalkDR%d.png", 8);
        walkDR = new Animation<>(FRAME_DURATION, drFrames);
        walkDR.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] dlFrames = loadFrames("sold/WalkDL%d.png", 8);
        walkDL = new Animation<>(FRAME_DURATION, dlFrames);
        walkDL.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] dieFrames = loadFrames("sold/Die%d.png", 7);
        die = new Animation<>(FRAME_DURATION, dieFrames);
        die.setPlayMode(Animation.PlayMode.NORMAL);

        // Load single-frame idle textures (cardinal + diagonal)
        idle = loadSingle("sold/Idle.png");
        idleR = loadSingle("sold/IdleR.png");
        idleL = loadSingle("sold/IdleL.png");
        idleU = loadSingle("sold/IdleU.png");
        idleD = loadSingle("sold/IdleD.png");
        idleUR = loadSingle("sold/IdleUR.png");
        idleUL = loadSingle("sold/IdleUL.png");
        idleDR = loadSingle("sold/IdleDR.png");
        idleDL = loadSingle("sold/IdleDL.png");

        // Fallback: if any idle direction is missing, use main idle
        if (idleR == null)
            idleR = idle;
        if (idleL == null)
            idleL = idle;
        if (idleU == null)
            idleU = idle;
        if (idleD == null)
            idleD = idle;
        if (idleUR == null)
            idleUR = idle;
        if (idleUL == null)
            idleUL = idle;
        if (idleDR == null)
            idleDR = idle;
        if (idleDL == null)
            idleDL = idle;

        TextureRegion[] rightAttack = loadFrames("sold/AttackR%d.png", 4);
        AttackRight = new Animation<>(FRAME_DURATION, rightAttack);
        AttackRight.setPlayMode(Animation.PlayMode.NORMAL);

        TextureRegion[] leftAttack = loadFrames("sold/AttackL%d.png", 4);
        AttackLeft = new Animation<>(FRAME_DURATION, leftAttack);
        AttackLeft.setPlayMode(Animation.PlayMode.NORMAL);

        TextureRegion[] upAttack = loadFrames("sold/AttackU%d.png", 4);
        AttackUp = new Animation<>(FRAME_DURATIONW, upAttack);
        AttackUp.setPlayMode(Animation.PlayMode.NORMAL);

        TextureRegion[] downAttack = loadFrames("sold/AttackD%d.png", 4);
        AttackDown = new Animation<>(FRAME_DURATIONW, downAttack);
        AttackDown.setPlayMode(Animation.PlayMode.NORMAL);

        this.health = 500;
        this.weapon = new Pistol();
        this.speed = 5;
        this.attackSpeed = 1;
        this.map = map;

        // initialiser gold
        this.gold = 50; // Start with 50 gold
    }

    /**
     * Loads an array of frames for an animation from file names matching a pattern.
     *
     * @param pattern Filename pattern with a %d placeholder for frame number
     * @param count   Number of frames to load
     * @return Array of loaded TextureRegion frames
     */
    private TextureRegion[] loadFrames(String pattern, int count) {
        TextureRegion[] frames = new TextureRegion[count];
        for (int i = 0; i < count; i++) {
            Texture tex = new Texture(Gdx.files.internal(String.format(pattern, i + 1)));
            loadedTextures.add(tex);
            frames[i] = new TextureRegion(tex);
        }
        return frames;
    }

    /**
     * Loads a single texture region from a file path.
     * 
     * @param path Path to the texture file
     * @return Loaded TextureRegion, or null if not found
     */
    private TextureRegion loadSingle(String path) {
        try {
            Texture tex = new Texture(Gdx.files.internal(path));
            loadedTextures.add(tex);
            return new TextureRegion(tex);
        } catch (Exception e) {
            return null;
        }
    }

    @lombok.Generated
    /**
     * Met à jour le Hero (déplacements avec LibGDX)
     * 
     * @param delta     Le temps écoulé depuis la dernière frame
     * @param mapWidth  Largeur de la map en pixels
     * @param mapHeight Hauteur de la map en pixels
     */
    public void update(float delta, float mapWidth, float mapHeight, List<Unit> units) {

        // --- RETARGET SYSTEM (toutes les 100 ms) ---
        retargetTimer += delta;
        if (retargetTimer >= retargetInterval) {
            retargetTimer = 0;

            Unit closest = findClosestEnemy(units);

            // Si pas de cible, ou morte, ou qu’un autre ennemi est plus proche → switch
            if (target == null || target.isDead() ||
                    (closest != null && calculateDistance(closest) < calculateDistance(target))) {

                target = closest;
            }
        }

        goldTimer += delta;
        if (goldTimer >= goldInterval) {
            this.addGold(10);
            goldTimer = 0f;
        }

        // --- ATTAQUE ---
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (target != null && !target.isDead()) {
                this.attack();
            }
        }
        this.updateCooldown(delta);

        // Changing weapon
        // Pistol
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            if (this.gold >= 50 && !(this.weapon instanceof Pistol)) {
                this.weapon = new Pistol();
                this.removeGold(50);
                System.out.println("Changed weapon to Pistol : " + this.weapon.getDamage() + " damage, "
                        + this.weapon.getAttackSpeed() + " attacks/sec, " + this.weapon.getRange() + " range, "
                        + this.weapon.getMaxMunitions() + " munitions.");
            } else if (this.weapon instanceof Pistol) {
                System.out.println("You already have a Pistol.");
            } else {
                System.out.println("Not enough gold : 50 gold required to buy a Pistol -> You only have " + this.gold);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            if (this.gold >= 70 && !(this.weapon instanceof Shotgun)) {
                this.weapon = new Shotgun();
                this.removeGold(70);
                System.out.println("Changed weapon to Shotgun : " + this.weapon.getDamage() + " damage, "
                        + this.weapon.getAttackSpeed() + " attacks/sec, " + this.weapon.getRange() + " range, "
                        + this.weapon.getMaxMunitions() + " munitions.");
            } else if (this.weapon instanceof Shotgun) {
                System.out.println("You already have a Shotgun.");
            } else {
                System.out.println("Not enough gold : 70 gold required to buy a Shotgun -> You only have " + this.gold);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            if (this.gold >= 100 && !(this.weapon instanceof SMG)) {
                this.weapon = new SMG();
                this.removeGold(100);
                System.out.println("Changed weapon to SMG : " + this.weapon.getDamage() + " damage, "
                        + this.weapon.getAttackSpeed() + " attacks/sec, " + this.weapon.getRange() + " range, "
                        + this.weapon.getMaxMunitions() + " munitions.");
            } else if (this.weapon instanceof SMG) {
                System.out.println("You already have a SMG.");
            } else {
                System.out.println("Not enough gold : 100 gold required to buy a SMG -> You only have " + this.gold);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            if (this.gold >= 150 && !(this.weapon instanceof AssaultRifle)) {
                this.weapon = new AssaultRifle();
                this.removeGold(150);
                System.out.println("Changed weapon to Assault Rifle : " + this.weapon.getDamage() + " damage, "
                        + this.weapon.getAttackSpeed() + " attacks/sec, " + this.weapon.getRange() + " range, "
                        + this.weapon.getMaxMunitions() + " munitions.");
            } else if (this.weapon instanceof AssaultRifle) {
                System.out.println("You already have a Assault Rifle.");
            } else {
                System.out.println(
                        "Not enough gold : 150 gold required to buy an Assault Rifle -> You only have " + this.gold);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            if (this.gold >= 200 && !(this.weapon instanceof SniperRifle)) {
                this.weapon = new SniperRifle();
                this.removeGold(200);
                System.out.println("Changed weapon to Sniper Rifle : " + this.weapon.getDamage() + " damage, "
                        + this.weapon.getAttackSpeed() + " attacks/sec, " + this.weapon.getRange() + " range, "
                        + this.weapon.getMaxMunitions() + " munitions.");
            } else if (this.weapon instanceof SniperRifle) {
                System.out.println("You already have a Sniper Rifle.");
            } else {
                System.out.println(
                        "Not enough gold : 200 gold required to buy a Sniper Rifle -> You only have " + this.gold);
            }
        }

        // -- Reload Weapon --
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            this.weapon.reload();
            this.attackCooldown = this.weapon.getReloadTimer();
        }

        // --- DÉPLACEMENT ---
        moving = false;

        // Support diagonal movement by reading keys independently
        float base = speed * delta * 60f;
        float dx = 0f, dy = 0f;
        boolean pressRight = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean pressLeft = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean pressUp = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean pressDown = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);

        if (pressRight && !pressLeft)
            dx += base;
        if (pressLeft && !pressRight)
            dx -= base;
        if (pressUp && !pressDown)
            dy += base;
        if (pressDown && !pressUp)
            dy -= base;

        if (dx != 0f || dy != 0f) {
            // determine facing based on dx/dy signs
            if (dx > 0 && dy > 0) {
                direction = Direction.UP_RIGHT;
            } else if (dx < 0 && dy > 0) {
                direction = Direction.UP_LEFT;
            } else if (dx > 0 && dy < 0) {
                direction = Direction.DOWN_RIGHT;
            } else if (dx < 0 && dy < 0) {
                direction = Direction.DOWN_LEFT;
            } else if (dx > 0) {
                direction = Direction.RIGHT;
            } else if (dx < 0) {
                direction = Direction.LEFT;
            } else if (dy > 0) {
                direction = Direction.UP;
            } else if (dy < 0) {
                direction = Direction.DOWN;
            }
            tryMove(dx, dy, mapWidth, mapHeight, target);
            moving = true;
        }

        if (moving) {
            stateTime += delta;
        } else {
            if (direction == Direction.ATTACKDOWN || direction == Direction.ATTACKLEFT
                    || direction == Direction.ATTACKRIGHT || direction == Direction.ATTACKUP) {

                stateTime += delta;

                // Si animation d'attaque finie → retour direction précédente
                float attackDur = getCurrentAttackAnimationDuration();
                if (attackDur > 0f && stateTime >= attackDur) {
                    direction = prevDirection;
                    stateTime = 0f;
                }

            } else if (direction == Direction.DIE) {
                stateTime += delta;

            } else {
                stateTime = 0f;
            }
        }
        if (this.health <= 0) {
            if (direction != Direction.DIE) {
                direction = Direction.DIE;
                moving = false;
                stateTime = 0f; 
            }
        }
    }

    /**
     * Unified movement method - replaces moveUp/Down/Left/Right
     * 
     * @param deltaX       X velocity
     * @param deltaY       Y velocity
     * @param mapWidth     Map width boundary
     * @param mapHeight    Map height boundary
     * @param closestEnemy Cached closest enemy for collision
     */
    private void tryMove(float deltaX, float deltaY, float mapWidth, float mapHeight, Unit closestEnemy) {
        float newX;
        float newY;
        if (direction == Direction.DOWN_LEFT || direction == Direction.DOWN_RIGHT || direction == Direction.UP_LEFT
                || direction == Direction.UP_RIGHT) {
            newX = this.posX + deltaX / 2;
            newY = this.posY + deltaY / 2;
        } else {
            newX = this.posX + deltaX;
            newY = this.posY + deltaY;
        }

        // Check map collision and enemy collision
        if (!map.isCollisionRect(newX, newY, this.width, this.height) &&
                !checkHeroEnemyCollisions(newX, newY, closestEnemy)
                && !checkHeroSoldierCollisions(newX, newY, findClosestSoldier(this.allyBase.getUnits()))) {

            // Apply boundaries and set position
            newX = Math.max(0, Math.min(newX, mapWidth - this.width));
            newY = Math.max(0, Math.min(newY, mapHeight - this.height));

            this.setSpritePosX(newX);
            this.setSpritePosY(newY);
        }
    }

    /**
     * Checks if the hero collides with a given enemy at the specified position.
     *
     * @param newX  Nouvelle position X du héros
     * @param newY  Nouvelle position Y du héros
     * @param enemy Ennemi à vérifier
     * @return true si collision détectée, false sinon
     */
    private boolean checkHeroEnemyCollisions(float newX, float newY, Unit enemy) {
        if (enemy == null || enemy.isDead()) {
            return false;
        }

        // Calculate distance between hero and enemy
        float dx = newX - enemy.getPosX();
        float dy = newY - enemy.getPosY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < this.getWidth()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the hero collides with a given allied soldier at the specified
     * position.
     *
     * @param newX    Nouvelle position X du héros
     * @param newY    Nouvelle position Y du héros
     * @param soldier Soldat allié à vérifier
     * @return true si collision détectée, false sinon
     */
    protected boolean checkHeroSoldierCollisions(float newX, float newY, Unit soldier) {
        if (soldier == null || soldier.isDead()) {
            return false;
        }

        // Calculate distance between hero and soldier
        float dx = newX - soldier.getPosX();
        float dy = newY - soldier.getPosY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < this.getWidth()) {
            return true;
        }
        return false;
    }

    @Override
    /**
     * Find the closest enemy within attack range
     * 
     * @param units List of all enemy units
     * @param range Attack range
     * @return Closest enemy in range, or null if none found
     */
    protected Unit findClosestEnemy(List<Unit> enemies) {
        Unit closest = null;
        double minDistance = Double.MAX_VALUE;
        for (Unit enemy : enemies) {
            double distance = calculateDistance(enemy);
            if (distance < minDistance && !enemy.isDead()) {
                minDistance = distance;
                closest = enemy;
            }
        }
        return closest;
    }

    /**
     * Finds the closest allied soldier to the hero.
     *
     * @param units Liste des unités alliées
     * @return Soldat le plus proche ou null si aucun trouvé
     */
    protected Unit findClosestSoldier(List<Unit> units) {
        Unit closest = null;
        double minDistance = Double.MAX_VALUE;
        for (Unit soldier : units) {
            double distance = calculateDistance(soldier);
            if (distance < minDistance && !soldier.isDead()) {
                minDistance = distance;
                closest = soldier;
            }
        }
        return closest;
    }

    /**
     * Finds the closest enemy within attack range.
     *
     * @param enemies List of all enemy units
     * @return Closest enemy in range, or null if none found
     */
    protected Unit findClosestEnemyInRange(List<Unit> enemies) {
        Unit closest = null;
        double bestDist = Double.MAX_VALUE;

        for (Unit enemy : enemies) {
            if (enemy.isDead())
                continue;

            double distance = calculateDistance(enemy);
            if (distance < this.weapon.getRange() && distance < bestDist) {
                bestDist = distance;
                closest = enemy;
            }
        }
        return closest;
    }

    // Keep public methods for backward compatibility
    /**
     * Moves the hero up on the map.
     * 
     * @param delta     Time since last frame
     * @param mapHeight Height of the map
     * @param enemies   List of enemy units
     */
    protected void moveUp(float delta, float mapHeight, List<Unit> enemies) {
        tryMove(0, speed * delta * 60, Float.MAX_VALUE, mapHeight, findClosestEnemy(enemies));
    }

    /**
     * Moves the hero down on the map.
     * 
     * @param delta   Time since last frame
     * @param enemies List of enemy units
     */
    protected void moveDown(float delta, List<Unit> enemies) {
        tryMove(0, -speed * delta * 60, Float.MAX_VALUE, Float.MAX_VALUE, findClosestEnemy(enemies));
    }

    /**
     * Moves the hero left on the map.
     * 
     * @param delta   Time since last frame
     * @param enemies List of enemy units
     */
    protected void moveLeft(float delta, List<Unit> enemies) {
        tryMove(-speed * delta * 60, 0, Float.MAX_VALUE, Float.MAX_VALUE, findClosestEnemy(enemies));
    }

    /**
     * Moves the hero right on the map.
     * 
     * @param delta    Time since last frame
     * @param mapWidth Width of the map
     * @param enemies  List of enemy units
     */
    protected void moveRight(float delta, float mapWidth, List<Unit> enemies) {
        tryMove(speed * delta * 60, 0, mapWidth, Float.MAX_VALUE, findClosestEnemy(enemies));
    }

    /**
     * Sets the hero's weapon.
     * 
     * @param weapon Weapon to set
     */
    public void setWeapon(Weapon weapon) {
        if (weapon != null) {
            this.weapon = weapon;
        }
    }

    /**
     * Attacks the current target if possible. Handles cooldown, range, damage, and
     * animation direction.
     * Plays the shoot sound if available and reloads weapon if out of munitions.
     *
     * Overrides the attack behavior from Unit.
     */
    @Override
    public void attack() {
        if (target == null || target.isDead()) {
            return;
        }
        double distance = Math.sqrt(Math.pow(this.posX - target.getPosX(), 2) +
                Math.pow(this.posY - target.getPosY(), 2));
        if (distance > weapon.getRange()) {
            return;
        }
        if (attackCooldown <= 0 && weapon != null) {
            if (weapon.getMunitions() > 0 || weapon.getMaxMunitions() == -1) {
                // Save current non-attack facing so we can revert after animation
                if (direction == Direction.RIGHT || direction == Direction.LEFT || direction == Direction.UP
                        || direction == Direction.DOWN) {
                    prevDirection = direction;
                }
                if (direction == Direction.RIGHT) {
                    direction = Direction.ATTACKRIGHT;
                } else if (direction == Direction.LEFT) {
                    direction = Direction.ATTACKLEFT;
                } else if (direction == Direction.UP) {
                    direction = Direction.ATTACKUP;
                } else if (direction == Direction.DOWN) {
                    direction = Direction.ATTACKDOWN;
                }
                stateTime = 0f;

                weapon.attack();
                int totalDamage = weapon.getDamage();

                target.takeDamage(totalDamage);
                attackCooldown = weapon.getAttackSpeed();

                // Jouer le son de tir si les sons sont activés
                if (shootSound != null && AudioSettings.isSoundEnabled()) {
                    shootSound.play(0.7f); // Volume à 70%
                }
            } else {
                weapon.reload();
                attackCooldown = weapon.getReloadTimer();
            }
        }
    }

    @lombok.Generated
    /**
     * Renders the hero on the screen using the current animation frame and
     * direction.
     * Adjusts the visual size and alignment of the sprite for correct display.
     *
     * @param batch SpriteBatch used for drawing the hero
     */
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;
        float visualWidth;
        float visualHeight;

        switch (direction) {
            case RIGHT:
                if (moving) {
                    currentFrame = walkRight.getKeyFrame(stateTime, true);
                    visualWidth = 50;
                    visualHeight = 50;
                } else {
                    currentFrame = (idleR != null) ? idleR : idle;
                    visualWidth = 30;
                    visualHeight = 50;
                }
                break;
            case LEFT:
                if (moving) {
                    currentFrame = walkLeft.getKeyFrame(stateTime, true);
                    visualWidth = 50;
                    visualHeight = 50;
                } else {
                    currentFrame = (idleL != null) ? idleL : idle;
                    visualWidth = 30;
                    visualHeight = 50;
                }
                break;
            case UP:
                if (moving) {
                    currentFrame = walkUp.getKeyFrame(stateTime, true);
                    visualWidth = 30;
                    visualHeight = 50;
                } else {
                    currentFrame = (idleU != null) ? idleU : idle;
                    visualWidth = 30;
                    visualHeight = 50;
                }
                break;
            case UP_RIGHT:
                if (moving) {
                    currentFrame = walkUR.getKeyFrame(stateTime, true);
                    visualWidth = 40;
                    visualHeight = 50;
                } else {
                    currentFrame = (idleUR != null) ? idleUR : idle;
                    visualWidth = 40;
                    visualHeight = 50;
                }
                break;
            case UP_LEFT:
                if (moving) {
                    currentFrame = walkUL.getKeyFrame(stateTime, true);
                    visualWidth = 40;
                    visualHeight = 50;
                } else {
                    currentFrame = (idleUL != null) ? idleUL : idle;
                    visualWidth = 40;
                    visualHeight = 50;
                }
                break;
            case DOWN_RIGHT:
                if (moving) {
                    currentFrame = walkDR.getKeyFrame(stateTime, true);
                    visualWidth = 40;
                    visualHeight = 50;
                } else {
                    currentFrame = (idleDR != null) ? idleDR : idle;
                    visualWidth = 40;
                    visualHeight = 50;
                }
                break;
            case DOWN_LEFT:
                if (moving) {
                    currentFrame = walkDL.getKeyFrame(stateTime, true);
                    visualWidth = 40;
                    visualHeight = 50;
                } else {
                    currentFrame = (idleDL != null) ? idleDL : idle;
                    visualWidth = 40;
                    visualHeight = 50;
                }
                break;
            case ATTACKDOWN:
                currentFrame = AttackDown.getKeyFrame(stateTime, false);
                visualWidth = 30;
                visualHeight = 50;
                break;
            case ATTACKRIGHT:
                currentFrame = AttackRight.getKeyFrame(stateTime, false);
                visualWidth = 40;
                visualHeight = 50;
                break;
            case ATTACKLEFT:
                currentFrame = AttackLeft.getKeyFrame(stateTime, false);
                visualWidth = 40;
                visualHeight = 50;
                break;
            case ATTACKUP:
                currentFrame = AttackUp.getKeyFrame(stateTime, false);
                visualWidth = 30;
                visualHeight = 50;
                break;
            case DIE:
                currentFrame = die.getKeyFrame(stateTime, false);
                visualWidth = 40;
                visualHeight = 50;
                break;
            default:
                if (moving) {
                    currentFrame = walkDown.getKeyFrame(stateTime, true);
                    visualWidth = 30;
                    visualHeight = 50;
                } else {
                    currentFrame = (idleD != null) ? idleD : idle;
                    visualWidth = 30;
                    visualHeight = 50;
                }
                break;
        }

        // Dessiner le sprite plus grand visuellement (90x90) mais hitbox reste 32x48

        // Centrer horizontalement et aligner les pieds du sprite avec le bas de la
        // hitbox
        float offsetX = (this.width - visualWidth) / 2;
        float offsetY = 0; // Aligner le bas du sprite avec le bas de la hitbox (pieds alignés)

        batch.draw(currentFrame, this.posX + offsetX, this.posY + offsetY, visualWidth, visualHeight);
    }

    // === HEALTH SYSTEM ===

    /**
     * Get current health
     * 
     * @return Current health value
     */
    public int getCurrentHealth() {
        return this.health;
    }

    /**
     * Get maximum health
     * 
     * @return Maximum health value
     */
    public int getMaxHealth() {
        return this.maxHealth;
    }

    /**
     * Set maximum health
     * 
     * @param maxHealth New maximum health
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
    }

    /**
     * Heal the hero
     * 
     * @param amount Amount to heal
     */
    public void heal(int amount) {
        this.health = Math.min(maxHealth, this.health + amount);
    }

    // === GOLD SYSTEM ===

    /**
     * Get current gold amount
     * 
     * @return Current gold
     */
    public int getGold() {
        return this.gold;
    }

    /**
     * Set gold amount directly
     * 
     * @param gold Gold amount to set
     */
    public void setGold(int gold) {
        this.gold = Math.max(0, gold);
    }

    /**
     * Add gold to current amount
     * 
     * @param amount Amount to add
     */
    public void addGold(int amount) {
        this.gold += amount;
    }

    /**
     * Remove gold from current amount
     * 
     * @param amount Amount to remove
     * @return true if there was enough gold to remove, false otherwise
     */
    public boolean removeGold(int amount) {
        if (this.gold >= amount) {
            this.gold -= amount;
            return true;
        }
        return false;
    }

    // === AUDIO SYSTEM ===

    /**
     * Set the shoot sound effect
     * 
     * @param shootSound Sound to play when hero shoots
     */
    public void setShootSound(Sound shootSound) {
        this.shootSound = shootSound;
    }

    public Sound getShootSound() {
        return shootSound;
    }

    public Weapon getWeapon() {
        return this.weapon;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

}