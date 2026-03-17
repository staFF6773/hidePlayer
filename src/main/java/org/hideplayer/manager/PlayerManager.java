package org.hideplayer.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hideplayer.HidePlayer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Maneja el estado de ocultación y nicknames de los jugadores usando JSON (Gson).
 */
public class PlayerManager {

    private final HidePlayer plugin;
    private Map<UUID, HiddenData> hiddenPlayers;
    private final File dataFile;
    private final Gson gson;

    public PlayerManager(HidePlayer plugin) {
        this.plugin = plugin;
        this.hiddenPlayers = new HashMap<>();
        this.dataFile = new File(plugin.getDataFolder(), "data.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadData();
    }

    private void loadData() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
                saveData(); // Guarda un JSON vacío {}
            } catch (IOException e) {
                plugin.getLogger().severe("No se pudo crear data.json: " + e.getMessage());
            }
            return;
        }

        try (FileReader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<UUID, HiddenData>>() {}.getType();
            Map<UUID, HiddenData> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                hiddenPlayers = loaded;
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Error al leer data.json: " + e.getMessage());
        }
    }

    private void saveData() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(hiddenPlayers, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Error al guardar data.json: " + e.getMessage());
        }
    }

    /**
     * Oculta la identidad de un jugador y guarda.
     */
    public void hidePlayer(Player player, String nick, String skin) {
        hiddenPlayers.put(player.getUniqueId(), new HiddenData(player.getName(), nick, skin));
        saveData();

        applyHiddenState(player, nick, skin, true);
    }

    /**
     * Solo aplica el estado visual en el juego.
     */
    public void applyHiddenState(Player player, String nick, String skin, boolean applySkin) {
        String formattedNick = ChatColor.translateAlternateColorCodes('&', nick);
        player.setDisplayName(formattedNick);
        player.setPlayerListName(formattedNick);

        if (applySkin) {
            plugin.getSkinManager().setSkin(player, skin);
        }
    }

    /**
     * Restaura la identidad de un jugador de forma permanente.
     */
    public void showPlayer(Player player) {
        if (!hiddenPlayers.containsKey(player.getUniqueId()))
            return;

        HiddenData data = hiddenPlayers.remove(player.getUniqueId());
        saveData();

        if (player.isOnline()) {
            player.setDisplayName(data.getOriginalName());
            player.setPlayerListName(data.getOriginalName());
            plugin.getSkinManager().resetSkin(player);
        }
    }

    public boolean isHidden(Player player) {
        return hiddenPlayers.containsKey(player.getUniqueId());
    }

    public String getOriginalName(Player player) {
        HiddenData data = hiddenPlayers.get(player.getUniqueId());
        return data != null ? data.getOriginalName() : player.getName();
    }

    public String getRealNameByNick(String nick) {
        String strippedTargetNick = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', nick));
        for (HiddenData data : hiddenPlayers.values()) {
            String strippedNick = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', data.getNick()));
            if (strippedNick.equalsIgnoreCase(strippedTargetNick)) {
                return data.getOriginalName();
            }
        }
        return null;
    }
    
    public Map<UUID, HiddenData> getHiddenPlayers() {
        return hiddenPlayers;
    }

    
    public HiddenData getHiddenData(Player player) {
        return hiddenPlayers.get(player.getUniqueId());
    }

    public static class HiddenData {
        private final String originalName;
        private final String nick;
        private final String skin;

        public HiddenData(String originalName, String nick, String skin) {
            this.originalName = originalName;
            this.nick = nick;
            this.skin = skin;
        }

        public String getOriginalName() { return originalName; }
        public String getNick() { return nick; }
        public String getSkin() { return skin; }
    }
}
