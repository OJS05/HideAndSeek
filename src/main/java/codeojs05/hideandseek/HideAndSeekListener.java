package codeojs05.hideandseek;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class HideAndSeekListener implements Listener {

    @EventHandler
    public void onTag(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {

            Player damager = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            if (HideAndSeekMain.getSeekers().contains(damager)) {

                if (HideAndSeekMain.getHiders().contains(victim)) {

                    if ((damager.getInventory().getItemInMainHand().getType().equals(Material.AIR)) || (damager.getInventory().getItemInOffHand().getType().equals(Material.AIR))) {

                        HideAndSeekMain.getHiders().remove(victim);
                        HideAndSeekMain.getSeekers().add(victim);

                        for (Player seekers : HideAndSeekMain.getSeekers()) {
                            seekers.sendMessage(ChatColor.DARK_RED + victim.getDisplayName() + ChatColor.RESET + " has joined the" + ChatColor.DARK_RED + " SEEKER " + ChatColor.RESET + "team!");
                        }
                        for (Player hiders : HideAndSeekMain.getHiders()) {
                            hiders.sendMessage(ChatColor.DARK_RED + victim.getDisplayName() + ChatColor.RESET + " has joined the" + ChatColor.DARK_RED + " SEEKER " + ChatColor.RESET + "team!");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (HideAndSeekMain.getSeekers().contains(event.getPlayer())) return;

        if (HideAndSeekMain.getHiders().contains(event.getPlayer())) return;

        if (HideAndSeekMain.getExempt().contains(event.getPlayer())) return;

        HideAndSeekMain.getHiders().add(event.getPlayer());

    }
}
