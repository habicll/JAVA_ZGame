package com.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * The {@code hud} class manages the Heads-Up Display (HUD) for the game.
 * It is responsible for rendering and updating all user interface elements,
 * including health bars, gold counter, and base health indicators.
 * The HUD provides essential player and base information during gameplay.
 * Implements {@link Disposable} for proper resource management.
 */
public class hud implements Disposable {
    
    /**
     * Viewport for scaling and positioning UI elements.
     */
    private Viewport viewport;

    /**
     * Orthographic camera for rendering HUD elements in 2D.
     */
    private OrthographicCamera camera;

    /**
     * SpriteBatch for drawing textures and fonts.
     */
    private SpriteBatch batch;

    /**
     * ShapeRenderer for drawing shapes (bars, overlays).
     */
    private ShapeRenderer shapeRenderer;

    /**
     * BitmapFont for rendering text in the HUD.
     */
    private BitmapFont font;

    /**
     * Health bar component for displaying player health.
     */
    private healthbar healthBar;

    /**
     * Vertical health bar for the player's base.
     */
    private BaseHealthBar playerBaseHealthBar;

    /**
     * Vertical health bar for the enemy base.
     */
    private BaseHealthBar enemyBaseHealthBar;

    /**
     * Gold display component for showing current gold amount.
     */
    private gold goldDisplay;

    /**
     * X position for the player health bar.
     */
    private static final float HEALTH_BAR_X = 20f;

    /**
     * Y position for the player health bar.
     */
    private static final float HEALTH_BAR_Y = 525f;

    /**
     * X position for the gold display.
     */
    private static final float GOLD_X = 40f;

    /**
     * Y position for the gold display.
     */
    private static final float GOLD_Y = 535f;

    /**
     * Width of the vertical base health bars.
     */
    private static final float BASE_HEALTH_BAR_WIDTH = 8f;

    /**
     * Height of the vertical base health bars.
     */
    private static final float BASE_HEALTH_BAR_HEIGHT = 150f;

    /**
     * X position for the player base health bar (updated dynamically).
     */
    private float playerBaseHealthBarX = 20f;

    /**
     * Y position for the player base health bar (updated dynamically).
     */
    private float playerBaseHealthBarY = 150f;

    /**
     * X position for the enemy base health bar (updated dynamically).
     */
    private float enemyBaseHealthBarX = 755f;

    /**
     * Y position for the enemy base health bar (updated dynamically).
     */
    private float enemyBaseHealthBarY = 150f;
    
    /**
     * Constructs the HUD and initializes all UI components, rendering tools, and positions.
     * Sets up camera, viewport, health bars, and gold display.
     */
    public hud() {
        // Initialize camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        
        // Initialize rendering components
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont(); // Default font, you can load custom font here
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
        
        // Initialize health bar with heart icon
        healthBar = new healthbar(HEALTH_BAR_X, HEALTH_BAR_Y, 200f, 20f, "ui/heart.png"); // Hauteur à 20f
        
        // Initialize base health bars (verticales)
        playerBaseHealthBar = new BaseHealthBar(playerBaseHealthBarX, playerBaseHealthBarY, 
            BASE_HEALTH_BAR_WIDTH, BASE_HEALTH_BAR_HEIGHT);
        enemyBaseHealthBar = new BaseHealthBar(enemyBaseHealthBarX, enemyBaseHealthBarY, 
            BASE_HEALTH_BAR_WIDTH, BASE_HEALTH_BAR_HEIGHT);
        
        // Initialize gold display with coin icon
        goldDisplay = new gold(GOLD_X, GOLD_Y, "ui/gold.png");
    }
    
    /**
     * Constructs the HUD for testing or dependency injection, allowing custom rendering components.
     *
     * @param batch SpriteBatch for drawing
     * @param shapeRenderer ShapeRenderer for shapes
     * @param font BitmapFont for text
     * @param healthBar Health bar component
     * @param goldDisplay Gold display component
     */
    protected hud(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font,
                  healthbar healthBar, gold goldDisplay) {
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
        this.font = font;
        this.healthBar = healthBar;
        this.goldDisplay = goldDisplay;
    }
    
    /**
     * Updates the HUD with the current player health and gold values.
     *
     * @param currentHealth Current health of the player
     * @param maxHealth Maximum health of the player
     * @param currentGold Current gold amount
     */
    public void update(int currentHealth, int maxHealth, int currentGold) {
        healthBar.update(currentHealth, maxHealth);
        goldDisplay.update(currentGold);
    }
    
