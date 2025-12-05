package com.ui;
/**
 * Represents the game over overlay displayed when the player's hero dies.
 * <p>
 * This overlay renders a semi-transparent background, a central panel, a title, and interactive buttons (REPLAY, QUIT).
 * It manages rendering, input detection, hover effects, and resource cleanup. Integrates with libGDX's viewport and camera system for consistent UI scaling.
 * Implements {@link Disposable} for proper resource management.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class GameOverOverlay implements Disposable {
    
    /**
     * Viewport for scaling and positioning overlay elements.
     * Ensures the overlay is rendered consistently across different screen sizes.
     */
    private Viewport viewport;

    /**
     * Orthographic camera for rendering overlay elements in 2D space.
     */
    private OrthographicCamera camera;

    /**
     * Layout for measuring and positioning the title text.
     */
    private GlyphLayout titleLayout;

    /**
     * SpriteBatch for drawing textures and fonts.
     */
    private SpriteBatch batch;

    /**
     * ShapeRenderer for drawing shapes (background, overlays, buttons).
     */
    private ShapeRenderer shapeRenderer;

    /**
     * Font for rendering the title text.
     */
    private BitmapFont titleFont;

    /**
     * Font for rendering button text.
     */
    private BitmapFont buttonFont;

    /**
     * Rectangle representing the REPLAY button for click detection and hover effect.
     */
    private Rectangle replayButton;

    /**
     * Rectangle representing the QUIT button for click detection and hover effect.
     */
    private Rectangle quitButton;

    /**
     * Index of the currently hovered button (-1 = none, 0 = REPLAY, 1 = QUIT).
     * Used to manage hover effects and highlight the correct button.
     */
    private int selectedIndex = -1;

    /**
     * Logical width of the overlay in pixels.
     */
    private static final float OVERLAY_WIDTH = 800f;

    /**
     * Logical height of the overlay in pixels.
     */
    private static final float OVERLAY_HEIGHT = 600f;

    /**
     * Width of the buttons in pixels.
     */
    private static final float BUTTON_WIDTH = 200f;

    /**
     * Height of the buttons in pixels.
     */
    private static final float BUTTON_HEIGHT = 60f;

    /**
     * Constructs the GameOverOverlay and initializes all rendering resources, fonts, and button positions.
     * Sets up camera, viewport, rendering tools, and interactive elements for the overlay.
     */
    public GameOverOverlay() {
        // Initialize camera and viewport (fixed overlay)
        camera = new OrthographicCamera();
        viewport = new FitViewport(OVERLAY_WIDTH, OVERLAY_HEIGHT, camera);
        camera.position.set(OVERLAY_WIDTH / 2, OVERLAY_HEIGHT / 2, 0);
        
        // Initialize rendering components
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Initialize fonts
        titleFont = new BitmapFont();
        titleFont.setColor(Color.RED);
        titleFont.getData().setScale(3f);
        
        // Font identical to TitleScreen with border and shadow
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.borderWidth = 2.5f;
        parameter.borderColor = new Color(0f, 0f, 0f, 0.85f);
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = -2;
        parameter.shadowColor = new Color(0f, 0f, 0f, 0.7f);
        parameter.magFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
        parameter.minFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
        buttonFont = generator.generateFont(parameter);
        generator.dispose();
        
        // Initialize button rectangles (centered)
        float centerX = OVERLAY_WIDTH / 2;
        float centerY = OVERLAY_HEIGHT / 2;
        
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
     * Renders the game over overlay, including background, title, and buttons.
     * Handles hover effects and button highlighting.
     * <p>
     * This method should be called every frame while the overlay is active.
     */
    public void render() {
        // Update hover effect
        updateHover();
        
        // Update camera
        camera.update();
        
        // Set projection matrices
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        
        // Draw semi-transparent background
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.6f); // Black with 70% opacity
        shapeRenderer.rect(0, 0, OVERLAY_WIDTH, OVERLAY_HEIGHT);
        shapeRenderer.end();
        
        // Draw central panel
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.9f); // Dark gray
        shapeRenderer.rect(
            OVERLAY_WIDTH / 2 - 300, 
            OVERLAY_HEIGHT / 2 - 150, 
            600, 
            300
        );
        shapeRenderer.end();
        
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        // Draw text
        batch.begin();
        
        // Title: "Game Over"
        GlyphLayout titleLayout = new GlyphLayout(titleFont, "Game Over");
        float titleX = (OVERLAY_WIDTH - titleLayout.width) / 2f;
        titleFont.draw(batch, "Game Over", 
            titleX, 
            OVERLAY_HEIGHT / 2 + 120
        );
        
        // Hover effect for REPLAY (visually on top, but index 1 due to Y inversion)
        GlyphLayout replayLayout = new GlyphLayout(buttonFont, "REPLAY");
        float replayX = (OVERLAY_WIDTH - replayLayout.width) / 2f;
        Color replayColor = (selectedIndex == 1) ? Color.YELLOW : Color.WHITE;
        buttonFont.setColor(replayColor);
        buttonFont.draw(batch, replayLayout, replayX, replayButton.y + 40);

        // Hover effect for QUIT (visually on bottom, but index 0 due to Y inversion)
        GlyphLayout quitLayout = new GlyphLayout(buttonFont, "QUIT");
        float quitX = (OVERLAY_WIDTH - quitLayout.width) / 2f;
        Color quitColor = (selectedIndex == 0) ? Color.YELLOW : Color.WHITE;
        buttonFont.setColor(quitColor);
        buttonFont.draw(batch, quitLayout, quitX, quitButton.y + 40);
        
        batch.end();
    }

    /**
     * Draws a button with background and border using the specified color.
     * Used for custom button rendering and highlighting.
     *
     * @param button Rectangle representing the button area
     * @param color Color for the button background
     */
    private void drawButton(Rectangle button, Color color) {
        // Draw button background
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(color.r, color.g, color.b, 0.6f);
        shapeRenderer.rect(button.x, button.y, button.width, button.height);
        shapeRenderer.end();
        
        // Draw button border
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(button.x, button.y, button.width, button.height);
        shapeRenderer.end();
    }

    /**
     * Handles mouse click events on the overlay and determines which button was clicked.
     * Converts screen coordinates to overlay coordinates and checks button hitboxes.
     *
     * @param screenX Screen X coordinate of the click
     * @param screenY Screen Y coordinate of the click
     * @return "replay" if REPLAY button was clicked, "quit" if QUIT button was clicked, {@code null} otherwise
     */
    public String handleClick(int screenX, int screenY) {
        // Convert screen coordinates to viewport coordinates using unproject (same as TitleScreen)
        Vector2 mouse = viewport.unproject(new Vector2(screenX, screenY));
        float worldX = mouse.x;
        float worldY = mouse.y;
        
        if (replayButton.contains(worldX, worldY)) {
            return "replay";
        }
        
        if (quitButton.contains(worldX, worldY)) {
            return "quit";
        }
        
        return null;
    }

    /**
     * Updates the hover effect for buttons based on mouse position.
     * Sets {@code selectedIndex} to the hovered button index for visual feedback.
     * Should be called every frame before rendering.
     */
    private void updateHover() {
        Vector2 mouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        float mx = mouse.x;
        float my = mouse.y;

        selectedIndex = -1;

        // QUIT button (bottom, index 1) - uses Rectangle for detection
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

        // REPLAY button (top, index 0) - uses Rectangle for detection
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
    }

    /**
     * Resizes the overlay viewport when the window size changes.
     * Updates camera position to keep the overlay centered.
     *
     * @param width New width of the window
     * @param height New height of the window
     */
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(OVERLAY_WIDTH / 2, OVERLAY_HEIGHT / 2, 0);
    }

    /**
     * Disposes all resources used by the overlay, including rendering components and fonts.
     * Should be called when the overlay is no longer needed to free memory and GPU resources.
     */
    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        titleFont.dispose();
        buttonFont.dispose();
    }
}