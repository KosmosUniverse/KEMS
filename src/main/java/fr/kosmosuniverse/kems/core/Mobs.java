package fr.kosmosuniverse.kems.core;

import fr.kosmosuniverse.kems.Kems;
import fr.kosmosuniverse.kems.utils.FileUtils;
import fr.kosmosuniverse.kems.utils.Versions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.material.Colorable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author KosmosUniverse
 */
public class Mobs {
    private static Mobs instance;
    private List<Mob> mobList;

    public static synchronized Mobs getInstance() {
        if (instance == null) {
            instance = new Mobs();
        }

        return instance;
    }

    public void loadMobs() {
        mobList = new ArrayList<>();

        try {
            String rawValues = FileUtils.readFileContent(Kems.getInstance().getResource(findFilename()));
            processRawJSON(rawValues);
        } catch (IOException e) {
            Bukkit.getLogger().severe(Langs.getInstance().getMessage("cannotLoadMobs"));
        }
    }

   private String findFilename() {
        String currentVersion = Kems.getInstance().getServer().getVersion().substring(0, 6);

        if (Kems.getInstance().getResource("mobs_" + currentVersion + ".json") != null) {
            return "mobs_" + currentVersion + ".json";
        } else {
            String version = Versions.getInstance().getClosestVersion(currentVersion);
            if (version != null) {
                return "mobs_" + version + ".json";
            }
        }

        return "mobs_1.20.6.json";
   }

    private void processRawJSON(String content) {
        JSONTokener tokenizer = new JSONTokener(content);
        JSONArray mainArray = new JSONArray(tokenizer);

        mainArray.forEach(o -> {
            JSONObject mobObj = ((JSONObject) o);

            try {
                mobList.add(new Mob(mobObj.getString("type").toUpperCase(), mobObj.getInt("points"), mobObj.has("attribute") ? mobObj.getString("attribute") : null));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning(Langs.getInstance().getMessage("mobNotInGame").replace("%s", mobObj.getString("type").toUpperCase()));
            }
        });
    }

    public void clear() {
        if (mobList != null) {
            mobList.clear();
        }
    }

    public Mob getMob(Entity entity) {
        List<Mob> mobs = mobList.stream().filter(p -> p.getType() == entity.getType()).toList();

        if (mobs.size() == 1) {
            return mobs.getFirst();
        }

        Attribute attribute = mobs.stream().filter(Mob::hasAttribute).toList().getFirst().getAttribute();

        if (attribute.getType() == Attribute.AttributeType.BABY) {
            if (entity instanceof Ageable entityAge) {
                return mobs.stream().filter(m -> !m.hasAttribute() == entityAge.isAdult()).findFirst().orElse(null);
            }
        } else {
            if (entity instanceof Colorable entityColor) {
                return mobs.stream().filter(m -> m.getAttribute().getOption().toUpperCase().equals(Objects.requireNonNull(entityColor.getColor()).toString())).findFirst().orElse(null);
            }
        }

        return null;
    }
}
