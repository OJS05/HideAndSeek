package pw.biome.hideandseek.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import pw.biome.hideandseek.HideAndSeek;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HSPlayer {

    @Getter
    private static final ConcurrentHashMap<UUID, HSPlayer> hsPlayerMap = new ConcurrentHashMap<>();

    @Getter
    private final UUID uuid;

    @Getter
    private final String name;

    @Getter
    private HSTeam currentTeam;

    @Getter
    @Setter
    private boolean exempt;

    @Getter
    private int leaveTaskId;

    public HSPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public void setCurrentTeam(HSTeam newTeam) {
        if (currentTeam != null) currentTeam.removePlayer(this);

        newTeam.addPlayer(this);

        this.currentTeam = newTeam;

        Bukkit.broadcastMessage(ChatColor.AQUA + name + " is now a " + ChatColor.GOLD + newTeam.getName());

        // Calculate winner after every team change
        HideAndSeek.getInstance().getGameManager().calculateWinner();
    }

    public static HSPlayer getOrCreate(UUID uuid, String displayName) {
        if (hsPlayerMap.contains(uuid)) {
            return hsPlayerMap.get(uuid);
        }
        return new HSPlayer(uuid, displayName);
    }

    public static HSPlayer getExact(UUID uuid) {
        return hsPlayerMap.get(uuid);
    }

    public void startLeaveTask() {
        leaveTaskId = Bukkit.getScheduler().runTaskLater(HideAndSeek.getInstance(), () -> {
            setCurrentTeam(HideAndSeek.getInstance().getGameManager().getSeekers()); // Add to seeker if they have been logged out for >10min
        }, 10 * (60 * 20)).getTaskId();
    }

    public void cancelLeaveTask() {
        if (leaveTaskId != 0) {
            Bukkit.getScheduler().cancelTask(leaveTaskId);
        }
    }
}
