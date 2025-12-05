package com.main.map;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;


/**
 * Represents the game map loaded from a Tiled TMX file.
 * <p>
 * Handles map loading, rendering, collision detection, and scaling. Manages collision rectangles for obstacles and provides access to map properties and rendering.
 * Used for all spatial and collision logic in the game.
 */
public class WarMap {
    /**
     * Height of the map in tiles.
     */
    private int mapHeight;
    /**
     * Width of the map in tiles.
     */
    private int mapWidth;
    /**
     * Width of a single tile in pixels.
     */
    private int tileWidth;
    /**
     * Height of a single tile in pixels.
     */
    private int tileHeight;
    /**
     * Scale factor for enlarging the map (default: 2.0).
     */
    private float scale = 2.0f;
    /**
     * List of collision rectangles representing obstacles on the map.
     */
    private List<Rectangle> collisionRects;
    /**
     * The TiledMap object loaded from the TMX file.
     */
    private TiledMap tiledMap;
    /**
     * Renderer for drawing the tiled map.
     */
    private OrthogonalTiledMapRenderer renderer;
    /**
     * Loader for TMX map files.
     */
    private TmxMapLoader mapLoader;

    /**
     * Constructs a new WarMap instance, loading the TMX map and initializing collision rectangles.
     * Automatically renders the map after loading.
     */
    public WarMap(){
        this.collisionRects = new ArrayList<>();
        loadTmxMap();
        render();
    }

    /**
     * Loads the TMX map file and initializes map properties, renderer, and collision rectangles.
     * Handles scaling and attempts to create the renderer, with fallback for headless mode.
     */
    private void loadTmxMap(){
        mapLoader = new TmxMapLoader();
        tiledMap = mapLoader.load("map/JAVAGAMEZ.tmx");

        float scale = 2.0f;
        // Try to create the renderer (may fail in headless test mode)
        try {
            renderer = new OrthogonalTiledMapRenderer(tiledMap, scale);
        } catch (Exception e) {
            // In headless test mode, renderer may not be created (missing shaders)
            System.err.println("Warning: Could not create renderer (headless mode?): " + e.getMessage());
            renderer = null;
        }

        this.mapHeight = tiledMap.getProperties().get("height", Integer.class);
        this.mapWidth = tiledMap.getProperties().get("width", Integer.class);
        this.tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);
        this.tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);

        // Load collision objects from the "COLLISION" layer
        loadCollisionObjects();
    }

    /**
     * Loads collision objects from the map's collision layer and applies scaling.
     * Supports multiple possible layer names for compatibility. Populates the collisionRects list.
     */
    private void loadCollisionObjects() {
        String[] possibleNames = {"collision", "colision", "Calque d'Objets 1", "COLLISION"};

        for (String layerName : possibleNames) {
            if (tiledMap.getLayers().get(layerName) != null) {

                for (MapObject object : tiledMap.getLayers().get(layerName).getObjects()) {
                    float x = object.getProperties().get("x", Float.class);
                    float y = object.getProperties().get("y", Float.class);
                    float width = object.getProperties().get("width", Float.class);
                    float height = object.getProperties().get("height", Float.class);

                    // Apply scale to rectangles
                    Rectangle scaledRect = new Rectangle(
                        x * scale,
                        y * scale,
                        width * scale,
                        height * scale
                    );

                    collisionRects.add(scaledRect);
                }
                return;
            }
        }
    }

    /**
     * Renders the tiled map using the renderer, if available.
     */
    public void render(){
        if (renderer != null){
            renderer.render();
        }
    }

    /**
     * Checks if a rectangle (entity hitbox) overlaps any collision rectangle (obstacle).
     * Used for entity-based collision detection (player, enemy, etc.).
     *
     * @param x      X coordinate of the rectangle
     * @param y      Y coordinate of the rectangle
     * @param width  Width of the rectangle
     * @param height Height of the rectangle
     * @return True if the rectangle overlaps any collision rectangle, false otherwise
     */
    public boolean isCollisionRect(float x, float y, float width, float height){
        Rectangle entityRect = new Rectangle(x, y, width, height);
        for (Rectangle rect : collisionRects) {
            if (entityRect.overlaps(rect)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the camera view for the map renderer.
     *
     * @param camera OrthographicCamera to set as the view
     */
    public void setView(com.badlogic.gdx.graphics.OrthographicCamera camera) {
        if (renderer != null) {
            renderer.setView(camera);
        }
    }

    /**
     * Returns the TiledMap object for direct access to map data.
     *
     * @return TiledMap instance
     */
    public TiledMap getMap(){
        return this.tiledMap;
    }

    /**
     * Returns the width of the map in pixels, accounting for scaling.
     *
     * @return Map width in pixels
     */
    public int getMapWidthInPixels() {
        return (int)(mapWidth * tileWidth * scale);
    }

    /**
     * Returns the height of the map in pixels, accounting for scaling.
     *
     * @return Map height in pixels
     */
    public int getMapHeightInPixels() {
        return (int)(mapHeight * tileHeight * scale);
    }

    /**
     * Returns the height of the map in tiles.
     *
     * @return Map height in tiles
     */
    public int getMapHeight() {
        return this.mapHeight;
    }

    /**
     * Returns the width of the map in tiles.
     *
     * @return Map width in tiles
     */
    public int getMapWidth() {
        return this.mapWidth;
    }

    /**
     * Returns the list of collision rectangles for obstacles on the map.
     *
     * @return List of collision rectangles
     */
    public List<Rectangle> getCollisionRects() {
        return collisionRects;
    }

    /**
     * Disposes of map resources, including the TiledMap and renderer.
     * Should be called when the map is no longer needed to free resources.
     */
    public void dispose(){
        if (tiledMap != null){
            tiledMap.dispose();
        }
        if (renderer != null){
            renderer.dispose();
        }
    }
}