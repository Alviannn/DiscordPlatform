package dev.luckynetwork.alviann.discordplatform.color;

import lombok.Getter;

import java.util.regex.Pattern;

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

    public static final String COLOR_CHAR_1 = "§";
    public static final String COLOR_CHAR_2 = "&";

    public static final Pattern STRIP_COLOR_PATTERN_1 = Pattern.compile("(?i)" + COLOR_CHAR_1 + "[0-9A-FK-OR]");
    public static final Pattern STRIP_COLOR_PATTERN_2 = Pattern.compile("(?i)" + COLOR_CHAR_2 + "[0-9A-FK-OR]");

    @Getter private final String type1;
    @Getter private final String type2;
    @Getter private final String name;

    ChatColor(char code, String name) {
        this.name = name;
        this.type1 = COLOR_CHAR_1 + code;
        this.type2 = COLOR_CHAR_2 + code;
    }

    @Override
    public String toString() {
        return this.type1;
    }

    /**
     * Strips the given message of all color codes
     *
     * @param input String to strip of color
     * @return A copy of the input string, without any coloring
     */
    public static String stripColor(String input) {
        if (input == null)
            return null;

        input = STRIP_COLOR_PATTERN_1.matcher(input).replaceAll("");
        input = STRIP_COLOR_PATTERN_2.matcher(input).replaceAll("");

        return input;
    }
}
