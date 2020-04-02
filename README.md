## Discord Platform
[![](https://jitpack.io/v/Alviannn/DiscordPlatform.svg)](https://jitpack.io/#Alviannn/DiscordPlatform)

This is a discord platform program, with this now you can host several discord bots all at once!

##### To add this to your project (Maven):

1. Add the repository to repositories section
    ```xml
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
    ```
2. Add the dependency to the dependencies section
    ```xml
    <dependency>
        <groupId>com.github.Alviannn</groupId>
        <artifactId>DiscordPlatform</artifactId>
        <version>1.3.1</version>
    </dependency>
    ```

##### Code Example:
```java
public class MyBot extends DiscordPlugin {

    private JDA client;

    @Override
    public void onStart() {
        try {
            client = JDABuilder.createDefault("bot token")
                .build()
                .awaitReady();
    
            this.getLogger().info("I have started :v");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShutdown() {
        try {
            client.shutdownNow();
            this.getLogger().info("Bye bye T-T");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConsoleCommand(String[] args) {
        this.getLogger().info("My command has been executed");
    }

}
```

There's also a dependency configuration for you to manage :3 (from depends.json)
<br>

WARNING: the JDA isn't compiled within the program to decrease the file size <br>
meaning that you have to keep the default config except for several dependencies <br>
like inside the code block below.
```json
{
  "depends": [
    {
      "name": "SQLHelper-2.5.jar",
      "url": "https://github.com/Alviannn/SQLHelper/releases/download/2.5/SQLHelper-2.5.jar"
    },
    {
      "name": "jda-utilities-commons-3.0.3.jar",
      "url": "https://dl.bintray.com/jagrosh/maven/com/jagrosh/jda-utilities-commons/3.0.3/jda-utilities-commons-3.0.3.jar"
    }
  ]
}
```

To load a discord bot using DiscordPlatform you need to add a file called `plugin.properties`
inside the jar file

This is a sample `plugin.properties`
```properties
# (required) the plugin name
name=DiscordPlatform
# (required) the plugin main class
main=dev.luckynetwork.alviann.discordplatform.DiscordPlatform
# (optional) the plugin version
version=1.2
# (optional) tells the plugin author
author=Alviann
# (optional) the plugin description
description=this is a discord loader platform
# (optional) the command prefix to be executed in console
command=discordplatform
# (optional) the command aliases to be executed in console
command-aliases=platform,manager,dplatform
```

If there are any bugs please let me know! <br>
With that being said enjoy the program and happy coding :D