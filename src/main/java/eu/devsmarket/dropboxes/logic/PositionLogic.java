package eu.devsmarket.dropboxes.logic;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Random;

public class PositionLogic {
    private final Random random = new Random();

    public Location getRandomSafeLocation(World world, int minX, int maxX, int minZ, int maxZ) {
        int x = getRandomCord(minX, maxX);
        int z = getRandomCord(minZ, maxZ);
        int y = findSafeY(world, x, z);
        while (y == 0) {
            y = findSafeY(world, x, z);
        }
        return new Location(world, x, y, z);
    }

    private int findSafeY(World world, int x, int z) {
        for (int y = 255; y >= 0; y--) {
            if (isSafeLocation(world, x, y, z)) {
                return y + 1;
            }
        }
        return 0;
    }

    private int getRandomCord(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    private boolean isSafeLocation(World world, int x, int y, int z) {
        return world.getBlockAt(x, y, z).getType() != Material.AIR &&
                world.getBlockAt(x, y + 1, z).getType() == Material.AIR &&
                world.getBlockAt(x, y + 2, z).getType() == Material.AIR;
    }
}
