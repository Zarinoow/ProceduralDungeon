package fr.foxelia.tools.minecraft.ui.color;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to handle colored console output.
 * <br>This class is a copycat of the ChatColor class from the Bukkit API
 * <br>It was created because the Log4Shell vulnerability patch disable
 * the use of the ChatColor directly in the console.
 * <br><br>License: CC BY-SA 4.0
 * @author Foxelia, Zarinoow
 * @version 1.0

 */
public class ColoredConsole {

    private static final Map<Character, ColoredConsole> BY_CHAR = new HashMap<>();
    private static final Map<String, ColoredConsole> BY_NAME = new HashMap<>();
    public static final char COLOR_CHAR = '\u00A7';

    public static final ColoredConsole BLACK = new ColoredConsole(ChatColor.BLACK, "\u001B[30m");
    public static final ColoredConsole DARK_BLUE = new ColoredConsole(ChatColor.DARK_BLUE, "\u001B[34m");
    public static final ColoredConsole DARK_GREEN = new ColoredConsole(ChatColor.DARK_GREEN, "\u001B[32m");
    public static final ColoredConsole DARK_AQUA = new ColoredConsole(ChatColor.DARK_AQUA, "\u001B[36m");
    public static final ColoredConsole DARK_RED = new ColoredConsole(ChatColor.DARK_RED, "\u001B[31m");
    public static final ColoredConsole DARK_PURPLE = new ColoredConsole(ChatColor.DARK_PURPLE, "\u001B[35m");
    public static final ColoredConsole GOLD = new ColoredConsole(ChatColor.GOLD, "\u001B[33m");
    public static final ColoredConsole GRAY = new ColoredConsole(ChatColor.GRAY, "\u001B[37m");
    public static final ColoredConsole DARK_GRAY = new ColoredConsole(ChatColor.DARK_GRAY, "\u001B[90m");
    public static final ColoredConsole BLUE = new ColoredConsole(ChatColor.BLUE, "\u001B[94m");
    public static final ColoredConsole GREEN = new ColoredConsole(ChatColor.GREEN, "\u001B[92m");
    public static final ColoredConsole AQUA = new ColoredConsole(ChatColor.AQUA, "\u001B[96m");
    public static final ColoredConsole RED = new ColoredConsole(ChatColor.RED, "\u001B[91m");
    public static final ColoredConsole LIGHT_PURPLE = new ColoredConsole(ChatColor.LIGHT_PURPLE, "\u001B[95m");
    public static final ColoredConsole YELLOW = new ColoredConsole(ChatColor.YELLOW, "\u001B[93m");
    public static final ColoredConsole WHITE = new ColoredConsole(ChatColor.WHITE, "\u001B[97m");
    public static final ColoredConsole MAGIC = new ColoredConsole(ChatColor.MAGIC, "\u001B[5m");
    public static final ColoredConsole BOLD = new ColoredConsole(ChatColor.BOLD, "\u001B[1m");
    public static final ColoredConsole STRIKETHROUGH = new ColoredConsole(ChatColor.STRIKETHROUGH, "\u001B[9m");
    public static final ColoredConsole UNDERLINE = new ColoredConsole(ChatColor.UNDERLINE, "\u001B[4m");
    public static final ColoredConsole ITALIC = new ColoredConsole(ChatColor.ITALIC, "\u001B[3m");
    public static final ColoredConsole RESET = new ColoredConsole(ChatColor.RESET, "\u001B[0m");

    private final char code;
    private final ChatColor bukkitColor;
    private final String ansiColor;

    private ColoredConsole(ChatColor bukkitColor, String ansiColor) {
        this.code = bukkitColor.toString().charAt(1);
        this.bukkitColor = bukkitColor;
        this.ansiColor = ansiColor;

        BY_CHAR.put(code, this);
        BY_NAME.put(bukkitColor.name(), this);
    }

    public char getCode() {
        return code;
    }

    public ChatColor getBukkitColor() {
        return bukkitColor;
    }

    public String toString() {
        return ansiColor;
    }

    public static String translateAlternateColorCodes(String textToTranslate) {
        return translateAlternateColorCodes(COLOR_CHAR, textToTranslate);

    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        for(Map.Entry<Character, ColoredConsole> entry : BY_CHAR.entrySet()) {
            String regex = "(?i)".concat(new String(new char[]{altColorChar, entry.getKey()}));
            textToTranslate = textToTranslate.replaceAll(regex, entry.getValue().toString());
        }
        return textToTranslate + RESET;
    }

    public static ColoredConsole getByChar(char code) {
        return BY_CHAR.get(code) == null ? RESET : BY_CHAR.get(code);
    }

    public static ColoredConsole getByName(String name) {
        return BY_NAME.get(name) == null ? RESET : BY_NAME.get(name);
    }

    public static ColoredConsole getByBukkitColor(ChatColor bukkitColor) {
        return getByName(bukkitColor.getName());
    }

    public String getName() {
        return this.bukkitColor.getName();
    }

    public Color getColor() {
        return this.bukkitColor.getColor();
    }

}
