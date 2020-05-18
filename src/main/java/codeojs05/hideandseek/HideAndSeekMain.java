package codeojs05.hideandseek;


import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pro.husk.ichat.iChat;
import pro.husk.ichat.obj.PlayerCache;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class HideAndSeekMain extends JavaPlugin {

    @Getter
    private final List<UUID> seekers = new ArrayList<>();

    @Getter
    private final List<UUID> hiders = new ArrayList<>();

    @Getter
    private final List<UUID> exempt = new ArrayList<>();

    @Getter
    @Setter
    private boolean canHiderJoin;

    @Getter
    private boolean gameRunning;

    @Getter
    private static int gameLength;

    @Override
    public void onEnable() {
        getCommand("hideandseek").setExecutor(new HideAndSeekCommands(this));
        getServer().getPluginManager().registerEvents(new HideAndSeekListener(this), this);

        saveDefaultConfig();

        gameLength = getConfig().getInt("GameLength");

        getServer().getScheduler().scheduleSyncRepeatingTask(this, this::updateScoreboards, (10 * 20), (10 * 20));
    }

    public void updateScoreboards() {
        if (gameRunning) {
            // We want to overwrite the updateScoreboard of iChat
            int scoreboardTaskId = iChat.getScoreboardTaskId();
            if (scoreboardTaskId != 0) {
                Bukkit.getScheduler().cancelTask(iChat.getScoreboardTaskId());
            }

            // Update tab list name for each player depending on which team they are in
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                for (UUID uuid : seekers) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) player.setPlayerListName(ChatColor.DARK_RED + player.getDisplayName());
                }

                for (UUID uuid : hiders) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) player.setPlayerListName(ChatColor.GOLD + player.getDisplayName());
                }

                for (UUID uuid : exempt) {
                    Player player = Bukkit.getPlayer(uuid);
                    PlayerCache playerCache = PlayerCache.getFromUUID(uuid);

                    if (playerCache != null && player != null) {
                        boolean afk = playerCache.isAFK();

                        ChatColor prefix = playerCache.getRank().getPrefix();

                        if (afk) {
                            player.setPlayerListName(ChatColor.GRAY + player.getName());
                        } else {
                            player.setPlayerListName(prefix + player.getName());
                        }
                    }
                }
            });
        }
    }

    private void restartRegularTablistTask() {
        int taskId = getServer().getScheduler().scheduleSyncRepeatingTask(iChat.getPlugin(), () -> {
            iChat.getPlugin().updateScoreboards();
        }, (10 * 20), (10 * 20));

        iChat.setScoreboardTaskId(taskId);
    }

    public void setGameRunning(boolean newValue) {
        gameRunning = newValue;

        if (!newValue) {
            restartRegularTablistTask();
        }
    }
}