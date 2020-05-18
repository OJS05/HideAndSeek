package pw.biome.hideandseek.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import pw.biome.hideandseek.HideAndSeek;

import java.util.UUID;

public class HSListener implements Listener {

    private final HideAndSeek plugin;

    public HSListener(HideAndSeek plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTag(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {

            Player damager = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            if (plugin.getSeekers().contains(damager.getUniqueId())) {
                if (plugin.getHiders().contains(victim.getUniqueId())) {
                    if ((damager.getInventory().getItemInMainHand().getType().equals(Material.AIR))
                            || (damager.getInventory().getItemInOffHand().getType().equals(Material.AIR))) {

                        plugin.getHiders().remove(victim.getUniqueId());
                        plugin.getSeekers().add(victim.getUniqueId());

                        for (UUID seeker : plugin.getSeekers()) {
                            Player seekers = Bukkit.getPlayer(seeker);
                            if (seekers != null) {
                                seekers.sendMessage(ChatColor.DARK_RED + victim.getDisplayName() + ChatColor.RESET + " has joined the" + ChatColor.DARK_RED + " SEEKER " + ChatColor.RESET + "team!");
                            }
                        }

                        for (UUID hider : plugin.getHiders()) {
                            Player hiders = Bukkit.getPlayer(hider);
                            if (hiders != null) {
                                hiders.sendMessage(ChatColor.DARK_RED + victim.getDisplayName() + ChatColor.RESET + " has joined the" + ChatColor.DARK_RED + " SEEKER " + ChatColor.RESET + "team!");
                            }
                        }

                        if (plugin.getHiders().size() == 0) {
                            plugin.setGameRunning(false);
                            for (UUID seeker : plugin.getSeekers()) {
                                Player seekers = Bukkit.getPlayer(seeker);
                                if (seekers != null) {
                                    seekers.sendMessage(ChatColor.DARK_RED + "The seekers have won!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (plugin.isCanHiderJoin()) {
            if (plugin.getSeekers().contains(uuid)) return;
            if (plugin.getHiders().contains(uuid)) return;
            if (plugin.getExempt().contains(uuid)) return;

            plugin.getHiders().add(uuid);
        }
    }
}
