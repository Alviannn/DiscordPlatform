package dev.luckynetwork.alviann.discordplatform.color;

import lombok.Getter;

/**
 * from BungeeCord source code
 */
public enum ChatColor {

    /**
     * Represents black.
     */
    BLACK('0', "black"),
    /**
     * Represents dark blue.
     */
    DARK_BLUE('1', "dark_blue"),
    /**
     * Represents dark green.
     */
    DARK_GREEN('2', "dark_green"),
    /**
     * Represents dark blue (aqua).
     */
    DARK_AQUA('3', "dark_aqua"),
    /**
     * Represents dark red.
     */
    DARK_RED('4', "dark_red"),
    /**
     * Represents dark purple.
     */
    DARK_PURPLE('5', "dark_purple"),
    /**
     * Represents gold.
     */
    GOLD('6', "gold"),
    /**
     * Represents gray.
     */
    GRAY('7', "gray"),
    /**
     * Represents dark gray.
     */
    DARK_GRAY('8', "dark_gray"),
    /**
     * Represents blue.
     */
    BLUE('9', "blue"),
    /**
     * Represents green.
     */
    GREEN('a', "green"),
    /**
     * Represents aqua.
     */
    AQUA('b', "aqua"),
    /**
     * Represents red.
     */
    RED('c', "red"),
    /**
     * Represents light purple.
     */
    LIGHT_PURPLE('d', "light_purple"),
    /**
     * Represents yellow.
     */
    YELLOW('e', "yellow"),
    /**
     * Represents white.
     */
    WHITE('f', "white"),
    /**
     * Represents magical characters that change around randomly.
     */
    MAGIC('k', "obfuscated"),
    /**
     * Makes the text bold.
     */
    BOLD('l', "bold"),
    /**
     * Makes a line appear through the text.
     */
    STRIKETHROUGH('m', "strikethrough"),
    /**
     * Makes the text appear underlined.
     */
    UNDERLINE('n', "underline"),
    /**
     * Makes the text italic.
     */
    ITALIC('o', "italic"),
    /**
     * Resets all previous chat colors or formats.
     */
    RESET('r', "reset");

    public static final String COLOR_CHAR = "§";
    public static final String COLOR_CHAR_2 = "&";

    @Getter private final String type1;
    @Getter private final String type2;
    @Getter private final String name;

    ChatColor(char code, String name) {
        this.name = name;
        this.type1 = COLOR_CHAR + code;
        this.type2 = COLOR_CHAR_2 + code;
    }

    @Override
    public String toString() {
        return this.type1;
    }
}
