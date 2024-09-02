package fr.kosmosuniverse.kems.core;

import fr.kosmosuniverse.kems.Kems;
import fr.kosmosuniverse.kems.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            String rawValues = FileUtils.readFileContent(Kems.getInstance().getResource("mobs.json"));
            processRawJSON(rawValues);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[K.E.M.S] : Couldn't read mobs.json resource, please contact K.E.M.S developer.");
        }
    }

    private void processRawJSON(String content) {
        JSONTokener tokenizer = new JSONTokener(content);
        JSONArray mainArray = new JSONArray(tokenizer);

        mainArray.forEach(o -> {
            JSONObject mobObj = ((JSONObject) o);

            try {
                mobList.add(new Mob(mobObj.getString("type").toUpperCase(), mobObj.getInt("points")));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("[K.E.M.S] : Couldn't load {" + mobObj.getString("type").toUpperCase() + "} mob because it isn't found in game.");
            }
        });
    }

    public void clear() {
        if (mobList != null) {
            mobList.clear();
        }
    }

    public int getMobPoints(EntityType type) {
        Optional<Mob> mob = mobList.stream().filter(p -> p.getType() == type).findFirst();

        return mob.map(Mob::getPoints).orElse(-1);
    }
}
