package com.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Manages global audio settings for the game.
 * Handles music volume and sound effects on/off state.
 * Settings are persisted using LibGDX Preferences.
 */
public class AudioSettings {
    
    private static final String PREFS_NAME = "zombie-game-settings";
    private static final String MUSIC_VOLUME_KEY = "musicVolume";
    private static final String SOUND_ENABLED_KEY = "soundEnabled";
    
    private static Preferences prefs;
    private static float musicVolume = 0.5f;
    private static boolean soundEnabled = true;
    
    /**
     * Initializes the audio settings by loading saved preferences.
     */
    public static void initialize() {
        try {
            prefs = Gdx.app.getPreferences(PREFS_NAME);
            musicVolume = prefs.getFloat(MUSIC_VOLUME_KEY, 0.5f);
            soundEnabled = prefs.getBoolean(SOUND_ENABLED_KEY, true);
        } catch (Exception e) {
            System.err.println("Could not load preferences: " + e.getMessage());
            musicVolume = 0.5f;
            soundEnabled = true;
        }
    }
    
    /**
     * Gets the current music volume (0.0 to 1.0).
     * 
     * @return Music volume between 0.0 and 1.0
     */
    public static float getMusicVolume() {
        return musicVolume;
    }
    
    /**
     * Sets the music volume and saves it to preferences.
     * 
     * @param volume Volume level between 0.0 and 1.0
     */
    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0f, Math.min(1f, volume));
        savePreferences();
    }
    
    /**
     * Checks if sound effects are enabled.
     * 
     * @return True if sounds are enabled, false otherwise
     */
    public static boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    /**
     * Enables or disables sound effects and saves the setting.
     * 
     * @param enabled True to enable sounds, false to disable
     */
    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        savePreferences();
    }
    
    /**
     * Toggles sound effects on/off.
     * 
     * @return The new sound enabled state
     */
    public static boolean toggleSound() {
        soundEnabled = !soundEnabled;
        savePreferences();
        return soundEnabled;
    }
    
    /**
     * Saves current settings to preferences.
     */
    private static void savePreferences() {
        if (prefs != null) {
            try {
                prefs.putFloat(MUSIC_VOLUME_KEY, musicVolume);
                prefs.putBoolean(SOUND_ENABLED_KEY, soundEnabled);
                prefs.flush();
            } catch (Exception e) {
                System.err.println("Could not save preferences: " + e.getMessage());
            }
        }
    }
}
