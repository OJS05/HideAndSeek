package codeojs05.hideandseek;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class HideAndSeekMain extends JavaPlugin {

    private static final List<Player> seekerList = new ArrayList<>();

    public static List<Player> getSeekers() {
        return seekerList;
    }

    private static final List<Player> hiderList = new ArrayList<>();

    public static List<Player> getHiders() {
        return hiderList;
    }

    private static final List<Player> exemptList = new ArrayList<>();

    public static List<Player> getExempt() { return exemptList; }

    private static HideAndSeek instance;

    public static HideAndSeek getInstance() { return instance; }



    @Override
    public void onEnable() {

        getCommand("hideandseek").setExecutor(new HideAndSeekCommands());

    }


}
