package eu.devsmarket.dropboxes.listner;

import eu.devsmarket.dropboxes.data.LootBoxesData;
import eu.devsmarket.dropboxes.object.LootBoxObj;
import eu.devsmarket.dropboxes.utils.MessageUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;

public class BlockDestroyListener implements Listener {
    private final LootBoxesData lootBoxesData = LootBoxesData.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.CHEST) {
            for (LootBoxObj lootBoxObj : lootBoxesData.getLootBoxesList()){
                if(block.getLocation().getBlockX() == lootBoxObj.getLocation().getBlockX()
                        && block.getLocation().getBlockZ() == lootBoxObj.getLocation().getBlockZ()){
                    Arrays.stream(lootBoxObj.getArmorStand()).forEach(Entity::remove);
                    lootBoxesData.removeLootBox(lootBoxObj);
                    MessageUtil.sendMessageToEveryone("lootbox_destroyed_message", lootBoxObj.getLocation());
                    return;
                }
            }
        }
    }
}
