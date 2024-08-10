package fr.kosmosuniverse.kems.core;

import fr.kosmosuniverse.kems.Kems;
import fr.kosmosuniverse.kems.utils.FileUtils;
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
            System.err.println(e.getMessage());
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
                System.err.println("Mob " + mobObj.getString("type").toUpperCase() + " does not exist.");
            }
        });
    }

    public void clear() {
        mobList.clear();
    }

    public int getMobPoints(EntityType type) {
        Optional<Mob> mob = mobList.stream().filter(p -> p.getType() == type).findFirst();

        return mob.map(Mob::getPoints).orElse(-1);
    }
}
