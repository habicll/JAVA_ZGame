package com.main.effects;

/**
 * Abstract base class for all effects applied to units.
 */
public abstract class Effect {
    /** Name of the effect. */
    protected String name;
    /** Type of the effect. */
    protected Type type;
    /**
     * Types of effects that can be applied to units.
     */
    protected enum Type{
        /** Buff effect. */
        BUFF,
        /** Debuff effect. */
        DEBUFF,
        /** Area of effect. */
        AOE,
        /** Damage over time. */
        DOT,
        /** Direct damage. */
        DAMAGE
    }

    /**
     * Creates a new effect.
     * @param name Name of the effect
     * @param type Type of the effect
     */
    public Effect(String name, Type type){
        this.type = type;
        this.name = name;
    }

    /**
     * Gets the name of the effect.
     * @return Name of the effect
     */
    public String getName(){
        return name;
    }

    /**
     * Gets the type of the effect.
     * @return Type of the effect
     */
    public Type getType(){
        return type;
    }
}
