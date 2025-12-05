package com.main.abilities;

import com.main.effects.Heal;
import com.main.entities.player.Ability;

/**
 * Ability that restores a large amount of health to the player.
 */
public class Medikit extends Ability{
    /**
     * Creates the Medikit ability.
     */
    public Medikit(){
        super("Medikit", "Take a big shot of morphin to cope with the pain", 60, new Heal(150));
    }
}
