package dev.luckynetwork.alviann.discordplatform.plugin;

import dev.luckynetwork.alviann.discordplatform.logger.Logger;
import lombok.Getter;

import java.io.File;
import java.io.InputStream;

public abstract class DiscordPlugin {

    @Getter private Logger logger;
    @Getter private PluginDescription description;

    public abstract void onStart();

    public abstract void onShutdown();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File getDataFolder() {
        File dataFolder = this.description.getDataFolder();

        if (!dataFolder.exists())
            dataFolder.mkdir();

        return dataFolder;
    }

    public InputStream getResource(String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }

    void init(PluginDescription description) {
        this.logger = Logger.getLogger(description.getName());
        this.description = description;
    }

}
