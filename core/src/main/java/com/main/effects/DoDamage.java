package com.main.effects;

/**
 * Effect that deals damage to a unit within a certain range.
 */
public class DoDamage extends Effect{
    /** Amount of damage dealt. */
    private final int damage;
    /** Range of the effect. */
    private final int range;

    /**
     * Creates a new DoDamage effect.
     * @param damage Amount of damage
     * @param range Range of the effect
     */
    public DoDamage(int damage, int range){
        super("doDamage", Effect.Type.DAMAGE);
        this.damage = damage;
        this.range = range;
    }

    /**
     * Gets the amount of damage dealt.
     * @return Amount of damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Gets the range of the effect.
     * @return Range of the effect
     */
    public int getRange() {
        return range;
    }   
}
