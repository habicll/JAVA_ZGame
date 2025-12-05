package com.main.utils;

/**
 * Represents a 2D position in the game world using integer coordinates.
 * Used for storing and manipulating entity or object locations.
 */
public class Position {

    /**
     * The X coordinate of the position.
     * Used for horizontal placement in the game world.
     */
    private int posX;

    /**
     * The Y coordinate of the position.
     * Used for vertical placement in the game world.
     */
    private int posY;

    /**
     * Constructs a new Position with the specified coordinates.
     *
     * @param posX The X coordinate.
     * @param posY The Y coordinate.
     */
    public Position(int posX, int posY){
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Returns the X coordinate of this position.
     *
     * @return The X coordinate.
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Returns the Y coordinate of this position.
     *
     * @return The Y coordinate.
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Sets the X coordinate of this position.
     *
     * @param posX The new X coordinate.
     */
    public void setPosX(int posX){
        this.posX = posX;
    }

    /**
     * Sets the Y coordinate of this position.
     *
     * @param posY The new Y coordinate.
     */
    public void setPosY(int posY){
        this.posY = posY;
    }
}