package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.CarriedInteractions.GuiParticleSpawnersLogic;
import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    @Shadow
    protected int topPos;

    @Shadow
    protected int leftPos;


    @Inject(
            at = @At("HEAD"),
            //~ if >= 26.1 'renderFloatingItem' -> 'extractFloatingItem'
            method = "extractFloatingItem"
    )
    protected void mixedRenderFloatingItem(GuiGraphicsExtractor guiGraphics,
                                           ItemStack itemStack,
                                           int i, int j,
                                           @Nullable String string,
                                           CallbackInfo callbackInfo) {
        GlobalDirt.carriedItem = itemStack;

    }

    @Inject(
            at = @At("HEAD"),
            //~ if >= 26.1 'render' -> 'extractRenderState'
            method = "extractRenderState"
    )
    public void renderMixinHead(GuiGraphicsExtractor guiGraphics, int i, int j, float f, CallbackInfo ci) {
        GlobalDirt.setGlobalGuiGraphicsExtractor(guiGraphics);
        GlobalDirt.updateTimer();
        GlobalDirt.slotCount = 0;

    }

    @Inject(
            at = @At("TAIL"),
            //~ if >= 26.1 'render' -> 'extractRenderState'
            method = "extractRenderState"
    )
    public void renderMixinTail(GuiGraphicsExtractor guiGraphics, int i, int j, float f, CallbackInfo ci) {
        GlobalDirt.tailUpdateTimer();
        GlobalDirt.updateMousePositions();

        if ((boolean) ItemInteractionsConfig.getSetting("debug")) {
            //~ if >= 21.6 'drawString' -> 'text' {
            guiGraphics.text(Minecraft.getInstance().font, "msCounter: " + msCounter, 0, 50, 0xFFFFFFFF);
            guiGraphics.text(Minecraft.getInstance().font, "absSpeed: " + absSpeed, 0, 60, isShaking ? 0xFFFFFF20 : 0xFFFFFFFF);
            //~}
        }

        if ((boolean) ItemInteractionsConfig.getSetting("gui_particles"))
            GuiParticleSpawnersLogic.mainLogic(guiGraphics);


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
    boolean item_interactions_mod$dead = false;


    @Inject(
            at = @At("TAIL"),
            //~ if >= 26.1 'render' -> 'extract'
            method = "extractSlot"
    )
    void checkForParticlesWhenRenderSlot(GuiGraphicsExtractor guiGraphics, Slot slot, int i, int j, CallbackInfo ci) {
        if (!(boolean) ItemInteractionsConfig.getSetting("gui_particles")) return;

        this.item_interactions_mod$dead = GuiParticleSpawnersLogic.checkAndTick(guiGraphics, slot, item_interactions_mod$dead, leftPos, topPos, GlobalDirt.slotCount);
        GlobalDirt.slotCount++;

    }


}



