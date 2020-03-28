package dev.luckynetwork.alviann.discordplatform.logger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Logger {

    private final String name;

    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    public void log(String message) {
        System.out.println("[" + name + "]: " + message);
    }

    public void debug(String message) {
        System.out.println("[" + name + " - DEBUG]: " + message);
    }

    public void info(String message) {
        System.out.println("[" + name + " - INFO]: " + message);
    }

    public void error(String message) {
        System.out.println("[" + name + " - ERROR]: " + message);
    }

    public void warning(String message) {
        System.out.println("[" + name + " - WARNING]: " + message);
    }

    public void severe(String message) {
        System.out.println("[" + name + " - SEVERE]: " + message);
    }

}
