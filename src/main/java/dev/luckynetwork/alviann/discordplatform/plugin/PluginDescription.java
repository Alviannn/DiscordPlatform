package dev.luckynetwork.alviann.discordplatform.plugin;

import com.github.alviannn.sqlhelper.utils.Closer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.luckynetwork.alviann.discordplatform.DiscordPlatform;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
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
    private final Map<String, String> depends;
    private final File dataFolder;

    public static PluginDescription load(File pluginFile, InputStream propStream, InputStream dependsStream) throws IOException {
        if (propStream == null)
            throw new NullPointerException("Invalid plugin description!");

        Properties description = new Properties();
        Map<String, String> dependsMap = new HashMap<>();

        try (Closer closer = new Closer()) {
            InputStreamReader reader = closer.add(new InputStreamReader(propStream));
            description.load(reader);

            if (dependsStream != null) {
                closer.add(dependsStream);
                InputStreamReader dependsReader = closer.add(new InputStreamReader(dependsStream));
                JsonArray dependsArray = JsonParser.parseReader(dependsReader).getAsJsonArray();

                for (JsonElement element : dependsArray) {
                    JsonObject jObject = element.getAsJsonObject();
                    dependsMap.put(jObject.get("name").getAsString(), jObject.get("url").getAsString());
                }
            }
        }

        return new PluginDescription(
                description.getProperty("name"),
                description.getProperty("main"),
                description.getProperty("version"),
                description.getProperty("author"),
                description.getProperty("description"),
                pluginFile,
                dependsMap,
                new File(DiscordPlatform.getPluginFolder(), description.getProperty("name"))
        );
    }

}
