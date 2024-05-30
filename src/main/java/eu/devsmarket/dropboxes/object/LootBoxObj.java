package eu.devsmarket.dropboxes.object;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class LootBoxObj {
    private final Location location;
    private final ArmorStand[] armorStands;

    public LootBoxObj(Location location, ArmorStand[] armorStands) {
        this.location = location;
        this.armorStands = armorStands;
    }

    public Location getLocation() {
        return location;
    }

    public ArmorStand[] getArmorStand() {
        return armorStands;
    }
}
