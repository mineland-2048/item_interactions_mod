package dev.mineland.item_interactions_mod.CarriedInteractions;

import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.Spawner;
import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class SpawnerJsonObject {

    private final Spawner spawner;
    private final DataComponentMap includeComponents, excludeComponents;
    public final List<SpawnerItem> appliedItems;
    public SpawnerJsonObject(Spawner spawner, List<SpawnerItem> appliedItems, DataComponentMap includeComponents, DataComponentMap excludeComponents) {

        this.spawner = spawner;
        this.appliedItems = appliedItems;
        this.includeComponents = includeComponents;
        this.excludeComponents = excludeComponents;
    }


    public List<SpawnerItem> getItems() {
        return appliedItems;
    }

    public Spawner getSpawner() {
        return this.spawner;
    }

    public boolean matches(ItemStack item) {
        boolean result = false;
        for (SpawnerItem appliedItem : appliedItems) {
            result = result || appliedItem.matches(item);
        }

        return result;
    }

    public void addSpawnerItem(SpawnerItem item) {
        appliedItems.add(item);
    }

    public void clearSpawnerItems() {
        appliedItems.clear();
    }

    public SpawnerJsonObject copy() {
        return new SpawnerJsonObject(this.spawner, this.appliedItems, this.includeComponents, this.excludeComponents);
    }

    public static class SpawnerItem {
        ResourceLocation item;
        DataComponentMap includeComponents, excludeComponents;


        public SpawnerItem(Item item) {
            this(ResourceLocation.parse(item.toString()));
        }

        public SpawnerItem(Item item, DataComponentMap includeComponents) {
            this(ResourceLocation.parse(item.toString()), includeComponents);
        }

        public SpawnerItem(Item item, DataComponentMap includeComponents, DataComponentMap excludeComponents) {
            this(ResourceLocation.parse(item.toString()), includeComponents, excludeComponents);
        }

        public boolean matches(ItemStack item) {

//            TODO: SpawnerJsonObject match logic
            boolean result = true;
            for (TypedDataComponent<?> component : includeComponents) {
                result = item.getComponents().stream().anyMatch(typedDataComponent -> {
                    Item_interactions_mod.infoMessage("SpawnerItem match: " + typedDataComponent + " & " + component);
                    return typedDataComponent == component;
                } );
                if (!result) break;
            }
            return result;
        }


        public SpawnerItem(ResourceLocation itemResourceLocation) {
            this(itemResourceLocation, null, null);
        }

        public SpawnerItem(ResourceLocation itemResourceLocation, DataComponentMap includeComponents) {
            this(itemResourceLocation, includeComponents, null);
        }

        public SpawnerItem(ResourceLocation itemResourceLocation, DataComponentMap includeComponents, DataComponentMap excludeComponents) {
            this.item = itemResourceLocation;
            this.includeComponents = includeComponents;
            this.excludeComponents = excludeComponents;
        }
    }
}




