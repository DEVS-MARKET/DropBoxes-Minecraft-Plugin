package eu.devsmarket.dropboxes.logic;

import eu.devsmarket.dropboxes.DropBoxes;
import eu.devsmarket.dropboxes.data.LootBoxesData;
import eu.devsmarket.dropboxes.object.LootBoxObj;
import eu.devsmarket.dropboxes.utils.LootBoxType;
import eu.devsmarket.dropboxes.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LootBoxesLogic {
    private final LootBoxesData lootBoxesData = LootBoxesData.getInstance();
    private final PositionLogic positionLogic = new PositionLogic();
    private final Random random = new Random();
    private World world;
    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;

    public LootBoxesLogic() {
    }

    public LootBoxesLogic(World world, int minX, int maxX, int minZ, int maxZ) {
        this.world = world;
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    public void spawnLootBoxHere(Location location, LootBoxType type) {
        spawnLootBox(location, true, type);
    }

    public void removeAllTookLootBoxes() {
        try {
            FileConfiguration config = DropBoxes.CONFIG;
            if (config.getBoolean("remove_chest_if_empty")) {
                lootBoxesData.getTakenLootBoxesList().forEach(lootBoxObj -> {
                    for (int i = 0; i < 3; i++) {
                        Block block = world.getBlockAt(lootBoxObj.getLocation().getBlockX(), lootBoxObj.getLocation().getBlockY() + i, lootBoxObj.getLocation().getBlockZ());
                        if (block.getType() == Material.CHEST) {
                            Chest chest = (Chest) block.getState();
                            if (chest.getInventory().isEmpty()) {
                                block.setType(Material.AIR);
                                Arrays.stream(lootBoxObj.getArmorStand()).forEach(Entity::remove);
                                lootBoxesData.removeTakenLootBox(lootBoxObj);
                                MessageUtil.sendMessageToEveryone("lootbox_destroyed_message", lootBoxObj.getLocation());
                            } else {
                                return;
                            }
                        }
                    }
                });
            }
        } catch (ConcurrentModificationException ignored) {
        }
    }


    public void dropLootBox() {
        FileConfiguration config = DropBoxes.CONFIG;
        boolean differentMessages = config.getBoolean("different_messages_for_other_types");
        if (lootBoxesData.getLootBoxesList().size() < config.getInt("max_lootboxes_on_world")) {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            if (onlinePlayers.size() >= config.getInt("min_online_to_start_event")) {
                LootBoxType type = LootBoxType.RANDOM;
                Location location = positionLogic.getRandomSafeLocation(this.world, this.minX, this.maxX, this.minZ, this.maxZ);
                spawnLootBox(location, false, type);
                addBossBar(onlinePlayers, location, type);
                MessageUtil.sendMessageToEveryone(differentMessages ? type.name().toLowerCase() + "_lootbox_droped_message" : "common_lootbox_droped_message", location);
            } else {
                MessageUtil.sendMessageToEveryone("lootbox_event_canceled_message", true);
            }
        }
    }

    private void spawnLootBox(Location location, boolean locationFix, LootBoxType lootBoxType) {
        FileConfiguration config = DropBoxes.CONFIG;
        location.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) location.getBlock().getState();
        lootBoxType = lootBoxType == LootBoxType.RANDOM ? getRandomLootBoxType() : lootBoxType;
        chest.setCustomName(config.getString(lootBoxType.name().toLowerCase() + "_lootbox_chest_name").replace("&", "§"));
        chest.update();
        Inventory inv = chest.getInventory();
        inv.clear();

        List<?> items = config.getList(lootBoxType.name().toLowerCase() + "_items");
        for (int i = 0; i < inv.getSize(); i++) {
            if (random.nextInt(100) < config.getInt("chance_per_slot")) {
                Optional<ItemStack> item = generateRandomItem(items);
                if (item.isPresent()) inv.setItem(i, item.get());
            }
        }

        List<String> lootBoxLabels = config.getStringList(lootBoxType.name().toLowerCase() + "_lootbox_labels");
        if (locationFix) {
            location.add(0, -1 + (0.2 * (lootBoxLabels.size())), 0);
        } else {
            location.add(0.5, -1 + (0.2 * (lootBoxLabels.size())), 0.5);
        }
        ArmorStand[] armorStands = new ArmorStand[lootBoxLabels.size()];
        for (int i = 0; i < lootBoxLabels.size(); i++) {
            ArmorStand as = location.getWorld().spawn(new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ()).add(0.5, -0.2 * (i + 1), 0.5), ArmorStand.class);
            as.setCustomName(lootBoxLabels.get(i).replace("&", "§"));
            as.setCustomNameVisible(true);
            as.setVisible(false);
            as.setCollidable(false);
            as.setGravity(false);
            armorStands[i] = as;
        }
        lootBoxesData.addNewLootBox(new LootBoxObj(locationFix ? chest.getLocation() : location, armorStands));
    }

    private Optional<ItemStack> generateRandomItem(List<?> items) {
        Map<?, ?> map = getRandomItem(getAllItems(items));
        if (map != null) {
            String item = map.get("item").toString();
            String itemName = (String) map.get("item_name");
            List<?> itemLore = (List<?>) map.get("item_lore");
            List<?> itemEnchants = (List<?>) map.get("enchants");
            int amount = (boolean) map.get("random_amount") ? random.nextInt((int) map.get("amount")) : (int) map.get("amount");
            ItemStack stack = new ItemStack(Objects.requireNonNull(Material.getMaterial(item.toUpperCase())), amount == 0 ? 1 : amount);
            ItemMeta meta = stack.getItemMeta();
            if (itemName != null) meta.setDisplayName(itemName.replace("&", "§"));
            if (itemLore != null) {
                List<String> formatedText = new ArrayList<>();
                itemLore.forEach(text -> formatedText.add(String.valueOf(text).replace("&", "§")));
                meta.setLore(formatedText);
            }
            if (itemEnchants != null) {
                itemEnchants.forEach(e -> {
                    String[] enchArray = String.valueOf(e).split(" ");
                    meta.addEnchant(Objects.requireNonNull(Enchantment.getByName(enchArray[0])), Integer.parseInt(enchArray[1]), true);
                });
            }
            stack.setItemMeta(meta);
            return Optional.of(stack);
        }
        return Optional.empty();
    }

    private Map<Map<?, ?>, Integer> getAllItems(List<?> items) {
        Map<Map<?, ?>, Integer> itemsMap = new HashMap<>();
        for (Object item : items) {
            Map<?, ?> map = (Map<?, ?>) item;
            itemsMap.put(map, (Integer) map.get("chance"));
        }
        return itemsMap;
    }

    private Map<?, ?> getRandomItem(Map<Map<?, ?>, Integer> map) {
        int sum = 0;
        for (int value : map.values()) {
            sum += value;
        }
        int randomNum = random.nextInt(sum) + 1;
        int index = 0;
        for (Map.Entry<Map<?, ?>, Integer> entry : map.entrySet()) {
            index += entry.getValue();
            if (randomNum <= index) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void addBossBar(Collection<? extends Player> onlinePlayers, Location location, LootBoxType type) {
        FileConfiguration config = DropBoxes.CONFIG;
        boolean differentMessages = config.getBoolean("different_messages_for_other_types");
        if (config.getBoolean("bossbar_toggle")) {
            onlinePlayers.forEach(p -> {
                BossBar bossBar = Bukkit.createBossBar(MessageUtil.formatTextColor(Objects.requireNonNull(config.getString(differentMessages ? type.name().toLowerCase() + "_lootbox_droped_bossbar" : "common_lootbox_droped_bossbar")))
                        .replace("%x%", String.valueOf(location.getBlockX()))
                        .replace("%z%", String.valueOf(location.getBlockZ())), BarColor.valueOf(config.getString("bossbar_color")), BarStyle.SOLID);
                bossBar.addPlayer(p);
                bossBar.setVisible(true);
                Bukkit.getScheduler().runTaskLater(DropBoxes.getPlugin(DropBoxes.class), () -> bossBar.removePlayer(p), config.getInt("bossbar_visable_time") * 20L);

            });
        }
    }

    private LootBoxType getRandomLootBoxType(){
        FileConfiguration config = DropBoxes.CONFIG;
        Random random = new Random();
        double randomValue = random.nextDouble();

        if (randomValue < ((double) config.getInt("common_lootbox_chance") / 100)) {
            return LootBoxType.COMMON;
        } else {
            return LootBoxType.LEGENDARY;
        }
    }
}
