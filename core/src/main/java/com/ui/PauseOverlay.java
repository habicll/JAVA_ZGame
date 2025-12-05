package com.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
 * Represents the pause overlay UI displayed when the game is paused.
 * Manages rendering, button interactions, hover effects, and confirmation dialogs for quitting to the menu.
 * Handles resource management and viewport resizing for consistent UI behavior.
 */
public class PauseOverlay implements Disposable {
    
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    
    /**
     * Logical width of the overlay in world units.
     */
    private static final float OVERLAY_WIDTH = 800f;
    /**
     * Logical height of the overlay in world units.
     */
    private static final float OVERLAY_HEIGHT = 600f;

    /**
     * Font for the overlay title.
     */
    private BitmapFont titleFont;
    /**
     * Font for button labels.
     */
    private BitmapFont buttonFont;
    /**
     * Font for confirmation dialog text.
     */
    private BitmapFont confirmFont;

    /**
     * Layout for measuring and positioning the title text.
     */
    private GlyphLayout titleLayout;

    /**
     * Rectangle for RESUME button hitbox.
     */
    private Rectangle resumeButton;
    /**
     * Rectangle for OPTIONS button hitbox.
     */
    private Rectangle optionsButton;
    /**
     * Rectangle for QUIT button hitbox.
     */
    private Rectangle quitButton;
    /**
     * Rectangle for YES button hitbox in confirmation dialog.
     */
    private Rectangle yesButton;
    /**
     * Rectangle for NO button hitbox in confirmation dialog.
     */
    private Rectangle noButton;

    /**
     * Index of the currently hovered button (-1 = none, 0 = RESUME/YES, 1 = OPTIONS, 2 = QUIT/NO).
     */
    private int selectedIndex = -1;

    /**
     * Index of the last hovered button for tracking hover changes.
     */
    private int lastHoveredIndex = -1;

    /**
     * Time elapsed since hover started, used for smooth zoom transition.
     */
    private float hoverTime = 0f;

    /**
     * Duration of the hover zoom transition animation in seconds.
     */
    private static final float ZOOM_TRANSITION_DURATION = 0.15f;

    /**
     * Maximum scale factor when button is fully hovered (1.2 = 20% bigger).
     */
    private static final float MAX_ZOOM_SCALE = 1.2f;

    /**
     * Button width in world units.
     */
    private static final float BUTTON_WIDTH = 200f;
    /**
     * Button height in world units.
     */
    private static final float BUTTON_HEIGHT = 60f;

    /**
     * True if confirmation dialog is shown.
     */
    private boolean showConfirmation = false;
    
    /**
     * Constructs the PauseOverlay and initializes all UI components, fonts, and button hitboxes.
     * Loads custom fonts and sets up rectangles for button click detection and hover effects.
     * Ensures the overlay is ready for rendering and interaction.
     */
    public PauseOverlay() {
        viewport = new FitViewport(OVERLAY_WIDTH, OVERLAY_HEIGHT);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Initialize title font
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 48;
            parameter.color = Color.WHITE;
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
            titleFont.setColor(Color.WHITE);
            titleFont.getData().setScale(4f);
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
        
        // Initialize confirmation font (smaller)
        FreeTypeFontGenerator generator3 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter confirmParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        confirmParam.size = 24;
        confirmParam.borderWidth = 2f;
        confirmParam.borderColor = new Color(0f, 0f, 0f, 0.85f);
        confirmParam.shadowOffsetX = 2;
        confirmParam.shadowOffsetY = -2;
        confirmParam.shadowColor = new Color(0f, 0f, 0f, 0.7f);
        confirmParam.magFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
        confirmParam.minFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
        confirmFont = generator3.generateFont(confirmParam);
        generator3.dispose();
        
        // Initialize button rectangles (centered below the title)
        float centerX = OVERLAY_WIDTH / 2;
        float resumeY = OVERLAY_HEIGHT / 2f + 40f;
        float optionsY = resumeY - BUTTON_HEIGHT - 40f;
        float quitY = 40f; // bien en bas

        resumeButton = new Rectangle(
            centerX - BUTTON_WIDTH / 2,
            resumeY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );

        optionsButton = new Rectangle(
            centerX - BUTTON_WIDTH / 2,
            optionsY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );

        quitButton = new Rectangle(
            centerX - BUTTON_WIDTH / 2,
            quitY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );
        
        // Confirmation buttons (YES/NO)
        float confirmY = 100f; // position fixe pour la confirmation, similaire à l'ancien centre -150
        yesButton = new Rectangle(
            centerX - BUTTON_WIDTH - 20,
            confirmY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );

        noButton = new Rectangle(
            centerX + 20,
            confirmY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );
    }
    
