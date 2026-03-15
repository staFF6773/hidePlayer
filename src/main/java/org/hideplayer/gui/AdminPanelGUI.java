package org.hideplayer.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.hideplayer.HidePlayer;
import org.hideplayer.manager.PlayerManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class AdminPanelGUI implements Listener {

    private final HidePlayer plugin;
    public AdminPanelGUI(HidePlayer plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private String getBaseTitle() {
        String title = plugin.getSettings().getConfig().getString("admin-panel.title", "&cAdmin Panel - Page {page}");
        return ChatColor.translateAlternateColorCodes('&', title.split("\\{page\\}")[0]);
    }

    private String getFullTitle(int page) {
        String title = plugin.getSettings().getConfig().getString("admin-panel.title", "&cAdmin Panel - Page {page}");
        return ChatColor.translateAlternateColorCodes('&', title.replace("{page}", String.valueOf(page)));
    }

    public void openPanel(Player admin, int page) {
        Map<UUID, PlayerManager.HiddenData> hiddenPlayers = plugin.getPlayerManager().getHiddenPlayers();
        
        int configSize = plugin.getSettings().getConfig().getInt("admin-panel.size", 54);
        int size = Math.max(9, Math.min(54, (configSize / 9) * 9));
        
        String title = getFullTitle(page);
        Inventory inv = Bukkit.createInventory(null, size, title);

        int itemsPerPage = size - 9; // Last row for navigation
        int startIndex = (page - 1) * itemsPerPage;
        
        Object[] keys = hiddenPlayers.keySet().toArray();
        for (int i = 0; i < itemsPerPage; i++) {
            int index = startIndex + i;
            if (index >= keys.length) break;
            
            UUID uuid = (UUID) keys[index];
            PlayerManager.HiddenData data = hiddenPlayers.get(uuid);
            
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta != null) {
                Player target = Bukkit.getPlayer(uuid);
                if (target != null) {
                    meta.setOwningPlayer(target);
                } else {
                    meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
                }
                
                String headName = plugin.getSettings().getConfig().getString("admin-panel.player-head.name", "&a{original_name}");
                headName = headName.replace("{original_name}", data.getOriginalName())
                                   .replace("{nick}", data.getNick())
                                   .replace("{skin}", data.getSkin())
                                   .replace("{uuid}", uuid.toString());
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', headName));
                
                ArrayList<String> lore = new ArrayList<>();
                for (String line : plugin.getSettings().getConfig().getStringList("admin-panel.player-head.lore")) {
                    String loreLine = line.replace("{original_name}", data.getOriginalName())
                                          .replace("{nick}", data.getNick())
                                          .replace("{skin}", data.getSkin())
                                          .replace("{uuid}", uuid.toString());
                    lore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
                }
                meta.setLore(lore);
                
                item.setItemMeta(meta);
            }
            inv.setItem(i, item);
        }

        if (page > 1) {
            String matName = plugin.getSettings().getConfig().getString("admin-panel.prev-page-item.material", "ARROW");
            Material mat = Material.matchMaterial(matName);
            if (mat == null) mat = Material.ARROW;
            
            ItemStack prev = new ItemStack(mat);
            ItemMeta meta = prev.getItemMeta();
            if (meta != null) {
                String name = plugin.getSettings().getConfig().getString("admin-panel.prev-page-item.name", "&cPrevious Page");
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
                
                ArrayList<String> lore = new ArrayList<>();
                for (String line : plugin.getSettings().getConfig().getStringList("admin-panel.prev-page-item.lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(lore);
                
                prev.setItemMeta(meta);
            }
            inv.setItem(size - 6, prev);
        }
        
        if (keys.length > startIndex + itemsPerPage) {
            String matName = plugin.getSettings().getConfig().getString("admin-panel.next-page-item.material", "ARROW");
            Material mat = Material.matchMaterial(matName);
            if (mat == null) mat = Material.ARROW;
            
            ItemStack next = new ItemStack(mat);
            ItemMeta meta = next.getItemMeta();
            if (meta != null) {
                String name = plugin.getSettings().getConfig().getString("admin-panel.next-page-item.name", "&aNext Page");
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
                
                ArrayList<String> lore = new ArrayList<>();
                for (String line : plugin.getSettings().getConfig().getStringList("admin-panel.next-page-item.lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(lore);
                
                next.setItemMeta(meta);
            }
            inv.setItem(size - 4, next);
        }

        admin.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String baseTitle = getBaseTitle();
        if (event.getView().getTitle().startsWith(baseTitle)) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) return;
            if (event.getCurrentItem().getItemMeta() == null) return;
            
            Player player = (Player) event.getWhoClicked();
            String title = event.getView().getTitle();
            try {
                String pageStr = title.replace(baseTitle, "").trim();
                String rightSide = plugin.getSettings().getConfig().getString("admin-panel.title", "&cAdmin Panel - Page {page}").split("\\{page\\}")[1];
                rightSide = ChatColor.translateAlternateColorCodes('&', rightSide);
                
                if (!rightSide.isEmpty()) {
                    pageStr = pageStr.replace(rightSide, "");
                }
                
                int currentPage = Integer.parseInt(pageStr);
                
                String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
                String prevName = ChatColor.translateAlternateColorCodes('&', plugin.getSettings().getConfig().getString("admin-panel.prev-page-item.name", "&cPrevious Page"));
                String nextName = ChatColor.translateAlternateColorCodes('&', plugin.getSettings().getConfig().getString("admin-panel.next-page-item.name", "&aNext Page"));
                
                if (itemName.equals(prevName)) {
                    openPanel(player, Math.max(1, currentPage - 1));
                } else if (itemName.equals(nextName)) {
                    openPanel(player, currentPage + 1);
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {}
        }
    }
}
