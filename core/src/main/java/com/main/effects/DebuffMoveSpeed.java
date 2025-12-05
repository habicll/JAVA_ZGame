package com.main.effects;

/**
 * Effect that decreases the movement speed of a unit.
 */
public class DebuffMoveSpeed extends Effect{
    /** Value of the movement speed debuff. */
    private final int debuffMoveSpeedValue;
    
    /**
     * Creates a new DebuffMoveSpeed effect.
     * @param value Amount of movement speed debuff
     */
    public DebuffMoveSpeed(int value){
        super("debuffMoveSpeed", Effect.Type.DEBUFF);
        debuffMoveSpeedValue = value;
    }

    /**
     * Gets the movement speed debuff value.
     * @return Movement speed debuff value
     */
    public int getDebuffMoveSpeedValue(){
        return debuffMoveSpeedValue;
    }
}
