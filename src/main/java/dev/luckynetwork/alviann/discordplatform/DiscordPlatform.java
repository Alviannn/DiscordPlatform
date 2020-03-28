package dev.luckynetwork.alviann.discordplatform;

import com.github.alviannn.lib.dependencyhelper.DependencyHelper;
import dev.luckynetwork.alviann.discordplatform.logger.Logger;
import dev.luckynetwork.alviann.discordplatform.plugin.PluginManager;
import dev.luckynetwork.alviann.discordplatform.scheduler.Scheduler;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Scanner;

public class DiscordPlatform {

    @Getter private static Logger logger;
    @Getter private static File dependsFolder;
    @Getter private static File pluginFolder;
    @Getter private static PluginManager pluginManager;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SneakyThrows
    public synchronized void start() {
        logger = Logger.getLogger("DiscordPlatform");
        dependsFolder = new File("depends");
        pluginFolder = new File("plugins");

        if (!dependsFolder.exists())
            dependsFolder.mkdir();
        if (!pluginFolder.exists())
            pluginFolder.mkdir();

        DependencyHelper depHelper = new DependencyHelper(this.getClass().getClassLoader());
        depHelper.download("SQLHelper-2.5.jar", "https://github.com/Alviannn/SQLHelper/releases/download/2.5/SQLHelper-2.5.jar", dependsFolder.toPath());
        depHelper.loadDir(dependsFolder.toPath());

        pluginManager = new PluginManager(this);
        pluginManager.loadPlugins();
    }

    public static void main(String[] args) {
        DiscordPlatform platform = new DiscordPlatform();
        boolean stop = false;
        platform.start();

        InputStream stream = System.in;
        Scanner scanner = new Scanner(stream);

        while (scanner.hasNext()) {
            String line = scanner.nextLine().toLowerCase().trim();

            if (line.equals("stop") || line.equals("end")) {
                try {
                    Method plmCloseMethod = PluginManager.class.getDeclaredMethod("close");
                    plmCloseMethod.setAccessible(true);
                    plmCloseMethod.invoke(pluginManager);

                    Method schCloseMethod = Scheduler.class.getDeclaredMethod("close");
                    schCloseMethod.setAccessible(true);
                    schCloseMethod.invoke(null);

                    logger.info("Bye bye :3");
                    stop = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                logger.info("Invalid command line!");
            }

            if (stop)
                break;
        }
    }

}
