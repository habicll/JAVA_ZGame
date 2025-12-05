package com.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.entities.Unit;
import com.main.entities.player.Hero;
import com.main.map.Base;

public class UnitShop {

    private List<UnitShopButton> unitTypeButtons;
    private List<UnitShopButton> spawnPointButtons;
    private Base playerBase;
    private Hero hero;
    private gold goldDisplay;

    // UI Camera and Viewport (same as HUD)
    private Viewport viewport;
    private OrthographicCamera camera;
    private Vector3 touchPos;

    // Selected spawn point (default to 0)
    private int selectedSpawnPoint = 0;

    private static final float BUTTON_WIDTH = 70f;
    private static final float BUTTON_HEIGHT = 50f;
    private static final float BUTTON_SPACING = 10f;
    private static final float START_X = 540f; // À droite (800 - 3*60 - 2*10 - 20)
    private static final float UNIT_BUTTONS_Y = 520f; // En haut
    private static final float SPAWN_BUTTONS_Y = 460f; // Juste en dessous
    protected List<Texture> loadedTextures = new ArrayList<>();
    // protected TextureRegion idleFrame;

    // UI Viewport dimensions (same as HUD)
    private static final float UI_WIDTH = 800f;
    private static final float UI_HEIGHT = 600f;

    public UnitShop(Base playerBase, Hero hero, gold goldDisplay) {
        this.playerBase = playerBase;
        this.hero = hero;
        this.unitTypeButtons = new ArrayList<>();
        this.spawnPointButtons = new ArrayList<>();
        this.goldDisplay = goldDisplay;

        // Initialize UI camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(UI_WIDTH, UI_HEIGHT, camera);
        camera.position.set(UI_WIDTH / 2, UI_HEIGHT / 2, 0);

        touchPos = new Vector3();

        createButtons();
    }
    /**
     * Constructs a UnitShop with default gold display (for backward compatibility).
     *
     * @param playerBase The player's base for unit spawning and purchasing
     * @param hero The hero entity associated with the shop
     */
    public UnitShop(Base playerBase, Hero hero) {
        this(playerBase, hero, null);
    }

    private void createButtons() {

        Base.Type[] types = { Base.Type.MELEE, Base.Type.SNIPER, Base.Type.TANK };

        for (int i = 0; i < types.length; i++) {
            float buttonX = START_X + i * (BUTTON_WIDTH + BUTTON_SPACING);
            unitTypeButtons.add(new UnitShopButton(
                    buttonX,
                    UNIT_BUTTONS_Y,
                    BUTTON_WIDTH,
                    BUTTON_HEIGHT,
                    types[i]));
        }

        // Créer les 3 boutons de point de spawn (en bas)
        for (int i = 0; i < 3; i++) {
            float buttonX = START_X + i * (BUTTON_WIDTH + BUTTON_SPACING);
            UnitShopButton spawnBtn = new UnitShopButton(
                    buttonX,
                    SPAWN_BUTTONS_Y,
                    BUTTON_WIDTH,
                    BUTTON_HEIGHT,
                    i);
            // Select the first button by default
            if (i == selectedSpawnPoint) {
                spawnBtn.setSelected(true);
            }
            spawnPointButtons.add(spawnBtn);
        }
    }

    /**
     * Renders the unit shop UI, including all unit type and spawn point buttons.
     * Updates camera and sets projection matrices for UI rendering.
     *
     * @param shapeRenderer ShapeRenderer for drawing button backgrounds and shapes
     * @param batch SpriteBatch for drawing button textures and labels
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        for (UnitShopButton button : spawnPointButtons) {
            button.render(shapeRenderer, batch);
        }
        for (UnitShopButton button : unitTypeButtons) {
            button.render(shapeRenderer, batch);
        }
    }

    /**
     * Handles mouse click events on the shop UI.
     * Determines if a spawn point or unit type button was clicked and updates selection or purchases units accordingly.
     *
     * @param screenX Screen X coordinate of the click
     * @param screenY Screen Y coordinate of the click
     * @return true if a button was clicked and handled, false otherwise
     */
    public boolean handleClick(int screenX, int screenY) {
        touchPos.set(screenX, screenY, 0);
        viewport.unproject(touchPos);
        for (int i = 0; i < spawnPointButtons.size(); i++) {
            UnitShopButton button = spawnPointButtons.get(i);
            if (button.isClicked(touchPos.x, touchPos.y)) {
                for (UnitShopButton btn : spawnPointButtons) {
                    btn.setSelected(false);
                }
                button.setSelected(true);
                selectedSpawnPoint = i;
                return true;
            }
        }
        for (UnitShopButton button : unitTypeButtons) {
            if (button.isClicked(touchPos.x, touchPos.y)) {
                Unit newUnit = playerBase.buyUnit(button.getUnitType(), selectedSpawnPoint, hero);
                if (newUnit != null) {
                    playerBase.addUnit(newUnit);
                    if (goldDisplay != null) {
                        goldDisplay.update(hero.getGold());
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Resizes the shop UI viewport when the window size changes.
     * Ensures UI elements remain properly scaled and positioned.
     *
     * @param width New screen width
     * @param height New screen height
     */
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(UI_WIDTH / 2, UI_HEIGHT / 2, 0);
    }

}
