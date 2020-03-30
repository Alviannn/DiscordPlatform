package dev.luckynetwork.alviann.discordplatform.scheduler;

import dev.luckynetwork.alviann.discordplatform.plugin.DiscordPlugin;

import java.util.*;

public class Scheduler {

    public static Map<DiscordPlugin, List<Timer>> SCHEDULE_MAP = new HashMap<>();

    public static Timer runTaskTimer(DiscordPlugin plugin, Runnable runnable, long delay, long period) {
        Timer timer = new Timer();

        List<Timer> timers = SCHEDULE_MAP.get(plugin);
        if (timers == null)
            timers = new ArrayList<>();

        timers.add(timer);
        SCHEDULE_MAP.put(plugin, timers);

        timer.scheduleAtFixedRate(getTimerTask(runnable), delay, period);
        return timer;
    }

    public static Timer runTaskLater(DiscordPlugin plugin, Runnable runnable, long delay) {
        Timer timer = new Timer();

        List<Timer> timers = SCHEDULE_MAP.get(plugin);
        if (timers == null)
            timers = new ArrayList<>();

        timers.add(timer);
        SCHEDULE_MAP.put(plugin, timers);

        timer.schedule(getTimerTask(runnable), delay);
        return timer;
    }

    public static void closeAll(DiscordPlugin plugin) {
        List<Timer> timers = SCHEDULE_MAP.get(plugin);

        for (Timer timer : timers) {
            try {
                timer.cancel();
            } catch (Exception ignored) {
            }
        }

        SCHEDULE_MAP.remove(plugin);
    }

    private static void close() {
        for (DiscordPlugin plugin : SCHEDULE_MAP.keySet())
            closeAll(plugin);

        SCHEDULE_MAP.clear();
    }

    private static TimerTask getTimerTask(Runnable runnable) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
