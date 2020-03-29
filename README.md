## Discord Platform

This is a discord platform program, with this now you can host several discord bots all at once!

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
    
            System.out.println("I have started :v");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShutdown() {
        try {
            client.shutdownNow();
            System.out.println("Bye bye T-T");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
```

There's also a dependency configuration for you to manage :3 (from depends.json)

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

If there are any bugs please let me know! <br>
With that being said enjoy the program and happy coding :D