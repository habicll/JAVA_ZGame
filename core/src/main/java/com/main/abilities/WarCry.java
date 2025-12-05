package com.main.abilities;

import com.main.effects.BuffDamage;
import com.main.entities.player.Ability;

public class WarCry extends Ability{
    public WarCry(){
        super("warCry", "Yell insults at your allies to boost their damage", 60, new BuffDamage(15));
    }
}
