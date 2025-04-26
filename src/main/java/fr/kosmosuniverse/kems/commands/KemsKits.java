package fr.kosmosuniverse.kems.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KemsKits {
    private static KemsKits instance;
    private final List<UUID> playersKit;

    public KemsKits() {
        playersKit = new ArrayList<>();
    }

    public static synchronized KemsKits getInstance() {
        if (instance == null) {
            instance = new KemsKits();
        }

        return instance;
    }

    public boolean canTakeKit(UUID playerUID) {
        return !playersKit.contains(playerUID);
    }

    public void clearKits() {
        playersKit.clear();
    }

    public void kitTaken(UUID player) {
        playersKit.add(player);
    }
}
