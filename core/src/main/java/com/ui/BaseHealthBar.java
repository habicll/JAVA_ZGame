package com.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;


/**
 * The {@code BaseHealthBar} class represents a vertical health bar for bases in the game (isometric style).
 * It visually displays the current and maximum health of a base, with color changes based on health percentage.
 * Implements {@link Disposable} for proper resource management.
 */
public class BaseHealthBar implements Disposable {
    
    /**
     * X position (bottom-left corner) of the health bar.
     */
    private float x;

    /**
     * Y position (bottom-left corner) of the health bar.
     */
    private float y;

    /**
     * Width of the health bar.
     */
    private float width;

    /**
     * Height of the health bar.
     */
    private float height;

    /**
     * Current health value displayed by the bar.
     */
    private int currentHealth;

    /**
     * Maximum health value displayed by the bar.
     */
    private int maxHealth;

    /**
     * Font for rendering health values (if needed).
     */
    private BitmapFont font;

    /**
     * Background color for the health bar (dark gray, pixel art style).
     */
    private static final Color BACKGROUND_COLOR = new Color(0.2f, 0.2f, 0.2f, 1f);

    /**
     * Border color for the health bar (black).
     */
    private static final Color BORDER_COLOR = new Color(0f, 0f, 0f, 1f);

    /**
     * Color for high health (bright green).
     */
    private static final Color HEALTH_COLOR = new Color(0.1f, 0.9f, 0.1f, 1f);

    /**
     * Color for low health (bright red).
     */
    private static final Color LOW_HEALTH_COLOR = new Color(0.9f, 0.1f, 0.1f, 1f);

    /**
     * Color for medium health (bright orange).
     */
    private static final Color MEDIUM_HEALTH_COLOR = new Color(1f, 0.6f, 0f, 1f);

    /**
     * Thickness of the health bar border.
     */
    private static final float BORDER_THICKNESS = 4f;
    
    /**
     * Constructs a vertical health bar for a base with specified position and dimensions.
     *
     * @param x X position (bottom-left corner)
     * @param y Y position (bottom-left corner)
     * @param width Width of the health bar
     * @param height Height of the health bar
     */
    public BaseHealthBar(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.currentHealth = 1000;
        this.maxHealth = 1000;
        
        // Initialiser la font
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(0.9f);
    }
    
    /**
     * Updates the current and maximum health values displayed by the bar.
     *
     * @param currentHealth Current health value
     * @param maxHealth Maximum health value
     */
    public void update(int currentHealth, int maxHealth) {
        this.currentHealth = Math.max(0, currentHealth);
        this.maxHealth = Math.max(1, maxHealth);
    }
    
    /**
     * Renders the vertical health bar using the provided {@link ShapeRenderer}.
     * The bar color changes based on health percentage (green, orange, red).
     *
     * @param shapeRenderer ShapeRenderer used for drawing the bar
     */
    public void render(ShapeRenderer shapeRenderer) {
        // Calculer le pourcentage de santé
        float healthPercentage = (float) currentHealth / maxHealth;
        float fillHeight = height * healthPercentage;
        
        // Déterminer la couleur
        Color fillColor = getHealthColor(healthPercentage);
        
        // Commencer le rendu
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Bordure noire
        shapeRenderer.setColor(BORDER_COLOR);
        shapeRenderer.rect(x - BORDER_THICKNESS, y - BORDER_THICKNESS, 
            width + BORDER_THICKNESS * 2, height + BORDER_THICKNESS * 2);
        
        // Fond gris
        shapeRenderer.setColor(BACKGROUND_COLOR);
        shapeRenderer.rect(x, y, width, height);
        
        // Remplissage de santé (de bas en haut)
        shapeRenderer.setColor(fillColor);
        shapeRenderer.rect(x, y, width, fillHeight);
        
        shapeRenderer.end();
    }
    
    /**
     * Returns the color to use for the health bar fill based on health percentage.
     *
     * @param percentage Health percentage (0.0 to 1.0)
     * @return Color for the health bar fill
     */
    private Color getHealthColor(float percentage) {
        if (percentage > 0.5f) {
            return HEALTH_COLOR; // Vert
        } else if (percentage > 0.25f) {
            return MEDIUM_HEALTH_COLOR; // Orange
        } else {
            return LOW_HEALTH_COLOR; // Rouge
        }
    }
    
    /**
     * Sets the position of the health bar (bottom-left corner).
     *
     * @param x New X position
     * @param y New Y position
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Sets the dimensions of the health bar.
     *
     * @param width New width
     * @param height New height
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Disposes the font resource used by the health bar.
     * Should be called when the health bar is no longer needed to free memory.
     */
    @Override
    public void dispose() {
        font.dispose();
    }
}
