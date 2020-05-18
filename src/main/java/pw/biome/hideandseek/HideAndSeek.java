package pw.biome.hideandseek;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import pw.biome.hideandseek.commands.HSCommands;
import pw.biome.hideandseek.listener.HSListener;
import pw.biome.hideandseek.util.GameManager;

public final class HideAndSeek extends JavaPlugin {

    @Getter
    private static HideAndSeek instance;

    @Getter
    private GameManager gameManager;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("hideandseek").setExecutor(new HSCommands());
        getServer().getPluginManager().registerEvents(new HSListener(this), this);

        saveDefaultConfig();

        gameManager = new GameManager();

        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> gameManager.updateScoreboards(), (10 * 20), (10 * 20));
    }
}
