package org.hideplayer.manager;

import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.InputDataResult;
import org.bukkit.Bukkit;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.hideplayer.util.LoggerUtils;

/**
 * Maneja la integración con SkinsRestorer v15+.
 */
public class SkinManager {

    private SkinsRestorer skinsRestorer;
    private final boolean enabled;

    public SkinManager() {
        boolean isEnabled = false;
        if (Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")) {
            try {
                // En v15, el método es .get()
                this.skinsRestorer = SkinsRestorerProvider.get();
                isEnabled = true;
                LoggerUtils.info("Integración con SkinsRestorer habilitada.");
            } catch (Exception e) {
                LoggerUtils.logException("Error al obtener la API de SkinsRestorer", e);
            }
        } else {
            LoggerUtils.warn("SkinsRestorer no encontrado. El cambio de skins no funcionará.");
        }
        this.enabled = isEnabled;
    }

    /**
     * Cambia la skin de un jugador.
     * 
     * @param player   El jugador
     * @param skinName El nombre de la skin a aplicar
     * @return true si se aplicó, false si falló
     */
    public void setSkin(Player player, String skinName) {
        if (!enabled || skinsRestorer == null)
            return;

        Bukkit.getScheduler().runTaskAsynchronously(org.hideplayer.HidePlayer.getInstance(), () -> {
            try {
                // En v15+, buscamos la skin por nombre (jugador premium o skin guardada)
                Optional<InputDataResult> result = skinsRestorer.getSkinStorage().findOrCreateSkinData(skinName);

                if (result.isPresent()) {
                    skinsRestorer.getPlayerStorage().setSkinIdOfPlayer(player.getUniqueId(), result.get().getIdentifier());
                    skinsRestorer.getSkinApplier(Player.class).applySkin(player);
                } else {
                    org.hideplayer.HidePlayer.getInstance().sendMessage(player, "messages.skin-error");
                }
            } catch (Exception e) {
                LoggerUtils.logException("Error al cambiar skin a " + player.getName(), e);
            }
        });
    }

    /**
     * Remueve la skin personalizada del jugador.
     * 
     * @param player El jugador
     */
    public void resetSkin(Player player) {
        if (!enabled || skinsRestorer == null)
            return;
            
        Bukkit.getScheduler().runTaskAsynchronously(org.hideplayer.HidePlayer.getInstance(), () -> {
            try {
                // Aplicar la skin con su propio UUID para resetear
                skinsRestorer.getPlayerStorage().removeSkinIdOfPlayer(player.getUniqueId());
                skinsRestorer.getSkinApplier(Player.class).applySkin(player);
            } catch (Exception e) {
                LoggerUtils.logException("Error al resetear skin a " + player.getName(), e);
            }
        });
    }

    public boolean isEnabled() {
        return enabled;
    }
}
