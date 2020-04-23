package com.github.alviannn.discordplatform.logger;

import com.github.alviannn.discordplatform.color.ColoredWriter;
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

        message = ColoredWriter.format(message);
        if (level != null)
            logger.log(level, message);
        else
            logger.print(message);
    }

}