    /**
     * Updates the health bars for both player and enemy bases.
     *
     * @param playerBaseHealth Current health of the player base
     * @param playerBaseMaxHealth Maximum health of the player base
     * @param enemyBaseHealth Current health of the enemy base
     * @param enemyBaseMaxHealth Maximum health of the enemy base
     */
    public void updateBaseHealth(int playerBaseHealth, int playerBaseMaxHealth, 
                                  int enemyBaseHealth, int enemyBaseMaxHealth) {
        playerBaseHealthBar.update(playerBaseHealth, playerBaseMaxHealth);
        enemyBaseHealthBar.update(enemyBaseHealth, enemyBaseMaxHealth);
    }
    
    /**
     * Updates the positions of the base health bars based on the world coordinates of the bases.
     * Called from GameScreen to synchronize UI with game world.
     *
     * @param playerBaseX X position of the player base in world coordinates
     * @param playerBaseY Y position of the player base in world coordinates
     * @param enemyBaseX X position of the enemy base in world coordinates
     * @param enemyBaseY Y position of the enemy base in world coordinates
     * @param gameCamera Game camera for coordinate conversion
     */
    public void updateBaseHealthBarPositions(float playerBaseX, float playerBaseY,
                                              float enemyBaseX, float enemyBaseY,
                                              OrthographicCamera gameCamera) {
        // Barres verticales centrées horizontalement sur les bases
        float baseWidth = 96f;
        
        // Centrer horizontalement : posX + (largeur_base / 2) - (largeur_barre / 2)
        float centerOffsetX = (baseWidth / 2) - (BASE_HEALTH_BAR_WIDTH / 2);
        
        // Centrer verticalement

        float baseHeight = 300f;
        float centerOffsetY = (baseHeight / 2) - (BASE_HEALTH_BAR_HEIGHT / 2);
        
        // Position de la barre du joueur (centrée sur la base)
        playerBaseHealthBarX = playerBaseX + centerOffsetX;
        playerBaseHealthBarY = playerBaseY + centerOffsetY;
        
        // Position de la barre ennemie (centrée sur la base)
        enemyBaseHealthBarX = enemyBaseX + centerOffsetX;
        enemyBaseHealthBarY = enemyBaseY + centerOffsetY;
        
        // Mettre à jour les positions des barres de vie verticales
        playerBaseHealthBar.setPosition(playerBaseHealthBarX, playerBaseHealthBarY);
        enemyBaseHealthBar.setPosition(enemyBaseHealthBarX, enemyBaseHealthBarY);
    }
    
    /**
     * Renders the HUD, including health bar and gold display.
     * Should be called every frame to update UI elements.
     */
    public void render() {
        // Update camera
        camera.update();
        
        // Set projection matrices for UI elements (health bar, gold)
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        
        // Render health bar with heart icon
        healthBar.render(shapeRenderer, batch);
        
        // Render gold display with coin icon
        goldDisplay.render(batch);
    }
    
    /**
     * Renders the base health bars in the game world using the provided camera.
     *
     * @param gameCamera Camera from the game world for correct positioning
     */
    public void renderBaseHealthBars(OrthographicCamera gameCamera) {
        // Set projection matrix to game camera for world-space rendering
        shapeRenderer.setProjectionMatrix(gameCamera.combined);
        batch.setProjectionMatrix(gameCamera.combined);
        
        // Render base health bars verticales in game world
        playerBaseHealthBar.render(shapeRenderer);
        enemyBaseHealthBar.render(shapeRenderer);
    }
    
    /**
     * Resizes the HUD viewport when the window size changes.
     *
     * @param width New width of the window
     * @param height New height of the window
     */
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
    }
    
    /**
     * Adds gold to the current amount displayed in the HUD.
     *
     * @param amount Amount of gold to add
     */
    public void addGold(int amount) {
        goldDisplay.addGold(amount);
    }
    
    /**
     * Removes gold from the current amount displayed in the HUD.
     *
     * @param amount Amount of gold to remove
     * @return {@code true} if gold was successfully removed, {@code false} if not enough gold
     */
    public boolean removeGold(int amount) {
        return goldDisplay.removeGold(amount);
    }
    
    /**
     * Gets the current gold amount displayed in the HUD.
     *
     * @return Current gold value
     */
    public int getGold() {
        return goldDisplay.getGold();
    }
    
    /**
     * Sets the gold amount directly in the HUD.
     *
     * @param gold Gold amount to set
     */
    public void setGold(int gold) {
        goldDisplay.setGold(gold);
    }
    
    /**
     * Gets the gold display component for direct access.
     *
     * @return The {@link gold} display object
     */
    public gold getGoldDisplay() {
        return goldDisplay;
    }
    
    /**
     * Disposes all resources used by the HUD, including rendering components and UI elements.
     * Should be called when the HUD is no longer needed to free memory.
     */
    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        healthBar.dispose();
        playerBaseHealthBar.dispose();
        enemyBaseHealthBar.dispose();
        goldDisplay.dispose();
    }
}
