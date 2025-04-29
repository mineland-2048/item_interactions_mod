package dev.mineland.item_interactions_mod.CarriedInteractions;

import dev.mineland.item_interactions_mod.*;
import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.Spawner;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class checkForParticles {
    public static boolean check(GuiGraphics guiGraphics, Slot slot, boolean dead, int leftPos, int topPos, int slotCount) {
//        if (dead) return true;
        try {
            if (!GlobalDirt.shouldTickParticles) return false;
            ItemStack slotItem = slot.getItem();
            List<Spawner> itemSpawner = GuiSpawnerRegistry.get(slotItem);
            if (GlobalDirt.slotSpawners.size() <= slotCount) GlobalDirt.slotSpawners.add(null);

            List<Spawner> currentSpawner = GlobalDirt.slotSpawners.get(slotCount);

            if (ItemInteractionsConfig.debugDraws) guiGraphics.renderOutline(slot.x, slot.y, 16, 16, 0xFFFFFFFF);
            if (itemSpawner.isEmpty() && currentSpawner.isEmpty()) {
//                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, 16, 16, 0xFF);
                return false;
            }

            if (itemSpawner.isEmpty()) {
                GlobalDirt.slotSpawners.set(slotCount, null);
                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xFFFF0000);
                return false;
            }

            int globalX = slot.x + leftPos + 8;
            int globalY = slot.y + topPos + 8;


            if ((currentSpawner == null || currentSpawner.isEmpty())) {

                GlobalDirt.slotSpawners.set(slotCount, new ArrayList<>());
                for (Spawner spawner : itemSpawner) {
                    spawner = spawner.newInstance(slotCount);

                    GlobalDirt.slotSpawners.get(slotCount).add(spawner);

//                    spawner.init(guiGraphics, globalX, globalY, 0, 0, 0, 0);
                    spawner.tick(guiGraphics, globalX, globalY, 0, 0, 0, 0);

                }

                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xFF00FF00);
                return false;
            }


            boolean isDifferent = true;
            List<ResourceLocation> ids = GuiSpawnerRegistry.getSpawnerIds(slot.getItem());

            for (Spawner s : currentSpawner) {
                isDifferent = !ids.contains(ResourceLocation.parse(s.getName()));
                if (isDifferent) break;
            }

            if (isDifferent) {
//                itemSpawner = itemSpawner.newInstance(slotCount);
                List<Spawner> newSpawners = new ArrayList<>();
                for (Spawner s : itemSpawner) {
                    s = s.newInstance(slotCount);
                    newSpawners.add(s);

                    s.tick(guiGraphics, globalX, globalY, 0, 0, 0, 0);


                }
                GlobalDirt.slotSpawners.set(slotCount, newSpawners);

//                itemSpawner.init(guiGraphics, globalX, globalY, 0, 0, 0, 0);

                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xFFFFFF00);
                return false;

            }


            if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x8000FF00);

            for (Spawner s : itemSpawner) {
                s.tick(guiGraphics, globalX, globalY, 0, 0, 0, 0);

            }
            return false;

        } catch (Exception e) {
            if (!dead) Item_interactions_mod.warnMessage("Error! \n" + e);
            return true;
        }


    }

}
