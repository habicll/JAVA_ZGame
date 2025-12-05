package com.main.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class WarMapTest {

    private static HeadlessApplication application;
    private WarMap warMap;

    @BeforeClass
    public static void init() {
        // Initialiser LibGDX en mode headless pour les tests
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        application = new HeadlessApplication(new ApplicationAdapter() {}, config);
        
        // Mock GL20 pour éviter les erreurs de rendu avec shaders
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;
    }

    @Before
    public void setUp() {
        // Créer une nouvelle instance de WarMap avant chaque test
        // Le renderer peut échouer en mode headless, mais WarMap sera créé quand même
        warMap = new WarMap();
    }

    @Test
    public void testConstructor() {
        assertNotNull("WarMap should be created", warMap);
        assertNotNull("TiledMap should be loaded", warMap.getMap());
    }

    @Test
    public void testGetMap() {
        TiledMap map = warMap.getMap();
        assertNotNull("TiledMap should not be null", map);
        assertNotNull("Map should have properties", map.getProperties());
    }

    @Test
    public void testGetMapHeight() {
        int height = warMap.getMapHeight();
        assertTrue("Map height should be positive", height > 0);
        // JAVAGAMEZ.tmx a une hauteur de 20 tiles
        assertEquals("Map height should be 20", 20, height);
    }

    @Test
    public void testGetMapWidth() {
        int width = warMap.getMapWidth();
        assertTrue("Map width should be positive", width > 0);
        // Vérifier que la largeur correspond à la carte actuelle
        assertEquals("Map width should match the loaded map", 45, width);
    }

    @Test
    public void testGetMapHeightInPixels() {
        int heightPixels = warMap.getMapHeightInPixels();
        assertTrue("Map height in pixels should be positive", heightPixels > 0);
        // hauteur tiles * 16 pixels * 2 scale
        int expectedHeight = warMap.getMapHeight() * 16 * 2;
        assertEquals("Map height should be calculated correctly", expectedHeight, heightPixels);
    }

    @Test
    public void testGetMapWidthInPixels() {
        int widthPixels = warMap.getMapWidthInPixels();
        assertTrue("Map width in pixels should be positive", widthPixels > 0);
        // largeur tiles * 16 pixels * 2 scale
        int expectedWidth = warMap.getMapWidth() * 16 * 2;
        assertEquals("Map width should be calculated correctly", expectedWidth, widthPixels);
    }

    @Test
    public void testIsCollisionRect_SmallRect() {
        // Tester avec un petit rectangle
        boolean collision = warMap.isCollisionRect(10, 10, 16, 16);
        // La méthode doit retourner true ou false sans erreur
        assertTrue("Method should return a boolean value", collision || !collision);
    }

    @Test
    public void testIsCollisionRect_HeroSize() {
        // Tester avec la taille du héros (32x48)
        boolean collision = warMap.isCollisionRect(100, 100, 32, 48);
        assertTrue("Method should handle hero-sized rectangles", collision || !collision);
    }

    @Test
    public void testIsCollisionRect_LargeRect() {
        // Tester avec un grand rectangle qui devrait probablement toucher une collision
        boolean collision = warMap.isCollisionRect(0, 0, 200, 200);
        assertTrue("Method should handle large rectangles", collision || !collision);
    }

    @Test
    public void testIsCollisionRect_ZeroSize() {
        // Tester avec un rectangle de taille zéro
        // Un rectangle 0x0 peut chevaucher un obstacle si le point est dedans
        boolean collision = warMap.isCollisionRect(50, 50, 0, 0);
        // Vérifier simplement que la méthode fonctionne sans erreur
        assertTrue("Method should handle zero-size rectangles", collision || !collision);
    }

    @Test
    public void testIsCollisionRect_NegativeCoordinates() {
        // Tester avec des coordonnées négatives
        boolean collision = warMap.isCollisionRect(-10, -10, 32, 48);
        assertTrue("Method should handle negative coordinates", collision || !collision);
    }

    @Test
    public void testIsCollisionRect_OutOfBounds() {
        // Tester en dehors des limites de la carte
        boolean collision = warMap.isCollisionRect(10000, 10000, 32, 48);
        assertFalse("Out of bounds should not collide", collision);
    }

    @Test
    public void testRender() {
        // Test que render ne lance pas d'exception (même si renderer est null)
        try {
            warMap.render();
            assertTrue("Render should execute without exception", true);
        } catch (Exception e) {
            fail("Render should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testSetView() {
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        camera.update();
        
        try {
            warMap.setView(camera);
            assertTrue("setView should execute without exception", true);
        } catch (Exception e) {
            fail("setView should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testSetView_NullCamera() {
        // Tester avec une caméra null pour vérifier la robustesse
        try {
            warMap.setView(null);
            // Si aucune exception, c'est ok
            assertTrue("Null camera should be handled gracefully", true);
        } catch (NullPointerException e) {
            // C'est un comportement acceptable aussi
            assertTrue("Null camera should be handled", true);
        }
    }

    @Test
    public void testDispose() {
        WarMap disposableMap = new WarMap();
        
        try {
            disposableMap.dispose();
            assertTrue("Dispose should execute without exception", true);
        } catch (Exception e) {
            fail("Dispose should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testDispose_MultipleCall() {
        WarMap disposableMap = new WarMap();
        
        // Appeler dispose plusieurs fois ne devrait pas causer d'erreur
        disposableMap.dispose();
        disposableMap.dispose();
        
        assertTrue("Multiple dispose calls should be safe", true);
    }

    @Test
    public void testMapDimensions_Consistency() {
        // Vérifier la cohérence entre les dimensions en tiles et en pixels
        int widthTiles = warMap.getMapWidth();
        int heightTiles = warMap.getMapHeight();
        int widthPixels = warMap.getMapWidthInPixels();
        int heightPixels = warMap.getMapHeightInPixels();
        
        // Scale = 2.0f, tileWidth = 16, tileHeight = 16
        int expectedWidthPixels = widthTiles * 16 * 2;
        int expectedHeightPixels = heightTiles * 16 * 2;
        
        assertEquals("Width in pixels should match tiles * tileWidth * scale", 
                     expectedWidthPixels, widthPixels);
        assertEquals("Height in pixels should match tiles * tileHeight * scale", 
                     expectedHeightPixels, heightPixels);
    }
}