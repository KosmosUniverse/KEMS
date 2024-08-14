package fr.kosmosuniverse.kems.core;

import lombok.Getter;
import lombok.Setter;

/**
 * @author KosmosUniverse
 */
@Getter
@Setter
public class ConfigHolder {
    private boolean spread;
    private boolean shop;
    private int spreadDistance;
    private int spreadRadius;
    private int timeLimit;
    private int pointLimit;
    private int deathPenalty;
    private Ranks rankLimit;
    private Mode mode;
    private long specialSpawnDelay;
    private Level level;
}
