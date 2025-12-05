package com.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.GameScreen;
import com.main.entities.Unit;
import com.main.entities.player.Hero;

public class Target {

    private Hero hero;
    private Unit target;
    private Unit currentTarget;

    private Viewport viewport;
    private Vector3 touchPos;

    private static final float CHOOSE_WIDTH = 15f;
    private static final float CHOOSE_HEIGHT = 15f;
    private float CHOOSE_X = 528f; // À droite (800 - 3*60 - 2*10 - 20)
    private static final float CHOOSE_Y = 54f; // Position au-dessus de l'inventaire
    protected List<Texture> loadedTextures = new ArrayList<>();
    private Texture chooseTexture;

    private static final float UI_WIDTH = 800f;
    private static final float UI_HEIGHT = 600f;
    protected TextureRegion idleFrame;
    protected TextureRegion chooseFrame;
    private Rectangle bounds;
    private OrthographicCamera worldCamera;

    public Target(Hero hero, OrthographicCamera camera) {
        this.hero = hero;
        this.target = hero.getTarget();
        System.out.println(target);

        // if

        // Initialize UI camera and viewport
        // camera = new OrthographicCamera();
        // viewport = new FitViewport(UI_WIDTH, UI_HEIGHT, camera);
        // camera.position.set(UI_WIDTH / 2, UI_HEIGHT / 2, 0);

        // touchPos = new Vector3();

        Texture chooseTex = new Texture(Gdx.files.internal("Frames/aim.png"));
        this.chooseFrame = new TextureRegion(chooseTex);
        this.chooseTexture = chooseTex;

        // START_X is already in pixels; do not multiply by BUTTON_WIDTH again.
        float chooseX = CHOOSE_X;

        // Button area: use BUTTON_WIDTH x BUTTON_HEIGHT (not full UI height)
        this.bounds = new Rectangle(chooseX, CHOOSE_Y, CHOOSE_WIDTH, CHOOSE_HEIGHT);
        // register the texture for disposal if needed
        this.worldCamera = camera;
    }

    private void reloadWeaponTexture(Unit target) {
        // dispose previous textures
        // for (Texture t : loadedTextures) {
        // try {
        // t.dispose();
        // } catch (Exception e) {
        // }
        // }
        // loadedTextures.clear();

        // load new texture
        try {
            this.target = target;
            this.currentTarget = target;
            bounds.x = target.getPosX();
            bounds.y = target.getPosY();
            // System.out.println("Target: target position changed -> " + target);
        } catch (Exception e) {
            System.out.println("Target: failed to update for " + target + " : " + e.getMessage());

        }
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        // Ensure UI camera/projection is used for UI rendering
        // camera.update();
        // viewport.apply();
        // shapeRenderer.setProjectionMatrix(camera.combined);
        // batch.setProjectionMatrix(camera.combined);

        Unit heroTarget = hero.getTarget();
        if (this.currentTarget == null || !currentTarget.equals(heroTarget) || currentTarget.getPosX() == bounds.x
                || currentTarget.getPosY() == bounds.y) {
            reloadWeaponTexture(heroTarget);
        }
        // if (this.currentTarget == null){
        // return;
        // }

        TextureRegion currentFrame = chooseFrame;

        float imgW = currentFrame.getRegionWidth();
        float imgH = currentFrame.getRegionHeight();

        float scale = (float) Math.min(bounds.width / imgW * 1.5, bounds.height / imgH * 1.5); // garder ratio

        float drawW = imgW * scale;
        float drawH = imgH * scale;

        if(target != null){
        float positionX = target.getPosX();
        float positionY = target.getPosY();
        

        if (target != null) {
            batch.begin();
            batch.setProjectionMatrix(worldCamera.combined);
            if(target.getType().equals("WZombie")){
                positionX += 37;
                positionY += 20;
            }
            else if (target.getType().equals("CZombie")){
                positionX += 15;
                positionY += 2;
            }            
            else if (target.getType().equals("FZombie")){
                positionX += 5;
                positionY += 30;
            }
            batch.draw(currentFrame, positionX, positionY,  drawW, drawH);
            batch.end();
        }
    }
    }

    public void dispose() {
        for (Texture t : loadedTextures) {
            try {
                t.dispose();
            } catch (Exception e) {
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

    // public void resize(int width, int height) {
    // viewport.update(width, height);
    // camera.position.set(UI_WIDTH / 2, UI_HEIGHT / 2, 0);
    // }

}
