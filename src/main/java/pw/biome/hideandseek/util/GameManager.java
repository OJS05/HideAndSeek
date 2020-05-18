package pw.biome.hideandseek.util;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pro.husk.ichat.iChat;
import pro.husk.ichat.obj.PlayerCache;
import pw.biome.hideandseek.HideAndSeek;
import pw.biome.hideandseek.objects.HSPlayer;
import pw.biome.hideandseek.objects.HSTeam;

import java.util.concurrent.ThreadLocalRandom;

public class GameManager {

    @Getter
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    @Getter
    private HSTeam hiders;

    @Getter
    private HSTeam seekers;

    @Getter
    @Setter
    private boolean canHiderJoin;

    @Getter
    private boolean gameRunning;

    @Getter
    private int gameLength;

    public void setupGame() {
        hiders = new HSTeam("Hiders", TeamType.HIDER);
        seekers = new HSTeam("Seekers", TeamType.SEEKER);

        gameLength = HideAndSeek.getInstance().getConfig().getInt("game-length");
    }

    public void createGame() {
        if (gameRunning) return;
        ImmutableList<Player> onlinePlayers = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
        Player randomPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));
        HSPlayer randomHsPlayer = HSPlayer.getExact(randomPlayer.getUniqueId());

        randomHsPlayer.setCurrentTeam(seekers);

        onlinePlayers.forEach(player -> {
            HSPlayer hsPlayer = HSPlayer.getExact(player.getUniqueId());
            if (hsPlayer.getCurrentTeam() == null) hsPlayer.setCurrentTeam(hiders);
        });

        canHiderJoin = true;
        gameRunning = true;

        Bukkit.getScheduler().runTaskLater(HideAndSeek.getInstance(), () -> canHiderJoin = false, (gameLength / 4) * 60 * 60 * 20);
        Bukkit.getScheduler().runTaskLater(HideAndSeek.getInstance(), () -> gameRunning = false, gameLength * 60 * 60 * 20);
        Bukkit.getScheduler().runTaskLater(HideAndSeek.getInstance(), this::calculateWinner, (gameLength * 60 * 60 * 20) - 1);
    }

    public void setGameRunning(boolean newValue) {
        gameRunning = newValue;
        if (!newValue) {
            iChat.getPlugin().restartScoreboardTask();
        }
    }

    public void calculateWinner() {
        int hiderSize = hiders.getMembers().size();

        if (hiderSize == 0) {
            // Process win ?
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "The seekers have won!");

            // Remove the data from the old game
            hiders.getMembers().clear();
            seekers.getMembers().clear();

            setGameRunning(false);
        }
    }

    public void updateScoreboards() {
        if (gameRunning) {
            // We want to overwrite the updateScoreboard of iChat
            iChat.getPlugin().stopScoreboardTask();

            Bukkit.getScheduler().runTaskAsynchronously(HideAndSeek.getInstance(), () -> {
                ImmutableList<Player> onlinePlayers = ImmutableList.copyOf(Bukkit.getServer().getOnlinePlayers());
                onlinePlayers.forEach(player -> {
                    HSPlayer hsPlayer = HSPlayer.getExact(player.getUniqueId());
                    PlayerCache playerCache = PlayerCache.getFromUUID(player.getUniqueId());

                    ChatColor prefix = playerCache.getRank().getPrefix();

                    boolean afk = playerCache.isAFK(); // might not be async safe....

                    if (afk) {
                        prefix = ChatColor.GRAY;
                    }

                    switch (hsPlayer.getCurrentTeam().getTeamType()) {
                        case SEEKER:
                            player.setPlayerListName(ChatColor.DARK_RED + player.getDisplayName());
                        case HIDER:
                            player.setPlayerListName(ChatColor.GOLD + player.getDisplayName());
                        default:
                            player.setPlayerListName(prefix + player.getDisplayName());
                    }
                });
            });
        }
    }
}
