package dev.mineland.item_interactions_mod;

import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.GuiParticleSpawner;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
//? >= 26.1 {
/*import net.minecraft.world.item.ItemStackTemplate;
*///?}

import java.util.*;

public record SpawnerRegistry() {
//    public static Registry<SpawnerRegistry> SPAWNER_REGISTRIES = new MappedRegistry<>(
//            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("item_interactions_mod", "spawners")), Lifecycle.stable()
//    );
//
//    public static final Registry<ItemStack> MAPPED_ITEMS_REGISTRY = new MappedRegistry<>(
//            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("item_interactions_mod", "mapped")), Lifecycle.stable()
//    );

//    public static Registry<Identifier> WILDCARD_ITEMS_REGISTRY = new MappedRegistry<>(
//            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("item_interactions_mod", "*")), Lifecycle.stable()
//    );


    public static Map<String, List<Identifier>> MAPPED_ITEMS_LIST = new HashMap<>();

    public static Map<Identifier, GuiParticleSpawner> SPAWNER_MAP = new HashMap<>();


    public static void clear() {
//        SPAWNER_REGISTRIES = new MappedRegistry<>(
//                ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("item_interactions_mod", "spawners")), Lifecycle.stable()
//        );
//
//        WILDCARD_ITEMS_REGISTRY = new MappedRegistry<>(
//                ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("item_interactions_mod", "*")), Lifecycle.stable()
//        );

        MAPPED_ITEMS_LIST.clear();
        SPAWNER_MAP.clear();

    }

    public static List<GuiParticleSpawner> get(ItemStack item) {

        String itemId;

        //? >= 26.1 {
        /*itemId = item.typeHolder().getRegisteredName();
        *///?} else
        itemId = item.getItemHolder().getRegisteredName();

        List<Identifier> spawnerIds = MAPPED_ITEMS_LIST.get(itemId);
        List<GuiParticleSpawner> result = new ArrayList<>();

        if (spawnerIds == null) return result;
        for (Identifier id : spawnerIds) {
            GuiParticleSpawner guiParticleSpawner = getSpawnerFromId(id);

            if (guiParticleSpawner != null && guiParticleSpawner.matches(item)) result.add(guiParticleSpawner);
        }
        return result;
    }


    public static List<Identifier> getList(ItemStack item) {

        String itemId;
        //? >= 26.1 {
        /*itemId = item.typeHolder().getRegisteredName();
         *///?} else
        itemId = item.getItemHolder().getRegisteredName();


        List<Identifier> spawnerIds = MAPPED_ITEMS_LIST.get(itemId);
        List<Identifier> result = new ArrayList<>();

        if (spawnerIds == null) return result;

        for (Identifier id : spawnerIds) {
            GuiParticleSpawner guiParticleSpawner = getSpawnerFromId(id);
            if (guiParticleSpawner != null && guiParticleSpawner.matches(item)) result.add(id);
        }
        return result;

    }
    public static GuiParticleSpawner getSpawnerFromId(Identifier id) {
//        if (SPAWNER_REGISTRIES.cocontainsKey(id)) return SPAWNER_REGISTRIES.getValueOrThrow(ResourceKey.createid));
        return SPAWNER_MAP.getOrDefault(id, null);
    }

    public static void register(GuiParticleSpawner guiParticleSpawner, Identifier id) {
        //~ if >= 26.1 'ItemStack' -> 'ItemStackTemplate' {
        // if >= 26.1 '.getItem()' -> '.item().value()' {

        List<ItemStack> items = guiParticleSpawner.getAppliedItems();
        SPAWNER_MAP.put(id, guiParticleSpawner);
        for (ItemStack itemStack : items) {

            String itemId;

            //? >= 26.1 {
            /*itemId = itemStack.typeHolder().getRegisteredName();
            *///?} else {
            itemId = itemStack.getItemHolder().getRegisteredName();
            //?}


            if (!MAPPED_ITEMS_LIST.containsKey(itemId)) {
                MAPPED_ITEMS_LIST.put(itemId, new ArrayList<>());
            } else if (MAPPED_ITEMS_LIST.get(itemId).contains(id)) {
                continue;
            }

            MAPPED_ITEMS_LIST.get(itemId).add(id);

        }
        //}
        //~}

    }

    public static boolean compareSpawner(List<GuiParticleSpawner> carriedGuiParticleSpawner, ItemStack carriedItem) {
        List<Identifier> carriedList = new ArrayList<>();
        List<Identifier> itemList = SpawnerRegistry.getList(carriedItem);

        for (GuiParticleSpawner s : carriedGuiParticleSpawner) {
            carriedList.add(s.getName());
        }

        return new HashSet<>(carriedList).containsAll(itemList);

    }

    public static List<GuiParticleSpawner> create(List<GuiParticleSpawner> itemGuiParticleSpawnerList) {
        List<GuiParticleSpawner> result = new ArrayList<>();
        for (GuiParticleSpawner a : itemGuiParticleSpawnerList) {
            result.add(a.duplicate());
        }
        return result;
    }
}
