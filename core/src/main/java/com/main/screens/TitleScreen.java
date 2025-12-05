
/**
 * The TitleScreen class represents the main menu screen of the game.
 * It displays the background, logo, and menu buttons (PLAY, QUIT),
 * handles user input for menu navigation, and manages rendering and resource disposal.
 * This screen is the entry point for the player before starting gameplay.
 */
package com.main.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.Main;


/**
 * Main menu screen for the game, showing background, logo, and menu buttons.
 * Handles input and rendering for the title/menu interface.
 */
public class TitleScreen implements Screen {


    protected Main game;
    protected SpriteBatch batch;
    protected OrthographicCamera camera;
    protected Viewport viewport;
    protected BitmapFont font;
    protected Texture background;
    protected Texture titleLogo;
    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 480f;
    private String[] menuItems = {"PLAY", "OPTIONS", "QUIT"};
    protected int selectedIndex = -1;
    private int lastHoveredIndex = -1;
    private float hoverTime = 0f;
    private static final float ZOOM_TRANSITION_DURATION = 0.15f;
    private static final float MAX_ZOOM_SCALE = 1.2f;

    /**
     * Constructs the TitleScreen and initializes all resources (background, logo, font, camera, viewport).
     * Handles resource loading and fallback for headless mode.
     *
     * @param game Reference to the main game instance for screen transitions
     */
    public TitleScreen(Main game) {
        this.game = game;
        
        try {
            batch = new SpriteBatch();
        } catch (Exception e) {
            System.err.println("Warning: Could not create SpriteBatch (headless mode?)");
            batch = null;
        }

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        // Image de fond
        try {
            background = new Texture(Gdx.files.internal("ui/titlescreen.png"));
        } catch (Exception e) {
            System.err.println("Warning: Could not load background texture (headless mode?)");
            background = null;
        }

        // Logo principal
        try {
            titleLogo = new Texture(Gdx.files.internal("ui/titlelogo.png"));
        } catch (Exception e) {
            System.err.println("Warning: Could not load title logo texture (headless mode?)");
            titleLogo = null;
        }

        // === Police rétro pixel pour le menu
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 32;
            parameter.borderWidth = 2.5f;
            parameter.borderColor = new Color(0f, 0f, 0f, 0.85f);
            parameter.shadowOffsetX = 2;
            parameter.shadowOffsetY = -2;
            parameter.shadowColor = new Color(0f, 0f, 0f, 0.7f);
            parameter.magFilter = Texture.TextureFilter.Nearest;
            parameter.minFilter = Texture.TextureFilter.Nearest;
            font = generator.generateFont(parameter);
            generator.dispose();
        } catch (Exception e) {
            System.err.println("Warning: Could not load font (headless mode?)");
            font = new BitmapFont(); // Fallback to default font
        }
    }

    /**
     * Renders the title screen, including background, logo, and menu buttons.
     * Handles menu hover and input logic each frame.
     *
     * @param delta Time elapsed since last frame (in seconds)
     */
    @Override
    public void render(float delta) {
        if (batch == null) return; // Skip rendering in headless mode
        
        Gdx.gl.glClearColor(0f, 0f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        updateHover();
        
        // Update zoom animation time
        if (selectedIndex != -1 && selectedIndex == lastHoveredIndex) {
            hoverTime = Math.min(hoverTime + delta, ZOOM_TRANSITION_DURATION);
        }
        
        handleInput();

        batch.begin();

        // Fond
        if (background != null) {
            batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }

        // LOGO PRINCIPAL
        if (titleLogo != null) {
            float logoWidth = 500f;  // Agrandi encore plus
            float logoHeight = 500f; // Carré pour respecter les proportions originales
            float logoX = (WORLD_WIDTH - logoWidth) / 2f;
            float logoY = WORLD_HEIGHT - logoHeight + 30f; // Remonté plus haut
            batch.draw(titleLogo, logoX, logoY, logoWidth, logoHeight);
        }

        // ==== MENU - BOUTONS EN LIGNE ====
        if (font != null) {
            // Position Y avec espace entre le logo et les boutons
            float buttonY = 60f; // Position descendue
            
            // Calculer les layouts pour centrer les trois boutons (sans scale)
            font.getData().setScale(0.8f);
            GlyphLayout playLayout = new GlyphLayout(font, "PLAY");
            GlyphLayout optionsLayout = new GlyphLayout(font, "OPTIONS");
            GlyphLayout quitLayout = new GlyphLayout(font, "QUIT");
            font.getData().setScale(1f);
            
            // Espacement entre les boutons
            float buttonSpacing = 80f;
            
            // Largeur totale occupée par les trois boutons
            float totalWidth = playLayout.width + buttonSpacing + optionsLayout.width + buttonSpacing + quitLayout.width;
            
            // Position de départ pour centrer l'ensemble
            float startX = (WORLD_WIDTH - totalWidth) / 2f;
            
            // Dessiner PLAY
            float playX = startX;
            float playScale = 1.0f;
            font.getData().setScale(0.8f * playScale);
            font.setColor(Color.WHITE);
            font.draw(batch, "PLAY", playX, buttonY);
            font.getData().setScale(1f);
            
            // Dessiner OPTIONS
            float optionsX = startX + playLayout.width + buttonSpacing;
            float optionsScale = 1.0f;
            font.getData().setScale(0.8f * optionsScale);
            font.setColor(Color.WHITE);
            font.draw(batch, "OPTIONS", optionsX, buttonY);
            font.getData().setScale(1f);
            
            // Dessiner QUIT
            float quitX = startX + playLayout.width + buttonSpacing + optionsLayout.width + buttonSpacing;
            float quitScale = 1.0f;
            font.getData().setScale(0.8f * quitScale);
            font.setColor(Color.WHITE);
            font.draw(batch, "QUIT", quitX, buttonY);
            font.getData().setScale(1f);
        }

        batch.end();
    }

    /**
     * Draws menu text with a soft shadow for visual clarity.
     *
     * @param text The text to render
     * @param x X position
     * @param y Y position
     * @param color Main color of the text
     */
    private void drawTextWithShadow(String text, float x, float y, Color color) {
        if (font == null) return;
        
        GlyphLayout layout = new GlyphLayout(font, text);

        // Ombre
        font.setColor(0f, 0f, 0f, 0.6f);
        font.draw(batch, layout, x + 3, y - 3);

        // Couleur principale
        font.setColor(color);
        font.draw(batch, layout, x, y);
    }

    /**
     * Draws menu text with inverted colors and a thick yellow shadow for hover effect.
     *
     * @param text The text to render
     * @param x X position
     * @param y Y position
     */
    private void drawTextWithShadowInverted(String text, float x, float y) {
        if (font == null) return;
        
        GlyphLayout layout = new GlyphLayout(font, text);

        // Ombre jaune/dorée (plus épaisse pour effet de fond)
        font.setColor(Color.YELLOW);
        for (int offsetX = -2; offsetX <= 2; offsetX++) {
            for (int offsetY = -2; offsetY <= 2; offsetY++) {
                font.draw(batch, layout, x + offsetX, y + offsetY);
            }
        }

        // Texte noir par-dessus
        font.setColor(Color.BLACK);
        font.draw(batch, layout, x, y);
    }

    /**
     * Updates the selectedIndex based on mouse hover over menu buttons.
     * Calculates button rectangles and checks mouse position for hover detection.
     */
    private void updateHover() {
        if (font == null) return;
        
        font.getData().setScale(0.8f);
        
        Vector2 mouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        float mx = mouse.x;
        float my = mouse.y;

        int previousIndex = selectedIndex;
        selectedIndex = -1;
        
        // Position Y des boutons (même que dans render)
        float buttonY = 60f;
        
        // Calculer les layouts
        GlyphLayout playLayout = new GlyphLayout(font, "PLAY");
        GlyphLayout optionsLayout = new GlyphLayout(font, "OPTIONS");
        GlyphLayout quitLayout = new GlyphLayout(font, "QUIT");
        
        float buttonSpacing = 80f;
        float totalWidth = playLayout.width + buttonSpacing + optionsLayout.width + buttonSpacing + quitLayout.width;
        float startX = (WORLD_WIDTH - totalWidth) / 2f;
        
        // Zone de collision PLAY
        float playX = startX;
        Rectangle playRect = new Rectangle(
            playX - 10,
            buttonY - playLayout.height - 5,
            playLayout.width + 20,
            playLayout.height + 10
        );
        
        if (playRect.contains(mx, my)) {
            selectedIndex = 0;
            font.getData().setScale(1f);
            return;
        }
        
        // Zone de collision OPTIONS
        float optionsX = startX + playLayout.width + buttonSpacing;
        Rectangle optionsRect = new Rectangle(
            optionsX - 10,
            buttonY - optionsLayout.height - 5,
            optionsLayout.width + 20,
            optionsLayout.height + 10
        );
        
        if (optionsRect.contains(mx, my)) {
            selectedIndex = 1;
            font.getData().setScale(1f);
            return;
        }
        
        // Zone de collision QUIT
        float quitX = startX + playLayout.width + buttonSpacing + optionsLayout.width + buttonSpacing;
        Rectangle quitRect = new Rectangle(
            quitX - 10,
            buttonY - quitLayout.height - 5,
            quitLayout.width + 20,
            quitLayout.height + 10
        );
        
        if (quitRect.contains(mx, my)) {
            selectedIndex = 2;
        }
        
        // Reset hover time if we changed button or stopped hovering
        if (selectedIndex != previousIndex) {
            hoverTime = 0f;
            lastHoveredIndex = selectedIndex;
        }
        
        font.getData().setScale(1f);
    }

    /**
     * Calculates the zoom scale factor for a button based on hover time.
     * Creates a smooth zoom transition effect when hovering over menu items.
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
     * Handles user input for menu selection (mouse click).
     * Triggers screen transitions or application exit based on selected menu item.
     */
    protected void handleInput() {
        if (Gdx.input.justTouched() && selectedIndex != -1) {
            switch (menuItems[selectedIndex]) {
                case "PLAY":
                    game.showGameScreen();
                    break;
                case "OPTIONS":
                    game.showOptionsScreen(false);
                    break;
                case "QUIT":
                    Gdx.app.postRunnable(() -> {
                        Gdx.app.exit();
                        if (Gdx.app.getType() == Application.ApplicationType.Desktop)
                            System.exit(0);
                    });
                    break;
            }
        }
    }
    
    /**
     * Helper method for testing handleInput with a specific menu selection.
     * Temporarily sets selectedIndex, processes input, then restores previous selection.
     *
     * @param selection Index of the menu item to simulate selection
     */
    protected void handleInputWithSelection(int selection) {
        int oldSelection = selectedIndex;
        selectedIndex = selection;
        handleInput();
        selectedIndex = oldSelection;
    }

    /**
     * Handles screen resizing and updates the viewport.
     *
     * @param width New width of the screen
     * @param height New height of the screen
     */
    @Override public void resize(int width, int height) { viewport.update(width, height, true); }

    /**
     * Called when the screen is shown. No additional logic required.
     */
    @Override public void show() {}

    /**
     * Called when the game is paused. No additional logic required.
     */
    @Override public void pause() {}

    /**
     * Called when the game is resumed. No additional logic required.
     */
    @Override public void resume() {}

    /**
     * Called when the screen is hidden. No additional logic required.
     */
    @Override public void hide() {}

    /**
     * Disposes all resources used by the title screen (batch, font, textures).
     * Should be called when the screen is no longer needed to free memory.
     */
    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (background != null) background.dispose();
        if (titleLogo != null) titleLogo.dispose();
    }
}