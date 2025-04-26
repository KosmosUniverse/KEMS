package fr.kosmosuniverse.kems.core;

import fr.kosmosuniverse.kems.Kems;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author KosmosUniverse
 */
public class Logger {
    private static Logger logger = null;
    private final String path;
    private final DateTimeFormatter dtf;

    public Logger() {
        String folder = Kems.getInstance().getDataFolder().getPath() + File.separator + Kems.getInstance().getDescription().getVersion();
        directoryExistsOrCreate(folder);
        dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy.HH-mm-ss").withZone(ZoneId.systemDefault());

        this.path = folder + File.separator + "kems_" + dtf.format(Instant.now()) + ".log";
        try (FileWriter writer = new FileWriter(this.path)) {
            writer.write("[KEMS] : Created log file.\n");
        } catch (IOException e) {
            Bukkit.getLogger().severe(e.getMessage());
        }
    }

    private void directoryExistsOrCreate(String folder) {
        File file = new File(folder);

        if (!file.isDirectory()) {
            file.mkdirs();
        }
    }

    public synchronized static Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }

        return logger;
    }

    public void log(String msg) {
        try (FileWriter writer = new FileWriter(this.path, true)) {
            writer.append("[").append(dtf.format(Instant.now())).append("] : ").append(msg).append("\n");
        } catch (IOException e) {
            Bukkit.getLogger().severe(e.getMessage());
        }
    }
}
