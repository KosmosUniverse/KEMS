package fr.kosmosuniverse.kems.core;

import lombok.Getter;
import org.bukkit.entity.EntityType;

/**
 * @author KosmosUniverse
 */
@Getter
public class Mob {
    private final EntityType type;
    private final int points;
    private final Attribute attribute;

    public Mob(String type, int points, String rawAttribute) throws IllegalArgumentException {
        this.type = EntityType.valueOf(type);
        this.points = points;

        if (rawAttribute != null) {
            if (rawAttribute.equals("BABY")) {
                this.attribute = new Attribute(Attribute.AttributeType.BABY);
            } else {
                this.attribute = new Attribute(Attribute.AttributeType.COLOR, rawAttribute);
            }
        } else {
            this.attribute = null;
        }
    }

    public boolean hasAttribute() {
        return attribute != null;
    }
}
