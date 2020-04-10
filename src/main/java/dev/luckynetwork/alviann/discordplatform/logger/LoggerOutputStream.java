package dev.luckynetwork.alviann.discordplatform.logger;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class LoggerOutputStream extends ByteArrayOutputStream {

    private final Logger logger;
    private final Level level;

    @Override
    public void flush() throws IOException {
        String message = toString("utf8");
        String separator = System.getProperty("line.separator");

        super.reset();

        if (message == null || message.trim().isEmpty() || message.trim().equals(separator))
            return;

        logger.log(level, message);
    }

}