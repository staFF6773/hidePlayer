package org.hideplayer.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.hideplayer.HidePlayer;
import org.hideplayer.manager.PlayerManager;

public class ConnectionListener implements Listener {

    private final HidePlayer plugin;

    public ConnectionListener(HidePlayer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Notificar actualización si corresponde
        if (player.hasPermission("hideplayer.admin")) {
            plugin.getUpdateChecker().notifyPlayer(player);
        }
        
        // Comprobar si está en el archivo y restaurar su estado visual
        if (plugin.getPlayerManager().isHidden(player)) {
            PlayerManager.HiddenData data = plugin.getPlayerManager().getHiddenData(player);
            if (data != null) {
                // Falsificar mensaje de entrada real si la opción está activa
                if (plugin.getSettings().getConfig().getBoolean("fake-connection-messages", true)) {
                    String fakeJoin = plugin.getLangConfig().getConfig().getString("messages.fake-join", "&e%player% joined the game");
                    String nick = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', data.getNick()));
                    event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', fakeJoin.replace("%player%", nick)));
                }

                // Aplicar con retraso de 5 ticks (1/4 de segundo) 
                // para que el evento de unirse procese la skin global y los plugins de tablist
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        // Enviamos true para asegurar que la skin se re-aplique o verifique
                        plugin.getPlayerManager().applyHiddenState(player, data.getNick(), data.getSkin(), true);
                    }
                }, 5L);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getPlayerManager().isHidden(player)) {
            PlayerManager.HiddenData data = plugin.getPlayerManager().getHiddenData(player);
            if (data != null) {
                // Falsificar mensaje de salida real si la opción está activa
                if (plugin.getSettings().getConfig().getBoolean("fake-connection-messages", true)) {
                    String fakeQuit = plugin.getLangConfig().getConfig().getString("messages.fake-quit", "&e%player% left the game");
                    String nick = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', data.getNick()));
                    event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', fakeQuit.replace("%player%", nick)));
                }
            }
        }
    }
}
