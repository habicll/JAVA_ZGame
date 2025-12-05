package com.main.effects;

/**
 * Effect that increases the amount of gold earned by a unit.
 */
public class GoldMultiplier extends Effect{
    private final int multiplier;

    /**
     * Creates a new GoldMultiplier effect.
     * @param value Amount of gold multiplier
     */
    public GoldMultiplier(int value){
        super("goldMultiplier", Effect.Type.BUFF);
        multiplier = value;
    }

    /**
     * Gets the gold multiplier value.
     * @return Gold multiplier value
     */
    public int getMultiplier() {
        return multiplier;
    }
}
