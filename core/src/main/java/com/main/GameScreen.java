package com.main;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.entities.Unit;
import com.main.entities.player.Hero;
import com.main.map.Base;
import com.main.map.WarMap;
import com.ui.BaseDestroyedOverlay;
import com.ui.BaseZombieDestroyedOverlay;
import com.ui.GameOverOverlay;
import com.ui.Inventory;
import com.ui.Target;
import com.ui.PauseOverlay;
import com.ui.UnitShop;
import com.ui.hud;
import com.utils.AudioSettings;

/**
 * Main game screen for the project, managing rendering, input, game state, and
 * UI overlays.
 * <p>
 * Handles the core gameplay loop, including unit spawning, camera movement,
 * collision rendering, HUD updates, overlays, and audio management.
 * Interacts with all major game entities, bases, hero, map, and UI components.
 */
public class GameScreen implements Screen {
    /**
     * Main SpriteBatch used for rendering all game sprites.
     */
    private SpriteBatch batch;
    /**
     * Optional background image texture (may be unused).
     */
    private Texture image;
    /**
     * ShapeRenderer for drawing debug shapes and collision boxes.
     */
    private ShapeRenderer shapeRenderer;

    /**
     * Reference to the main game application.
     */
    private Main game;
    /**
     * The main playable hero unit.
     */
    private Hero hero;
    /**
     * The game map, including collision and rendering logic.
     */
    private WarMap map;
    /**
     * Camera used for rendering and following the hero.
     */
    private OrthographicCamera camera;
    /**
     * Viewport for scaling and resizing the camera view.
     */
    private Viewport viewport;
    /**
     * Flag indicating if the hero is currently moving.
     */
    private boolean moving;
    /**
     * Reference to the enemy base (spawns zombies).
     */
    private Base enemyBase;
    /**
     * Reference to the player base (spawns soldiers).
     */
    private Base playerBase;
    /**
     * Width of the map in pixels.
     */
    private int mapWidth;
    /**
     * Height of the map in pixels.
     */
    private int mapHeight;

    /**
     * HUD display for health, gold, and other game stats.
     */
    private hud hudDisplay;
    /**
     * Overlay displayed when the game is over.
     */
    private GameOverOverlay gameOverOverlay;
    /**
     * Overlay displayed when the player base is destroyed.
     */
    private BaseDestroyedOverlay baseDestroyedOverlay;
    /**
     * Overlay displayed when the enemy base is destroyed (victory).
     */
    private BaseZombieDestroyedOverlay baseZombieDestroyedOverlay;
    /**
     * Overlay displayed when the game is paused.
     */
    private PauseOverlay pauseOverlay;
    /**
     * Flag to toggle display of unit attack ranges (activated with 'R' key).
     */
    private boolean showRanges = false;
    /**
     * UI component for buying units during gameplay.
     */
    private UnitShop unitShop;
    private Inventory inventory;
    private Target target;

    /**
     * Background music for the game.
     */
    private Music backgroundMusic;
    /**
     * Sound effect for shooting.
     */
    private Sound shootSound;

    /**
     * Font used for pause display (deprecated, replaced by PauseOverlay).
     */
    private BitmapFont pauseFont;

    private float deathTimer = 0f;
    private static final float DEATH_ANIM_DURATION = 1.2f;

    /**
     * Enum representing the current state of the game (playing, paused, game over,
     * etc.).
     */
    private enum GameState {
        PLAYING,
        PAUSE,
        DYING,
        GAME_OVER,
        BASE_DESTROYED,
        ZOMBIE_BASE_DESTROYED,

    }

    /**
     * Current game state.
     */
    private GameState gameState = GameState.PLAYING;
    /**
     * Tracks if the pause key is currently pressed to avoid repeated toggling.
     */
    private boolean pauseKeyPressed = false;

