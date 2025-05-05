package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.CarriedInteractions.GuiParticleSpawnersLogic;
import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
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

        if (ItemInteractionsConfig.debugDraws) {
            guiGraphics.drawString(Minecraft.getInstance().font, "msCounter: " + msCounter, 0, 50, 0xFFFFFFFF);
            guiGraphics.drawString(Minecraft.getInstance().font, "absSpeed: " + absSpeed, 0, 60, isShaking ? 0xFFFFFF20 : 0xFFFFFFFF);
        }

        if (ItemInteractionsConfig.enableGuiParticles) GuiParticleSpawnersLogic.mainLogic(guiGraphics);

        carriedItem = ItemStack.EMPTY;



    }

    @Inject(method = "init", at = @At("HEAD"))
    protected void initHeadMixin(CallbackInfo ci) {
        GlobalDirt.particleList.clear();
        GlobalDirt.slotSpawners.clear();

    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void initMixin(CallbackInfo ci) {
        GlobalDirt.restore();
    }


    @Unique
    boolean dead = false;

    @Inject(method = "renderSlot", at = @At("TAIL"))
    void checkForParticlesWhenRenderSlot(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        if (!ItemInteractionsConfig.enableGuiParticles) return;
        this.dead = GuiParticleSpawnersLogic.checkAndTick(guiGraphics, slot, dead, leftPos, topPos, GlobalDirt.slotCount);
        GlobalDirt.slotCount++;

    }




}



