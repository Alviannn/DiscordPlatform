package com.github.alviannn.discordplatform.plugin;

import com.github.alviannn.discordplatform.logger.Logger;
import lombok.Getter;

import java.io.File;
import java.io.InputStream;

@SuppressWarnings("unused")
public abstract class DiscordPlugin {

    @Getter private Logger logger;
    @Getter private PluginDescription description;
    protected Thread thread;

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
        System.out.println(message);
    }

    /**
     * initializes the plugin
     */
    void init(PluginDescription description) {
        this.logger = Logger.getLogger(description.getName());
        this.description = description;
    }

}
