package org.hideplayer.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hideplayer.HidePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Maneja el estado de ocultación y nicknames de los jugadores.
 */
public class PlayerManager {

    private final HidePlayer plugin;
    private final Map<UUID, String> hiddenPlayers; // UUID -> Original Name (o estado)

    public PlayerManager(HidePlayer plugin) {
        this.plugin = plugin;
        this.hiddenPlayers = new HashMap<>();
    }

    /**
     * Oculta la identidad de un jugador.
     */
    public void hidePlayer(Player player, String nick, String skin) {
        // Guardar estado
        hiddenPlayers.put(player.getUniqueId(), player.getName());

        // Cambiar DisplayName (Chat) y PlayerListName (Tab)
        String formattedNick = ChatColor.translateAlternateColorCodes('&', nick);
        player.setDisplayName(formattedNick);
        player.setPlayerListName(formattedNick);

        // Cambiar Skin
        plugin.getSkinManager().setSkin(player, skin);
    }

    /**
     * Restaura la identidad de un jugador.
     */
    public void showPlayer(Player player) {
        if (!hiddenPlayers.containsKey(player.getUniqueId()))
            return;

        // Restaurar nombres originales
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());

        // Restaurar Skin
        plugin.getSkinManager().resetSkin(player);

        hiddenPlayers.remove(player.getUniqueId());
    }

    public boolean isHidden(Player player) {
        return hiddenPlayers.containsKey(player.getUniqueId());
    }
}
