package dev.mineland.item_interactions_mod.CarriedInteractions;

import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.Spawner;
import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import dev.mineland.item_interactions_mod.ItemInteractionsResources;
import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class checkForParticles {
    public static boolean check(GuiGraphics guiGraphics, Slot slot, boolean dead, int leftPos, int topPos, int slotCount) {
//        if (dead) return true;
        try {

            int slotIndex = slotCount;
            ItemStack slotItem = slot.getItem();
            Spawner itemSpawner = ItemInteractionsResources.ParticleList.get(slotItem.getItem().toString());
            if (GlobalDirt.slotSpawners.size() <= slotIndex) GlobalDirt.slotSpawners.add(null);
            Spawner currentSpawner = GlobalDirt.slotSpawners.get(slotIndex);

            if (ItemInteractionsConfig.debugDraws) guiGraphics.renderOutline(slot.x, slot.y, 16, 16, 0xFFFFFFFF);
            if (itemSpawner == null && currentSpawner == null) {
//                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, 16, 16, 0xFF);
                return false;
            }

            if (itemSpawner == null) {
                GlobalDirt.slotSpawners.set(slotIndex, null);
                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xFFFF0000);
                return false;
            }

            int globalX = slot.x + leftPos;
            int globalY = slot.y + topPos;


            if ((currentSpawner == null)) {

                itemSpawner = itemSpawner.newInstance(slotIndex);
                GlobalDirt.slotSpawners.set(slotIndex, itemSpawner);

                itemSpawner.init(guiGraphics, globalX, globalY, 0, 0, 0, 0);
                itemSpawner.tick(guiGraphics, globalX, globalY, 0, 0, 0, 0);

                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xFF00FF00);
                return false;
            }



            if ((!currentSpawner.getName().equals(itemSpawner.getName()))) {
                itemSpawner = itemSpawner.newInstance(slotIndex);
                GlobalDirt.slotSpawners.set(slotIndex, itemSpawner);

                itemSpawner.init(guiGraphics, globalX, globalY, 0, 0, 0, 0);
                itemSpawner.tick(guiGraphics, globalX, globalY, 0, 0, 0, 0);


                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xFFFFFF00);
                return false;

            }


            if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x8000FF00);

            itemSpawner.tick(guiGraphics, globalX, globalY, 0, 0, 0, 0);
            return false;

        } catch (Exception e) {
            if (!dead) Item_interactions_mod.warnMessage("Error! \n" + e);
            return true;
        }


    }

}
