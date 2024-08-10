package fr.kosmosuniverse.kems.tabcompleters;

import fr.kosmosuniverse.kems.Kems;
import fr.kosmosuniverse.kems.core.Level;
import fr.kosmosuniverse.kems.core.Mode;
import fr.kosmosuniverse.kems.core.Ranks;
import fr.kosmosuniverse.kems.utils.FileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.*;

/**
 * @author KosmosUniverse
 */
public class KemsConfigTabCompleter implements TabCompleter {
    private final Map<String, List<String>> configValues = new HashMap<>();

    public KemsConfigTabCompleter() {
        try {
            String rawValues = FileUtils.readFileContent(Kems.getInstance().getResource("configValues.json"));
            processRawToList(rawValues);
        } catch (IOException e) {
            // TODO
        }

        addEnums();
    }

    private void processRawToList(String content) {
        JSONTokener tokenizer = new JSONTokener(content);
        JSONObject mainObject = new JSONObject(tokenizer);

        for (String key : mainObject.keySet()) {
            String[] raw = mainObject.getString(key).split(":");

            configValues.put(key, Arrays.asList(raw));
        }
    }

    private void addEnums() {
        List<String> enumElems = new ArrayList<>();

        for (Mode value : Mode.values()) {
            enumElems.add(value.name());
        }

        configValues.put("MODE", enumElems);

        enumElems = new ArrayList<>();

        for (Level value : Level.values()) {
            enumElems.add(value.name());
        }

        configValues.put("LEVEL", enumElems);

        enumElems = new ArrayList<>();

        for (Ranks value : Ranks.values()) {
            enumElems.add(value.name());
        }

        configValues.put("RANK_LIMIT", enumElems);
    }

    public void clear() {
        configValues.clear();
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        if (args.length == 0) {
            return new ArrayList<>(configValues.keySet());
        } else if (args.length % 2 == 1) {
            List<String> ret = new ArrayList<>(configValues.keySet());

            for (String arg : args) {
                ret.remove(arg);
            }

            return ret;
        } else {
            if (configValues.containsKey(args[args.length - 2])) {
                return configValues.get(args[args.length - 2]);
            }
        }

        return new ArrayList<>();
    }
}
