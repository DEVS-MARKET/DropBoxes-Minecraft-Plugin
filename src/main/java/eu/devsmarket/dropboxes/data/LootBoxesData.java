package eu.devsmarket.dropboxes.data;

import eu.devsmarket.dropboxes.object.LootBoxObj;

import java.util.ArrayList;
import java.util.Arrays;

public class LootBoxesData {
    private static LootBoxesData lootBoxesData;
    private final ArrayList<LootBoxObj> lootBoxesList = new ArrayList<>();
    private final ArrayList<LootBoxObj> takenLootBoxesList = new ArrayList<>();

    public static LootBoxesData getInstance(){
        if(lootBoxesData == null){
            lootBoxesData = new LootBoxesData();
        }
        return lootBoxesData;
    }

    public void addNewLootBox(LootBoxObj... lootBoxesObj){
        lootBoxesList.addAll(Arrays.asList(lootBoxesObj));
    }

    public void removeLootBox(LootBoxObj lootBoxObj){
        lootBoxesList.remove(lootBoxObj);
    }

    public ArrayList<LootBoxObj> getLootBoxesList() {
        return lootBoxesList;
    }

    public ArrayList<LootBoxObj> getTakenLootBoxesList() {
        return takenLootBoxesList;
    }

    public void addTakenLootBox(LootBoxObj... lootBoxesObj){
        takenLootBoxesList.addAll(Arrays.asList(lootBoxesObj));
    }

    public void removeTakenLootBox(LootBoxObj lootBoxObj){
        takenLootBoxesList.remove(lootBoxObj);
    }
}
