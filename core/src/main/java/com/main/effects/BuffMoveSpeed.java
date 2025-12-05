package com.main.effects;

/**
 * Effect that increases the movement speed of a unit.
 */
public class BuffMoveSpeed extends Effect{
    /** Value of the movement speed boost. */
    private final int buffMoveSpeedValue;
    
    /**
     * Creates a new BuffMoveSpeed effect.
     * @param value Amount of movement speed boost
     */
    public BuffMoveSpeed(int value){
        super("buffMoveSpeed", Effect.Type.DEBUFF);
        buffMoveSpeedValue = value;
    }

    /**
     * Gets the movement speed boost value.
     * @return Movement speed boost value
     */
    public int getBuffMoveSpeedValue(){
        return buffMoveSpeedValue;
    }
}