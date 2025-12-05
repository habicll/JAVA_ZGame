package com.main.effects;

/**
 * Effect that deals damage over time to a unit.
 */
public class Poison extends Effect{
    private final int damagePerSecond;

    public Poison(int value){
        super("poison", Effect.Type.DOT);
        damagePerSecond = value;
    }

    /**
     * Gets the damage dealt per second.
     * @return Damage per second
     */
    public int getDamagePerSecond() {
        return damagePerSecond;
    }
}
