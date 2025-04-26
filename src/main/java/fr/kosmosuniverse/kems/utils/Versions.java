package fr.kosmosuniverse.kems.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KosmosUniverse
 */
public class Versions {
    private final List<String> versions;
    private static Versions instance;

    public Versions() {
        versions = new ArrayList<>();
        versions.add("1.20.6");
    }

    public String getClosestVersion(String currentVersion) {
        List<String> tmpList = new ArrayList<>(versions);

        tmpList.add(currentVersion);
        tmpList.sort(String::compareTo);

        String ret = tmpList.indexOf(currentVersion) == 0 ? null : tmpList.get(tmpList.indexOf(currentVersion) - 1);
        tmpList.clear();

        return ret;
    }

    public static synchronized Versions getInstance() {
        if (instance == null) {
            instance = new Versions();
        }

        return instance;
    }
}
