package com.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;


/**
 * The {@code gold} class is a UI component for displaying the player's gold or currency.
 * It renders the gold value, optionally with a coin icon, and provides methods to update, add, remove, and set gold.
 * Implements {@link Disposable} for proper resource management.
 */
public class gold implements Disposable {

    private float x;
    private float y;
    private int gold;
    private BitmapFont font;
    private Texture coinIcon;
    private boolean hasIcon;
    private static final float ICON_SIZE = 45f;
    private static final float TEXT_OFFSET_X = 49f;
    private static final Color GOLD_COLOR = new Color(1f, 0.84f, 0f, 1f);
    private static final Color BACKGROUND_COLOR = new Color(0.2f, 0.2f, 0.2f, 0.7f);
    private static final float PADDING_X = 10f;
    private static final float PADDING_Y = 8f;
    
    /**
     * Constructs a gold display UI component without an icon.
     *
     * @param x X position of the display
     * @param y Y position of the display
     */
    public gold(float x, float y) {
        this.x = x;
        this.y = y;
        this.gold = 0;
        this.hasIcon = false;
        
        // Initialize font
        font = new BitmapFont();
        // Utiliser un jaune plus brillant et saturé pour meilleure visibilité
        font.setColor(new Color(1f, 1f, 0f, 1f)); // Jaune pur très visible
        font.getData().setScale(1.9f);
        
        // Set text to be bold and crisp
        font.getRegion().getTexture().setFilter(
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear,
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
        );
    }
    
    /**
     * Constructs a gold display UI component with a coin icon.
     *
     * @param x X position of the display
     * @param y Y position of the display
     * @param coinIconPath Path to the coin icon texture
     */
    public gold(float x, float y, String coinIconPath) {
        this(x, y);
        try {
            this.coinIcon = new Texture(coinIconPath);
            this.hasIcon = true;
        } catch (Exception e) {
            System.err.println("Could not load coin icon: " + coinIconPath);
            this.hasIcon = false;
        }
    }
    
    /**
     * Updates the gold value displayed.
     *
     * @param gold Current gold amount
     */
    public void update(int gold) {
        this.gold = Math.max(0, gold); // Ensure non-negative
    }
    
    /**
     * Renders the gold display (icon and value) using the provided {@link SpriteBatch}.
     *
     * @param batch SpriteBatch for rendering
     */
    public void render(SpriteBatch batch) {
        batch.begin();
        
        float textX = x;
        
        // Draw coin icon if available (aligned vertically with text)
        if (hasIcon && coinIcon != null) {
            // Calculate vertical center alignment with the text
            float iconY = y - font.getLineHeight() / 2 - ICON_SIZE / 3 + 2;
            batch.draw(coinIcon, x, iconY, ICON_SIZE, ICON_SIZE);
            textX += TEXT_OFFSET_X;
        }
        
        // Draw gold text with subtle black outline
        String goldText = " " + gold;
        
        // Save original color
        Color originalColor = font.getColor().cpy();
        
        // Draw subtle black outline (4 directions only)
        font.setColor(Color.BLACK);
        float offset = 1f;
        font.draw(batch, goldText, textX - offset, y);
        font.draw(batch, goldText, textX + offset, y);
        font.draw(batch, goldText, textX, y - offset);
        font.draw(batch, goldText, textX, y + offset);
        
        // Draw main text in yellow
        font.setColor(originalColor);
        font.draw(batch, goldText, textX, y);
        
        batch.end();
    }
    
    /**
     * Renders the gold display with a background using the provided {@link ShapeRenderer} and {@link SpriteBatch}.
     *
     * @param shapeRenderer ShapeRenderer for background
     * @param batch SpriteBatch for text and icon
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        // Calculate background dimensions
        String goldText = "Gold: " + gold;
        float textWidth = font.getRegion().getRegionWidth() * goldText.length() * 0.4f; // Approximate
        float textHeight = font.getLineHeight();
        
        float bgWidth = textWidth + PADDING_X * 2;
        if (hasIcon) {
            bgWidth += TEXT_OFFSET_X;
        }
        float bgHeight = textHeight + PADDING_Y * 2;
        
        // Draw background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BACKGROUND_COLOR);
        shapeRenderer.rect(x - PADDING_X, y - textHeight + PADDING_Y, bgWidth, bgHeight);
        shapeRenderer.end();
        
        // Draw foreground (icon + text)
        render(batch);
    }
    
    /**
     * Sets the position of the gold display.
     *
     * @param x New X position
     * @param y New Y position
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Adds gold to the current amount displayed.
     *
     * @param amount Amount of gold to add
     */
    public void addGold(int amount) {
        // this.gold += amount;
        // if (this.gold <= 0)
        //     this.gold = 0;
        this.setGold(this.gold + amount);
    }
    
    /**
     * Removes gold from the current amount displayed.
     *
     * @param amount Amount of gold to remove
     * @return {@code true} if gold was successfully removed, {@code false} if not enough gold
     */
    public boolean removeGold(int amount) {
        if (this.gold >= amount) {
            this.gold -= amount;
            return true;
        }
        return false;
    }
    
    /**
     * Sets the gold amount directly in the display.
     *
     * @param gold Gold amount to set
     */
    public void setGold(int gold) {
        this.gold = Math.max(0, gold);
    }
    
    /**
     * Gets the current gold amount displayed.
     *
     * @return Current gold value
     */
    public int getGold() {
        return gold;
    }
    
    /**
     * Sets the color of the font used for the gold display.
     *
     * @param color New color for the font
     */
    public void setColor(Color color) {
        font.setColor(color);
    }
    
    /**
     * Sets the scale of the font used for the gold display.
     *
     * @param scale Font scale (must be positive)
     */
    public void setFontScale(float scale) {
        if (scale > 0f)
            font.getData().setScale(scale);
    }
    
    /**
     * Disposes all resources used by the gold display, including font and icon.
     * Should be called when the display is no longer needed to free memory.
     */
    @Override
    public void dispose() {
        font.dispose();
        if (coinIcon != null) {
            coinIcon.dispose();
        }
    }
}
