package fr.kosmosuniverse.kems.core;

import fr.kosmosuniverse.kems.Kems;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author KosmosUniverse
 */
public class Langs {
    public static final List<String> availableLangs = List.of(new String[]{"en", "fr"});
    private static Langs instance;
    private static final Map<String, String> langs = new HashMap<>();

    public boolean loadLangs(String lang) {
        String filename = lang == null ? "lang.yml" : "lang-" + lang + ".yml";
        filename = "langs/" + filename;
        System.out.println(filename);

        try (Reader reader = new InputStreamReader(Kems.getInstance().getResource(filename), StandardCharsets.UTF_8)) {
            YamlConfiguration langConf = YamlConfiguration.loadConfiguration(reader);
            langConf.getKeys(true).forEach(key -> langs.put(key, langConf.getString(key)));
        } catch (Exception e) {
            if (lang != null) {
                Bukkit.getLogger().warning("Could not load lang for " + lang + ", using default.");
                loadLangs(null);
            } else {
                Bukkit.getLogger().severe("Cannot load langs !");
                return false;
            }
        }

        return true;
    }

    public static synchronized Langs getInstance() {
        if (instance == null) {
            instance = new Langs();
        }

        return instance;
    }

    public String getMessage(String key) {
        return langs.get(key);
    }
}
