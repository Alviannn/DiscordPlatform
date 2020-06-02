## Discord Platform

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
        <version>1.4.0</version>
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
meaning that you have to keep the default depends.json<br>

To load a discord bot using DiscordPlatform you need to add a file called `plugin.properties`
inside the jar file.
 
 Example of `plugin.properties`
```properties
# (required) the plugin name
name=DiscordPlatform
# (required) the plugin main class
main=com.github.alviannn.discordplatform.DiscordPlatform
# (optional) the plugin version
version=1.3.8
# (optional) tells the plugin author
author=Alviann
# (optional) the plugin description
description=this is a discord loader platform
# (optional) the command prefix to be executed in console
command=discordplatform
# (optional) the command aliases to be executed in console
command-aliases=platform,manager,dplatform
```

You can also bring custom external dependencies like the example below
```json
[
  {
    "name": "jda-utilities-command-3.0.3.jar",
    "url": "https://dl.bintray.com/jagrosh/maven/com/jagrosh/jda-utilities-command/3.0.3/jda-utilities-command-3.0.3.jar"
  },
  {
    "name": "jda-utilities-commons-3.0.3.jar",
    "url": "https://dl.bintray.com/jagrosh/maven/com/jagrosh/jda-utilities-commons/3.0.3/jda-utilities-commons-3.0.3.jar"
  },
  {
    "name": "jda-utilities-doc-3.0.3.jar",
    "url": "https://dl.bintray.com/jagrosh/maven/com/jagrosh/jda-utilities-doc/3.0.3/jda-utilities-doc-3.0.3.jar"
  },
  {
    "name": "jda-utilities-menu-3.0.3.jar",
    "url": "https://dl.bintray.com/jagrosh/maven/com/jagrosh/jda-utilities-menu/3.0.3/jda-utilities-menu-3.0.3.jar"
  },
  {
    "name": "jda-utilities-oauth2-3.0.3.jar",
    "url": "https://dl.bintray.com/jagrosh/maven/com/jagrosh/jda-utilities-oauth2/3.0.3/"
  },
  {
    "name": "json-20190722.jar",
    "url": "https://repo1.maven.org/maven2/org/json/json/20190722/json-20190722.jar"
  }
]
```

If there are any bugs please let me know! <br>
With that being said enjoy the program and happy coding :D