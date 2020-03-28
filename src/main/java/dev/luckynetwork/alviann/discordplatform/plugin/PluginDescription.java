package dev.luckynetwork.alviann.discordplatform.plugin;

import com.github.alviannn.sqlhelper.utils.Closer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.util.Properties;

@RequiredArgsConstructor
@Getter
public class PluginDescription {

    private final String name;
    private final String mainClass;
    private final String version;
    private final String author;
    private final String description;
    private final File pluginFile;

    public static PluginDescription load(File pluginFile, InputStream resource) throws IOException {
        if (resource == null)
            throw new NullPointerException("Invalid plugin description!");

        Properties description = new Properties();

        try (Closer closer = new Closer()) {
            InputStreamReader reader = closer.add(new InputStreamReader(resource));
            description.load(reader);
        }

        return new PluginDescription(
                description.getProperty("name"),
                description.getProperty("main"),
                description.getProperty("version"),
                description.getProperty("author"),
                description.getProperty("description"),
                pluginFile
        );
    }

}
