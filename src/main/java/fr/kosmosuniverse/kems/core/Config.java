package fr.kosmosuniverse.kems.core;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author KosmosUniverse
 */
public class Config {
    private static final String SPREADPLAYER = "game_settings.spreadplayers.enable";
    private static final String SHOP = "game_settings.enable_shop";
    private static final String SPREAD_DISTANCE = "game_settings.spreadplayers.minimum_distance";
    private static final String SPREAD_RADIUS = "game_settings.spreadplayers.minimum_radius";
    private static final String TIME_LIMIT = "game_settings.time_limit_mode.time_limit";
    private static final String POINT_LIMIT = "game_settings.point_limit_mode.point_limit";
    private static final String DEATH_PENALTY = "game_settings.point_limit_mode.death_penalty";
    private static final String SPECIAL_DELAY = "game_settings.base_special_spawn_delay";
    private static final String RANK_LIMIT = "game_settings.rank_limit_mode.rank_limit";
    private static final String MODE = "game_settings.mode";
    private static final String LEVEL = "game_settings.level";

    private static Config instance;
    private ConfigHolder configValues;
    private Map<String, Consumer<String>> configElems = null;

    /**
     * Get Config instance
     *
     * @return Config instance
     */
    public static synchronized Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }

        return instance;
    }

    /**
     * Clear Config Elements
     */
    public void clear() {
        configElems.clear();
    }

    /**
     * Get config values
     *
     * @return config values
     */
    public ConfigHolder getConfigValues() {
        return configValues;
    }

    /**
     * Setup config elements and check config file values
     *
     * @param configFile    config file used to define startup config values
     */
    public void setupConfig(FileConfiguration configFile) {
        configValues = new ConfigHolder();
        configElems = new HashMap<>();

        configElems.put("SPREADPLAYERS", (String b) -> setSpreadPlayers(Boolean.parseBoolean(b)));
        configElems.put("SHOP", (String b) -> setShop(Boolean.parseBoolean(b)));
        configElems.put("SPREAD_MIN_DISTANCE", (String i) -> setSpreadPlayersDistance(Integer.parseInt(i)));
        configElems.put("SPREAD_MIN_RADIUS", (String i) -> setSpreadPlayersRadius(Integer.parseInt(i)));
        configElems.put("TIME_LIMIT", (String i) -> setTimeLimit(Integer.parseInt(i)));
        configElems.put("POINTS_LIMIT", (String i) -> setPointsLimit(Integer.parseInt(i)));
        configElems.put("DEATH_PENALTY", (String i) -> setDeathPenalty(Integer.parseInt(i)));
        configElems.put("SPECIAL_SPAWN_DELAY", (String i) -> setSpecialSpawnDelay(Long.parseLong(i)));
        configElems.put("RANK_LIMIT", this::setRankLimit);
        configElems.put("MODE", this::setMode);
        configElems.put("LEVEL", this::setLevel);

        checkAndSetConfig(configFile);
    }

    /**
     * Check config file keys and values
     *
     * @param configFile    config file
     */
    private void checkAndSetConfig(FileConfiguration configFile) {
        if (!configFile.contains(SPREADPLAYER)) {
            configFile.set(SPREADPLAYER, false);
        }

        if (!configFile.contains(SHOP)) {
            configFile.set(SHOP, false);
        }

        if (!configFile.contains(SPREAD_DISTANCE) ||
            configFile.getInt(SPREAD_DISTANCE) < 1) {
            configFile.set(SPREAD_DISTANCE, 500);
        }

        if (!configFile.contains(SPREAD_RADIUS) ||
                configFile.getInt(SPREAD_RADIUS) < 1) {
            configFile.set(SPREAD_RADIUS, 1000);
        }

        if (!configFile.contains(TIME_LIMIT) ||
                configFile.getInt(TIME_LIMIT) < 1) {
            configFile.set(TIME_LIMIT, 45);
        }

        if (!configFile.contains(POINT_LIMIT) ||
                configFile.getInt(POINT_LIMIT) < 1) {
            configFile.set(POINT_LIMIT, 10000);
        }

        if (!configFile.contains(DEATH_PENALTY)) {
            configFile.set(DEATH_PENALTY, 50);
        }

        if (!configFile.contains(SPECIAL_DELAY) ||
                configFile.getInt(SPECIAL_DELAY) < 1) {
            configFile.set(SPECIAL_DELAY, 4);
        }

        if (!configFile.contains(RANK_LIMIT)) {
            configFile.set(RANK_LIMIT, Ranks.HEROBRINE.toString());
        }

        if (!configFile.contains(MODE)) {
            configFile.set(MODE, Mode.NO_MODE.toString());
        }

        if (!configFile.contains(LEVEL)) {
            configFile.set(LEVEL, Level.EASY.toString());
        }

        setValues(configFile);
    }

    /**
     * Define config value using config file
     *
     * @param configFile    Config file
     */
    private void setValues(FileConfiguration configFile) {
        configValues.setSpread(configFile.getBoolean(SPREADPLAYER));
        configValues.setSpread(configFile.getBoolean(SHOP));
        configValues.setSpreadDistance(configFile.getInt(SPREAD_DISTANCE));
        configValues.setSpreadRadius(configFile.getInt(SPREAD_RADIUS));
        configValues.setTimeLimit(configFile.getInt(TIME_LIMIT));
        configValues.setPointLimit(configFile.getInt(POINT_LIMIT));
        configValues.setDeathPenalty(configFile.getInt(DEATH_PENALTY));
        configValues.setSpecialSpawnDelay(configFile.getLong(SPECIAL_DELAY));
        configValues.setRankLimit(Ranks.valueOf(configFile.getString(RANK_LIMIT)));
        configValues.setMode(Mode.valueOf(configFile.getString(MODE)));
        configValues.setLevel(Level.valueOf(configFile.getString(LEVEL)));
    }

    /**
     * Check if config knows a specific key
     *
     * @param key   The key to check
     *
     * @return if found True, False instead
     */
    public boolean hasKey(String key) {
        return configElems.containsKey(key);
    }

    /**
     * Set a specific config value
     *
     * @param key   the config key to modify
     * @param value The new value to set
     *
     * @return True if set correctly, False instead
     */
    public boolean setConfigValue(String key, String value) {
        boolean ret = true;

        try {
            configElems.get(key).accept(value);
        } catch (Exception e) {
            ret = false;
        }

        return ret;
    }

    /**
     * Display the entire configuration
     *
     * @return the string that represent the configuration
     */
    public String displayConfig() {

        return ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Configuration" + "\n" +
                ChatColor.BLUE + "Spreadplayers: " + ChatColor.GOLD + configValues.isSpread() + "\n" +
                ChatColor.BLUE + "  - Distance: " + ChatColor.GOLD + configValues.getSpreadDistance() + "\n" +
                ChatColor.BLUE + "  - Radius: " + ChatColor.GOLD + configValues.getSpreadRadius() + "\n" +
                ChatColor.BLUE + "Shop: " + ChatColor.GOLD + configValues.isShop() + "\n" +
                ChatColor.BLUE + "Time Limit Mode: " + ChatColor.GOLD + "\n" +
                ChatColor.BLUE + "  - Time Limit: " + ChatColor.GOLD + configValues.getTimeLimit() + "\n" +
                ChatColor.BLUE + "Point Limit Mode: " + ChatColor.GOLD + "\n" +
                ChatColor.BLUE + "  - Point Limit: " + ChatColor.GOLD + configValues.getPointLimit() + "\n" +
                ChatColor.BLUE + "Death Penalty: " + ChatColor.GOLD + configValues.getDeathPenalty() + "\n" +
                ChatColor.BLUE + "Rank Limit Mode: " + ChatColor.GOLD + "\n" +
                ChatColor.BLUE + "  - Rank Limit: " + ChatColor.GOLD + configValues.getRankLimit() + "\n" +
                ChatColor.BLUE + "Mode: " + ChatColor.GOLD + configValues.getMode().getdisplayString() + "\n" +
                ChatColor.BLUE + "Base Special Spawn Delay: " + ChatColor.GOLD + configValues.getSpecialSpawnDelay() + "\n" +
                ChatColor.BLUE + "Level: " + ChatColor.GOLD + configValues.getLevel() + "\n" ;
    }

    /**
     * Set the Spreadplayers boolean value
     *
     * @param spreadPlayers the new state to set
     */
    private void setSpreadPlayers(boolean spreadPlayers) {
        configValues.setSpread(spreadPlayers);
    }

    /**
     * Set the Shop boolean value
     *
     * @param shop the new state to set
     */
    private void setShop(boolean shop) {
        configValues.setShop(shop);
    }

    /**
     * Set the Spreadplayers integer minimal distance
     *
     * @param distance  The new distance to set
     */
    private void setSpreadPlayersDistance(int distance) {
        configValues.setSpreadDistance(distance);
    }

    /**
     * Set the Spreadplayers integer minimal radius
     *
     * @param radius    The new radius to set
     */
    private void setSpreadPlayersRadius(int radius) {
        configValues.setSpreadRadius(radius);
    }

    /**
     * Sets the time limit in minutes
     *
     * @param timeLimit The new time limit
     */
    private void setTimeLimit(int timeLimit) {
        configValues.setTimeLimit(timeLimit);
    }

    private void setPointsLimit(int pointLimit) {
        configValues.setPointLimit(pointLimit);
    }

    private void setDeathPenalty(int deathPenalty) {
        configValues.setDeathPenalty(deathPenalty);
    }

    private void setRankLimit(String rankLimit) {
        try {
            configValues.setRankLimit(Ranks.valueOf(rankLimit));
        } catch (IllegalArgumentException e) {
            // TODO
        }
    }

    private void setMode(String mode) {
        try {
            configValues.setMode(Mode.valueOf(mode));
        } catch (IllegalArgumentException e) {
            // TODO
        }
    }

    private void setSpecialSpawnDelay(long specialSpawnDelay) {
        configValues.setSpecialSpawnDelay(specialSpawnDelay);
    }

    private void setLevel(String level) {
        try {
            configValues.setLevel(Level.valueOf(level));
        } catch (IllegalArgumentException e) {
            // TODO
        }
    }
}