    /**
     * Renders the pause overlay UI, including background, title, buttons, and confirmation dialog.
     * Handles hover effects and button highlighting based on mouse position.
     * Should be called every frame while the game is paused.
     */
    public void render() {
        // Update hover effect
        updateHover();
        
        // Update zoom animation time
        if (selectedIndex != -1 && selectedIndex == lastHoveredIndex) {
            hoverTime = Math.min(hoverTime + Gdx.graphics.getDeltaTime(), ZOOM_TRANSITION_DURATION);
        }

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        float screenWidth = viewport.getWorldWidth();
        float screenHeight = viewport.getWorldHeight();

        // Draw semi-transparent black background
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, screenWidth, screenHeight);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        
        if (!showConfirmation) {
            // Titre "PAUSED"
            String title = "PAUSED";
            titleLayout.setText(titleFont, title);
            
            float titleX = (screenWidth - titleLayout.width) / 2f;
            float titleY = screenHeight / 2f + 150f;
            
            titleFont.draw(batch, title, titleX, titleY);
            
            // Draw buttons RESUME, OPTIONS, and QUIT with zoom effect
            float resumeScale = getZoomScale(0);
            buttonFont.getData().setScale(resumeScale);
            buttonFont.setColor(Color.WHITE);
            GlyphLayout resumeScaledLayout = new GlyphLayout(buttonFont, "RESUME");
            float resumeX = (screenWidth - resumeScaledLayout.width) / 2f;
            buttonFont.draw(batch, resumeScaledLayout, resumeX, resumeButton.y + 20);
            buttonFont.getData().setScale(1f);

            float optionsScale = getZoomScale(1);
            buttonFont.getData().setScale(optionsScale);
            buttonFont.setColor(Color.WHITE);
            GlyphLayout optionsScaledLayout = new GlyphLayout(buttonFont, "OPTIONS");
            float optionsX = (screenWidth - optionsScaledLayout.width) / 2f;
            buttonFont.draw(batch, optionsScaledLayout, optionsX, optionsButton.y + 20);
            buttonFont.getData().setScale(1f);

            float quitScale = getZoomScale(2);
            buttonFont.getData().setScale(quitScale);
            buttonFont.setColor(Color.WHITE);
            GlyphLayout quitScaledLayout = new GlyphLayout(buttonFont, "QUIT");
            float quitX = (screenWidth - quitScaledLayout.width) / 2f;
            buttonFont.draw(batch, quitScaledLayout, quitX, quitButton.y + 20);
            buttonFont.getData().setScale(1f);
        } else {
            // Confirmation screen
            String confirmText = "RETURN TO MENU ?";
            GlyphLayout confirmLayout = new GlyphLayout(confirmFont, confirmText);
            float confirmX = (screenWidth - confirmLayout.width) / 2f;
            float confirmY = screenHeight / 2f + 100f;
            
            confirmFont.setColor(Color.YELLOW);
            confirmFont.draw(batch, confirmText, confirmX, confirmY);
            
            // Draw YES and NO buttons with zoom effect
            float yesScale = getZoomScale(0);
            buttonFont.getData().setScale(yesScale);
            buttonFont.setColor(Color.WHITE);
            GlyphLayout yesScaledLayout = new GlyphLayout(buttonFont, "YES");
            float yesX = yesButton.x + (BUTTON_WIDTH - yesScaledLayout.width) / 2f;
            buttonFont.draw(batch, yesScaledLayout, yesX, yesButton.y + 40);
            buttonFont.getData().setScale(1f);

            float noScale = getZoomScale(1);
            buttonFont.getData().setScale(noScale);
            buttonFont.setColor(Color.WHITE);
            GlyphLayout noScaledLayout = new GlyphLayout(buttonFont, "NO");
            float noX = noButton.x + (BUTTON_WIDTH - noScaledLayout.width) / 2f;
            buttonFont.draw(batch, noScaledLayout, noX, noButton.y + 40);
            buttonFont.getData().setScale(1f);
        }
        
