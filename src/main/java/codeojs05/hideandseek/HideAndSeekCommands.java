package codeojs05.hideandseek;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class HideAndSeekCommands implements CommandExecutor {

    private final List<UUID> cooldown = new ArrayList<>();

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final HideAndSeekMain plugin;

    public HideAndSeekCommands(HideAndSeekMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("create")) {
                if (sender.hasPermission("hideandseek.admin")) {
                    Player randomPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));

                    plugin.getSeekers().add(randomPlayer.getUniqueId());
                    Bukkit.broadcastMessage(ChatColor.DARK_RED + randomPlayer.getDisplayName() + ChatColor.RESET + " is the seeker... Go hide!");

                    onlinePlayers.forEach(player -> plugin.getHiders().add(player.getUniqueId()));

                    plugin.getHiders().remove(randomPlayer.getUniqueId());

                    plugin.setCanHiderJoin(true);
                    plugin.setGameRunning(true);

                    Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.setCanHiderJoin(false), (HideAndSeekMain.getGameLength() / 4) * 60 * 60 * 20);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.setGameRunning(false), HideAndSeekMain.getGameLength() * 60 * 60 * 20);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (plugin.isGameRunning()) {
                            if (plugin.getHiders().size() > 0) {

                                for (UUID uuid : plugin.getSeekers()) {
                                    Player seekers = Bukkit.getPlayer(uuid);
                                    if (seekers != null) {
                                        seekers.sendMessage(ChatColor.DARK_RED + "The Hiders have won!");
                                    }
                                }

                                for (UUID uuid : plugin.getHiders()) {
                                    Player hiders = Bukkit.getPlayer(uuid);
                                    if (hiders != null) {
                                        hiders.sendMessage(ChatColor.GOLD + "The Hiders have won");
                                    }
                                }

                                plugin.setGameRunning(false);
                            }
                        }
                    }, (HideAndSeekMain.getGameLength() * 60 * 60 * 20) - 1);
                }
            }

            if (args[0].equalsIgnoreCase("hint")) {
                if (plugin.isGameRunning()) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (plugin.getSeekers().contains(player.getUniqueId())) {
                            if (!cooldown.contains(player.getUniqueId())) {
                                Player randomHider = Bukkit.getPlayer(plugin.getHiders().get(random.nextInt(plugin.getHiders().size())));
                                if (randomHider == null) return true;

                                sender.sendMessage("A random hider is at" + randomHider.getLocation().getBlockX() + randomHider.getLocation().getBlockZ() + ".");

                                cooldown.add(player.getUniqueId());

                                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> cooldown.remove(player.getUniqueId()), (plugin.getSeekers().size() * 180 * 20));
                            } else {
                                sender.sendMessage(ChatColor.RED + "You are still on a cooldown!");
                            }
                        }

                        if (plugin.getHiders().contains(player.getUniqueId())) {
                            if (!cooldown.contains(player.getUniqueId())) {
                                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                    List<Double> seekerDistance = new ArrayList<>();

                                    for (UUID uuid : plugin.getSeekers()) {
                                        Player distance = Bukkit.getPlayer(uuid);
                                        if (distance != null) {
                                            seekerDistance.add(distance.getLocation().distance(((Player) sender).getLocation()));
                                        }
                                    }

                                    sender.sendMessage("The nearest seeker is " + ChatColor.DARK_RED + Collections.min(seekerDistance) + ChatColor.RESET + " blocks away.");

                                    cooldown.add(player.getUniqueId());

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> cooldown.remove(player.getUniqueId()), (plugin.getHiders().size() * 120 * 20));
                                });
                            } else {
                                sender.sendMessage(ChatColor.RED + "You are still on a cooldown!");
                            }
                        }
                    }
                }
            }

            if (args[0].equalsIgnoreCase("list")) {
                if (plugin.isGameRunning()) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        player.sendMessage("Seekers:");

                        for (UUID uuid : plugin.getSeekers()) {
                            Player seeker = Bukkit.getPlayer(uuid);
                            if (seeker != null) sender.sendMessage(seeker.getDisplayName());
                        }

                        player.sendMessage("Hiders:");

                        for (UUID uuid : plugin.getHiders()) {
                            Player hider = Bukkit.getPlayer(uuid);
                            if (hider != null) sender.sendMessage(hider.getDisplayName());
                        }
                    }
                }
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("exempt")) {
                    if (plugin.isGameRunning()) {
                        if (sender.hasPermission("hideandseek.admin")) {
                            Player targetPlayer = Bukkit.getPlayer(args[1]);

                            if (targetPlayer != null) {
                                plugin.getHiders().remove(targetPlayer.getUniqueId());
                                plugin.getSeekers().remove(targetPlayer.getUniqueId());
                                plugin.getExempt().add(targetPlayer.getUniqueId());
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}