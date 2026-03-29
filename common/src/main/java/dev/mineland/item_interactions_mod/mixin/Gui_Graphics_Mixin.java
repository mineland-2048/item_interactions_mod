package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.GuiRendererHelper;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

//~ if >= 26.1 'net.minecraft.client.gui.render.state.GuiRenderState' -> 'net.minecraft.client.renderer.state.gui.GuiRenderState'
import net.minecraft.client.renderer.state.gui.GuiRenderState;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiGraphicsExtractor.class)
// using underscores because stonecutter replaces this string
public abstract class Gui_Graphics_Mixin{//
    @Shadow @Final private Minecraft minecraft;

    @Shadow @Final private GuiRenderState guiRenderState;

    @Shadow @Final private Matrix3x2fStack pose;

    //    smooth-swapping compat by doing the item tilting after their swap
    @Inject(
            at = @At("HEAD"),
            //~ if >= 26.1 'renderItem(' -> 'item('
            method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V"
    )
    private void renderItemHead(LivingEntity livingEntity, Level level, ItemStack itemStack, int x, int y, int seed, CallbackInfo ci) {
        if (!itemStack.isEmpty() && GlobalDirt.carriedItem == itemStack) {

//            GuiGraphicsExtractor self = (GuiGraphicsExtractor) (Object) this;
            ItemStackRenderState scratchItemStackRenderState = new ItemStackRenderState();
            this.minecraft.getItemModelResolver().updateForTopItem(scratchItemStackRenderState, itemStack, ItemDisplayContext.GUI, level, livingEntity, seed);

            GlobalDirt.isCurrentItem3d = scratchItemStackRenderState.usesBlockLight();

            if (item_interactions_mod$canAnimate() && !ItemInteractionsConfig.getAnimationSetting().getId().equals("none")) {
                GuiRendererHelper.renderItem(this.guiRenderState, itemStack, level, livingEntity, seed, minecraft, x, y, 20000);


//                Need to check mod compat here
                pose.pushMatrix();
                pose.scale(0, 0);

            }

        }
    }


    //~ if >= 26.1 'renderItem(' -> 'item('
    @Inject(at = @At("TAIL"), method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V")
    private void renderItemTail(LivingEntity livingEntity, Level level, ItemStack itemStack, int i, int j, int k, CallbackInfo ci) {
        if (!itemStack.isEmpty() && GlobalDirt.carriedItem == itemStack) {
            if (item_interactions_mod$canAnimate() && !ItemInteractionsConfig.getAnimationSetting().getId().equals("none")) {
                pose.popMatrix();
            }

        }
    }

    @Inject(
            at = @At("TAIL"),
            //~ if >= 26.1 '/gui/render/state/' -> '/renderer/state/gui/'
            method = "<init>(Lnet/minecraft/client/Minecraft;Lorg/joml/Matrix3x2fStack;Lnet/minecraft/client/renderer/state/gui/GuiRenderState;II)V"
    )
    private void setGlobalGuiGraphicsExtractor(Minecraft minecraft, Matrix3x2fStack matrix3x2fStack, GuiRenderState guiRenderState, int x, int y, CallbackInfo ci) {
        var self = (GuiGraphicsExtractor) (Object) this;
        GlobalDirt.setGlobalGuiGraphicsExtractor(self);
        GlobalDirt.setGlobalGuiRenderState(guiRenderState);
    }

    @Unique
    private static boolean item_interactions_mod$canAnimate() {
        if (Minecraft.getInstance().level == null) return true;

        return Minecraft.getInstance().level.tickRateManager().runsNormally() || Minecraft.getInstance().isPaused();
    }


}
