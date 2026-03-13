package org.hideplayer.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.util.logging.Level;

/**
 * Utilidad para manejo de logs y errores con formato consistente.
 */
public class LoggerUtils {

    private static final String PREFIX = "[HidePlayer] ";

    public static void info(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + PREFIX + ChatColor.WHITE + message);
    }

    public static void warn(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + PREFIX + "ADVERTENCIA: " + message);
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + PREFIX + "ERROR: " + message);
    }

    public static void logException(String context, Exception e) {
        error("Ocurrió un error en: " + context);
        error("Mensaje: " + e.getMessage());
        Bukkit.getLogger().log(Level.SEVERE, PREFIX + " Detalles técnicos:", e);
    }
}
