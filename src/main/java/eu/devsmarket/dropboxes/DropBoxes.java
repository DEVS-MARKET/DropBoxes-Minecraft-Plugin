package eu.devsmarket.dropboxes;

import eu.devsmarket.dropboxes.commands.CommandManager;
import eu.devsmarket.dropboxes.commands.Commands;
import eu.devsmarket.dropboxes.commands.impl.DropBoxCommand;
import eu.devsmarket.dropboxes.listner.BlockDestroyListener;
import eu.devsmarket.dropboxes.listner.InteractionListener;
import eu.devsmarket.dropboxes.logic.LootBoxesLogic;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class DropBoxes extends JavaPlugin {
    public static LootBoxesLogic LOOTBOXESLOGIC;
    public static FileConfiguration CONFIG;
    public static int TASKID = 0;
    private final CommandManager commandManager = CommandManager.getInstance();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        CONFIG = getConfig();
        LOOTBOXESLOGIC = new LootBoxesLogic(getServer().getWorld(CONFIG.getString("world_name")),
                getConfig().getInt("world_minX"),
                getConfig().getInt("world_maxX"),
                getConfig().getInt("world_minZ"),
                getConfig().getInt("world_maxZ"));
        registerEvents(
                new BlockDestroyListener(),
                new InteractionListener()
        );
        registerCommands(
                new DropBoxCommand()
        );
        getLogger().info("DropBoxes Plugin - by 0WhiteDev [devs.market] has been enabled");
        TASKID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, LOOTBOXESLOGIC::dropLootBox, 0L, 20L * 60 * CONFIG.getInt("drop_per_min"));
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, LOOTBOXESLOGIC::removeAllTookLootBoxes, 0L, 20L * 10);
    }

    @Override
    public void onDisable() {
        getLogger().info("DropBoxes Plugin - by 0WhiteDev [devs.market] has been disabled");
    }

    private void registerEvents(Listener... listeners){
        Arrays.stream(listeners).forEach(e -> getServer().getPluginManager().registerEvents(e, this));
    }

    private void registerCommands(Commands... commands){
        commandManager.registerCommand(commands);
    }


}
