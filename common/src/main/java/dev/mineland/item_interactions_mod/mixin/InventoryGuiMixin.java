package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.CarriedInteractions.Particles.BaseParticle;
import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.Spawner;
import dev.mineland.item_interactions_mod.CarriedInteractions.checkForParticles;
import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import dev.mineland.item_interactions_mod.ItemInteractionsResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import static dev.mineland.item_interactions_mod.GlobalDirt.*;


@Mixin(AbstractContainerScreen.class)
public abstract class InventoryGuiMixin {
    @Shadow protected int topPos;

    @Shadow protected int leftPos;



    @Inject(method = "renderFloatingItem",at = @At("HEAD"))
    protected void mixedRenderFloatingItem(GuiGraphics guiGraphics,
                                           ItemStack itemStack,
                                           int i, int j,
                                           @Nullable String string,
                                           CallbackInfo callbackInfo) {
        GlobalDirt.carriedItem = itemStack;





    }

    @Inject(method = "render", at = @At("HEAD"))
    public void renderMixinHead(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        GlobalDirt.updateTimer();
        GlobalDirt.slotCount = 0;

    }
    @Inject(method = "render", at = @At("TAIL"))
    public void renderMixinTail(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        GlobalDirt.tailUpdateTimer();
        GlobalDirt.updateMousePositions();

//        if (!GlobalDirt.shouldTickParticles) return;

        if (carriedItem == null || carriedItem.isEmpty() || ItemInteractionsResources.getFromItem(carriedItem) == null) carriedSpawner = null;
        else if (carriedItem != null && !carriedItem.isEmpty()) {
            if (!ItemInteractionsResources.compareSpawner(carriedSpawner, carriedItem)) {
                Spawner newSpawner = ItemInteractionsResources.getFromItem(carriedItem);

                if (newSpawner != null) {
                    carriedSpawner = newSpawner.newInstance(-1);
                    carriedSpawner.onCarried(guiGraphics, lastMouseX, lastMouseY, speedX*0.1, speedY*0.1, 0, 0);


                }
                else GlobalDirt.carriedSpawner = null;
            }
        }



        if (carriedSpawner != null) {
            if (ItemInteractionsConfig.debugDraws) guiGraphics.fill((int) lastMouseX - 8, (int) lastMouseY - 8, (int) lastMouseX + 2, (int) lastMouseY + 2, 0xFF00FFFF);
            carriedSpawner.tick(guiGraphics, lastMouseX - 8, lastMouseY - 8, mouseDeltaX, mouseDeltaY, 0, 0);
        }


        List<BaseParticle> shouldDelete = new ArrayList<>();
        for (BaseParticle particle : GlobalDirt.particleList) {
            particle.tick();
            particle.render();
            if (particle.shouldDelete) shouldDelete.add(particle);
        }

//        for (BaseParticle particle : shouldDelete) { ; }
        GlobalDirt.particleList.removeAll(shouldDelete);

    }

    @Inject(method = "init", at = @At("HEAD"))
    protected void initHeadMixin(CallbackInfo ci) {
        GlobalDirt.particleList.clear();
        GlobalDirt.slotSpawners.clear();

    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void initMixin(CallbackInfo ci) {
//        GlobalDirt.msCounter = 0;
//        GlobalDirt.topPos = this.topPos;
//        GlobalDirt.leftPos = this.leftPos;
//        GlobalDirt.speedX = 0;
//        GlobalDirt.speedY = 0;
//        this.dead = false;

        GlobalDirt.restore();
    }


    @Unique
    boolean dead = false;

    @Unique
    @Inject(method = "renderSlot", at = @At("TAIL"))
    void checkForParticlesWhenRenderSlot(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        this.dead = checkForParticles.check(guiGraphics, slot, dead, leftPos, topPos, GlobalDirt.slotCount);
        GlobalDirt.slotCount++;

    }




}



