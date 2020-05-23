package pw.biome.hideandseek.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pw.biome.hideandseek.HideAndSeek;
import pw.biome.hideandseek.objects.HSPlayer;
import pw.biome.hideandseek.util.TeamType;

public class HideAndSeekListener implements Listener {

    @EventHandler
    public void playerPreLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        HSPlayer hsPlayer = HSPlayer.getOrCreate(player.getUniqueId(), player.getDisplayName());

        if (HideAndSeek.getInstance().getGameManager().isGameRunning()) {
            if (!hsPlayer.isExempt() && hsPlayer.getCurrentTeam() == null) {
                if (HideAndSeek.getInstance().getGameManager().isCanHiderJoin()) {
                    hsPlayer.setCurrentTeam(HideAndSeek.getInstance().getGameManager().getHiders(), true);
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
                victimHsPlayer.setCurrentTeam(damagerHsPlayer.getCurrentTeam(), true);
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (!HideAndSeek.getInstance().getGameManager().isGameRunning()) return;

        HSPlayer hsPlayer = HSPlayer.getExact(event.getPlayer().getUniqueId());
        if (!hsPlayer.isExempt()) hsPlayer.startLeaveTask();
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        if (!HideAndSeek.getInstance().getGameManager().isGameRunning()) return;

        HSPlayer hsPlayer = HSPlayer.getExact(event.getUniqueId());
        if (!hsPlayer.isExempt()) hsPlayer.cancelLeaveTask();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        HSPlayer hsPlayer = HSPlayer.getExact(player.getUniqueId());

        if (hsPlayer.getCurrentTeam().getTeamType() == TeamType.HIDER) {
            player.sendMessage(ChatColor.RED + "You can't break blocks as a hider");
            event.setCancelled(true);
        }
    }
}
