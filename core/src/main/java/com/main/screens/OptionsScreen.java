package com.main.screens;

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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.Main;
import com.utils.AudioSettings;

/**
 * Options screen for configuring game audio settings.
 * Provides controls for music volume and sound effects toggle.
 */
public class OptionsScreen implements Screen {
    // Espacement vertical pour chaque section
    private float volumeLabelY;
    private float brightnessLabelY;
    private float soundLabelY;

    private Main game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;
    private BitmapFont titleFont;
    private Texture background;

    private static final float WORLD_WIDTH = 800f;
    private static final float WORLD_HEIGHT = 480f;

    // Music volume slider
    private Rectangle volumeSlider;
    private Rectangle volumeHandle;
    private boolean isDraggingVolume = false;
    private float musicVolume;

    // Brightness slider
    private Rectangle brightnessSlider;
    private Rectangle brightnessHandle;
    private boolean isDraggingBrightness = false;
    private float brightness = 1.0f; // Default brightness (1.0 = normal)

    // Sound effects toggle button
    private Rectangle soundButton;
    private boolean soundEnabled;

    // Back button
    private Rectangle backButton;
    private int selectedIndex = -1; // 0 = back button

    // Hover animation variables
    private int lastHoveredIndex = -1;
    private float hoverTime = 0f;
    private static final float ZOOM_TRANSITION_DURATION = 0.15f;
    private static final float MAX_ZOOM_SCALE = 1.2f;

    private boolean fromPause;

    /**
     * Constructs the OptionsScreen and initializes all UI components.
     *
     * @param game Reference to the main game instance
     * @param fromPause True if accessed from pause menu, false if from title screen
     */
    public OptionsScreen(Main game, boolean fromPause) {
        this.game = game;
        this.fromPause = fromPause;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        // Load background
        try {
            background = new Texture(Gdx.files.internal("ui/titlescreen.png"));
        } catch (Exception e) {
            System.err.println("Warning: Could not load background texture");
            background = null;
        }

        // Load fonts
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P.ttf"));
            
            // Title font
            FreeTypeFontGenerator.FreeTypeFontParameter titleParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
            titleParam.size = 48;
            titleParam.borderWidth = 3f;
            titleParam.borderColor = Color.BLACK;
            titleParam.shadowOffsetX = 3;
            titleParam.shadowOffsetY = 3;
            titleParam.shadowColor = new Color(0, 0, 0, 0.5f);
            titleFont = generator.generateFont(titleParam);

            // Regular font
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
            System.err.println("Warning: Could not load font");
            font = new BitmapFont();
            titleFont = new BitmapFont();
        }

        // Initialize audio settings
        musicVolume = AudioSettings.getMusicVolume();
        soundEnabled = AudioSettings.isSoundEnabled();

        // Initialize slider (centered horizontally)
        float sliderWidth = 400f;
        float sliderHeight = 10f;
        float sliderX = (WORLD_WIDTH - sliderWidth) / 2f;

        // Espacement entre chaque section
        float sectionSpacing = 40f;

        // Volume
        volumeLabelY = 340f;
        float volumeSliderY = volumeLabelY - 50f; // plus d'espace
        volumeSlider = new Rectangle(sliderX, volumeSliderY, sliderWidth, sliderHeight);
        float handleX = sliderX + (sliderWidth * musicVolume) - 10f;
        volumeHandle = new Rectangle(handleX, volumeSliderY - 10f, 20f, 30f);

        // Luminosité
        brightnessLabelY = volumeSliderY - sectionSpacing;
        float brightnessSliderY = brightnessLabelY - 50f; // plus d'espace
        brightnessSlider = new Rectangle(sliderX, brightnessSliderY, sliderWidth, sliderHeight);
        float brightnessHandleX = sliderX + (sliderWidth * ((brightness - 0.5f) / 1.5f)) - 10f;
        brightnessHandle = new Rectangle(brightnessHandleX, brightnessSliderY - 10f, 20f, 30f);

        // Effets sonores
        soundLabelY = brightnessSliderY - sectionSpacing;
        // Place le bouton sur la même ligne que le label
        float soundButtonY = soundLabelY - 20f;
        float soundButtonX = (WORLD_WIDTH / 2f) + 120f;
        soundButton = new Rectangle(soundButtonX, soundButtonY, 100f, 40f);

        // Bouton retour
        float backButtonY = 20f; // bien en bas
        backButton = new Rectangle(WORLD_WIDTH / 2f - 100f, backButtonY, 200f, 50f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        updateHover();
        handleInput();

        // Draw semi-transparent background overlay if from pause
        if (fromPause) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.7f);
            shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            shapeRenderer.end();
            
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        batch.begin();

        // Draw background
        if (background != null && !fromPause) {
            batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }

        // Draw title
        titleFont.setColor(Color.WHITE);
        GlyphLayout titleLayout = new GlyphLayout(titleFont, "OPTIONS");
        float titleX = (WORLD_WIDTH - titleLayout.width) / 2f;
        titleFont.draw(batch, titleLayout, titleX, WORLD_HEIGHT - 50f);

        // Draw music volume label
        font.getData().setScale(0.6f);
        font.setColor(Color.WHITE);
        GlyphLayout volumeLabel = new GlyphLayout(font, "MUSIC VOLUME");
        float labelX = (WORLD_WIDTH - volumeLabel.width) / 2f;
        font.draw(batch, volumeLabel, labelX, volumeLabelY);

        // Draw brightness label
        GlyphLayout brightnessLabel = new GlyphLayout(font, "BRIGHTNESS");
        float brightnessLabelX = (WORLD_WIDTH - brightnessLabel.width) / 2f;
        font.draw(batch, brightnessLabel, brightnessLabelX, brightnessLabelY);

