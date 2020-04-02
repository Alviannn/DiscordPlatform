package dev.luckynetwork.alviann.discordplatform.plugin;

import dev.luckynetwork.alviann.discordplatform.color.ColoredWriter;
import dev.luckynetwork.alviann.discordplatform.logger.Logger;
import lombok.Getter;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;

public abstract class DiscordPlugin {

    @Getter private Logger logger;
    @Getter private PluginDescription description;

    /**
     * this method will be called on plugin start
     */
    public abstract void onStart();

    /**
     * this method will be called on plugin shutdown
     */
    public abstract void onShutdown();

    /**
     * listens to a command execution inside the console
     *
     * <p>this follows the plugin.properties inside the plugin
     * if there are none then it won't do anything</p>
     */
    public void onConsoleCommand(String command, String[] args) {

    }

    /**
     * gets the data folder for this plugin
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File getDataFolder() {
        File dataFolder = this.description.getDataFolder();

        if (!dataFolder.exists())
            dataFolder.mkdir();

        return dataFolder;
    }

    /**
     * gets a resource in a shape of InputStream from the current jar file
     *
     * <p>Here's for more explanation {@link ClassLoader#getResourceAsStream(String)}</p>
     *
     * @param name the file name
     */
    public InputStream getResource(String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }

    /**
     * sends a message to the console, it's like using the {@code System.out.println}
     * but with the ability to use colors
     *
     * @param message the message
     */
    public void sendToConsole(String message) {
        System.out.println(ColoredWriter.format(message));
    }

    /**
     * initializes the plugin
     */
    void init(PluginDescription description) {
        this.logger = Logger.getLogger(description.getName());
        this.description = description;
    }

}
