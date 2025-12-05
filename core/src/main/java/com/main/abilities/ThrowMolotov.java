package com.main.abilities;

import com.main.effects.Poison;
import com.main.entities.player.Ability;

/**
 * Ability that throws a molotov cocktail to deal damage over time in an area.
 */
public class ThrowMolotov extends Ability{
    /**
     * Creates the ThrowMolotov ability.
     */
    public ThrowMolotov(){
        super("Throw Molotov", "Throw a cocktail molotov at the designated area", 30, new Poison(20));
    }
}
