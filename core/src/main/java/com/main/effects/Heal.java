package com.main.effects;

/**
 * Effect that restores health to a unit.
 */
public class Heal extends Effect{
    private final int regen;

    /**
     * Creates a new Heal effect.
     * @param value Amount of health restored
     */
    public Heal(int value){
        super("regen", Effect.Type.BUFF);
        regen = value;
    }

    /**
     * Gets the health regeneration value.
     * @return Health regeneration value
     */
    public int getRegen() {
        return regen;
    }
}
