package dev.mineland.item_interactions_mod;

import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.GuiParticleSpawner;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public record SpawnerRegistry() {
//    public static Registry<SpawnerRegistry> SPAWNER_REGISTRIES = new MappedRegistry<>(
//            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("item_interactions_mod", "spawners")), Lifecycle.stable()
//    );
//
//    public static final Registry<ItemStack> MAPPED_ITEMS_REGISTRY = new MappedRegistry<>(
//            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("item_interactions_mod", "mapped")), Lifecycle.stable()
//    );

//    public static Registry<ResourceLocation> WILDCARD_ITEMS_REGISTRY = new MappedRegistry<>(
//            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("item_interactions_mod", "*")), Lifecycle.stable()
//    );


    public static Map<Item, List<ResourceLocation>> MAPPED_ITEMS_LIST = new HashMap<>();

    public static Map<ResourceLocation, GuiParticleSpawner> SPAWNER_MAP = new HashMap<>();


    public static void clear() {
//        SPAWNER_REGISTRIES = new MappedRegistry<>(
//                ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("item_interactions_mod", "spawners")), Lifecycle.stable()
//        );
//
//        WILDCARD_ITEMS_REGISTRY = new MappedRegistry<>(
//                ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("item_interactions_mod", "*")), Lifecycle.stable()
//        );

        MAPPED_ITEMS_LIST.clear();
        SPAWNER_MAP.clear();

    }

    public static List<GuiParticleSpawner> get(ItemStack item) {
        List<ResourceLocation> spawnerIds = MAPPED_ITEMS_LIST.get(item.getItem());
        List<GuiParticleSpawner> result = new ArrayList<>();

        for (ResourceLocation id : spawnerIds) {
            GuiParticleSpawner guiParticleSpawner = getSpawnerFromId(id);

            if (guiParticleSpawner != null && guiParticleSpawner.matches(item)) result.add(guiParticleSpawner);
        }
        return result;
    };


    public static List<ResourceLocation> getList(ItemStack item) {
        List<ResourceLocation> spawnerIds = MAPPED_ITEMS_LIST.get(item.getItem());
        List<ResourceLocation> result = new ArrayList<>();

        for (ResourceLocation id : spawnerIds) {
            GuiParticleSpawner guiParticleSpawner = getSpawnerFromId(id);
            if (guiParticleSpawner != null && guiParticleSpawner.matches(item)) result.add(id);
        }
        return result;

    }
    public static GuiParticleSpawner getSpawnerFromId(ResourceLocation id) {
//        if (SPAWNER_REGISTRIES.containsKey(id)) return SPAWNER_REGISTRIES.getValueOrThrow(ResourceKey.createid));
        return SPAWNER_MAP.getOrDefault(id, null);
    }

    public static void register(GuiParticleSpawner guiParticleSpawner, ResourceLocation id) {
        List<ItemStack> items = guiParticleSpawner.appliedItems;
        SPAWNER_MAP.put(id, guiParticleSpawner);
        for (ItemStack item : items) {
            if (!MAPPED_ITEMS_LIST.containsKey(item.getItem())) {
                MAPPED_ITEMS_LIST.put(item.getItem(), new ArrayList<>());
            }

            if (MAPPED_ITEMS_LIST.get(item.getItem()).contains(id)) continue;
            MAPPED_ITEMS_LIST.get(item.getItem()).add(id);

        }

    }

    public static boolean compareSpawner(List<GuiParticleSpawner> carriedGuiParticleSpawner, ItemStack carriedItem) {
        List<ResourceLocation> carriedList = new ArrayList<>();
        List<ResourceLocation> itemList = SpawnerRegistry.getList(carriedItem);

        for (GuiParticleSpawner s : carriedGuiParticleSpawner) {
            carriedList.add(s.getName());
        }

        return new HashSet<>(itemList).containsAll(carriedList);

    }
}
