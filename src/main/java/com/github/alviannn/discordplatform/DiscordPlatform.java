package com.github.alviannn.discordplatform;

import com.github.alviannn.discordplatform.closer.Closer;
import com.github.alviannn.discordplatform.logger.Level;
import com.github.alviannn.discordplatform.logger.Logger;
import com.github.alviannn.discordplatform.logger.LoggerOutputStream;
import com.github.alviannn.discordplatform.plugin.DiscordPlugin;
import com.github.alviannn.discordplatform.plugin.PluginDescription;
import com.github.alviannn.discordplatform.plugin.PluginManager;
import com.github.alviannn.lib.dependencyhelper.DependencyHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.SneakyThrows;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;

public class DiscordPlatform {

    @Getter private static Logger logger;
    @Getter private static PluginManager pluginManager;

    @Getter private static File dependsFolder;
    @Getter private static File pluginFolder;
    @Getter private static File logsFolder;

    @Getter private static PrintStream consoleStream;
    @Getter private static PrintStream errorStream;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SneakyThrows
    public void start() {
        consoleStream = System.out;
        errorStream = System.err;

        logger = Logger.getLogger("DiscordPlatform");
        dependsFolder = new File("depends");
        pluginFolder = new File("plugins");
        logsFolder = new File("logs");

        System.setOut(new PrintStream(new LoggerOutputStream(Logger.getLogger(null), null), true));
        System.setErr(new PrintStream(new LoggerOutputStream(Logger.getLogger(null), Level.ERROR), true));

        AnsiConsole.systemInstall();

        JsonArray depends;
        File dependsConfigFile = new File("depends.json");

        if (!dependsFolder.exists())
            dependsFolder.mkdir();
        if (!pluginFolder.exists())
            pluginFolder.mkdir();

        ClassLoader classLoader = this.getClass().getClassLoader();
        try (Closer closer = new Closer()) {
            InputStream resource = closer.add(classLoader.getResourceAsStream("depends.json"));

            if (resource == null)
                throw new NullPointerException("Cannot retrieve depends.json!");

            if (!dependsConfigFile.exists())
                Files.copy(resource, dependsConfigFile.toPath());

            FileReader reader = closer.add(new FileReader(dependsConfigFile));
            depends = JsonParser.parseReader(reader).getAsJsonArray();
        }

        if (depends == null)
            throw new FileNotFoundException("Missing depends.json!");

        Map<String, String> retrievedDependencies = new HashMap<>();
        for (JsonElement element : depends) {
            JsonObject depend = element.getAsJsonObject();
            retrievedDependencies.put(depend.get("name").getAsString(), depend.get("url").getAsString());
        }

        DependencyHelper helper = new DependencyHelper(classLoader);

        logger.debug("Downloading dependencies...");
        helper.download(retrievedDependencies, dependsFolder.toPath());

        logger.debug("Loading dependencies...");
        helper.loadDir(dependsFolder.toPath());
        logger.debug("Dependencies are now loaded!");

        logger.debug("Loading any plugin(s)!");
        pluginManager = new PluginManager(this);
        pluginManager.loadPlugins();

        logger.debug("Plugins are loaded!");
        logger.debug("Platform has been started!");
    }

    @SuppressWarnings("unchecked")
    private void handleCommand(String command, String[] args) {
        Collection<DiscordPlugin> plugins = pluginManager.getPlugins();

        switch (command) {
            case "stop":
            case "end": {
                pluginManager.unloadAllPlugins();
                AnsiConsole.systemUninstall();

                logger.info("Bye bye :3");
                System.exit(0);
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
                        return;
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
                        return;
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
                    return;
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
                    return;
                }

                String targetName = pluginTarget.getDescription().getName();

                pluginManager.unloadPlugin(targetName);
                logger.info("Successfully unloaded " + targetName + "!");
                break;
            }
            case "reload": {
                if (args.length == 0) {
                    logger.info("Usage: reload <name>");
                    return;
                }

                this.handleCommand("unload", new String[]{args[0]});
                this.handleCommand("load", new String[]{args[0]});

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
                    return;

                try {
                    selectedPlugin.onConsoleCommand(command, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public static void main(String[] mainArgs) {
        DiscordPlatform platform = new DiscordPlatform();
        platform.start();

        new Thread(() -> {
            InputStream stream = System.in;
            Scanner scanner = new Scanner(stream);

            while (scanner.hasNext()) {
                String fullCommand = scanner.nextLine().trim();
                String command = fullCommand.split(" ")[0].toLowerCase();

                String[] array = fullCommand.split(" ");
                String[] args = Arrays.copyOfRange(array, 1, array.length);

                platform.handleCommand(command, args);
            }

            AnsiConsole.systemUninstall();
        }).start();
    }

}
