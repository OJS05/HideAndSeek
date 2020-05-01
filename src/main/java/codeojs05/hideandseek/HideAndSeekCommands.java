package codeojs05.hideandseek;

import codeojs05.hideandseek.HideAndSeekMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class HideAndSeekCommands extends JavaPlugin implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
        ThreadLocalRandom random = ThreadLocalRandom.current();

        if (args.length > 0) {

            if (args[0].equalsIgnoreCase("create")) {

                if (sender.hasPermission("hideandseek.admin")) {

                    Player randomPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));
                    OfflinePlayer[] hiders = Bukkit.getOfflinePlayers();

                    HideAndSeekMain.getSeekers().add(randomPlayer);
                    Bukkit.broadcastMessage(ChatColor.DARK_RED + randomPlayer.getName() + ChatColor.RESET + " is the seeker... Go hide!");

                    HideAndSeekMain.getHiders().addAll(onlinePlayers);
                    HideAndSeekMain.getHiders().remove(randomPlayer);

                }
            }

            if (args[0].equalsIgnoreCase("hint")) {
                if (sender instanceof Player) {

                    Player player = (Player) sender;

                    if (HideAndSeekMain.getSeekers().contains(player)) {


                    }

                    if (HideAndSeekMain.getHiders().contains(player)) {

                        List<Double> seekerDistance = new ArrayList<>();

                        for (Player distance : HideAndSeekMain.getSeekers()) {
                            seekerDistance.add(distance.getLocation().distance(((Player) sender).getLocation()));
                        }

                        sender.sendMessage("The nearest seeker is " + ChatColor.DARK_RED + Collections.min(seekerDistance) + ChatColor.RESET + " blocks away.");

                    }

                }
            }

            if (args[0].equalsIgnoreCase("list")) {
                if (sender instanceof Player) {

                    Player player = (Player) sender;
                    player.sendMessage("Seekers:");

                    for (Player seeker : HideAndSeekMain.getSeekers()) {
                        sender.sendMessage(seeker.getName());
                    }

                    player.sendMessage("Hiders:");

                    for (Player hider : HideAndSeekMain.getHiders()) {
                        sender.sendMessage(hider.getName());
                    }
                }
            }

            if (args.length == 2) {

                if (args[0].equalsIgnoreCase("exempt")) {

                    if (sender.hasPermission("hideandseek.admin")) {

                        Player targetPlayer = Bukkit.getPlayer(args[1]);

                        HideAndSeekMain.getHiders().remove(targetPlayer);
                        HideAndSeekMain.getSeekers().remove(targetPlayer);
                        HideAndSeekMain.getExempt().add(targetPlayer);

                    }
                }
            }
        }
        return true;
    }
}

