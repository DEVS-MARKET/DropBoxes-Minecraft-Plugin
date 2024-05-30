package eu.devsmarket.dropboxes.commands.impl;

import eu.devsmarket.dropboxes.DropBoxes;
import eu.devsmarket.dropboxes.commands.Commands;
import eu.devsmarket.dropboxes.logic.LootBoxesLogic;
import eu.devsmarket.dropboxes.utils.LootBoxType;
import eu.devsmarket.dropboxes.utils.MessageUtil;
import eu.devsmarket.dropboxes.utils.TabCompleteObj;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class DropBoxCommand implements Commands {
    private final LootBoxesLogic lootBoxesLogic = new LootBoxesLogic();

    @Override
    public String getName() {
        return "dropboxes";
    }

    @Override
    public void runCommand(String[] args, CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length != 0) {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "create":
                        onCreateCommand(player, args);
                        break;
                    case "list":
                        onListCommand(player);
                        break;
                    case "reload":
                        onReloadCommand(player);
                        break;
                    case "drop":
                        onDropCommand();
                        break;
                    case "silentcreate":
                        onSilentCreateCommand(player, args);
                        break;
                    default:
                        onHelpCommand(player);
                        break;
                }
            }else{
                onHelpCommand(player);
            }
        }
    }

    @Override
    public void initializeCommand() {
        PluginCommand dropBoxesCommand = dropBoxes.getCommand(getName());
        Objects.requireNonNull(dropBoxesCommand).setExecutor(commandExecutor);
        dropBoxesCommand.setTabCompleter(commandExecutor);
    }

    @Override
    public ArrayList<TabCompleteObj> getTips() {
        ArrayList<TabCompleteObj> tipsList = new ArrayList<>();
        tipsList.add(new TabCompleteObj(0, getName(), new String[]{"create", "list", "reload", "drop", "silentcreate"}));
        tipsList.add(new TabCompleteObj(1, "create", new String[]{"random", "legendary", "common"}));
        tipsList.add(new TabCompleteObj(1, "silentcreate", new String[]{"random", "legendary", "common"}));
        return tipsList;
    }

    private void onCreateCommand(Player player, String[] args) {
        if(args.length < 2){
            MessageUtil.sendMessageToPlayer("&cTo use this command, add another argument that will indicate the lootbox type (random, common, legendary)", false, player);
            return;
        }
        LootBoxType type = LootBoxType.valueOf(args[1].toUpperCase());
        boolean differentMessages = dropBoxes.getConfig().getBoolean("different_messages_for_other_types");
        lootBoxesLogic.spawnLootBoxHere(player.getLocation(), type);
        MessageUtil.sendMessageToEveryone(differentMessages ? type.name().toLowerCase() + "_lootbox_droped_message" : "common_lootbox_droped_message", player.getLocation());
    }

    private void onHelpCommand(Player player){
        MessageUtil.sendMessageToPlayer("&7Available commands for &b/dropboxes:", false, player);
        MessageUtil.sendMessageToPlayer("&b/dropboxes create <type> &8- &7cause a drop in your location", false, player);
        MessageUtil.sendMessageToPlayer("&b/dropboxes list &8- &7shows the locations of lootboxes that have not been caputed yet", false, player);
        MessageUtil.sendMessageToPlayer("&b/dropboxes reload &8- &7reloads plugin configuration", false, player);
        MessageUtil.sendMessageToPlayer("&b/dropboxes drop &8- &7cause a drop in a random place in the world", false, player);
        MessageUtil.sendMessageToPlayer("&b/dropboxes silentcreate <type> &8- &7cause a drop in your location without message for others", false, player);
    }

    private void onListCommand(Player player) {
        MessageUtil.sendListToPlayer("lootbox_list_title", "lootbox_list_label", player);
    }

    private void onReloadCommand(Player player) {
        dropBoxes.reloadConfig();
        DropBoxes.CONFIG = dropBoxes.getConfig();
        DropBoxes.LOOTBOXESLOGIC = new LootBoxesLogic(dropBoxes.getServer().getWorld(Objects.requireNonNull(dropBoxes.getConfig().getString("world_name"))),
                dropBoxes.getConfig().getInt("world_minX"),
                dropBoxes.getConfig().getInt("world_maxX"),
                dropBoxes.getConfig().getInt("world_minZ"),
                dropBoxes.getConfig().getInt("world_maxZ"));
        MessageUtil.sendMessageToPlayer("lootbox_reload_message", player);
        Bukkit.getScheduler().cancelTask(DropBoxes.TASKID);
        Bukkit.getScheduler().scheduleSyncDelayedTask(dropBoxes, () -> DropBoxes.TASKID = Bukkit.getScheduler().scheduleSyncRepeatingTask(dropBoxes, DropBoxes.LOOTBOXESLOGIC::dropLootBox, 0L, 20L * 60 * DropBoxes.CONFIG.getInt("drop_per_min")), 20L * 60 * DropBoxes.CONFIG.getInt("drop_per_min"));
    }

    private void onDropCommand() {
        DropBoxes.LOOTBOXESLOGIC.dropLootBox();
    }

    private void onSilentCreateCommand(Player player, String[] args) {
        if(args.length < 2){
            MessageUtil.sendMessageToPlayer("&cTo use this command, add another argument that will indicate the lootbox type (random, common, legendary)", false, player);
            return;
        }
        LootBoxType type = LootBoxType.valueOf(args[1].toUpperCase());
        boolean differentMessages = dropBoxes.getConfig().getBoolean("different_messages_for_other_types");
        lootBoxesLogic.spawnLootBoxHere(player.getLocation(), type);
        MessageUtil.sendMessageToPlayer(differentMessages ? type.name().toLowerCase() + "_lootbox_droped_message" : "common_lootbox_droped_message", player.getLocation(), player);
    }
}