    /**
     * Constructs the main game screen, initializing all game entities, overlays,
     * HUD, audio, and UI components.
     *
     * @param game Reference to the main game application
     */
    public GameScreen(Main game) {
        this.game = game;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer(); // Pour debug
        camera = new OrthographicCamera();
        // Viewport plus petit pour zoom sur le héros (600x450 au lieu de 800x600)
        viewport = new FitViewport(600, 450, camera);

        viewport.update(com.badlogic.gdx.Gdx.graphics.getWidth(), com.badlogic.gdx.Gdx.graphics.getHeight(), true);

        map = new WarMap();
        this.mapWidth = map.getMapWidthInPixels();
        this.mapHeight = map.getMapHeightInPixels();
        this.enemyBase = new Base(20, 300, false, this.mapHeight); // false = spawn zombies
        this.playerBase = new Base(0, 300, true, this.mapHeight); // true = spawn soldiers
        hero = new Hero(map.getMapWidthInPixels() / 2, map.getMapHeightInPixels() / 2, this.map, this.playerBase);
        this.playerBase.setHero(hero);
        // Initialize HUD
        this.hudDisplay = new hud();
        // Initialize Game Over Overlay
        this.gameOverOverlay = new GameOverOverlay();
        // Initialize Base Destroyed Overlay
        this.baseDestroyedOverlay = new BaseDestroyedOverlay();
        // Initialize Zombie Base Destroyed Overlay
        this.baseZombieDestroyedOverlay = new BaseZombieDestroyedOverlay();
        // Initialize Pause Overlay
        this.pauseOverlay = new PauseOverlay();
        // Initialize Pause Font (deprecated - using PauseOverlay now)
        this.pauseFont = new BitmapFont();
        this.pauseFont.setColor(Color.WHITE);
        this.pauseFont.getData().setScale(4f);
        // Initialize Unit Shop
        this.unitShop = new UnitShop(playerBase, hero, hudDisplay.getGoldDisplay());
        this.unitShop = new UnitShop(playerBase, hero);
        this.inventory = new Inventory(hero);
        this.target = new Target(hero, camera);

        // Load audio
        loadSounds();
    }

