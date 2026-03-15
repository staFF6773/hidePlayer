package org.hideplayer.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.hideplayer.HidePlayer;
import org.hideplayer.manager.PlayerManager;

public class HidePlayerExpansion extends PlaceholderExpansion {

    private final HidePlayer plugin;

    public HidePlayerExpansion(HidePlayer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "Developer"; // PAPI fallará si esto devuelve vacío
    }

    @Override
    public String getIdentifier() {
        return "hideplayer";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String params) {
        if (offlinePlayer == null || !offlinePlayer.isOnline()) {
            return "";
        }

        Player player = offlinePlayer.getPlayer();
        if (player == null) return "";

        boolean isHidden = plugin.getPlayerManager().isHidden(player);

        if (params.equalsIgnoreCase("is_hidden")) {
            return isHidden ? "true" : "false";
        }

        if (params.equalsIgnoreCase("original_name")) {
            return isHidden ? plugin.getPlayerManager().getOriginalName(player) : player.getName();
        }

        if (params.equalsIgnoreCase("nick")) {
            if (isHidden) {
                PlayerManager.HiddenData data = plugin.getPlayerManager().getHiddenData(player);
                return data != null ? data.getNick() : player.getName();
            }
            return player.getName();
        }
        
        if (params.equalsIgnoreCase("skin")) {
            if (isHidden) {
                PlayerManager.HiddenData data = plugin.getPlayerManager().getHiddenData(player);
                return data != null ? data.getSkin() : player.getName();
            }
            return player.getName();
        }

        return null;
    }
}
