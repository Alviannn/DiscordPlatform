package dev.luckynetwork.alviann.discordplatform.logger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Logger {

    private final String name;

    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    public void log(String message) {
        this.print("[" + name + "]: " + message);
    }

    public void debug(String message) {
        this.print("[" + name + " - DEBUG]: " + message);
    }

    public void info(String message) {
        this.print("[" + name + " - INFO]: " + message);
    }

    public void error(String message) {
        this.print("[" + name + " - ERROR]: " + message);
    }

    public void warning(String message) {
        this.print("[" + name + " - WARNING]: " + message);
    }

    public void severe(String message) {
        this.print("[" + name + " - SEVERE]: " + message);
    }

    private void print(String message) {
        System.out.println("[" + this.getDateFormat() + "]" + message);
    }

    private String getDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd - HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return dateFormat.format(System.currentTimeMillis());
    }

}
