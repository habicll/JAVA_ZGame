package com.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


/**
 * The {@code BaseDestroyedOverlay} class displays an overlay when the player's base is destroyed.
 * It renders a semi-transparent background, a "GAME OVER" title, and interactive buttons (REPLAY, QUIT).
 * Handles input, hover effects, and resource management for the overlay.
 * Implements {@link Disposable} for proper cleanup.
 */
public class BaseDestroyedOverlay implements Disposable {
    
    /**
     * Viewport for scaling and positioning overlay elements.
     */
    private Viewport viewport;

    /**
     * SpriteBatch for drawing textures and fonts.
     */
    private SpriteBatch batch;

    /**
     * ShapeRenderer for drawing shapes (background, overlays).
     */
    private ShapeRenderer shapeRenderer;

    /**
     * Logical width of the overlay.
     */
    private static final float OVERLAY_WIDTH = 800f;

    /**
     * Logical height of the overlay.
     */
    private static final float OVERLAY_HEIGHT = 600f;

    /**
     * Font for rendering the title text.
     */
    private BitmapFont titleFont;

    /**
     * Font for rendering button text.
     */
    private BitmapFont buttonFont;

    /**
     * Layout for measuring and positioning the title text.
     */
    private GlyphLayout titleLayout;

    /**
     * Rectangle representing the REPLAY button for click detection.
     */
    private Rectangle replayButton;

    /**
     * Rectangle representing the QUIT button for click detection.
     */
    private Rectangle quitButton;

    /**
     * Index of the currently hovered button (-1 = none, 0 = REPLAY, 1 = QUIT).
     */
    private int selectedIndex = -1;

    /**
     * Width of the buttons.
     */
    private static final float BUTTON_WIDTH = 200f;

    /**
     * Height of the buttons.
     */
    private static final float BUTTON_HEIGHT = 60f;
    
    /**
     * Constructs the overlay and initializes all resources, fonts, and button positions.
     * Sets up viewport, rendering tools, and interactive elements.
     */
    public BaseDestroyedOverlay() {
        viewport = new FitViewport(OVERLAY_WIDTH, OVERLAY_HEIGHT);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Initialiser le font pour le titre
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 36;
            parameter.color = Color.RED;
            parameter.borderWidth = 3f;
            parameter.borderColor = Color.BLACK;
            parameter.shadowOffsetX = 3;
            parameter.shadowOffsetY = 3;
            parameter.shadowColor = new Color(0, 0, 0, 0.5f);
            
            titleFont = generator.generateFont(parameter);
            generator.dispose();
        } catch (Exception e) {
            System.err.println("Could not load PressStart2P font: " + e.getMessage());
            titleFont = new BitmapFont();
            titleFont.setColor(Color.RED);
            titleFont.getData().setScale(3f);
        }
        
        titleLayout = new GlyphLayout();
        
        // Initialize button font (same style as GameOverOverlay)
        FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter buttonParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        buttonParam.size = 32;
        buttonParam.borderWidth = 2.5f;
        buttonParam.borderColor = new Color(0f, 0f, 0f, 0.85f);
        buttonParam.shadowOffsetX = 2;
        buttonParam.shadowOffsetY = -2;
        buttonParam.shadowColor = new Color(0f, 0f, 0f, 0.7f);
        buttonParam.magFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
        buttonParam.minFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
        buttonFont = generator2.generateFont(buttonParam);
        generator2.dispose();
        
        // Initialize button rectangles (centered below the title)
        float centerX = OVERLAY_WIDTH / 2;
        float centerY = OVERLAY_HEIGHT / 2 - 50;
        
        replayButton = new Rectangle(
            centerX - BUTTON_WIDTH / 2, 
            centerY - 20, 
            BUTTON_WIDTH, 
            BUTTON_HEIGHT
        );
        
