package codeojs05.hideandseek;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
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

    public static List<Player> getExempt() {
        return exemptList;
    }

    private static HideAndSeekMain instance;

    public static HideAndSeekMain getInstance() {
        return instance;
    }

    @Getter
    @Setter
    private static boolean canHiderJoin = false;

    private static int gameLength = 4;

    public static int getGameLength() {
        return gameLength;
    }

    @Override
    public void onEnable() {
        instance = this;

        getCommand("hideandseek").setExecutor(new HideAndSeekCommands());

    }


}
