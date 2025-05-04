package dev.mineland.item_interactions_mod.CarriedInteractions;

import dev.mineland.item_interactions_mod.*;
import dev.mineland.item_interactions_mod.CarriedInteractions.Particles.BaseParticle;
import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.GuiParticleSpawner;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static dev.mineland.item_interactions_mod.GlobalDirt.*;
import static dev.mineland.item_interactions_mod.GlobalDirt.mouseDeltaY;

public class GuiParticleSpawnersLogic {
//    Is ran once per slot. It
    public static boolean checkAndTick(GuiGraphics guiGraphics, Slot slot, boolean dead, int leftPos, int topPos, int slotCount) {
//        if (dead) return true;

        if (!GlobalDirt.shouldTickParticles) return false;

        while (GlobalDirt.slotSpawners.size() <= slotCount) GlobalDirt.slotSpawners.add(null);


        try {
            ItemStack slotItem = slot.getItem();
            List<GuiParticleSpawner> itemGuiParticleSpawnerList = SpawnerRegistry.get(slotItem);
            List<ResourceLocation> currentSpawnersList = GlobalDirt.slotSpawners.getIdList(slotCount);
            List<ResourceLocation> itemSpawnersIdList = SpawnerRegistry.getList(slotItem);

            if (ItemInteractionsConfig.debugDraws) guiGraphics.renderOutline(slot.x, slot.y, 16, 16, 0xFFFFFFFF);

//            No previous spawner and no new spawner
            if (itemGuiParticleSpawnerList.isEmpty() && currentSpawnersList.isEmpty()) {
//                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, 16, 16, 0xFF);
                return false;
            }

//
//            The slot had a previous spawner but no longer will
            if (itemGuiParticleSpawnerList.isEmpty()) {
                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0xFFFF0000);
                GlobalDirt.slotSpawners.set(slotCount, new ArrayList<>());
                return false;
            }

            int globalX = slot.x + leftPos + 8;
            int globalY = slot.y + topPos + 8;


//            Slot was empty or had some spawners but will receive a new set of spawners
            if ((currentSpawnersList.isEmpty()) || !new HashSet<>(currentSpawnersList).containsAll(itemSpawnersIdList)) {
                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, currentSpawnersList.isEmpty() ? 0xFF00FF00 : 0xFFFFFF00);



                GlobalDirt.slotSpawners.set(slotCount, itemGuiParticleSpawnerList, isInventoryScrolling ? "onIdle" : "onPut");
                GlobalDirt.slotSpawners.tick(slotCount, spawnerTickDelta, guiGraphics, globalX, globalY, 0f, 0f);
                GlobalDirt.slotSpawners.setState(slotCount, "onIdle");


                return false;
            }




//            No spawner updates. Just tick
            if (ItemInteractionsConfig.debugDraws) guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x8000FF00);
            GlobalDirt.slotSpawners.tick(slotCount, spawnerTickDelta, guiGraphics, globalX, globalY, 0f, 0f);
            return false;

        } catch (Exception e) {
            if (!dead) Item_interactions_mod.warnMessage("Error! \n" + e);
            return true;
        }


    }


//    Is ran once per frame
    public static void mainLogic(GuiGraphics guiGraphics) {

        List<BaseParticle> shouldDelete = new ArrayList<>();
            if (GlobalDirt.shouldTickParticles) {


            // if the carried is empty or the item has no spawners then clear the carried spawner
            if (carriedItem == null || carriedItem.isEmpty() || SpawnerRegistry.get(carriedItem).isEmpty()) carriedGuiParticleSpawner.clear();

    //        if there is a carried item
            else if (carriedItem != null && !carriedItem.isEmpty()) {


    //            if its a different item from the previous tick
                if (!SpawnerRegistry.compareSpawner(carriedGuiParticleSpawner, carriedItem) || (carriedGuiParticleSpawner.isEmpty())) {

    //                if it has a spawner, use it and pick it up
                    List<GuiParticleSpawner> newGuiParticleSpawner = SpawnerRegistry.get(carriedItem);
                    if (!newGuiParticleSpawner.isEmpty()) {
                        carriedGuiParticleSpawner = newGuiParticleSpawner;
    //                    carriedGuiParticleSpawnerTimers.clear();
                        Collections.fill(carriedGuiParticleSpawnerTimer, 0f);
                        carriedGuiParticleSpawner.forEach((spawner) -> spawner.setState("onPickup"));
                        GlobalDirt.slotSpawners.tickSpawners(-1, carriedGuiParticleSpawner, spawnerTickDelta, guiGraphics, (float) lastMouseX, (float) lastMouseY, (float) mouseDeltaX, (float) mouseDeltaY);
                        carriedGuiParticleSpawner.forEach((spawner) -> spawner.setState("onIdle"));


                    }
                    else GlobalDirt.carriedGuiParticleSpawner.clear();
                }
            }
    //        Particle ticker

            for (BaseParticle particle : GlobalDirt.particleList) {
                particle.tick();
                particle.render();
                if (particle.shouldDelete) shouldDelete.add(particle);
            }

            if (carriedGuiParticleSpawner != null) {
                if (ItemInteractionsConfig.debugDraws) guiGraphics.fill((int) lastMouseX - 8, (int) lastMouseY - 8, (int) lastMouseX + 2, (int) lastMouseY + 2, 0xFF00FFFF);


                carriedGuiParticleSpawner.forEach((spawner) -> spawner.setState(GlobalDirt.isShaking ? "onShake" : "onCarried"));
                GlobalDirt.slotSpawners.tickSpawners(-1, carriedGuiParticleSpawner, spawnerTickDelta, guiGraphics, (float) lastMouseX, (float) lastMouseY, (float) mouseDeltaX, (float) mouseDeltaY);
                carriedGuiParticleSpawner.forEach((spawner) -> spawner.setState("onIdle"));

            }

            isInventoryScrolling = false;

        } else {
            for (BaseParticle particle : GlobalDirt.particleList) {
                particle.render();
//                if (particle.shouldDelete) shouldDelete.add(particle);
            }

        }

        GlobalDirt.particleList.removeAll(shouldDelete);


    }
}
