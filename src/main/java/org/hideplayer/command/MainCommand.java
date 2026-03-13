package org.hideplayer.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.hideplayer.HidePlayer;

import java.util.ArrayList;
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
            // Aquí puedes mostrar la versión o ayuda
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bHidePlayer &7v" + plugin.getDescription().getVersion()));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Usa &f/hp reload &7para recargar."));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("hideplayer.admin")) {
                plugin.sendMessage(sender, "messages.no-permission");
                return true;
            }

            plugin.getSettings().reload();
            plugin.sendMessage(sender, "messages.plugin-reloaded");
            return true;
        }

        // Si el subcomando no existe
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cComando desconocido. Usa /hp para ayuda."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("reload");
            // Filtramos por lo que el usuario está escribiendo
            return suggestions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
