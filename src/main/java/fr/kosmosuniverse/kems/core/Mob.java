package fr.kosmosuniverse.kems.core;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EntityType;

/**
 * @author KosmosUniverse
 */
@Getter
@Setter
public class Mob {
    private final EntityType type;
    private final int points;

    public Mob(String type, int points) throws IllegalArgumentException {
        this.type = EntityType.valueOf(type);
        this.points = points;
    }
}