        batch.end();
    }
    
    /**
     * Draws button text with an inverted effect (yellow background, black text) for hover highlighting.
     * Used to visually indicate the currently hovered or selected button.
     *
     * @param text The button label to draw.
     * @param x The X position for the text.
     * @param y The Y position for the text.
     */
    private void drawTextInverted(String text, float x, float y) {
        GlyphLayout layout = new GlyphLayout(buttonFont, text);

        // Draw thick yellow shadow for highlight effect
        buttonFont.setColor(Color.YELLOW);
        for (int offsetX = -2; offsetX <= 2; offsetX++) {
            for (int offsetY = -2; offsetY <= 2; offsetY++) {
                buttonFont.draw(batch, layout, x + offsetX, y + offsetY);
            }
        }

        // Draw black text on top
        buttonFont.setColor(Color.BLACK);
        buttonFont.draw(batch, layout, x, y);
    }
    
    /**
     * Updates the hover effect for buttons based on the current mouse position.
     * Determines which button is currently hovered and updates selectedIndex accordingly.
     * Supports both main menu and confirmation dialog states.
     */
    private void updateHover() {
        Vector2 mouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        float mx = mouse.x;
        float my = mouse.y;

        int previousIndex = selectedIndex;
        selectedIndex = -1;

        if (!showConfirmation) {
            // RESUME button
            GlyphLayout resumeLayout = new GlyphLayout(buttonFont, "RESUME");
            float resumeX = (OVERLAY_WIDTH - resumeLayout.width) / 2f;

            Rectangle resumeHitbox = new Rectangle(
                resumeX - 10,
                resumeButton.y + 40 - resumeLayout.height - 5,
                resumeLayout.width + 20,
                resumeLayout.height + 10
            );

            if (resumeHitbox.contains(mx, my)) {
                selectedIndex = 0;
            }

            // OPTIONS button
            GlyphLayout optionsLayout = new GlyphLayout(buttonFont, "OPTIONS");
            float optionsX = (OVERLAY_WIDTH - optionsLayout.width) / 2f;

            Rectangle optionsHitbox = new Rectangle(
                optionsX - 10,
                optionsButton.y + 40 - optionsLayout.height - 5,
                optionsLayout.width + 20,
                optionsLayout.height + 10
            );

            if (optionsHitbox.contains(mx, my)) {
                selectedIndex = 1;
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
                selectedIndex = 2;
            }
        } else {
            // YES button
            GlyphLayout yesLayout = new GlyphLayout(buttonFont, "YES");
            float yesX = yesButton.x + (BUTTON_WIDTH - yesLayout.width) / 2f;

            Rectangle yesHitbox = new Rectangle(
                yesX - 10,
                yesButton.y + 40 - yesLayout.height - 5,
                yesLayout.width + 20,
                yesLayout.height + 10
            );

            if (yesHitbox.contains(mx, my)) {
                selectedIndex = 0;
            }

            // NO button
            GlyphLayout noLayout = new GlyphLayout(buttonFont, "NO");
            float noX = noButton.x + (BUTTON_WIDTH - noLayout.width) / 2f;

            Rectangle noHitbox = new Rectangle(
                noX - 10,
                noButton.y + 40 - noLayout.height - 5,
                noLayout.width + 20,
                noLayout.height + 10
            );

            if (noHitbox.contains(mx, my)) {
                selectedIndex = 1;
            }
        }
        
        // Reset hover time if we changed button or stopped hovering
        if (selectedIndex != previousIndex) {
            hoverTime = 0f;
            lastHoveredIndex = selectedIndex;
        }
    }

    /**
     * Calculates the zoom scale factor for a button based on hover time.
     * Creates a smooth zoom transition effect when hovering over buttons.
     *
     * @param buttonIndex The index of the button to get the scale for
     * @return The scale factor (1.0 = normal size, MAX_ZOOM_SCALE = fully zoomed)
     */
    private float getZoomScale(int buttonIndex) {
        if (selectedIndex != buttonIndex) {
            return 1.0f;
        }
        
        // Calculate interpolation factor (0.0 to 1.0)
        float t = Math.min(hoverTime / ZOOM_TRANSITION_DURATION, 1.0f);
        
        // Smooth interpolation using ease-out cubic function
        t = 1f - (float)Math.pow(1f - t, 3);
        
        // Interpolate between 1.0 (normal) and MAX_ZOOM_SCALE (zoomed)
        return 1.0f + (MAX_ZOOM_SCALE - 1.0f) * t;
    }
    
    /**
     * Handles mouse click events on the overlay and determines which button was clicked.
     * Manages state transitions for confirmation dialogs and returns the corresponding action string.
     *
     * @param screenX The screen X coordinate of the click event.
     * @param screenY The screen Y coordinate of the click event.
     * @return "resume" if RESUME is clicked, "options" if OPTIONS is clicked, "confirm" if QUIT is clicked (shows confirmation), "quit" if YES is confirmed, "cancel" if NO is selected, null otherwise.
     */
    public String handleClick(int screenX, int screenY) {
        Vector2 mouse = viewport.unproject(new Vector2(screenX, screenY));
        float worldX = mouse.x;
        float worldY = mouse.y;

        if (!showConfirmation) {
            // Check RESUME button
            GlyphLayout resumeLayout = new GlyphLayout(buttonFont, "RESUME");
            float resumeX = (OVERLAY_WIDTH - resumeLayout.width) / 2f;

            Rectangle resumeHitbox = new Rectangle(
                resumeX - 10,
                resumeButton.y + 40 - resumeLayout.height - 5,
                resumeLayout.width + 20,
                resumeLayout.height + 10
            );

            if (resumeHitbox.contains(worldX, worldY)) {
                return "resume";
            }

            // Check OPTIONS button
            GlyphLayout optionsLayout = new GlyphLayout(buttonFont, "OPTIONS");
            float optionsX = (OVERLAY_WIDTH - optionsLayout.width) / 2f;

            Rectangle optionsHitbox = new Rectangle(
                optionsX - 10,
                optionsButton.y + 40 - optionsLayout.height - 5,
                optionsLayout.width + 20,
                optionsLayout.height + 10
            );

            if (optionsHitbox.contains(worldX, worldY)) {
                return "options";
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
                showConfirmation = true;
                return "confirm";
            }
        } else {
            // Check YES button
            GlyphLayout yesLayout = new GlyphLayout(buttonFont, "YES");
            float yesX = yesButton.x + (BUTTON_WIDTH - yesLayout.width) / 2f;

            Rectangle yesHitbox = new Rectangle(
                yesX - 10,
                yesButton.y + 40 - yesLayout.height - 5,
                yesLayout.width + 20,
                yesLayout.height + 10
            );

            if (yesHitbox.contains(worldX, worldY)) {
                showConfirmation = false; // Reset for next time
                return "quit";
            }

            // Check NO button
            GlyphLayout noLayout = new GlyphLayout(buttonFont, "NO");
            float noX = noButton.x + (BUTTON_WIDTH - noLayout.width) / 2f;

            Rectangle noHitbox = new Rectangle(
                noX - 10,
                noButton.y + 40 - noLayout.height - 5,
                noLayout.width + 20,
                noLayout.height + 10
            );

            if (noHitbox.contains(worldX, worldY)) {
                showConfirmation = false; // Back to pause menu
                return "cancel";
            }
        }

        return null;
    }
    
    /**
     * Resets the confirmation dialog state, returning to the main pause menu.
     * Used to exit the confirmation prompt and restore the main overlay.
     */
    public void resetConfirmation() {
        showConfirmation = false;
    }
    
    /**
     * Resizes the overlay viewport to match the new screen dimensions.
     * Ensures UI elements remain properly scaled and positioned.
     *
     * @param width The new screen width in pixels.
     * @param height The new screen height in pixels.
     */
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    
    /**
     * Releases all resources used by the overlay, including fonts, batch, and shape renderer.
     * Should be called when the overlay is no longer needed to prevent memory leaks.
     * Implements Disposable for proper resource management.
     */
    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (titleFont != null) titleFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
        if (confirmFont != null) confirmFont.dispose();
    }
}