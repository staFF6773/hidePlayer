package org.hideplayer.util;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.hideplayer.HidePlayer;

public class SoundUtils {

    /**
     * Reproduce un sonido al jugador basado en la configuración.
     * @param player El jugador al que se le reproducirá el sonido.
     * @param path La ruta en config.yml donde se encuentra el sonido (por ejemplo, "sounds.hide").
     */
    public static void playSound(Player player, String path) {
        if (player == null || !player.isOnline()) return;

        ConfigurationSection section = HidePlayer.getInstance().getSettings().getConfig().getConfigurationSection(path);
        if (section == null) return;

        boolean enabled = section.getBoolean("enabled", false);
        if (!enabled) return;

        String soundName = section.getString("sound");
        if (soundName == null || soundName.isEmpty()) return;

        double volume = section.getDouble("volume", 1.0);
        double pitch = section.getDouble("pitch", 1.0);

        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, (float) volume, (float) pitch);
        } catch (IllegalArgumentException e) {
            HidePlayer.getInstance().getLogger().warning("Invalid sound specified at " + path + ": " + soundName);
        }
    }
}
