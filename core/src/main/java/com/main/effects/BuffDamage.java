package com.main.effects;

/**
 * Effect that increases the damage of a unit.
 */
public class BuffDamage extends Effect {
    /** Value of the damage boost. */
    private final int boostDamageValue;

    /**
     * Creates a new BuffDamage effect.
     * @param value Amount of damage boost
     */
    public BuffDamage(int value){
        super("buffDamage", Effect.Type.BUFF);
        boostDamageValue = value;
    }

    /**
     * Gets the damage boost value.
     * @return Damage boost value
     */
    public int getBoostDamageValue(){
        return boostDamageValue;
    }
}
