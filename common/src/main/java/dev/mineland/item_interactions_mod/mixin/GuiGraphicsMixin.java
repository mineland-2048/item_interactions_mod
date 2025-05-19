package dev.mineland.item_interactions_mod.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.GuiRendererHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiGraphics.class)
public abstract class GuiGraphicsMixin{//
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private PoseStack pose;

//    smooth-swapping compat by doing the item tilting after their swap
    @Inject(order = 1500, at = @At("HEAD"), method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V")
    private void renderItemHead(LivingEntity livingEntity, Level level, ItemStack itemStack, int i, int j, int k, CallbackInfo ci) {
        if (!itemStack.isEmpty() && GlobalDirt.carriedItem == itemStack) {

            GuiGraphics self = (GuiGraphics) (Object) this;
            BakedModel scratchItemStackRenderState = this.minecraft.getItemRenderer().getModel(itemStack, level, livingEntity, k);

            GlobalDirt.isCurrentItem3d = scratchItemStackRenderState.usesBlockLight();

            if (iteminteractions$canAnimate()) {
                pose.pushPose();
                GuiRendererHelper.renderItem(self, itemStack, level, livingEntity, k, minecraft, i, j, 20000);
            }

        }
    }


    @Inject(at = @At("TAIL"), method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V")
    private void renderItemTail(LivingEntity livingEntity, Level level, ItemStack itemStack, int i, int j, int k, CallbackInfo ci) {
        if (!itemStack.isEmpty() && GlobalDirt.carriedItem == itemStack) {
            if (iteminteractions$canAnimate()) {
                pose.popPose();
            }

        }
    }


    @Unique
    private static boolean iteminteractions$canAnimate() {
        if (Minecraft.getInstance().level == null) return true;

        return Minecraft.getInstance().level.tickRateManager().runsNormally() || Minecraft.getInstance().isPaused();
    }


}
