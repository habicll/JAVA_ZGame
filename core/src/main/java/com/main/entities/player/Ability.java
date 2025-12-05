package com.main.entities.player;

import com.main.effects.Effect;

/**
 * Représente une capacité spéciale du héros
 */
/**
 * Représente une capacité spéciale du héros.
 * Permet d'appliquer un effet avec un temps de recharge.
 */
public class Ability {
    /** Nom de la capacité. */
    protected String name;
    /** Description de la capacité. */
    protected String description;
    /** Temps de recharge en tours ou secondes. */
    protected int cooldown;
    /** Effet appliqué par la capacité. */
    protected Effect effect;
    /** Temps de recharge restant avant la prochaine utilisation. */
    protected int currentCooldown;

    /**
     * Crée une nouvelle capacité.
     * @param name Nom de la capacité
     * @param description Description de la capacité
     * @param cooldown Temps de recharge
     * @param effect Effet appliqué
     */
    public Ability(String name, String description, int cooldown, Effect effect) {
        this.name = name;
        this.description = description;
        this.cooldown = cooldown;
        this.effect = effect;
        this.currentCooldown = 0;
    }

    /**
     * Retourne le nom de la capacité.
     * @return Nom de la capacité
     */
    public String getName() {
        return name;
    }

    /**
     * Retourne la description de la capacité.
     * @return Description de la capacité
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retourne le temps de recharge de la capacité.
     * @return Temps de recharge
     */
    public int getCooldown() {
        return cooldown;
    }

    /**
     * Retourne l'effet appliqué par la capacité.
     * @return Effet appliqué
     */
    public Effect getEffect() {
        return effect;
    }

    /**
     * Retourne le temps de recharge restant.
     * @return Temps de recharge restant
     */
    public int getCurrentCooldown() {
        return currentCooldown;
    }

    /**
     * Indique si la capacité est prête à être utilisée.
     * @return true si prête, false sinon
     */
    public boolean isReady() {
        return currentCooldown <= 0;
    }

    /**
     * Utilise la capacité si elle est prête.
     */
    public void use() {
        if (isReady()) {
            currentCooldown = cooldown;
            // Logique d'utilisation de la capacité
        }
    }

    /**
     * Met à jour le temps de recharge de la capacité.
     */
    public void updateCooldown() {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
    }
}

