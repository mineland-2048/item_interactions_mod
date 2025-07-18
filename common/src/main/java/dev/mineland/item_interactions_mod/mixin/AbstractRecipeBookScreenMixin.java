package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.CarriedInteractions.GuiParticleSpawnersLogic;
import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
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


@Mixin(AbstractRecipeBookScreen.class)
public abstract class AbstractRecipeBookScreenMixin {
//    @Shadow protected int topPos;
//
//    @Shadow protected int leftPos;




    @Inject(method = "render", at = @At("HEAD"))
    public void renderMixinHead(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        GlobalDirt.renderHead(guiGraphics);
    }


    @Inject(method = "render", at = @At("TAIL"))
    public void renderMixinTail(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        GlobalDirt.renderTail(guiGraphics);

    }



//    @Inject(method = "init", at = @At("HEAD"))
//    protected void initHeadMixin(CallbackInfo ci) {
//        GlobalDirt.particleList.clear();
//        slotSpawners.clear();
//    }
//
//    @Inject(method = "init", at = @At("TAIL"))
//    protected void initMixin(CallbackInfo ci) {
//        GlobalDirt.restore();
//    }


//    @Unique
//    boolean dead = false;


//    @Inject(method = "renderSlot", at = @At("TAIL"))
//    void checkForParticlesWhenRenderSlot(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
//        if (! (boolean) ItemInteractionsConfig.getSetting("gui_particles")) return;
//
//        this.dead = GuiParticleSpawnersLogic.checkAndTick(guiGraphics, slot, dead, leftPos, topPos, GlobalDirt.slotCount);
//        GlobalDirt.slotCount++;
//
//    }




}



