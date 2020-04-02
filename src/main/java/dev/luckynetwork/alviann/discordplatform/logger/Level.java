package dev.luckynetwork.alviann.discordplatform.logger;

import dev.luckynetwork.alviann.discordplatform.color.ChatColor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Level {

    NONE("", ChatColor.WHITE),
    INFO("INFO", ChatColor.GREEN),
    DEBUG("DEBUG", ChatColor.YELLOW),
    WARNING("WARNING", ChatColor.GOLD),
    ERROR("ERROR", ChatColor.RED),
    SEVERE("SEVERE", ChatColor.DARK_RED);

    private final String name;
    private final ChatColor color;

}
