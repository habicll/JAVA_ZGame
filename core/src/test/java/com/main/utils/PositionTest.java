package com.main.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class PositionTest {

    private Position position;

    @Before
    public void setUp() {
        position = new Position(10, 20);
    }

    @Test
    public void testConstructor() {
        assertNotNull(position);
        assertEquals(10, position.getPosX());
        assertEquals(20, position.getPosY());
    }

    @Test
    public void testConstructorWithZero() {
        Position pos = new Position(0, 0);
        assertEquals(0, pos.getPosX());
        assertEquals(0, pos.getPosY());
    }

    @Test
    public void testConstructorWithNegative() {
        Position pos = new Position(-10, -20);
        assertEquals(-10, pos.getPosX());
        assertEquals(-20, pos.getPosY());
    }

    @Test
    public void testGetPosX() {
        assertEquals(10, position.getPosX());
    }

    @Test
    public void testGetPosY() {
        assertEquals(20, position.getPosY());
    }

    @Test
    public void testSetPosX() {
        position.setPosX(30);
        assertEquals(30, position.getPosX());
    }

    @Test
    public void testSetPosY() {
        position.setPosY(40);
        assertEquals(40, position.getPosY());
    }

    @Test
    public void testSetPosXNegative() {
        position.setPosX(-5);
        assertEquals(-5, position.getPosX());
    }

    @Test
    public void testSetPosYNegative() {
        position.setPosY(-15);
        assertEquals(-15, position.getPosY());
    }

    @Test
    public void testSetPosXZero() {
        position.setPosX(0);
        assertEquals(0, position.getPosX());
    }

    @Test
    public void testSetPosYZero() {
        position.setPosY(0);
        assertEquals(0, position.getPosY());
    }

    @Test
    public void testMultipleChanges() {
        position.setPosX(100);
        position.setPosY(200);
        assertEquals(100, position.getPosX());
        assertEquals(200, position.getPosY());
        
        position.setPosX(50);
        position.setPosY(75);
        assertEquals(50, position.getPosX());
        assertEquals(75, position.getPosY());
    }
}