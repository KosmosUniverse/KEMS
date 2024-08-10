package fr.kosmosuniverse.kems.core;

/**
 * @author KosmosUniverse
 */
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

    public boolean isSpread() {
        return spread;
    }
    public void setSpread(boolean spread) {
        this.spread = spread;
    }
    public boolean hasShop() {
        return shop;
    }
    public void setShop(boolean shop) {
        this.shop = shop;
    }
    public int getSpreadDistance() {
        return spreadDistance;
    }

    public void setSpreadDistance(int spreadDistance) {
        this.spreadDistance = spreadDistance;
    }

    public int getSpreadRadius() {
        return spreadRadius;
    }

    public void setSpreadRadius(int spreadRadius) {
        this.spreadRadius = spreadRadius;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getPointLimit() {
        return pointLimit;
    }

    public void setPointLimit(int pointLimit) {
        this.pointLimit = pointLimit;
    }

    public int getDeathPenalty() {
        return deathPenalty;
    }

    public void setDeathPenalty(int deathPenalty) {
        this.deathPenalty = deathPenalty;
    }

    public Ranks getRankLimit() {
        return rankLimit;
    }

    public void setRankLimit(Ranks rankLimit) {
        this.rankLimit = rankLimit;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public long getSpecialSpawnDelay() {
        return specialSpawnDelay;
    }

    public void setSpecialSpawnDelay(long specialSpawnDelay) {
        this.specialSpawnDelay = specialSpawnDelay;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
