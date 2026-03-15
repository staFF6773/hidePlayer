package org.hideplayer;

import org.hideplayer.command.MainCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.hideplayer.config.ConfigManager;
import org.hideplayer.manager.PlayerManager;
import org.hideplayer.manager.SkinManager;
import org.hideplayer.util.LoggerUtils;

/**
 * Clase principal del plugin HidePlayer.
 * Sigue el patrón Singleton y maneja el ciclo de vida del plugin.
 */
public final class HidePlayer extends JavaPlugin {

    private static HidePlayer instance;
    private ConfigManager settings;
    private ConfigManager langConfig;
    private SkinManager skinManager;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        // Asignar instancia Singleton
        instance = this;

        LoggerUtils.info("Starting HidePlayer...");

        try {
            // Inicializar Configuración
            setupConfig();

            // Inicializar Managers
            this.skinManager = new SkinManager();
            this.playerManager = new PlayerManager(this);

            // Registrar Comandos
            MainCommand mainCmd = new MainCommand(this);
            getCommand("hideplayer").setExecutor(mainCmd);
            getCommand("hideplayer").setTabCompleter(mainCmd);

            // Registrar Eventos
            getServer().getPluginManager().registerEvents(new org.hideplayer.listener.ChatListener(this), this);
            getServer().getPluginManager().registerEvents(new org.hideplayer.listener.ConnectionListener(this), this);

            if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new org.hideplayer.hook.HidePlayerExpansion(this).register();
                LoggerUtils.info("PlaceholderAPI support enabled.");
            }

            LoggerUtils.info("Plugin enabled successfully.");
        } catch (Exception e) {
            LoggerUtils.logException("Initial plugin loading", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        LoggerUtils.info("Disabling HidePlayer...");
        instance = null;
    }

    private String getLanguageFileName() {
        String lang = settings.getConfig().getString("language", "en");
        if (!lang.endsWith(".yml")) {
            lang += ".yml";
        }
        if (lang.contains("/")) {
            return lang;
        }
        return "langs/" + lang;
    }

    private void setupConfig() {
        settings = new ConfigManager(this, "config.yml");
        
        // Ensure that default languages are extracted/created
        new ConfigManager(this, "langs/en.yml");
        new ConfigManager(this, "langs/es.yml");
        
        langConfig = new ConfigManager(this, getLanguageFileName());
    }

    public void reloadConfigs() {
        settings.reload();
        langConfig = new ConfigManager(this, getLanguageFileName());
    }

    /**
     * Obtiene la instancia principal del plugin.
     * 
     * @return HidePlayer instance
     */
    public static HidePlayer getInstance() {
        return instance;
    }

    /**
     * Obtiene el manejador de configuración.
     * 
     * @return ConfigManager
     */
    public ConfigManager getSettings() {
        return settings;
    }

    public ConfigManager getLangConfig() {
        return langConfig;
    }

    public SkinManager getSkinManager() {
        return skinManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    /**
     * Envía un mensaje formateado a un receptor (jugador o consola).
     * Reemplaza el placeholder %prefix% por el prefijo de la config.
     *
     * @param sender El receptor del mensaje
     * @param path   La ruta del mensaje en la configuración
     */
    public void sendMessage(org.bukkit.command.CommandSender sender, String path) {
        String message = langConfig.getConfig().getString(path);
        if (message == null || message.isEmpty())
            return;

        String prefix = settings.getConfig().getString("prefix", "");
        message = message.replace("%prefix%", prefix);

        // Si PAPI está habilitado y el sender es un jugador, parsear las variables
        if (sender instanceof org.bukkit.entity.Player && getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((org.bukkit.entity.Player) sender, message);
        }

        sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
    }
}
