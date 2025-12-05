package com.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

/**
 * Represents a health bar UI component for displaying the hero's health in the game.
 * Supports both default and custom textured rendering, with optional heart icon.
 * Handles health value updates, rendering, and resource management.
 */
public class healthbar implements Disposable {

    /**
     * X position of the health bar (top-left corner).
     */
    private float x;

    /**
     * Y position of the health bar (top-left corner).
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
     * Maximum health value for the bar.
     */
    private int maxHealth;

    /**
     * Font used for health text display.
     */
    private BitmapFont font;

    /**
     * Texture for the heart icon (optional).
     */
    private Texture heartIcon;

    /**
     * Indicates if the heart icon is enabled and loaded.
     */
    private boolean hasIcon;

    /**
     * Size of the heart icon in pixels.
     */
    private static final float ICON_SIZE = 26f;

    /**
     * Offset for positioning the heart icon relative to the bar.
     */
    private static final float ICON_OFFSET = 32f;

    /**
     * Texture for the custom health bar (optional).
     */
    private Texture healthbarTexture;

    /**
     * Indicates if the custom health bar texture is used.
     */
    private boolean useCustomTexture = false;

    /**
     * Width of the heart in the custom health bar image (pixels).
     */
    private static final int HEART_WIDTH_IN_IMAGE = 80;

    /**
     * X coordinate where the health bar starts after the heart in the custom image (pixels).
     */
    private static final int BAR_START_X = 80;

    /**
     * Background color for the health bar (dark gray, 2D style).
     */
    private static final Color BACKGROUND_COLOR = new Color(0.2f, 0.2f, 0.2f, 1f);

    /**
     * Border color for the health bar (dark gray).
     */
    private static final Color BORDER_COLOR = new Color(0.4f, 0.4f, 0.4f, 1f);

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
     * Thickness of the health bar border (pixels).
     */
    private static final float BORDER_THICKNESS = 4f;
    
