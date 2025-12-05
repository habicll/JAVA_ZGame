package com.main.abilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.main.effects.BuffMoveSpeed;

public class DopingProductsTest {

    private DopingProducts dopingProducts;

    @Before
    public void setUp() {
        dopingProducts = new DopingProducts();
    }

    @Test
    public void testConstructor() {
        assertNotNull(dopingProducts);
        assertEquals("Steroids", dopingProducts.getName());
        assertEquals("Inject chemicals into your allies to buff their movement speed", dopingProducts.getDescription());
        assertEquals(30, dopingProducts.getCooldown());
        assertNotNull(dopingProducts.getEffect());
        assertTrue(dopingProducts.getEffect() instanceof BuffMoveSpeed);
    }

    @Test
    public void testUse() {
        assertTrue(dopingProducts.isReady());
        dopingProducts.use();
        assertEquals(30, dopingProducts.getCurrentCooldown());
    }

    @Test
    public void testUpdateCooldown() {
        dopingProducts.use();
        dopingProducts.updateCooldown();
        assertEquals(29, dopingProducts.getCurrentCooldown());
    }
}