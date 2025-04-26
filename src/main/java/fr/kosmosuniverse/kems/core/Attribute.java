package fr.kosmosuniverse.kems.core;

import lombok.Getter;

/**
 * @author KosmosUniverse
 */
@Getter
public class Attribute {
    @Getter
    enum AttributeType {
        BABY(true),
        COLOR("BLANK");
        private boolean isBaby = false;
        private String color = "";

        AttributeType(boolean inputIsBaby) {
            isBaby = inputIsBaby;
        }
        AttributeType(String inputColor) {
            color = inputColor;
        }
    }

    private final AttributeType type;
    private String option;

    public Attribute(AttributeType type) {
        this.type = type;
    }

    public Attribute(AttributeType type, String option) {
        this.type = type;
        this.option = option;
    }
}
