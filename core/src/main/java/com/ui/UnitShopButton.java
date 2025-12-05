package com.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.main.map.Base;

/**
 * Represents a button in the unit shop UI for selecting unit types or spawn points.
 * Handles rendering, selection state, and click detection for shop interactions.
 */
public class UnitShopButton {

    /**
     * Enum for button type: unit type selection or spawn point selection.
     */
    public enum ButtonType {
        /** Button for selecting a unit type. */
        UNIT_TYPE,
        /** Button for selecting a spawn point. */
        SPAWN_POINT
    }

    /**
     * Rectangle representing the button's hitbox and position.
     */
    private Rectangle bounds;

    /**
     * The unit type associated with this button (if applicable).
     */
    private Base.Type unitType;

    /**
     * The spawn index associated with this button (nullable for unit type buttons).
     */
    private Integer spawnIndex;

    /**
     * The type of this button (unit type or spawn point).
     */
    private ButtonType buttonType;

    /**
     * Indicates whether this button is currently selected.
     */
    private boolean selected;

    /**
     * The label displayed on the button (unit type or spawn index).
     */
    private String label;

    /**
     * Font used for rendering the button label.
     */
    private BitmapFont font;

    /**
     * List of loaded textures for resource management.
     */
    protected List<Texture> loadedTextures = new ArrayList<>();

    /**
     * Texture region for the idle (default) button state.
     */
    protected TextureRegion idleFrame;

    /**
     * Constructs a button for selecting a unit type in the shop.
     *
     * @param x X position of the button
     * @param y Y position of the button
     * @param width Width of the button
     * @param height Height of the button
     * @param unitType The unit type associated with this button
     */
    public UnitShopButton(float x, float y, float width, float height, Base.Type unitType) {
        this.bounds = new Rectangle(x, y, width, height);
        this.unitType = unitType;
        this.spawnIndex = null;
        this.buttonType = ButtonType.UNIT_TYPE;
        this.selected = false;
        this.font = new BitmapFont();
        this.font.getData().setScale(1.2f);

        switch (unitType) {
            case MELEE:
                this.label = "melees";
                break;
            case SNIPER:
                this.label = "snipers";
                break;
            case TANK:
                this.label = "tanks";
                break;
            default:
                this.label = "?";
                break;
        }

        Texture idleTex = new Texture(Gdx.files.internal("Frames/" + this.label + ".png"));
        this.idleFrame = new TextureRegion(idleTex);
    }

    /**
     * Constructs a button for selecting a spawn point in the shop.
     *
     * @param x X position of the button
     * @param y Y position of the button
     * @param width Width of the button
     * @param height Height of the button
     * @param spawnIndex The spawn index associated with this button
     */
    public UnitShopButton(float x, float y, float width, float height, int spawnIndex) {
        this.bounds = new Rectangle(x, y, width, height);
        this.unitType = null;
        this.spawnIndex = spawnIndex;
        this.buttonType = ButtonType.SPAWN_POINT;
        this.selected = false;
        this.label = "" + (spawnIndex + 1);
        this.font = new BitmapFont();
        this.font.getData().setScale(1.2f);

        Texture idleTex = new Texture(Gdx.files.internal("Frames/" + this.label + ".png"));
        this.idleFrame = new TextureRegion(idleTex);
    }

    /**
     * Renders the button, including its border, label, and icon.
     * Handles selection highlighting and draws the appropriate texture or label.
     *
     * @param shapeRenderer ShapeRenderer for drawing button borders
     * @param batch SpriteBatch for drawing textures and labels
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        TextureRegion currentFrame = idleFrame;
        float imgW = currentFrame.getRegionWidth();
        float imgH = currentFrame.getRegionHeight();
        float scale = (float) Math.min(bounds.width / imgW * 1.5, bounds.height / imgH * 1.5);
        float drawW = imgW * scale;
        float drawH = imgH * scale;
        float drawX = bounds.x + (bounds.width - drawW) / 2;
        float drawY = bounds.y + (bounds.height - drawH) / 2;

        // Draw border (thicker if selected)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (selected) {
            shapeRenderer.setColor(Color.PURPLE);
            shapeRenderer.rect(drawX + 16, drawY, drawW - 37, drawH + 1);
        }
        shapeRenderer.end();

        // Draw label or icon
        batch.begin();
        batch.draw(currentFrame, drawX, drawY, drawW, drawH);
        batch.end();
    }

    /**
     * Checks if the button was clicked based on the given touch coordinates.
     *
     * @param touchX X coordinate of the touch
     * @param touchY Y coordinate of the touch
     * @return true if the button was clicked, false otherwise
     */
    public boolean isClicked(float touchX, float touchY) {
        return bounds.contains(touchX, touchY);
    }

    /**
     * Returns the unit type associated with this button.
     *
     * @return The unit type, or null if not applicable
     */
    public Base.Type getUnitType() {
        return unitType;
    }

    /**
     * Returns the spawn index associated with this button.
     *
     * @return The spawn index, or null if not applicable
     */
    public Integer getSpawnIndex() {
        return spawnIndex;
    }

    /**
     * Returns the type of this button (unit type or spawn point).
     *
     * @return The button type
     */
    public ButtonType getButtonType() {
        return buttonType;
    }

    /**
     * Sets the selection state of this button.
     *
     * @param selected true to select the button, false to deselect
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Returns whether this button is currently selected.
     *
     * @return true if selected, false otherwise
     */
    public boolean isSelected() {
        return selected;
    }
}