package com.main;

import com.badlogic.gdx.Game;
import com.main.screens.OptionsScreen;
import com.main.screens.TitleScreen;

/**
 * Main entry point for the game application.
 * <p>
 * Manages screen transitions and lifecycle for the game, including the main game screen and title screen.
 * Responsible for initializing, switching, and disposing screens.
 */
public class Main extends Game {
        /**
         * Global brightness value for game rendering (0.5 = dark, 2.0 = bright, 1.0 = normal)
         */
        private float brightness = 1.0f;

        public float getBrightness() { return brightness; }
        public void setBrightness(float value) { brightness = Math.max(0.5f, Math.min(2.0f, value)); }
    /**
     * The main game screen, responsible for gameplay.
     */
    private GameScreen gameScreen;
    /**
     * The title screen, shown at startup and when returning to menu.
     */
    private TitleScreen titleScreen;
    /**
     * The options screen for audio settings.
     */
    private OptionsScreen optionsScreen;

    /**
     * Initializes the game, creating and setting the title and game screens.
     * Called once at application startup.
     */
    @Override
    public void create() {
        gameScreen = new GameScreen(this);
        titleScreen = new TitleScreen(this);
        setScreen(titleScreen);
    }

    /**
     * Shows the title screen, switching from any other screen.
     */
    public void showTitleScreen() {
        setScreen(titleScreen);
    }

    /**
     * Shows the main game screen, resetting the game state before display.
     */
    public void showGameScreen() {
        gameScreen.reset();
        setScreen(gameScreen);
    }

    /**
     * Shows the options screen for audio settings.
     * @param fromPause True if accessed from pause menu, false if from title screen
     */
    public void showOptionsScreen(boolean fromPause) {
        optionsScreen = new OptionsScreen(this, fromPause);
        setScreen(optionsScreen);
    }

    /**
     * Resumes the game from pause state, returning to the game screen.
     */
    public void resumeGame() {
        setScreen(gameScreen);
        gameScreen.resumeFromPause();
    }

    /**
     * Returns to the pause menu from options screen.
     */
    public void returnToPauseMenu() {
        setScreen(gameScreen);
        // Game will remain paused, showing pause overlay
    }

    /**
     * Updates the music volume in the game screen when changed in options.
     */
    public void updateGameMusicVolume() {
        if (gameScreen != null) {
            gameScreen.updateMusicVolume();
        }
    }

    /**
     * Disposes of all resources used by the game, including screens.
     * Called when the application is closing.
     */
    @Override
    public void dispose() {
        super.dispose();
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        if (optionsScreen != null) {
            optionsScreen.dispose();
        }
    }
}
