package dev.luckynetwork.alviann.discordplatform.plugin;

import com.github.alviannn.sqlhelper.utils.Closer;
import dev.luckynetwork.alviann.discordplatform.DiscordPlatform;
import dev.luckynetwork.alviann.discordplatform.scheduler.Scheduler;
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
    private Map<String, DiscordPlugin> pluginMap = new HashMap<>();

    /**
     * detects all possible plugins on the plugins folder
     */
    @SuppressWarnings("ConstantConditions")
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
                JarEntry entry = jarFile.getJarEntry("plugin.properties");

                InputStream pluginDesc = jarFile.getInputStream(entry);
                description = PluginDescription.load(file, pluginDesc);
            }

            if (description.getName() == null) {
                DiscordPlatform.getLogger().debug("Plugin from " + file.getName() + " has no 'name'!");
                continue;
            }
            if (description.getMainClass() == null) {
                DiscordPlatform.getLogger().debug("Plugin from " + file.getName() + " has no 'main'!");
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
        ClassLoader classLoader = plugin.getClass().getClassLoader();

        if (description == null)
            throw new FileNotFoundException("Cannot find " + name + " plugin!");

        File pluginFile = description.getPluginFile();

        if (!pluginFile.exists())
            throw new FileNotFoundException("Cannot file " + pluginFile.getName() + "!");
        if (!pluginFile.getName().endsWith(".jar"))
            throw new IllegalAccessException("Cannot load file that aren't .jar(s)!");

        ClassLoader pluginLoader = new URLClassLoader(new URL[]{pluginFile.toURI().toURL()}, classLoader);
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
        }

        pluginMap.put(description.getName(), pluginInstance);
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

        try {
            plugin.onShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Scheduler.closeAll(plugin);

        URLClassLoader loader = (URLClassLoader) plugin.getClass().getClassLoader();
        try {
            loader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pluginMap.remove(name);
    }

    public Collection<DiscordPlugin> getPlugins() {
        return pluginMap.values();
    }

    private void close() {
        List<DiscordPlugin> plugins = new ArrayList<>(this.getPlugins());
        for (DiscordPlugin plugin : plugins)
            this.unloadPlugin(plugin.getDescription().getName());

        pluginMap.clear();
    }

}
