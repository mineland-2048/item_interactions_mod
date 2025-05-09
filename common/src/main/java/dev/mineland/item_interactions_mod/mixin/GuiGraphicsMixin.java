package dev.mineland.item_interactions_mod.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimScale;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimSpeed;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig.animation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import static dev.mineland.item_interactions_mod.GlobalDirt.*;

@Mixin(value = GuiGraphics.class)
public abstract class GuiGraphicsMixin{//
    @Shadow @Final private ItemStackRenderState scratchItemStackRenderState;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private PoseStack pose;

    @Shadow public abstract void flush();

    @Shadow @Final private MultiBufferSource.BufferSource bufferSource;

//    @Shadow public abstract void drawCenteredString(Font arg, String string, int i, int j, int k);

    @Shadow public abstract int drawString(Font arg, String string, int i, int j, int k);

//    @Shadow public abstract void fill(int i, int j, int k, int l, int m);

    @Unique float iteminteractions$animScaleScale;

    @Unique int iteminteractions$offset = -8;


//    smooth-swapping compat by doing the item tilting after their swap
    @Inject(order = 1500, at = @At("HEAD"), method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V")
    private void renderItemHead(LivingEntity livingEntity, Level level, ItemStack itemStack, int i, int j, int k, int l, CallbackInfo ci) {

        if (!itemStack.isEmpty() && GlobalDirt.carriedItem == itemStack) {
            this.minecraft.getItemModelResolver().updateForTopItem(this.scratchItemStackRenderState, itemStack, ItemDisplayContext.GUI, level, livingEntity, k);
            GlobalDirt.isCurrentItem3d = this.scratchItemStackRenderState.usesBlockLight();

            if (iteminteractions$canAnimate()) {

                this.pose.pushPose();
                switch (ItemInteractionsConfig.animationConfig) {
                    case animation.ANIM_SCALE -> {
                        this.pose.mulPose( AnimScale.makePose(this.pose, i, j, 0).last().pose() );
                    }

                    case animation.ANIM_SPEED -> {
                        PoseStack newPose = AnimSpeed.makePose(this.pose, i, j, 150, speedX, speedY, GlobalDirt.isCurrentItem3d);
                        this.pose.mulPose(newPose.last().pose());
                    }
                }

//                this.fill(i - 8, j - 8, i+8, j+8, 0xFFFFFFFF);
//                if (!skipCalcs) {
//                    lastMouseX = (float) Minecraft.getInstance().mouseHandler.xpos();
//                    lastMouseY = (float) Minecraft.getInstance().mouseHandler.ypos();
//
//                    this.drawString(this.minecraft.font, "lasMouseX: " + lastMouseX, i - 50, j - 80,0xFFFFFFFF);
//                    this.drawString(this.minecraft.font, "lasMouseY: " + lastMouseY, i - 50, j - 70,0xFFFFFFFF);
//                }


            }

        }
    }


    @Inject(at = @At("TAIL"), method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V")
    private void renderItemTail(LivingEntity livingEntity, Level level, ItemStack itemStack, int i, int j, int k, int l, CallbackInfo ci) {
        if (!itemStack.isEmpty() && GlobalDirt.carriedItem == itemStack) {
            if (iteminteractions$canAnimate()) {
                this.pose.popPose();
//                switch (ItemInteractionsConfig.animationConfig) {
//                    case ANIM_SPEED: {
//                        this.pose.mulPose(AnimSpeed.makeRollback(this.pose, i, j, 150, speedX, speedY).last().pose());
//                        break;
//                    }
//
//                    case ANIM_SCALE:
//                        this.pose.mulPose(AnimScale.rollback(this.pose, i, j, 0).last().pose());
//                        break;
//                }
            }



            
        }
    }


    @Unique
    private static boolean iteminteractions$canAnimate() {
        if (Minecraft.getInstance().level == null) return true;

        return Minecraft.getInstance().level.tickRateManager().runsNormally() || Minecraft.getInstance().isPaused();
    }


}
