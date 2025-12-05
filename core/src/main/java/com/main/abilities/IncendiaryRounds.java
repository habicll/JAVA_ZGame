package com.main.abilities;

import com.main.effects.DoDamageAOE;
import com.main.entities.player.Ability;

/**
 * Ability that gives explosive bullets to the player.
 */
public class IncendiaryRounds extends Ability{
    /**
     * Creates the IncendiaryRounds ability.
     */
    public IncendiaryRounds(){
        super("Incendiary Rounds", "A brand new model of bullets, making them explosives !", 90, new DoDamageAOE(30, 50));
    }
}
