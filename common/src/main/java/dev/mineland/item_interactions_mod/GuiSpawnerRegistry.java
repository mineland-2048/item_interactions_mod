package dev.mineland.item_interactions_mod;

import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.Spawner;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GuiSpawnerRegistry {
    private final static Map<ResourceLocation, Spawner> SPAWNERS = new HashMap<>();

    public static void register(ResourceLocation id, Spawner spawner) {
        SPAWNERS.put(id, spawner);
    }

    public static Spawner get(ResourceLocation id) {
        return SPAWNERS.get(id);
    }

    public static Collection<Spawner> getAll() {
        return SPAWNERS.values();
    }

    static void clear() {
        SPAWNERS.clear();
    }

}
