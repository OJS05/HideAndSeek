package pw.biome.hideandseek.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pro.husk.ichat.obj.PlayerCache;
import pw.biome.hideandseek.HideAndSeek;
import pw.biome.hideandseek.objects.HSPlayer;
import pw.biome.hideandseek.util.TeamType;

import java.util.UUID;

public class HideAndSeekListener implements Listener {

    @EventHandler
    public void playerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        PlayerCache playerCache = PlayerCache.getFromUUID(uuid);

        if (playerCache != null) {
            String name = playerCache.getDisplayName();

            HSPlayer hsPlayer = HSPlayer.getOrCreate(uuid, name);
            if (hsPlayer.getCurrentTeam() == null) {
                if (HideAndSeek.getInstance().getGameManager().isCanHiderJoin()) {
                    hsPlayer.setCurrentTeam(HideAndSeek.getInstance().getGameManager().getHiders());
                }
            }
        }
    }

    @EventHandler
    public void onTag(EntityDamageByEntityEvent event) {
        if (!HideAndSeek.getInstance().getGameManager().isGameRunning()) return;

        Entity victimEntity = event.getEntity();
        Entity damagerEntity = event.getDamager();

        if (victimEntity instanceof Player && damagerEntity instanceof Player) {
            Player victim = (Player) victimEntity;
            Player damager = (Player) damagerEntity;

            HSPlayer victimHsPlayer = HSPlayer.getExact(victim.getUniqueId());
            HSPlayer damagerHsPlayer = HSPlayer.getExact(damager.getUniqueId());

            if (damagerHsPlayer.getCurrentTeam().getTeamType() == TeamType.SEEKER &&
                    victimHsPlayer.getCurrentTeam().getTeamType() == TeamType.HIDER) {
                victimHsPlayer.setCurrentTeam(damagerHsPlayer.getCurrentTeam());
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (!HideAndSeek.getInstance().getGameManager().isGameRunning()) return;

        HSPlayer hsPlayer = HSPlayer.getExact(event.getPlayer().getUniqueId());
        hsPlayer.startLeaveTask();
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        if (!HideAndSeek.getInstance().getGameManager().isGameRunning()) return;

        HSPlayer hsPlayer = HSPlayer.getExact(event.getUniqueId());
        hsPlayer.cancelLeaveTask();
    }
}