    /**
     * Constructs a HealthBar without a heart icon.
     *
     * @param x X position (top-left corner)
     * @param y Y position (top-left corner)
     * @param width Width of the health bar
     * @param height Height of the health bar
     */
    public healthbar(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.currentHealth = 100;
        this.maxHealth = 100;
        this.hasIcon = false;
        
        // Initialize font for text display
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);
    }
    
    /**
     * Constructs a HealthBar with a heart icon and attempts to load a custom health bar texture.
     *
     * @param x X position (top-left corner)
     * @param y Y position (top-left corner)
     * @param width Width of the health bar
     * @param height Height of the health bar
     * @param heartIconPath Path to the heart icon texture
     */
    public healthbar(float x, float y, float width, float height, String heartIconPath) {
        this(x, y, width, height);
        
        // Attempt to load the heart icon texture
        try {
            this.heartIcon = new Texture(heartIconPath);
            this.hasIcon = true;
        } catch (Exception e) {
            this.hasIcon = false;
        }

        // Attempt to load the custom health bar texture
        try {
            this.healthbarTexture = new Texture(Gdx.files.internal("ui/healthbar.png"));
            this.useCustomTexture = true;
        } catch (Exception e) {
            this.useCustomTexture = false;
        }
    }
    
    /**
     * Updates the health values displayed by the health bar.
     *
     * @param currentHealth Current health value
     * @param maxHealth Maximum health value
     */
    public void update(int currentHealth, int maxHealth) {
        this.currentHealth = Math.max(0, currentHealth);
        this.maxHealth = Math.max(1, maxHealth);
    }
    
    /**
     * Renders the health bar using the default style (ShapeRenderer).
     * Draws the border, background, and health fill based on current health.
     *
     * @param shapeRenderer ShapeRenderer used for drawing shapes
     */
    public void render(ShapeRenderer shapeRenderer) {
        // Calculer le pourcentage de santé
        float healthPercentage = (float) currentHealth / maxHealth;
        float fillWidth = width * healthPercentage;
        
        // Déterminer la couleur de la barre de santé en fonction du pourcentage
        Color fillColor = getHealthColor(healthPercentage);
        
        // Commencer le rendu des formes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Draw thick black border (outer rectangle) - style pixel art
        shapeRenderer.setColor(BORDER_COLOR);
        shapeRenderer.rect(x - BORDER_THICKNESS, y - BORDER_THICKNESS, 
            width + BORDER_THICKNESS * 2, height + BORDER_THICKNESS * 2);
        
        // Draw white/gray background
        shapeRenderer.setColor(BACKGROUND_COLOR);
        shapeRenderer.rect(x, y, width, height);
        
        // Draw health fill (yellow/orange/red/green)
        shapeRenderer.setColor(fillColor);
        shapeRenderer.rect(x, y, fillWidth, height);
        
        shapeRenderer.end();
    }
    
    /**
     * Renders the health bar with optional heart icon and custom texture if available.
     * If the custom texture is loaded, uses it for rendering; otherwise falls back to default style.
     *
     * @param shapeRenderer ShapeRenderer for drawing shapes
     * @param batch SpriteBatch for drawing icons and textures
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        // Si on utilise la texture personnalisée
        if (useCustomTexture && healthbarTexture != null) {
            batch.begin();
            renderCustomTexture(batch);
            batch.end();
        } else {
            // Fallback: ancien style avec ShapeRenderer
            if (hasIcon && heartIcon != null) {
                batch.begin();
                batch.draw(heartIcon, x - ICON_OFFSET, y + (height - ICON_SIZE) / 2, ICON_SIZE, ICON_SIZE);
                batch.end();
            }
            render(shapeRenderer);
        }
    }
    
    /**
     * Renders the health bar using the custom texture (healthbar.png).
     * Draws the heart icon and overlays the health fill based on current health percentage.
     *
     * @param batch SpriteBatch used for drawing textures
     */
    private void renderCustomTexture(SpriteBatch batch) {
        float healthPercentage = (float) currentHealth / maxHealth;
        
        // Taille réduite mais bien visible
        float scale = 0.10f;
        float totalWidth = healthbarTexture.getWidth() * scale;
        float totalHeight = healthbarTexture.getHeight() * scale;
        
        // j'ai dessiner l'icône heart.png juste à côté de la barre
        if (hasIcon && heartIcon != null) {
            float heartSize = totalHeight * 1.2f;
            float heartOffset = 5f;
            batch.setColor(1f, 1f, 1f, 1f);
            batch.draw(heartIcon, x - heartSize - heartOffset, y, heartSize, heartSize);
        }
        
        // j'ai dessiner la barre complète
        batch.setColor(0.5f, 0.5f, 0.5f, 1f);
        batch.draw(healthbarTexture,
            x, y,
            totalWidth, totalHeight,
            0, 0,
            healthbarTexture.getWidth(), healthbarTexture.getHeight(),
            false, false);
        
        // j'ai dessiner le remplissage vert par-dessus
        if (healthPercentage > 0) {
            batch.setColor(1f, 1f, 1f, 1f);
            float fillWidth = totalWidth * healthPercentage;
            int sourceFillWidth = (int)(healthbarTexture.getWidth() * healthPercentage);
            
            batch.draw(healthbarTexture,
                x, y,
                fillWidth, totalHeight,
                0, 0,
                sourceFillWidth, healthbarTexture.getHeight(),
                false, false);
        }
    }
    
    /**
     * Determines the color of the health fill based on the current health percentage.
     *
     * @param percentage Health percentage (0.0 to 1.0)
     * @return Color representing the health state (green, orange, or red)
     */
    private Color getHealthColor(float percentage) {
        if (percentage > 0.5f) {
            return HEALTH_COLOR; // Green
        } else if (percentage > 0.25f) {
            return MEDIUM_HEALTH_COLOR; // Orange
        } else {
            return LOW_HEALTH_COLOR; // Red
        }
    }
    
    /**
     * Sets the position of the health bar.
     *
     * @param x X position (top-left corner)
     * @param y Y position (top-left corner)
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Sets the dimensions of the health bar.
     *
     * @param width Width of the health bar
     * @param height Height of the health bar
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Returns the current health value displayed by the bar.
     *
     * @return Current health value
     */
    public int getCurrentHealth() {
        return currentHealth;
    }
    
    /**
     * Returns the maximum health value for the bar.
     *
     * @return Maximum health value
     */
    public int getMaxHealth() {
        return maxHealth;
    }
    
    /**
     * Releases all resources used by the health bar, including font, heart icon, and custom texture.
     * Should be called when the health bar is no longer needed to prevent memory leaks.
     */
    @Override
    public void dispose() {
        font.dispose();
        if (heartIcon != null) {
            heartIcon.dispose();
        }
        if (healthbarTexture != null) {
            healthbarTexture.dispose();
        }
    }
}
