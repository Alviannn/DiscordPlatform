package com.github.alviannn.discordplatform.logger;

import com.github.alviannn.discordplatform.DiscordPlatform;
import com.github.alviannn.discordplatform.closer.Closer;
import com.github.alviannn.discordplatform.color.ChatColor;
import com.github.alviannn.discordplatform.color.ColoredWriter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@SuppressWarnings("unused")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Logger {

    private final String name;

    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    public void log(String message) {
        if (name == null || name.trim().isEmpty())
            this.print(Level.NONE.getColor() + "[" + this.getDateFormat() + "]: " + message);
        else
            this.print(Level.NONE.getColor() + "[" + this.getDateFormat() + "][" + name + "]: " + message);
    }

    public void log(Level level, String message) {
        if (name == null || name.trim().isEmpty())
            this.print(level.getColor() + "[" + this.getDateFormat() + "][" + level.getName() + "]: " + message);
        else
            this.print(level.getColor() + "[" + this.getDateFormat() + "][" + name + " - " + level.getName() + "]: " + message);
    }

    public void debug(String message) {
        this.log(Level.DEBUG, message);
    }

    public void info(String message) {
        this.log(Level.INFO, message);
    }

    public void warning(String message) {
        this.log(Level.WARNING, message);
    }

    public void error(String message) {
        this.log(Level.ERROR, message);
    }

    public void severe(String message) {
        this.log(Level.SEVERE, message);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void print(String message) {
        String format = ColoredWriter.format(message);
        DiscordPlatform.getConsoleStream().println(format);

        File logsFolder = DiscordPlatform.getLogsFolder();
        if (!logsFolder.exists())
            logsFolder.mkdir();

        String dateFormat = this.getDateFormat("yyyy-MM-dd");
        File logsFile = new File(logsFolder, dateFormat + ".log");

        try (Closer closer = new Closer()) {
            if (!logsFile.exists())
                logsFile.createNewFile();

            FileWriter fw = closer.add(new FileWriter(logsFile, true));
            PrintWriter writer = closer.add(new PrintWriter(fw, true));

            writer.println(ChatColor.stripColor(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDateFormat(String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return dateFormat.format(System.currentTimeMillis());
    }

    private String getDateFormat() {
        return this.getDateFormat("yyyy/MM/dd - HH:mm:ss");
    }

}
