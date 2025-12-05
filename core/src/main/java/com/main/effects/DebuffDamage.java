package com.main.effects;

/**
 * Effect that decreases the damage of a unit.
 */
public class DebuffDamage extends Effect{
    /** Value of the damage debuff. */
    private final int debuffDamage;

    /**
     * Creates a new DebuffDamage effect.
     * @param value Amount of damage debuff
     */
    public DebuffDamage(int value){
        super("debufDamage", Effect.Type.DEBUFF);
        debuffDamage = value;
    }

    /**
     * Gets the damage debuff value.
     * @return Damage debuff value
     */
    public int getDebuffDamage() {
        return debuffDamage;
    }
}
