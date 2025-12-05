package com.main.abilities;

import com.main.effects.GoldMultiplier;
import com.main.entities.player.Ability;

/**
 * Ability that increases gold earned by coating bullets in gold.
 */
public class MidasTouch extends Ability{
    /**
     * Creates the MidasTouch ability.
     */
    public MidasTouch(){
        super("Midas Touch", "Coat your bullets in gold and earn more goldo", 120, new GoldMultiplier(2));
    }
}