        quitButton = new Rectangle(
            centerX - BUTTON_WIDTH / 2, 
            centerY - 100, 
            BUTTON_WIDTH, 
            BUTTON_HEIGHT
        );
    }
    
    /**
     * Renders the destroyed base overlay, including background, title, and buttons.
     * Handles hover effects and button highlighting.
     */
    public void render() {
        // Update hover effect
        updateHover();
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        
        // Utiliser les dimensions réelles du viewport
        float screenWidth = viewport.getWorldWidth();
        float screenHeight = viewport.getWorldHeight();
        
        // Fond semi-transparent noir
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, screenWidth, screenHeight);
        shapeRenderer.end();
        
        Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        
        batch.begin();
        
        // Titre "VOTRE BASE A ÉTÉ DÉTRUITE"
        String title = "GAME OVER";
        titleLayout.setText(titleFont, title);
        
        // Adapter l'échelle de la font si le texte est trop large
        float scale = 1f;
        float maxWidth = screenWidth * 0.85f; // 85% de la largeur de l'écran
        if (titleLayout.width > maxWidth) {
            scale = maxWidth / titleLayout.width;
            titleFont.getData().setScale(scale);
            titleLayout.setText(titleFont, title);
        }
        
        float titleX = (screenWidth - titleLayout.width) / 2f;
        float titleY = screenHeight / 2f + 100f;
        
        titleFont.draw(batch, title, titleX, titleY);
        
        // Restaurer l'échelle originale
        if (scale != 1f) {
            titleFont.getData().setScale(1f);
        }
        
        // Draw buttons REPLAY and QUIT with hover effect
        GlyphLayout replayLayout = new GlyphLayout(buttonFont, "REPLAY");
        float replayX = (screenWidth - replayLayout.width) / 2f;
        if (selectedIndex == 0) {
            drawTextInverted("REPLAY", replayX, replayButton.y + 40);
        } else {
            buttonFont.setColor(Color.WHITE);
            buttonFont.draw(batch, replayLayout, replayX, replayButton.y + 40);
        }

        GlyphLayout quitLayout = new GlyphLayout(buttonFont, "QUIT");
        float quitX = (screenWidth - quitLayout.width) / 2f;
        if (selectedIndex == 1) {
            drawTextInverted("QUIT", quitX, quitButton.y + 40);
        } else {
            buttonFont.setColor(Color.WHITE);
            buttonFont.draw(batch, quitLayout, quitX, quitButton.y + 40);
        }
        
        batch.end();
    }
    
    /**
     * Draws button text with inverted effect (yellow background, black text) for hover state.
     *
     * @param text The text to render
     * @param x X position
     * @param y Y position
     */
    private void drawTextInverted(String text, float x, float y) {
        GlyphLayout layout = new GlyphLayout(buttonFont, text);

        // Ombre jaune/dorée (plus épaisse pour effet de fond)
        buttonFont.setColor(Color.YELLOW);
        for (int offsetX = -2; offsetX <= 2; offsetX++) {
            for (int offsetY = -2; offsetY <= 2; offsetY++) {
                buttonFont.draw(batch, layout, x + offsetX, y + offsetY);
            }
        }

        // Texte noir par-dessus
        buttonFont.setColor(Color.BLACK);
        buttonFont.draw(batch, layout, x, y);
    }
    
    /**
     * Updates the hover effect for buttons based on mouse position.
     * Sets {@code selectedIndex} to the hovered button index.
     */
    private void updateHover() {
        Vector2 mouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        float mx = mouse.x;
        float my = mouse.y;

        selectedIndex = -1;

        // REPLAY button
        GlyphLayout replayLayout = new GlyphLayout(buttonFont, "REPLAY");
        float replayX = (OVERLAY_WIDTH - replayLayout.width) / 2f;
        
        Rectangle replayHitbox = new Rectangle(
            replayX - 10, 
            replayButton.y + 40 - replayLayout.height - 5,
            replayLayout.width + 20,
            replayLayout.height + 10
        );
        
        if (replayHitbox.contains(mx, my)) {
            selectedIndex = 0;
        }

        // QUIT button
        GlyphLayout quitLayout = new GlyphLayout(buttonFont, "QUIT");
        float quitX = (OVERLAY_WIDTH - quitLayout.width) / 2f;
        
        Rectangle quitHitbox = new Rectangle(
            quitX - 10,
            quitButton.y + 40 - quitLayout.height - 5,
            quitLayout.width + 20,
            quitLayout.height + 10
        );
        
        if (quitHitbox.contains(mx, my)) {
            selectedIndex = 1;
        }
    }
    
    /**
     * Handles mouse click events on the overlay and determines which button was clicked.
     *
     * @param screenX Screen X coordinate of the click
     * @param screenY Screen Y coordinate of the click
     * @return "replay" if REPLAY button was clicked, "quit" if QUIT button was clicked, {@code null} otherwise
     */
    public String handleClick(int screenX, int screenY) {
        Vector2 mouse = viewport.unproject(new Vector2(screenX, screenY));
        float worldX = mouse.x;
        float worldY = mouse.y;
        
        // Check REPLAY button
        GlyphLayout replayLayout = new GlyphLayout(buttonFont, "REPLAY");
        float replayX = (OVERLAY_WIDTH - replayLayout.width) / 2f;
        
        Rectangle replayHitbox = new Rectangle(
            replayX - 10, 
            replayButton.y + 40 - replayLayout.height - 5,
            replayLayout.width + 20,
            replayLayout.height + 10
        );
        
        if (replayHitbox.contains(worldX, worldY)) {
            return "replay";
        }
        
        // Check QUIT button
        GlyphLayout quitLayout = new GlyphLayout(buttonFont, "QUIT");
        float quitX = (OVERLAY_WIDTH - quitLayout.width) / 2f;
        
        Rectangle quitHitbox = new Rectangle(
            quitX - 10,
            quitButton.y + 40 - quitLayout.height - 5,
            quitLayout.width + 20,
            quitLayout.height + 10
        );
        
        if (quitHitbox.contains(worldX, worldY)) {
            return "quit";
        }
        
        return null;
    }
    
    /**
     * Resizes the overlay viewport when the window size changes.
     *
     * @param width New width of the window
     * @param height New height of the window
     */
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    
    /**
     * Disposes all resources used by the overlay, including rendering components and fonts.
     * Should be called when the overlay is no longer needed to free memory.
     */
    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        titleFont.dispose();
        buttonFont.dispose();
    }
}