    /**
     * Loads all game sounds and music, assigns audio resources to hero and
     * overlays.
     * Handles errors and missing files gracefully.
     */
    private void loadSounds() {
        try {
            // Musique de fond
            backgroundMusic = com.badlogic.gdx.Gdx.audio
                    .newMusic(com.badlogic.gdx.Gdx.files.internal("sounds/debut.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(AudioSettings.getMusicVolume());
            backgroundMusic.play();

            // Son de tir
            shootSound = com.badlogic.gdx.Gdx.audio
                    .newSound(com.badlogic.gdx.Gdx.files.internal("sounds/coup de feu heros.mp3"));

            // Passer le son au héros
            hero.setShootSound(shootSound);

            // TEST: Jouer le son une fois au démarrage pour vérifier
            shootSound.play(1.0f);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Resets the game state after losing, reinitializing map, bases, hero, unit
     * shop, and audio.
     * Reassigns sound resources and resizes UI components.
     */
    public void reset() {
        this.map = new WarMap();
        this.enemyBase = new Base(1350, 300, false, this.mapHeight); // false = spawn zombies
        this.playerBase = new Base(-22, 300, true, this.mapHeight); // true = spawn soldiers
        this.hero = new Hero(map.getMapWidthInPixels() / 2, map.getMapHeightInPixels() / 2, this.map, this.playerBase);
        this.playerBase.setHero(hero);
        this.unitShop = new UnitShop(playerBase, hero, hudDisplay.getGoldDisplay());
        this.inventory = new Inventory(hero);
        this.target = new Target(hero, camera);

        // ✅ IMPORTANT: Réassigner le son au nouveau héros

        if (shootSound != null) {
            hero.setShootSound(shootSound);
        }

        // Resize the new unitShop to match current window size
        this.unitShop.resize(com.badlogic.gdx.Gdx.graphics.getWidth(), com.badlogic.gdx.Gdx.graphics.getHeight());
        this.inventory.resize(com.badlogic.gdx.Gdx.graphics.getWidth(), com.badlogic.gdx.Gdx.graphics.getHeight());
        // this.target.resize(com.badlogic.gdx.Gdx.graphics.getWidth(), com.badlogic.gdx.Gdx.graphics.getHeight());
        this.gameState = GameState.PLAYING;
    }

    /**
     * Called when the screen is shown. (No implementation needed.)
     */
    @Override
    public void show() {
    }

    /**
     * Main render loop for the game screen. Handles all rendering, camera movement,
     * overlays, HUD, and debug drawing.
     *
     * @param delta Time elapsed since last frame (seconds)
     */
    @Override
    public void render(float delta) {
        update(delta);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        float spriteHalfWidth = hero.getSprite().getWidth() / 2f;
        float spriteHalfHeight = hero.getSprite().getHeight() / 2f;

        float camX = hero.getPosX() + spriteHalfWidth;
        float camY = hero.getPosY() + spriteHalfHeight;

        float halfViewportWidth = viewport.getWorldWidth() / 2f;
        float halfViewportHeight = viewport.getWorldHeight() / 2f;

        float mapPxW = map.getMapWidthInPixels();
        float mapPxH = map.getMapHeightInPixels();

        if (mapPxW > viewport.getWorldWidth()) {
            camX = Math.max(halfViewportWidth, Math.min(camX, mapPxW - halfViewportWidth));
        } else {
            camX = mapPxW / 2f;
        }

        if (mapPxH > viewport.getWorldHeight()) {
            camY = Math.max(halfViewportHeight, Math.min(camY, mapPxH - halfViewportHeight));
        } else {
            camY = mapPxH / 2f;
        }

        camera.position.set(camX, camY, 0);
        camera.update();
        map.setView(camera);
        map.render();
        batch.setProjectionMatrix(camera.combined);
        // Appliquer la luminosité globale
        float brightness = game.getBrightness();
        batch.setColor(brightness, brightness, brightness, 1f);
        batch.begin();
        // Render toutes les unités
        for (Unit elem : enemyBase.getUnits()) {
            elem.render(batch);
        }
        for (Unit elem : playerBase.getUnits()) {
            elem.render(batch);
        }
        hero.render(batch);
        batch.end();

        // Render Unit Shop buttons
        unitShop.render(shapeRenderer, batch);
        inventory.render(shapeRenderer, batch);
        target.render(shapeRenderer, batch);

        // Render HUD (after game rendering)
        hudDisplay.update(hero.getCurrentHealth(), hero.getMaxHealth(), hero.getGold());
        hudDisplay.updateBaseHealth(playerBase.getHealth(), 1000, enemyBase.getHealth(), 1000);

        // Mettre à jour les positions des barres de vie des bases
        hudDisplay.updateBaseHealthBarPositions(
                playerBase.getPosX(), playerBase.getPosition().getPosY(),
                enemyBase.getPosX(), enemyBase.getPosition().getPosY(),
                camera);

        // Render base health bars in game world (with game camera)
        hudDisplay.renderBaseHealthBars(camera);
        hudDisplay.render();

        // Render Pause overlay if in Pause state
        if (gameState == GameState.PAUSE) {
            pauseOverlay.render();
        }

        // Render Game Over Overlay if in Game Over state
        if (gameState == GameState.GAME_OVER) {
            gameOverOverlay.render();
        }

        // Render Base Destroyed Overlay if player base is destroyed
        if (gameState == GameState.BASE_DESTROYED) {
            baseDestroyedOverlay.render();
        }

        // Render Zombie Base Destroyed Overlay if enemy base is destroyed (Victory!)
        if (gameState == GameState.ZOMBIE_BASE_DESTROYED) {
            baseZombieDestroyedOverlay.render();
        }

        // Draw range circles if enabled
        if (showRanges) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            // Draw player units ranges in green
            shapeRenderer.setColor(0, 1, 0, 0.5f); // Green with transparency
            for (Unit unit : playerBase.getUnits()) {
                if (!unit.isDead()) {
                    shapeRenderer.circle(unit.getPosX() + unit.getWidth() / 2,
                            unit.getPosY() + unit.getHeight() / 2,
                            unit.getRange());
                }
            }

            // Draw enemy units ranges in red
            shapeRenderer.setColor(1, 0, 0, 0.5f); // Red with transparency
            for (Unit unit : enemyBase.getUnits()) {
                if (!unit.isDead()) {
                    shapeRenderer.circle(unit.getPosX() + unit.getWidth() / 2,
                            unit.getPosY() + unit.getHeight() / 2,
                            unit.getRange());
                }
            }

            // Draw base collision hitboxes (purple for player, yellow for enemy)
            shapeRenderer.setColor(0.5f, 0, 0.5f, 0.5f); // Purple for player base hitbox
            com.badlogic.gdx.math.Rectangle playerBox = playerBase.getCollisionBox();
            shapeRenderer.rect(playerBox.x, playerBox.y, playerBox.width, playerBox.height);

            shapeRenderer.setColor(1, 1, 0, 0.5f); // Yellow for enemy base hitbox
            com.badlogic.gdx.math.Rectangle enemyBox = enemyBase.getCollisionBox();
            shapeRenderer.rect(enemyBox.x, enemyBox.y, enemyBox.width, enemyBox.height);

            shapeRenderer.end();
        }
    }

    /**
     * Updates the game state, handles input, unit spawning, overlays, and game
     * logic.
     * Processes all interactions, state transitions, and checks for win/loss
     * conditions.
     *
     * @param delta Time elapsed since last frame (seconds)
     */
    private void update(float delta) {
        // Toggle pause with ESCAPE key
        if (com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            if (!pauseKeyPressed) {
                pauseKeyPressed = true;
                if (gameState == GameState.PLAYING) {
                    gameState = GameState.PAUSE;
                } else if (gameState == GameState.PAUSE) {
                    gameState = GameState.PLAYING;

                }
            }
        } else {
            pauseKeyPressed = false;
        }

        // Handle unit shop clicks (only when playing)
        if (gameState == GameState.PLAYING && com.badlogic.gdx.Gdx.input.justTouched()) {
            unitShop.handleClick(
                    com.badlogic.gdx.Gdx.input.getX(),
                    com.badlogic.gdx.Gdx.input.getY());
        }

        // Check if hero is dead
        if (hero.getCurrentHealth() <= 0 && gameState == GameState.PLAYING) {
            backgroundMusic.stop();
            gameState = GameState.DYING;
        }

        // Check if player base is destroyed
        if (playerBase.isDestroyed() && gameState == GameState.PLAYING) {
            gameState = GameState.BASE_DESTROYED;

        }

        // Check if enemy base is destroyed (Victory!)
        if (enemyBase.isDestroyed() && gameState == GameState.PLAYING) {
            gameState = GameState.ZOMBIE_BASE_DESTROYED;

        }

        if(gameState == GameState.DYING){
            deathTimer += delta;
            if(deathTimer >= DEATH_ANIM_DURATION){
                deathTimer = 0f;
                gameState = GameState.GAME_OVER;
            }
        }

        // Handle Game Over clicks
        if (gameState == GameState.GAME_OVER && com.badlogic.gdx.Gdx.input.justTouched()) {
            String action = gameOverOverlay.handleClick(
                    com.badlogic.gdx.Gdx.input.getX(),
                    com.badlogic.gdx.Gdx.input.getY());

            if ("replay".equals(action)) {
                reset();
            } else if ("quit".equals(action)) {
                com.badlogic.gdx.Gdx.app.postRunnable(() -> game.showTitleScreen());
            }
        }

        // Handle Base Destroyed clicks
        if (gameState == GameState.BASE_DESTROYED && com.badlogic.gdx.Gdx.input.justTouched()) {
            String action = baseDestroyedOverlay.handleClick(
                    com.badlogic.gdx.Gdx.input.getX(),
                    com.badlogic.gdx.Gdx.input.getY());

            if ("replay".equals(action)) {
                reset();
            } else if ("quit".equals(action)) {
                com.badlogic.gdx.Gdx.app.postRunnable(() -> game.showTitleScreen());
            }
        }

        // Handle Zombie Base Destroyed clicks
        if (gameState == GameState.ZOMBIE_BASE_DESTROYED && com.badlogic.gdx.Gdx.input.justTouched()) {
            String action = baseZombieDestroyedOverlay.handleClick(
                    com.badlogic.gdx.Gdx.input.getX(),
                    com.badlogic.gdx.Gdx.input.getY());

            if ("replay".equals(action)) {
                reset();
            } else if ("quit".equals(action)) {
                com.badlogic.gdx.Gdx.app.postRunnable(() -> game.showTitleScreen());
            }
        }

        // Handle Pause clicks
        if (gameState == GameState.PAUSE && com.badlogic.gdx.Gdx.input.justTouched()) {
            String action = pauseOverlay.handleClick(
                    com.badlogic.gdx.Gdx.input.getX(),
                    com.badlogic.gdx.Gdx.input.getY());

            if ("resume".equals(action)) {
                gameState = GameState.PLAYING;
                pauseOverlay.resetConfirmation();
            } else if ("options".equals(action)) {
                System.out.println("Options clicked from pause!");
                com.badlogic.gdx.Gdx.app.postRunnable(() -> game.showOptionsScreen(true));
            } else if ("quit".equals(action)) {
                pauseOverlay.resetConfirmation();
                com.badlogic.gdx.Gdx.app.postRunnable(() -> game.showTitleScreen());
            }
        }

        // Don't update game if Game Over, Base Destroyed, or Paused
        if (gameState == GameState.GAME_OVER || gameState == GameState.PAUSE ||
                gameState == GameState.BASE_DESTROYED || gameState == GameState.ZOMBIE_BASE_DESTROYED) {
            return;
        }

        // Toggle range display with 'R' key
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.R)) {
            showRanges = !showRanges;
        }

        hero.update(delta, map.getMapWidthInPixels(), map.getMapHeightInPixels(), enemyBase.getUnits());

        // Remove dead enemies and give gold to hero
        removeDeadEnemiesAndGiveGold();

        // Spawn des ennemis
        enemyBase.spawnUnit(this, delta);

        // Spawn des alliés
        playerBase.spawnUnit(this, delta);

        // Update : les ennemis attaquent les alliés (et leur base) et vice-versa
        enemyBase.updateUnits(delta, playerBase.getUnits(), playerBase, this.hero);
        playerBase.updateUnits(delta, enemyBase.getUnits(), enemyBase, null);

        // Check for game over conditions
        if (playerBase.isDestroyed()) {
            //
            // TODO: Implement game over screen
        }
        if (enemyBase.isDestroyed()) {
            //
            // TODO: Implement victory screen
        }

        camera.position.set(hero.getPosX(), hero.getPosY(), 0);
    }

    /**
     * Removes dead enemies from the enemy base and rewards gold to the hero for
     * each kill.
     */
    private void removeDeadEnemiesAndGiveGold() {
        for (Unit enemy : enemyBase.getUnits()) {
            if (enemy.isDead()) {
                // Give gold to hero when enemy dies
                int goldReward = 15;
                hero.addGold(goldReward);
                // System.out.println("Enemy killed! +40 gold. Total: " + hero.getGold());
            }
        }
    }

    /**
     * Handles resizing of the game screen and all overlays/UI components.
     *
     * @param width  New width of the screen
     * @param height New height of the screen
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (hudDisplay != null) {
            hudDisplay.resize(width, height);
        }
        if (gameOverOverlay != null) {
            gameOverOverlay.resize(width, height);
        }
        if (baseDestroyedOverlay != null) {
            baseDestroyedOverlay.resize(width, height);
        }
        if (baseZombieDestroyedOverlay != null) {
            baseZombieDestroyedOverlay.resize(width, height);
        }
        if (pauseOverlay != null) {
            pauseOverlay.resize(width, height);
            if (unitShop != null) {
                unitShop.resize(width, height);
            }
            if (inventory != null) {
                inventory.resize(width, height);
            }
            // if (target != null) {
            //     target.resize(width, height);
            // }
        }
    }

    /**
     * Pauses the game and background music.
     */
    @Override
    public void pause() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    /**
     * Resumes the game and background music if in playing state.
     */
    @Override
    public void resume() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying() && gameState == GameState.PLAYING) {
            backgroundMusic.play();
        }
    }

    /**
     * Called when the screen is hidden. (No implementation needed.)
     */
    @Override
    public void hide() {
    }

    /**
     * Disposes of all resources used by the game screen, including textures, audio,
     * overlays, and UI components.
     */
    @Override
    public void dispose() {
        batch.dispose();
        if (image != null)
            image.dispose();
        if (hero != null)
            hero.dispose();
        if (map != null)
            map.dispose();
        if (hudDisplay != null)
            hudDisplay.dispose();
        if (gameOverOverlay != null)
            gameOverOverlay.dispose();
        if (baseDestroyedOverlay != null)
            baseDestroyedOverlay.dispose();
        if (baseZombieDestroyedOverlay != null)
            baseZombieDestroyedOverlay.dispose();
        if (pauseOverlay != null)
            pauseOverlay.dispose();
        if (inventory != null)
            inventory.dispose();
        if (target != null)
            target.dispose();
        if (pauseFont != null)
            pauseFont.dispose();

        // Dispose audio resources
        if (backgroundMusic != null)
            backgroundMusic.dispose();
        if (shootSound != null)
            shootSound.dispose();
    }

    /**
     * Returns the main SpriteBatch for rendering.
     *
     * @return SpriteBatch instance
     */
    public SpriteBatch getBatch() {
        return batch;
    }

    /**
     * Returns the background image texture (if used).
     *
     * @return Texture instance or null
     */
    public Texture getImage() {
        return image;
    }

    /**
     * Returns the main game application reference.
     *
     * @return Main game instance
     */
    public Main getGame() {
        return game;
    }

    /**
     * Returns the main playable hero unit.
     *
     * @return Hero instance
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * Returns the game map.
     *
     * @return WarMap instance
     */
    public WarMap getMap() {
        return map;
    }

    /**
     * Returns the camera used for rendering and following the hero.
     *
     * @return OrthographicCamera instance
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Returns the viewport for scaling and resizing the camera view.
     *
     * @return Viewport instance
     */
    public Viewport getViewport() {
        return viewport;
    }

    /**
     * Returns the enemy base (spawns zombies).
     *
     * @return Base instance
     */
    public Base getEnemyBase() {
        return enemyBase;
    }

    /**
     * Returns the player base (spawns soldiers).
     *
     * @return Base instance
     */
    public Base getPlayerBase() {
        return playerBase;
    }

    /**
     * Returns the width of the map in pixels.
     *
     * @return Map width in pixels
     */
    public int getMapWidth() {
        return mapWidth;
    }

    /**
     * Returns the height of the map in pixels.
     *
     * @return Map height in pixels
     */
    public int getMapHeight() {
        return mapHeight;
    }

    /**
     * Updates the background music volume based on AudioSettings.
     * Called when volume is changed in the options menu.
     */
    public void updateMusicVolume() {
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(AudioSettings.getMusicVolume());
        }
    }

    /**
     * Resumes the game from pause state.
     * Sets the game state back to PLAYING.
     */
    public void resumeFromPause() {
        gameState = GameState.PLAYING;
    }
}