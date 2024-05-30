package eu.devsmarket.dropboxes.listner;

import eu.devsmarket.dropboxes.data.LootBoxesData;
import eu.devsmarket.dropboxes.object.LootBoxObj;
import eu.devsmarket.dropboxes.utils.MessageUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.Objects;

public class InteractionListener implements Listener {
    private final LootBoxesData lootBoxesData = LootBoxesData.getInstance();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (Objects.requireNonNull(block).getType() == Material.CHEST) {
                for (LootBoxObj lootBoxObj : lootBoxesData.getLootBoxesList()){
                    for (int i = 0; i < 3; i++) {
                        if (block.getLocation().getBlockX() == lootBoxObj.getLocation().getBlockX()
                                && block.getLocation().getBlockY() == (lootBoxObj.getLocation().getBlockY() + i)
                                && block.getLocation().getBlockZ() == lootBoxObj.getLocation().getBlockZ()) {
                            Arrays.stream(lootBoxObj.getArmorStand()).forEach(Entity::remove);
                            lootBoxesData.removeLootBox(lootBoxObj);
                            lootBoxesData.addTakenLootBox(lootBoxObj);
                            MessageUtil.sendMessageToEveryone("lootbox_taken_over_message", event.getPlayer());
                            return;
                        }
                    }
                }
            }
        }
    }
}
