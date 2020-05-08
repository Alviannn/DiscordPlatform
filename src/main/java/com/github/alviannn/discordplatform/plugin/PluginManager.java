package com.github.alviannn.discordplatform.plugin;

import com.github.alviannn.discordplatform.DiscordPlatform;
import com.github.alviannn.discordplatform.logger.Logger;
import com.github.alviannn.discordplatform.scheduler.Scheduler;
import com.github.alviannn.lib.dependencyhelper.DependencyHelper;
import com.github.alviannn.sqlhelper.utils.Closer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@RequiredArgsConstructor
public class PluginManager {

    private final DiscordPlatform plugin;
    private final Map<String, DiscordPlugin> pluginMap = new HashMap<>();

    /**
     * detects all possible plugins on the plugins folder
     */
    @SneakyThrows
    private Map<String, PluginDescription> detectPlugins() {
        Map<String, PluginDescription> descriptions = new HashMap<>();

        File pluginFolder = DiscordPlatform.getPluginFolder();

        if (!pluginFolder.exists())
            throw new FileNotFoundException("Cannot find plugins folder!");

        File[] files = pluginFolder.listFiles();
        if (files.length == 0)
            return descriptions;

        for (File file : files) {
            if (!file.getName().endsWith(".jar"))
                continue;

            PluginDescription description;
            try (Closer closer = new Closer()) {
                JarFile jarFile = closer.add(new JarFile(file));

                JarEntry propEntry = jarFile.getJarEntry("plugin.properties");
                JarEntry dependsEntry = jarFile.getJarEntry("depends.json");

                InputStream pluginDesc = jarFile.getInputStream(propEntry);

                if (dependsEntry != null) {
                    InputStream dependencies = jarFile.getInputStream(dependsEntry);
                    description = PluginDescription.load(file, pluginDesc, dependencies);
                }
                else {
                    description = PluginDescription.load(file, pluginDesc, null);
                }
            }

            Logger logger = DiscordPlatform.getLogger();
            if (description.getName() == null) {
                logger.debug("Discord bot from " + file.getName() + " has no 'name'!");
                continue;
            }
            if (description.getMainClass() == null) {
                logger.debug("Discord bot from " + file.getName() + " has no 'main'!");
                continue;
            }
            if (description.getCommand() == null && description.getCommandAliases().length > 0) {
                logger.debug("Discord bot from " + file.getName() + " has no 'command' but has 'command-aliases'!");
                continue;
            }

            descriptions.put(description.getName(), description);
        }

        return descriptions;
    }

    /**
     * loads a plugin (discord bot)
     */
    @SneakyThrows
    public synchronized void loadPlugin(String name) {
        Map<String, PluginDescription> descriptionMap = this.detectPlugins();
        PluginDescription description = descriptionMap.get(name);
        if (description == null)
            throw new FileNotFoundException("Cannot find " + name + " plugin!");

        File pluginFile = description.getPluginFile();
        if (!pluginFile.exists())
            throw new FileNotFoundException("Cannot file " + pluginFile.getName() + "!");
        if (!pluginFile.getName().endsWith(".jar"))
            throw new IllegalAccessException("Cannot load file that aren't .jar(s)!");

        ClassLoader classLoader = plugin.getClass().getClassLoader();
        ClassLoader pluginLoader = new URLClassLoader(new URL[]{pluginFile.toURI().toURL()}, classLoader);

        this.loadDepends(description, pluginLoader);
        Class<?> main = pluginLoader.loadClass(description.getMainClass());

        Object instance = main.getDeclaredConstructor().newInstance();
        if (!(instance instanceof DiscordPlugin))
            throw new IllegalAccessException("Failed to load main class! The class isn't implemented with " + DiscordPlugin.class.getSimpleName() + "!");

        DiscordPlugin pluginInstance = (DiscordPlugin) instance;
        pluginInstance.init(description);

        try {
            pluginInstance.onStart();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        pluginMap.put(description.getName(), pluginInstance);
        Logger logger = DiscordPlatform.getLogger();

        String loadedMessage = "Loaded plugin " + description.getName();
        if (description.getVersion() != null)
            loadedMessage += " version " + description.getVersion();
        if (description.getAuthor() != null)
            loadedMessage += " by " + description.getAuthor();

        logger.debug(loadedMessage);
    }

    public synchronized void loadPlugins() {
        Map<String, PluginDescription> descriptionMap = this.detectPlugins();

        for (PluginDescription description : descriptionMap.values())
            this.loadPlugin(description.getName());
    }

    /**
     * unloads a plugin (discord bot)
     */
    public synchronized void unloadPlugin(String name) {
        if (!pluginMap.containsKey(name))
            return;

        DiscordPlugin plugin = pluginMap.get(name);
        Scheduler.closeAll(plugin);
        try {
            plugin.onShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        URLClassLoader loader = (URLClassLoader) plugin.getClass().getClassLoader();
        try {
            loader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pluginMap.remove(name);
        DiscordPlatform.getLogger().debug("Unloaded plugin " + plugin.getDescription().getName());

        // clears the leftovers
        System.gc();
    }

    /**
     * loads the dependencies for a specific plugin
     */
    @SneakyThrows
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public synchronized void loadDepends(PluginDescription description, ClassLoader loader) {
        DependencyHelper depHelper = new DependencyHelper(loader);
        Logger logger = Logger.getLogger(description.getName());

        Map<String, String> dependsMap = description.getDepends();

        if (dependsMap.size() == 0) {
            logger.info("No dependencies found!");
            return;
        }

        // creates the data folder earlier than expected for the plugin
        File dataFolder = description.getDataFolder();
        if (!dataFolder.exists())
            dataFolder.mkdir();

        // creates a depends folder inside the data folder
        File dependsFolder = new File(dataFolder, "depends");
        if (!dependsFolder.exists())
            dependsFolder.mkdir();

        // downloads and loads the dependencies if exists
        logger.info("Downloading dependencies...");
        depHelper.download(dependsMap, dependsFolder.toPath());

        logger.info("Loading dependencies...");
        depHelper.loadDir(dependsFolder.toPath());

        logger.info("Finished loading dependencies!");
    }

    /**
     * gets the exisiting plugins
     */
    public Collection<DiscordPlugin> getPlugins() {
        return pluginMap.values();
    }

    public void unloadAllPlugins() {
        List<DiscordPlugin> plugins = new ArrayList<>(this.getPlugins());

        for (DiscordPlugin plugin : plugins)
            this.unloadPlugin(plugin.getDescription().getName());

        pluginMap.clear();
    }

}