        // Draw sound effects label
        GlyphLayout soundLabel = new GlyphLayout(font, "SOUND EFFECTS");
        float soundLabelX = (WORLD_WIDTH / 2f) - soundLabel.width - 20f;
        font.draw(batch, soundLabel, soundLabelX, soundLabelY);

        // Draw back button with zoom effect
        float backScale = getZoomScale(0);
        font.getData().setScale(0.8f * backScale);
        font.setColor(Color.WHITE);
        GlyphLayout backScaledLayout = new GlyphLayout(font, "BACK");
        float backX = (WORLD_WIDTH - backScaledLayout.width) / 2f;
        font.draw(batch, backScaledLayout, backX, backButton.y + 35f);
        font.getData().setScale(1f);

        batch.end();

        // Draw slider
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Slider background
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
        shapeRenderer.rect(volumeSlider.x, volumeSlider.y, volumeSlider.width, volumeSlider.height);
        
        // Slider handle
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(volumeHandle.x, volumeHandle.y, volumeHandle.width, volumeHandle.height);

        // Brightness slider background
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
        shapeRenderer.rect(brightnessSlider.x, brightnessSlider.y, brightnessSlider.width, brightnessSlider.height);

        // Brightness slider handle
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(brightnessHandle.x, brightnessHandle.y, brightnessHandle.width, brightnessHandle.height);
        
        // Sound toggle button background
        if (soundEnabled) {
            shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 0.8f); // Green when enabled
        } else {
            shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 0.8f); // Red when disabled
        }
        shapeRenderer.rect(soundButton.x, soundButton.y, soundButton.width, soundButton.height);
        
        shapeRenderer.end();
        
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draw sound button text
        batch.begin();
        font.getData().setScale(0.6f);
        font.setColor(Color.WHITE);
        GlyphLayout soundButtonText = new GlyphLayout(font, soundEnabled ? "ON" : "OFF");
        float soundTextX = soundButton.x + (soundButton.width - soundButtonText.width) / 2f;
        float soundTextY = soundButton.y + soundButton.height / 2f + soundButtonText.height / 2f;
        font.draw(batch, soundButtonText, soundTextX, soundTextY);
        batch.end();
    }

    /**
     * Updates hover effect for the back button.
     */
    private void updateHover() {
        Vector2 mouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        float mx = mouse.x;
        float my = mouse.y;

        int previousIndex = selectedIndex;
        selectedIndex = -1;

        font.getData().setScale(0.8f);
        GlyphLayout backLayout = new GlyphLayout(font, "BACK");
        float backX = (WORLD_WIDTH - backLayout.width) / 2f;

        Rectangle backHitbox = new Rectangle(
            backX - 10,
            backButton.y + 35f - backLayout.height - 5,
            backLayout.width + 20,
            backLayout.height + 10
        );
        
        if (backHitbox.contains(mx, my)) {
            selectedIndex = 0;
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
     * Handles user input for slider, sound toggle, and back button.
     */
    private void handleInput() {
        Vector2 mouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        float mx = mouse.x;
        float my = mouse.y;

        // Handle volume slider dragging
        if (Gdx.input.isTouched()) {
            if (!isDraggingVolume && volumeHandle.contains(mx, my)) {
                isDraggingVolume = true;
            }

            if (isDraggingVolume) {
                updateVolumeSlider(mx);
            }

            // Handle brightness slider dragging
            if (!isDraggingBrightness && brightnessHandle.contains(mx, my)) {
                isDraggingBrightness = true;
            }
            if (isDraggingBrightness) {
                updateBrightnessSlider(mx);
            }
        } else {
            isDraggingVolume = false;
            isDraggingBrightness = false;
        }

        // Handle clicks
        if (Gdx.input.justTouched()) {
            // Sound toggle button
            if (soundButton.contains(mx, my)) {
                soundEnabled = !soundEnabled;
                AudioSettings.toggleSound();
            }

            // Back button
            if (selectedIndex == 0) {
                if (fromPause) {
                    game.returnToPauseMenu();
                } else {
                    game.showTitleScreen();
                }
            }
        }
    }
    /**
     * Updates the brightness slider position and applies the new brightness value.
     * @param mouseX The current mouse X position
     */
    private void updateBrightnessSlider(float mouseX) {
        float newHandleX = Math.max(brightnessSlider.x - 10f, Math.min(mouseX - 10f, brightnessSlider.x + brightnessSlider.width - 10f));
        brightnessHandle.x = newHandleX;

        // brightness range: 0.5 (min) to 2.0 (max)
        float percent = (brightnessHandle.x - brightnessSlider.x + 10f) / brightnessSlider.width;
        brightness = 0.5f + percent * 1.5f;
        brightness = Math.max(0.5f, Math.min(2.0f, brightness));
        // Synchronise la luminosité globale du jeu
        game.setBrightness(brightness);
    }

    /**
     * Updates the volume slider position and applies the new volume.
     *
     * @param mouseX The current mouse X position
     */
    private void updateVolumeSlider(float mouseX) {
        float newHandleX = Math.max(volumeSlider.x - 10f, Math.min(mouseX - 10f, volumeSlider.x + volumeSlider.width - 10f));
        volumeHandle.x = newHandleX;

        musicVolume = (volumeHandle.x - volumeSlider.x + 10f) / volumeSlider.width;
        musicVolume = Math.max(0f, Math.min(1f, musicVolume));

        AudioSettings.setMusicVolume(musicVolume);
        game.updateGameMusicVolume();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (titleFont != null) titleFont.dispose();
        if (background != null) background.dispose();
    }
}
