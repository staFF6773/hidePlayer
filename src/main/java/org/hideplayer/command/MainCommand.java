package org.hideplayer.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.hideplayer.HidePlayer;
import org.hideplayer.util.SoundUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Comando principal /hp o /hideplayer que maneja subcomandos.
 */
public class MainCommand implements CommandExecutor, TabCompleter {

    private final HidePlayer plugin;

    public MainCommand(HidePlayer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Here you can change the version or help
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bHidePlayer &7v" + plugin.getDescription().getVersion()));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Use &f/hp reload &7to reload."));
            return true;
        }

        if (args[0].equalsIgnoreCase("hide")) {
            if (!(sender instanceof Player)) {
                plugin.sendMessage(sender, "messages.player-only");
                return true;
            }

            Player player = (Player) sender;

            if (!sender.hasPermission("hideplayer.hide")) {
                plugin.sendMessage(sender, "messages.no-permission");
                SoundUtils.playSound(player, "sounds.error");
                return true;
            }

            if (args.length < 3) {
                plugin.sendMessage(sender, "messages.usage-hide");
                SoundUtils.playSound(player, "sounds.error");
                return true;
            }

            String nick = args[1];
            String skin = args[2];

            String strippedNick = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', nick));
            if (!strippedNick.matches("^[a-zA-Z0-9_]{3,16}$") || !skin.matches("^[a-zA-Z0-9_]{3,16}$")) {
                plugin.sendMessage(sender, "messages.invalid-name");
                SoundUtils.playSound(player, "sounds.error");
                return true;
            }

            plugin.getPlayerManager().hidePlayer(player, nick, skin);

            String msg = plugin.getLangConfig().getConfig().getString("messages.hidden-success");
            if (msg != null) {
                msg = msg.replace("%nick%", nick);
                String prefix = plugin.getSettings().getConfig().getString("prefix", "");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg.replace("%prefix%", prefix)));
            }
            SoundUtils.playSound(player, "sounds.hide");
            return true;
        }

        if (args[0].equalsIgnoreCase("show")) {
            if (!(sender instanceof Player)) {
                plugin.sendMessage(sender, "messages.player-only");
                return true;
            }

            Player player = (Player) sender;

            if (!sender.hasPermission("hideplayer.show")) {
                plugin.sendMessage(sender, "messages.no-permission");
                SoundUtils.playSound(player, "sounds.error");
                return true;
            }

            plugin.getPlayerManager().showPlayer(player);
            plugin.sendMessage(player, "messages.shown-success");
            SoundUtils.playSound(player, "sounds.show");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("hideplayer.admin")) {
                plugin.sendMessage(sender, "messages.no-permission");
                if (sender instanceof Player) {
                    SoundUtils.playSound((Player) sender, "sounds.error");
                }
                return true;
            }

            plugin.reloadConfigs();
            plugin.sendMessage(sender, "messages.plugin-reloaded");
            return true;
        }

        if (args[0].equalsIgnoreCase("panel") || args[0].equalsIgnoreCase("admin")) {
            if (!(sender instanceof Player)) {
                plugin.sendMessage(sender, "messages.player-only");
                return true;
            }

            if (!sender.hasPermission("hideplayer.admin")) {
                plugin.sendMessage(sender, "messages.no-permission");
                SoundUtils.playSound((Player) sender, "sounds.error");
                return true;
            }

            plugin.getAdminPanelGUI().openPanel((Player) sender, 1);
            return true;
        }

        // If the subcommand does not exist
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUnknown command. Use /hp for help."));
        if (sender instanceof Player) {
            SoundUtils.playSound((Player) sender, "sounds.error");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("reload");
            suggestions.add("hide");
            suggestions.add("show");
            suggestions.add("panel");
            suggestions.add("admin");
            // Filtramos por lo que el usuario está escribiendo
            return suggestions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("hide")) {
            return Collections.singletonList("<nickname>");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("hide")) {
            return Collections.singletonList("<skin_name>");
        }

        return new ArrayList<>();
    }
}
