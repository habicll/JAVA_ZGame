package com.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.entities.player.Hero;
import com.main.weapons.Weapon;
import com.ui.UnitShopButton.ButtonType;

public class Inventory {
    private List<UnitShopButton> unitTypeButtons;
    private List<UnitShopButton> spawnPointButtons;
    private Hero hero;
    private String weapon;
    private String currentWeaponName;

    private Viewport viewport;
    private OrthographicCamera camera;
    private Vector3 touchPos;

    private static final float BUTTON_WIDTH = 200f;
    private static final float BUTTON_HEIGHT = 100f;
    private static final float START_X = 550f; // À droite (800 - 3*60 - 2*10 - 20)
    private static final float UNIT_BUTTONS_Y = 20f; // En haut

    
    private static final float CHOOSE_WIDTH = 35f;
    private static final float CHOOSE_HEIGHT = 35f;
    private float CHOOSE_X = 528f; // À droite (800 - 3*60 - 2*10 - 20)
    private static final float CHOOSE_Y = 54f; // Position au-dessus de l'inventaire
    protected List<Texture> loadedTextures = new ArrayList<>();
    private Texture chooseTexture;
    
    private static final float UI_WIDTH = 800f;
    private static final float UI_HEIGHT = 600f; 
    protected TextureRegion idleFrame;
    protected TextureRegion chooseFrame;
    private Rectangle bounds;
    private Rectangle bounds2;

    public Inventory(Hero hero) {
        this.hero = hero;
        this.weapon = hero.getWeapon().getClass().getSimpleName();
        this.currentWeaponName = this.weapon;
        System.out.println(weapon);
        this.unitTypeButtons = new ArrayList<>();
        this.spawnPointButtons = new ArrayList<>();

        // if

        // Initialize UI camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(UI_WIDTH, UI_HEIGHT, camera);
        camera.position.set(UI_WIDTH / 2, UI_HEIGHT / 2, 0);

        touchPos = new Vector3();

        Texture idleTex = new Texture(Gdx.files.internal("inventory/inventory.png"));
        this.idleFrame = new TextureRegion(idleTex);

        Texture chooseTex = new Texture(Gdx.files.internal("inventory/choose.png"));
        this.chooseFrame = new TextureRegion(chooseTex);
        this.chooseTexture = chooseTex;
        // START_X is already in pixels; do not multiply by BUTTON_WIDTH again.
        float buttonX = START_X;
        float chooseX = CHOOSE_X;

        // Button area: use BUTTON_WIDTH x BUTTON_HEIGHT (not full UI height)
        this.bounds = new Rectangle(buttonX, UNIT_BUTTONS_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        this.bounds2 = new Rectangle(chooseX, CHOOSE_Y, CHOOSE_WIDTH, CHOOSE_HEIGHT);
        // register the texture for disposal if needed
        loadedTextures.add(idleTex);
    }

    private void reloadWeaponTexture(String weaponName) {
        // dispose previous textures
        // for (Texture t : loadedTextures) {
        //     try {
        //         t.dispose();
        //     } catch (Exception e) {
        //     }
        // }
        // loadedTextures.clear();

        // load new texture
        try {
            switch(weaponName){
                case "Pistol": 
                    this.CHOOSE_X = 528f;
                    break;
                case "Shotgun": 
                    this.CHOOSE_X = 528f + 52f;
                    break;
                case "SMG": 
                    this.CHOOSE_X = 528f + 52f*2; 
                    break;
                case "AssaultRifle": 
                    this.CHOOSE_X = 528f + 52f*3; 
                    break;
                case "SniperRifle": 
                    this.CHOOSE_X = 528f + 52f*4;
                    break; 
            }
            this.weapon = weaponName;
            this.currentWeaponName = weaponName;
            bounds2.x = CHOOSE_X;
            System.out.println("Inventory: weapon texture reloaded -> " + weaponName);
        } catch (Exception e) {
       System.out.println("Inventory: failed to load weapon texture for " + weaponName + " : " + e.getMessage());
       
    }
}


    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        // Ensure UI camera/projection is used for UI rendering
        camera.update();
        viewport.apply();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        // Check whether hero weapon changed and reload texture if needed
        String heroWeaponName = hero.getWeapon().getClass().getSimpleName();
        if (currentWeaponName == null || !currentWeaponName.equals(heroWeaponName)) {
            reloadWeaponTexture(heroWeaponName);
        }

        TextureRegion currentFrame = idleFrame;

        float imgW = currentFrame.getRegionWidth();
        float imgH = currentFrame.getRegionHeight();

        float scale = (float) Math.min(bounds.width / imgW * 1.5, bounds.height / imgH * 1.5); // garder ratio

        float drawW = imgW * scale;
        float drawH = imgH * scale;

        float drawX = bounds.x + (bounds.width - drawW) / 2;
        float drawY = bounds.y + (bounds.height - drawH) / 2;

        batch.begin();
            batch.draw(currentFrame, drawX, drawY, drawW, drawH);
            if (chooseFrame != null) {
                TextureRegion chooseRegion = chooseFrame;
                float cW = chooseRegion.getRegionWidth();
                float cH = chooseRegion.getRegionHeight();
                float cScale = Math.min(bounds2.width / cW, bounds2.height / cH);
                float cDrawW = cW * cScale;
                float cDrawH = cH * cScale;
                float cDrawX = bounds2.x + (bounds2.width - cDrawW) / 2;
                float cDrawY = bounds2.y + (bounds2.height - cDrawH) / 2;
                batch.draw(chooseRegion, cDrawX, cDrawY, cDrawW, cDrawH);
            }
        batch.end();
    }

    public void dispose() {
        for (Texture t : loadedTextures) {
            try {
                t.dispose();
            } catch (Exception e) {
                // ignore
            }
        }
        loadedTextures.clear();
        if (chooseTexture != null) {
            try {
                chooseTexture.dispose();
            } catch (Exception e) {
                // ignore
            }
            chooseTexture = null;
        }
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(UI_WIDTH / 2, UI_HEIGHT / 2, 0);
    }


}
