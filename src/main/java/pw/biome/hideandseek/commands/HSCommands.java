package pw.biome.hideandseek.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.biome.hideandseek.HideAndSeek;
import pw.biome.hideandseek.objects.HSPlayer;
import pw.biome.hideandseek.util.GameManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HSCommands implements CommandExecutor {

    private final List<UUID> cooldown = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        GameManager gameManager = HideAndSeek.getInstance().getGameManager();

        if (args.length == 0) {
            // Show list of all available commands here
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("create")) {
                if (sender.hasPermission("hideandseek.admin")) {
                    gameManager.createGame();
                }
            }

            if (args[0].equalsIgnoreCase("hint")) {
                if (gameManager.isGameRunning()) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        HSPlayer hsPlayer = HSPlayer.getExact(player.getUniqueId());
                        if (gameManager.getSeekers().getMembers().contains(hsPlayer)) {
                            if (!cooldown.contains(player.getUniqueId())) {
                                Bukkit.getScheduler().runTaskAsynchronously(HideAndSeek.getInstance(), () -> {
                                    int randomIndex = gameManager.getRandom().nextInt(gameManager.getHiders().getMembers().size());
                                    HSPlayer randomHsPlayerHider = gameManager.getHiders().getMembers().get(randomIndex);

                                    // There's a condition here when
                                /*
                                Hider player leaves -> their task wont run for 10 minutes to set them as seeker
                                If a seeker runs hint and the random lands on ^ that player
                                This will be null, which will return true and the user will not be sent a hint (they will not be given cooldown tho)
                                what we should do instead is loop and ensure that we did get an online hider
                                You can work this out ;)
                                or ignore, up to you
                                 */

                                    Player randomHider = Bukkit.getPlayer(randomHsPlayerHider.getUuid());

                                    if (randomHider != null) {
                                        sender.sendMessage(ChatColor.AQUA + "A random hider is at" + randomHider.getLocation().getBlockX() + ", "
                                                + randomHider.getLocation().getBlockZ() + ".");

                                        cooldown.add(player.getUniqueId());

                                        Bukkit.getServer().getScheduler().runTaskLater(HideAndSeek.getInstance(), () ->
                                                cooldown.remove(player.getUniqueId()), (gameManager.getSeekers().getMembers().size() * 180 * 20));
                                    }
                                });
                            } else {
                                sender.sendMessage(ChatColor.RED + "You are still on a cooldown!");
                            }
                        }

                        if (gameManager.getHiders().getMembers().contains(hsPlayer)) {
                            if (!cooldown.contains(player.getUniqueId())) {
                                Bukkit.getScheduler().runTaskAsynchronously(HideAndSeek.getInstance(), () -> {

                                    // There's a condition here when
                                /*
                                Hider player leaves -> their task after 10 mins sets them as SEEKER but they arent online
                                If a HIDER runs hint and the random lands on ^ that player
                                This will be null, which wont do anything and the user will not be sent a hint (they will not be given cooldown tho)
                                what we should do instead is loop and ensure that we did get an online hider
                                You can work this out ;)
                                or ignore, up to you
                                 */

                                    double distMin = 1000000000;
                                    for (HSPlayer seekers : gameManager.getSeekers().getMembers()) {
                                        Player seekerPlayer = Bukkit.getPlayer(seekers.getUuid());

                                        if (seekerPlayer != null) {
                                            double dist = player.getLocation().distance(seekerPlayer.getLocation());

                                            if (dist < distMin) {
                                                distMin = dist;
                                            }
                                        }
                                    }

                                    sender.sendMessage(ChatColor.AQUA + "The nearest seeker is " + ChatColor.DARK_RED + Math.round(distMin) + ChatColor.AQUA + " blocks away.");

                                    cooldown.add(player.getUniqueId());

                                    Bukkit.getServer().getScheduler().runTaskLater(HideAndSeek.getInstance(), () ->
                                            cooldown.remove(player.getUniqueId()), (gameManager.getHiders().getMembers().size() * 120 * 20));
                                });
                            } else {
                                sender.sendMessage(ChatColor.RED + "You are still on a cooldown!");
                            }
                        }
                    }
                }
            }

            if (args[0].equalsIgnoreCase("list")) {
                if (gameManager.isGameRunning()) {

                    sender.sendMessage(ChatColor.DARK_RED + "Seekers:");
                    gameManager.getSeekers().getMembers().forEach(hsPlayer ->
                            sender.sendMessage(ChatColor.RED + "- " + hsPlayer.getName()));

                    sender.sendMessage(ChatColor.GOLD + "Hiders:");
                    gameManager.getHiders().getMembers().forEach(hsPlayer ->
                            sender.sendMessage(ChatColor.GOLD + "- " + hsPlayer.getName()));
                }
            }

            if (args[0].equalsIgnoreCase("rebuild")) {
                if (sender.hasPermission("hideandseek.admin")) {
                    HSPlayer.getHsPlayerMap().clear();
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        HSPlayer hsPlayer = HSPlayer.getOrCreate(player.getUniqueId(), player.getDisplayName());
                    });
                    sender.sendMessage(ChatColor.GREEN + "Cache rebuilt");
                }
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("exempt")) {
                    if (sender.hasPermission("hideandseek.admin")) {
                        if (gameManager.isGameRunning()) {
                            Player targetPlayer = Bukkit.getPlayer(args[1]);

                            if (targetPlayer != null) {
                                HSPlayer hsPlayer = HSPlayer.getExact(targetPlayer.getUniqueId());
                                hsPlayer.setCurrentTeam(null, true);
                                hsPlayer.setExempt(true);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}