package dev.luckynetwork.alviann.discordplatform.logger;

import dev.luckynetwork.alviann.discordplatform.color.ColoredWriter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
        this.print(Level.NONE.getColor() + "[" + this.getDateFormat() + "][" + name + "]: " + message);
    }

    public void log(Level level, String message) {
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

    private void print(String message) {
        String format = ColoredWriter.format(message);
        System.out.println(format);
    }

    private String getDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd - HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return dateFormat.format(System.currentTimeMillis());
    }

}
