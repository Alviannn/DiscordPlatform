package dev.luckynetwork.alviann.discordplatform;

import com.github.alviannn.lib.dependencyhelper.DependencyHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.luckynetwork.alviann.discordplatform.logger.Logger;
import dev.luckynetwork.alviann.discordplatform.plugin.DiscordPlugin;
import dev.luckynetwork.alviann.discordplatform.plugin.PluginDescription;
import dev.luckynetwork.alviann.discordplatform.plugin.PluginManager;
import dev.luckynetwork.alviann.discordplatform.scheduler.Scheduler;
import lombok.Getter;
import lombok.SneakyThrows;
import org.fusesource.jansi.AnsiConsole;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;

public class DiscordPlatform {

    @Getter private static Logger logger;
    @Getter private static File dependsFolder;
    @Getter private static File pluginFolder;
    @Getter private static PluginManager pluginManager;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SneakyThrows
    public synchronized void start() {
        AnsiConsole.systemInstall();

        logger = Logger.getLogger("DiscordPlatform");
        dependsFolder = new File("depends");
        pluginFolder = new File("plugins");

        JsonArray depends;
        File dependsConfigFile = new File("depends.json");

        if (!dependsFolder.exists())
            dependsFolder.mkdir();
        if (!pluginFolder.exists())
            pluginFolder.mkdir();

        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream resource = classLoader.getResourceAsStream("depends.json")) {
            if (resource == null)
                throw new NullPointerException("Cannot retrieve depends.json!");

            if (!dependsConfigFile.exists())
                Files.copy(resource, dependsConfigFile.toPath());
        }

        try (FileReader reader = new FileReader(dependsConfigFile)) {
            depends = JsonParser.parseReader(reader).getAsJsonArray();
        }

        if (depends == null)
            throw new FileNotFoundException("Missing depends.json!");

        Map<String, String> retrievedDependencies = new HashMap<>();

        for (JsonElement element : depends) {
            JsonObject depend = element.getAsJsonObject();
            retrievedDependencies.put(depend.get("name").getAsString(), depend.get("url").getAsString());
        }

        DependencyHelper depHelper = new DependencyHelper(classLoader);

        logger.debug("Downloading dependencies...");
        depHelper.download(retrievedDependencies, dependsFolder.toPath());

        logger.debug("Loading dependencies...");
        depHelper.loadDir(dependsFolder.toPath());
        logger.debug("Dependencies are now loaded!");

        logger.debug("Loading any plugin(s)!");
        pluginManager = new PluginManager(this);
        pluginManager.loadPlugins();

        logger.debug("Plugins are loaded!");
        logger.debug("Platform has been started!");
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] mainArgs) {
        DiscordPlatform platform = new DiscordPlatform();
        platform.start();

        InputStream stream = System.in;
        Scanner scanner = new Scanner(stream);

        while (scanner.hasNext()) {
            String fullCommand = scanner.nextLine().trim();

            String command = fullCommand.split(" ")[0].toLowerCase();

            String[] array = fullCommand.split(" ");
            String[] args = Arrays.copyOfRange(array, 1, array.length);

            Collection<DiscordPlugin> plugins = pluginManager.getPlugins();

            switch (command) {
                case "stop":
                case "end": {
                    try {
                        Method plmCloseMethod = PluginManager.class.getDeclaredMethod("close");
                        plmCloseMethod.setAccessible(true);
                        plmCloseMethod.invoke(pluginManager);

                        Method schCloseMethod = Scheduler.class.getDeclaredMethod("close");
                        schCloseMethod.setAccessible(true);
                        schCloseMethod.invoke(null);

                        AnsiConsole.systemUninstall();

                        logger.info("Bye bye :3");
                        System.exit(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "plugins": {
                    List<String> pluginsName = new ArrayList<>();
                    for (DiscordPlugin plugin : plugins)
                        pluginsName.add(plugin.getDescription().getName());

                    String message = "Plugins (" + pluginsName.size() + "): " + String.join(", ", pluginsName);
                    logger.info(message);
                    break;
                }
                case "load": {
                    try {
                        if (args.length == 0) {
                            logger.info("Usage: load <name>");
                            continue;
                        }

                        String targetName = args[0].toLowerCase().trim();
                        PluginDescription descTarget = null;

                        Method detectPlugins = PluginManager.class.getDeclaredMethod("detectPlugins");
                        detectPlugins.setAccessible(true);
                        Map<String, PluginDescription> descMap = (Map<String, PluginDescription>) detectPlugins.invoke(pluginManager);

                        for (PluginDescription desc : descMap.values()) {
                            if (desc.getName().toLowerCase().equals(targetName)) {
                                descTarget = desc;
                                break;
                            }
                            String fileName = desc.getPluginFile().getName().replace(".jar", "");
                            if (fileName.toLowerCase().equals(targetName)) {
                                descTarget = desc;
                                break;
                            }
                        }

                        if (descTarget == null) {
                            logger.info("Cannot find discord bot to be loaded!");
                            continue;
                        }

                        pluginManager.loadPlugin(descTarget.getName());
                        logger.info("Successfully loaded " + targetName + "!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.info("Failed to load plugin!");
                    }
                    break;
                }
                case "unload": {
                    if (args.length == 0) {
                        logger.info("Usage: load <name>");
                        continue;
                    }

                    String pluginName = args[0].toLowerCase().trim();
                    DiscordPlugin pluginTarget = null;

                    for (DiscordPlugin plugin : plugins) {
                        PluginDescription desc = plugin.getDescription();

                        if (desc.getName().toLowerCase().equals(pluginName)) {
                            pluginTarget = plugin;
                            break;
                        }
                        String fileName = desc.getPluginFile().getName().replace(".jar", "");
                        if (fileName.toLowerCase().equals(pluginName)) {
                            pluginTarget = plugin;
                            break;
                        }
                    }

                    if (pluginTarget == null) {
                        logger.info("Cannot find discord bot to be unloaded!");
                        continue;
                    }

                    String targetName = pluginTarget.getDescription().getName();

                    pluginManager.unloadPlugin(targetName);
                    logger.info("Successfully unloaded " + targetName + "!");
                    break;
                }
                default: {
                    DiscordPlugin selectedPlugin = null;

                    for (DiscordPlugin plugin : plugins) {
                        String prefix = plugin.getDescription().getCommand();
                        String[] prefixAliases = plugin.getDescription().getCommandAliases();

                        if (command.equals(prefix)) {
                            selectedPlugin = plugin;
                            break;
                        }
                        if (prefixAliases.length > 1) {
                            for (String alias : prefixAliases) {
                                if (!command.equals(alias))
                                    continue;

                                selectedPlugin = plugin;
                                break;
                            }

                            if (selectedPlugin != null)
                                break;
                        }
                    }

                    if (selectedPlugin == null)
                        continue;

                    try {
                        selectedPlugin.onConsoleCommand(command, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            AnsiConsole.systemUninstall();
        }
    }

}
