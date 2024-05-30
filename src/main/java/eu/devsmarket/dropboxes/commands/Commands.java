package eu.devsmarket.dropboxes.commands;

import eu.devsmarket.dropboxes.DropBoxes;
import eu.devsmarket.dropboxes.utils.TabCompleteObj;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public interface Commands {
    DropBoxes dropBoxes = DropBoxes.getPlugin(DropBoxes.class);
    CommandRunner commandExecutor = new CommandRunner();
    String getName();
    void runCommand(String[] args, CommandSender sender);
    void initializeCommand();
    ArrayList<TabCompleteObj> getTips();
}
