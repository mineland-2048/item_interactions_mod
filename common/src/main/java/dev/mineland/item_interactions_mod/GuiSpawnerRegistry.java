package dev.mineland.item_interactions_mod;

import com.google.gson.JsonObject;
import dev.mineland.item_interactions_mod.CarriedInteractions.SpawnerJsonObject;
import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.Spawner;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class GuiSpawnerRegistry {
    private final static Map<ResourceLocation, SpawnerJsonObject> SPAWNERS = new HashMap<>();

    private final static Map<ResourceLocation, List<ResourceLocation>> ITEM_SPAWNERS = new HashMap<>();

    public static void register(ResourceLocation id, SpawnerJsonObject spawnerObject) {
        SPAWNERS.put(id, spawnerObject);
        for (SpawnerJsonObject.SpawnerItem item : spawnerObject.appliedItems) {

        }

    }

    public static SpawnerJsonObject getObject(ResourceLocation id) {
        return SPAWNERS.get(id);
    }

    public static List<ResourceLocation> getSpawnerIds(ItemStack item) {
        return ITEM_SPAWNERS.get(ResourceLocation.parse(item.getItem().toString()));
    }
    public static List<Spawner> get(ItemStack item) {
        List<ResourceLocation> list = ITEM_SPAWNERS.get(ResourceLocation.parse(item.getItem().toString()));
        List<Spawner> result = new ArrayList<>();

        Item_interactions_mod.infoMessage("Getting spawners for " + item);
        for (ResourceLocation id : list) {

            SpawnerJsonObject obj = SPAWNERS.get(id);
            if (obj == null) {
                Item_interactions_mod.infoMessage("[.] " + id);
                continue;
            }

            if (obj.matches(item)) {
                result.add(obj.getSpawner());
                Item_interactions_mod.infoMessage("[✓] " + id);
                continue;
            }

            Item_interactions_mod.infoMessage("[x] " + id);

        }

        return result;
    }

    public static Collection<SpawnerJsonObject> getAll() {
        return SPAWNERS.values();
    }

    static void clear() {
        SPAWNERS.clear();
    }

}
