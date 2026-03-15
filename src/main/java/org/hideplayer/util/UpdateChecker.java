package org.hideplayer.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hideplayer.HidePlayer;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Utilidad para comprobar si hay actualizaciones del plugin usando la API de GitHub.
 */
public class UpdateChecker {

    private final HidePlayer plugin;
    private final String repoName;
    private String latestVersion = null;

    public UpdateChecker(HidePlayer plugin, String repoName) {
        this.plugin = plugin;
        this.repoName = repoName; // e.g., "staFF6773/hidePlayer"
    }

    /**
     * Obtiene la última versión desde GitHub de forma asincrónica.
     * @param consumer Consumer que recibe la versión (o null si falló)
     */
    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                URL url = new URL("https://api.github.com/repos/" + this.repoName + "/releases/latest");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                // User-Agent is sometimes required by GitHub API
                connection.setRequestProperty("User-Agent", "HidePlayer-UpdateChecker");

                if (connection.getResponseCode() == 200) {
                    try (InputStream inputStream = connection.getInputStream();
                         Scanner scanner = new Scanner(inputStream)) {
                        if (scanner.hasNext()) {
                            String json = scanner.useDelimiter("\\A").next();
                            String tagKey = "\"tag_name\":\"";
                            int index = json.indexOf(tagKey);
                            if (index != -1) {
                                int start = index + tagKey.length();
                                int end = json.indexOf("\"", start);
                                String version = json.substring(start, end);
                                // Remove 'v' or 'V' prefix if present
                                if (version.startsWith("v") || version.startsWith("V")) {
                                    version = version.substring(1);
                                }
                                consumer.accept(version);
                            } else {
                                consumer.accept(null);
                            }
                        } else {
                            consumer.accept(null);
                        }
                    }
                } else if (connection.getResponseCode() == 404) {
                    if (plugin.getSettings().getConfig().getBoolean("debug", false)) {
                        LoggerUtils.info("No releases found in the GitHub repository (Error 404).");
                    }
                    consumer.accept(null);
                } else {
                    if (plugin.getSettings().getConfig().getBoolean("debug", false)) {
                        LoggerUtils.warn("Unexpected response from GitHub API: " + connection.getResponseCode());
                    }
                    consumer.accept(null);
                }
            } catch (Exception exception) {
                LoggerUtils.warn("Could not check for updates: " + exception.getMessage());
                consumer.accept(null);
            }
        });
    }

    /**
     * Compara dos versiones para saber si existe una más reciente.
     */
    private boolean isNewerVersion(String current, String remote) {
        try {
            String[] currentParts = current.split("\\.");
            String[] remoteParts = remote.split("\\.");
            int length = Math.max(currentParts.length, remoteParts.length);
            for (int i = 0; i < length; i++) {
                int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i].replaceAll("[^0-9]", "")) : 0;
                int remotePart = i < remoteParts.length ? Integer.parseInt(remoteParts[i].replaceAll("[^0-9]", "")) : 0;
                if (currentPart < remotePart) {
                    return true;
                }
                if (currentPart > remotePart) {
                    return false;
                }
            }
        } catch (Exception e) {
            // Si hay un error parseando las versiones, hacemos un chequeo simple
            return !current.equalsIgnoreCase(remote);
        }
        return false;
    }

    /**
     * Comprueba si hay actualizaciones y notifica por consola.
     */
    public void checkForUpdates() {
        if (!plugin.getSettings().getConfig().getBoolean("check_updates", true)) {
            return;
        }

        getVersion(version -> {
            if (version == null) return;
            
            this.latestVersion = version;
            String currentVersion = plugin.getDescription().getVersion();
            
            if (isNewerVersion(currentVersion, version)) {
                LoggerUtils.info("");
                LoggerUtils.info(ChatColor.YELLOW + "A new update is available for HidePlayer!");
                LoggerUtils.info(ChatColor.YELLOW + "Current version: " + ChatColor.RED + currentVersion);
                LoggerUtils.info(ChatColor.YELLOW + "New version: " + ChatColor.GREEN + version);
                LoggerUtils.info(ChatColor.YELLOW + "Download it at: " + ChatColor.AQUA + "https://github.com/" + this.repoName + "/releases/latest");
                LoggerUtils.info("");
            } else {
                if (plugin.getSettings().getConfig().getBoolean("debug", false)) {
                    LoggerUtils.info("You are running the latest version of HidePlayer.");
                }
            }
        });
    }

    /**
     * Notifica a un jugador de una actualización si corresponde.
     * @param player El jugador a notificar.
     */
    public void notifyPlayer(Player player) {
        if (!plugin.getSettings().getConfig().getBoolean("check_updates", true)) {
            return;
        }
        
        if (latestVersion == null) {
            return;
        }

        String currentVersion = plugin.getDescription().getVersion();
        if (isNewerVersion(currentVersion, latestVersion) && player.hasPermission("hideplayer.admin")) {
            java.util.List<String> messages = plugin.getLangConfig().getConfig().getStringList("messages.update-available");
            if (messages != null && !messages.isEmpty()) {
                String prefix = plugin.getSettings().getConfig().getString("prefix", "");
                for (String line : messages) {
                    String formatted = line.replace("%prefix%", prefix)
                                         .replace("%current_version%", currentVersion)
                                         .replace("%new_version%", latestVersion)
                                         .replace("%link%", "https://github.com/" + this.repoName + "/releases/latest");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', formatted));
                }
            } else {
                // Fallback message if not configured
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&bHidePlayer&8] &eA new update is available! &c" + currentVersion + " &e-> &a" + latestVersion));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&bHidePlayer&8] &eDownload: &bhttps://github.com/" + this.repoName + "/releases/latest"));
            }
        }
    }
}
