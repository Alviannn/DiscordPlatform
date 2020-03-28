package dev.luckynetwork.alviann.discordplatform;

import com.github.alviannn.lib.dependencyhelper.DependencyHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.luckynetwork.alviann.discordplatform.logger.Logger;
import dev.luckynetwork.alviann.discordplatform.plugin.PluginManager;
import dev.luckynetwork.alviann.discordplatform.scheduler.Scheduler;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DiscordPlatform {

    @Getter private static Logger logger;
    @Getter private static File dependsFolder;
    @Getter private static File pluginFolder;
    @Getter private static PluginManager pluginManager;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SneakyThrows
    public synchronized void start() {
        logger = Logger.getLogger("DiscordPlatform");
        dependsFolder = new File("depends");
        pluginFolder = new File("plugins");

        JsonObject dependsConfig;
        File dependsConfigFile = new File("depends.json");

        if (!dependsFolder.exists())
            dependsFolder.mkdir();
        if (!pluginFolder.exists())
            pluginFolder.mkdir();

        try (InputStream resource = this.getClass().getClassLoader().getResourceAsStream("depends.json")) {
            if (resource == null)
                throw new NullPointerException("Cannot retrieve depends.json!");

            if (!dependsConfigFile.exists())
                Files.copy(resource, dependsConfigFile.toPath());
        }

        try (FileReader reader = new FileReader(dependsConfigFile)) {
            dependsConfig = JsonParser.parseReader(reader).getAsJsonObject();
        }

        if (dependsConfig == null)
            throw new FileNotFoundException("Missing depends.json!");

        JsonArray dependsArray = dependsConfig.get("depends").getAsJsonArray();
        Map<String, String> depends = new HashMap<>();

        for (JsonElement element : dependsArray) {
            JsonObject depend = element.getAsJsonObject();
            depends.put(depend.get("name").getAsString(), depend.get("url").getAsString());
        }

        DependencyHelper depHelper = new DependencyHelper(this.getClass().getClassLoader());

        logger.debug("Downloading dependencies...");
        depHelper.download(depends, dependsFolder.toPath());

        logger.debug("Loading dependencies...");
        depHelper.loadDir(dependsFolder.toPath());
        logger.debug("Dependencies are now loaded!");

        logger.debug("Loading any plugin(s)!");
        pluginManager = new PluginManager(this);
        pluginManager.loadPlugins();

        logger.debug("Plugins are loaded!");
        logger.debug("Platform has been started!");
    }

    public static void main(String[] args) {
        DiscordPlatform platform = new DiscordPlatform();
        platform.start();

        InputStream stream = System.in;
        Scanner scanner = new Scanner(stream);

        while (scanner.hasNext()) {
            String line = scanner.nextLine().toLowerCase().trim();

            if (line.equals("stop") || line.equals("end")) {
                try {
                    Method plmCloseMethod = PluginManager.class.getDeclaredMethod("close");
                    plmCloseMethod.setAccessible(true);
                    plmCloseMethod.invoke(pluginManager);

                    Method schCloseMethod = Scheduler.class.getDeclaredMethod("close");
                    schCloseMethod.setAccessible(true);
                    schCloseMethod.invoke(null);

                    logger.info("Bye bye :3");
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                logger.info("Invalid command line!");
            }
        }
    }

}
