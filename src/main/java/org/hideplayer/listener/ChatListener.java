package org.hideplayer.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.hideplayer.HidePlayer;

import java.util.Iterator;
import java.util.Set;

public class ChatListener implements Listener {

    private final HidePlayer plugin;

    public ChatListener(HidePlayer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (plugin.getPlayerManager().isHidden(player)) {
            String originalName = plugin.getPlayerManager().getOriginalName(player);

            // Copiamos los destinatarios actuales (para no afectar si otro plugin los modificó)
            Set<Player> recipients = event.getRecipients();

            // Mensaje que verán los administradores
            String formatAddon = plugin.getSettings().getConfig().getString("admin-chat-format", "%1$s &8(%hideplayer_original_name%)&r");
            if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                formatAddon = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, formatAddon);
            } else {
                // Fallback básico si PAPI no está instalado
                formatAddon = formatAddon.replace("%hideplayer_original_name%", originalName)
                                         .replace("%hideplayer_nick%", player.getName());
            }

            String adminFormat = event.getFormat().replace("%1$s", formatAddon);
            String adminMessageFormatted = String.format(adminFormat, player.getDisplayName(), event.getMessage());
            String finalAdminMessage = ChatColor.translateAlternateColorCodes('&', adminMessageFormatted);

            // Enviar mensaje diferente a los admins y removerlos de los destinatarios originales
            Iterator<Player> iterator = recipients.iterator();
            while (iterator.hasNext()) {
                Player recipient = iterator.next();
                if (recipient.hasPermission("hideplayer.admin")) {
                    recipient.sendMessage(finalAdminMessage);
                    iterator.remove(); // Evitamos que reciban el mensaje normal de Bukkit
                }
            }
        }
    }
}
