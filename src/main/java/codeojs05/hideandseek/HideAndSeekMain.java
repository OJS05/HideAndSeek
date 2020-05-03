package codeojs05.hideandseek;


import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class HideAndSeekMain extends JavaPlugin {

    @Getter
    private static final List<Player> seekers = new ArrayList<>();

    @Getter
    private static final List<Player> hiders = new ArrayList<>();

    @Getter
    private static final List<Player> exempt = new ArrayList<>();

    @Getter
    private static HideAndSeekMain instance;

    @Getter
    @Setter
    private static boolean canHiderJoin;

    @Getter
    @Setter
    private static boolean gameRunning;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("hideandseek").setExecutor(new HideAndSeekCommands());

        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

}