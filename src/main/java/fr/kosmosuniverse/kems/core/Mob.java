package fr.kosmosuniverse.kems.core;

import org.bukkit.entity.EntityType;

/**
 * @author KosmosUniverse
 */
public class Mob {
    private final EntityType type;
    private final int points;

    public Mob(EntityType type, int points) {
        this.type = type;
        this.points = points;
    }

    public Mob(String type, int points) {
        this.type = EntityType.valueOf(type);
        this.points = points;
    }

    public EntityType getType() {
        return type;
    }

    public int getPoints() {
        return points;
    }
}
