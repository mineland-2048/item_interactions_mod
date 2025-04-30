package dev.mineland.item_interactions_mod.CarriedInteractions;

import dev.mineland.item_interactions_mod.*;
import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.Spawner;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.List;

public class checkForParticles {
    public static boolean check(GuiGraphics guiGraphics, Slot slot, boolean dead, int leftPos, int topPos, int slotCount) {
//        if (dead) return true;
        try {
            if (!GlobalDirt.shouldTickParticles) return false;
            ItemStack slotItem = slot.getItem();
            List<Spawner> itemSpawnerList = SpawnerRegistry.get(slotItem);
            while (GlobalDirt.slotSpawners.size() <= slotCount) GlobalDirt.slotSpawners.add(null);
            List<ResourceLocation> currentSpawnersList = GlobalDirt.slotSpawners.getIdList(slotCount);
            List<ResourceLocation> itemSpawnersIdList = SpawnerRegistry.getList(slotItem);

            if (ItemInteractionsConfig.debugDraws) guiGraphics.renderOutline(slot.x, slot.y, 16, 16, 0xFFFFFFFF);
            if (itemSpawnerList.isEmpty() && currentSpawnersList.isEmpty()) {
//                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, 16, 16, 0xFF);
                return false;
            }

            if (itemSpawnerList.isEmpty()) {
                GlobalDirt.slotSpawners.set(slotCount, (Spawner) null);
                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xFFFF0000);
                return false;
            }

            int globalX = slot.x + leftPos;
            int globalY = slot.y + topPos;


            if ((currentSpawnersList.isEmpty())) {

                GlobalDirt.slotSpawners.set(slotCount, itemSpawnerList);

//                itemSpawnerList.init(guiGraphics, globalX, globalY, 0, 0, 0, 0);
                GlobalDirt.slotSpawners.tick(slotCount, GlobalDirt.tickCounter, guiGraphics, globalX, globalY, 0f, 0f);

                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xFF00FF00);
                return false;
            }



            if (! new HashSet<>(currentSpawnersList).containsAll(itemSpawnersIdList)) {
//                itemSpawnerList = itemSpawnerList.newInstance(slotCount);
                GlobalDirt.slotSpawners.set(slotCount, itemSpawnerList);

//                itemSpawnerList.init(guiGraphics, globalX, globalY, 0, 0, 0, 0);
                GlobalDirt.slotSpawners.tick(slotCount, GlobalDirt.tickCounter, guiGraphics, globalX, globalY, 0f, 0f);


                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xFFFFFF00);
                return false;

            }


            if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x8000FF00);

            GlobalDirt.slotSpawners.tick(slotCount, GlobalDirt.tickCounter, guiGraphics, globalX, globalY, 0f, 0f);
            return false;

        } catch (Exception e) {
            if (!dead) Item_interactions_mod.warnMessage("Error! \n" + e);
            return true;
        }


    }

}
