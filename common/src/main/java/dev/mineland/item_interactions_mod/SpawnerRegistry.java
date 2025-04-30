package dev.mineland.item_interactions_mod;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.Spawner;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SpawnerRegistry {
    public static final Registry<SpawnerRegistry> SPAWNER_REGISTRIES = new MappedRegistry<>(
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("item_interactions_mod", "spawners")), Lifecycle.stable()
    );
//
//    public static final Registry<ItemStack> MAPPED_ITEMS_REGISTRY = new MappedRegistry<>(
//            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("item_interactions_mod", "mapped")), Lifecycle.stable()
//    );

    public static final Registry<ResourceLocation> WILDCARD_ITEMS_REGISTRY = new MappedRegistry<>(
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("item_interactions_mod", "*")), Lifecycle.stable()
    );


    public static final Map<Item, List<ResourceLocation>> MAPPED_ITEMS_LIST = new HashMap<>();




    public static List<Spawner> get(ItemStack item) {
        List<ResourceLocation> spawnerIds = MAPPED_ITEMS_LIST.get(item.getItem());
        List<Spawner> result = new ArrayList<>();

        for (ResourceLocation id : spawnerIds) {
            Spawner spawner = getSpawnerFromId(id);
            if (spawner.matches(item)) result.add(spawner);
        }
        return result;
    };


    public static List<ResourceLocation> getList(ItemStack item) {
        List<ResourceLocation> spawnerIds = MAPPED_ITEMS_LIST.get(item.getItem());
        List<ResourceLocation> result = new ArrayList<>();

        for (ResourceLocation id : spawnerIds) {
            Spawner spawner = getSpawnerFromId(id);
            if (spawner.matches(item)) result.add(id);
        }
        return result;

    }
    public static Spawner getSpawnerFromId(ResourceLocation id) {
        SPAWNER_REGISTRIES.get(id);
    }

    public static Spawner register(Spawner spawner, ResourceLocation id) {
        List<ItemStack> items = spawner.appliedItems;
        for (ItemStack item : items) {
            if (!MAPPED_ITEMS_LIST.containsKey(item.getItem())) {
                MAPPED_ITEMS_LIST.put(item.getItem(), new ArrayList<>());
            }

            MAPPED_ITEMS_LIST.get(item.getItem()).add(id);

        }
    }
}
