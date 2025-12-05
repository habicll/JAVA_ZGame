package com.main.abilities;

import com.main.effects.BuffMoveSpeed;
import com.main.entities.player.Ability;

/**
 * Ability that buffs the movement speed of allied units using chemicals.
 */
public class DopingProducts extends Ability{
    /**
     * Creates the DopingProducts ability.
     */
    public DopingProducts(){
        super("Steroids", "Inject chemicals into your allies to buff their movement speed", 30, new BuffMoveSpeed(30));
    }
}
