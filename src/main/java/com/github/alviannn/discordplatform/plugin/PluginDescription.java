package com.github.alviannn.discordplatform.plugin;

import com.github.alviannn.discordplatform.DiscordPlatform;
import com.github.alviannn.sqlhelper.utils.Closer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.util.*;

@RequiredArgsConstructor
@Getter
public class PluginDescription {

    private final String name;
    private final String mainClass;

    private final String version;
    private final String author;
    private final String description;

    private final String command;
    private final String[] commandAliases;

    private final File pluginFile;
    private final Map<String, String> depends;
    private final File dataFolder;

    public static PluginDescription load(File pluginFile, InputStream propStream, InputStream dependsStream) throws IOException {
        if (propStream == null)
            throw new NullPointerException("Invalid plugin description!");

        Properties description = new Properties();
        Map<String, String> dependsMap = new HashMap<>();

        try (Closer closer = new Closer()) {
            closer.add(propStream);
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

        String name = description.getProperty("name");
        if (name == null || name.isEmpty())
            name = null;

        String command = description.getProperty("command");
        if (command == null || command.isEmpty())
            command = null;
        else
            command = command.toLowerCase().trim();

        return new PluginDescription(
                name,
                description.getProperty("main"),
                description.getProperty("version"),
                description.getProperty("author"),
                description.getProperty("description"),
                command,
                formatAliases(description.getProperty("command-aliases")),
                pluginFile,
                dependsMap,
                new File(DiscordPlatform.getPluginFolder(), description.getProperty("name"))
        );
    }

    /**
     * reformats the command aliases
     */
    private static String[] formatAliases(String aliases) {
        if (aliases == null)
            return new String[0];

        List<String> newArrays = new ArrayList<>();
        for (String alias : aliases.split(",")) {
            String newAlias = alias.toLowerCase().trim();

            if (newAlias.isEmpty())
                continue;

            newArrays.add(newAlias);
        }

        if (newArrays.isEmpty())
            return new String[0];

        return newArrays.toArray(new String[0]);
    }

}
