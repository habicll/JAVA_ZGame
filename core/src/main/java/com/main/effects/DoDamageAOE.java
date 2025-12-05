package com.main.effects;

/**
 * Effect that deals area-of-effect damage to units.
 */
public class DoDamageAOE extends Effect{
    /** Amount of damage dealt. */
    private final int damage;
    /** Area of effect radius. */
    private final int aoe;

    /**
     * Creates a new DoDamageAOE effect.
     * @param damage Amount of damage
     * @param aoe Area of effect radius
     */
    public DoDamageAOE(int damage, int aoe){
        super("doDamageAOE", Effect.Type.AOE);
        this.damage = damage;
        this.aoe = aoe;
    }

    /**
     * Gets the amount of damage dealt.
     * @return Amount of damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Gets the area of effect radius.
     * @return Area of effect radius
     */
    public int getAoe() {
        return aoe;
    }
}
