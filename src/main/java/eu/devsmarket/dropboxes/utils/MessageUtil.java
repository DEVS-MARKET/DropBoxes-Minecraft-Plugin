package eu.devsmarket.dropboxes.utils;

import eu.devsmarket.dropboxes.DropBoxes;
import eu.devsmarket.dropboxes.data.LootBoxesData;
import eu.devsmarket.dropboxes.object.LootBoxObj;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class MessageUtil {
    private static final LootBoxesData lootBoxesData = LootBoxesData.getInstance();

    public static void sendMessageToEveryone(String message, boolean isConfigKey){
        FileConfiguration config = DropBoxes.CONFIG;
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        if(isConfigKey) {
            config.getStringList(message).forEach(s -> onlinePlayers.forEach(p -> p.sendMessage(formatTextColor(s))));
        }else{
            onlinePlayers.forEach(p -> p.sendMessage(formatTextColor(message)));
        }
    }

    public static void sendMessageToEveryone(String key, Location location){
        DropBoxes.CONFIG.getStringList(key).forEach(s -> Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(formatTextColor(s)
                .replace("%x%", String.valueOf(location.getBlockX()))
                .replace("%z%", String.valueOf(location.getBlockZ())))));
    }

    public static void sendMessageToEveryone(String key, Player player){
        DropBoxes.CONFIG.getStringList(key).forEach(s -> Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(formatTextColor(s)
                .replace("%player%", player.getName()))));
    }

    public static void sendMessageToPlayer(String message, boolean isConfigKey, Player player){
        FileConfiguration config = DropBoxes.CONFIG;
        if(isConfigKey) {
            DropBoxes.CONFIG.getStringList(message).forEach(s -> player.sendMessage(formatTextColor(s)));
        }else{
            player.sendMessage(formatTextColor(message));
        }
    }

    public static void sendMessageToPlayer(String key, Player player){
        DropBoxes.CONFIG.getStringList(key).forEach(s -> player.sendMessage(formatTextColor(s)));
    }

    public static void sendMessageToPlayer(String key, Location location, Player player){
        DropBoxes.CONFIG.getStringList(key).forEach(s -> Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(formatTextColor(s)
                .replace("%x%", String.valueOf(location.getBlockX()))
                .replace("%z%", String.valueOf(location.getBlockZ()))
                .replace("%player%", player.getName()))));
    }

    public static void sendListToPlayer(String titleKey, String labelKey, Player player){
        player.sendMessage(formatTextColor(Objects.requireNonNull(DropBoxes.CONFIG.getString(titleKey))));
        List<LootBoxObj> lootBoxObjs = lootBoxesData.getLootBoxesList();
        for (int i = 0; i < lootBoxObjs.size(); i++) {
            Location location = lootBoxObjs.get(i).getLocation();
            player.sendMessage(formatTextColor(Objects.requireNonNull(DropBoxes.CONFIG.getString(labelKey)))
                    .replace("%number%", String.valueOf(i+1))
                    .replace("%x%", String.valueOf(location.getBlockX()))
                    .replace("%z%", String.valueOf(location.getBlockZ())));
        }
    }

    public static String formatTextColor(String string){
        return string.replace("&", "§");
    }

}
