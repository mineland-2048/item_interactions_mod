package dev.mineland.item_interactions_mod;

import com.mojang.blaze3d.vertex.*;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimTemplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static dev.mineland.item_interactions_mod.GlobalDirt.*;

public class GuiRendererHelper {

    public static ItemStack prevItem = ItemStack.EMPTY;
    public static void renderItem(GuiGraphics guiGraphics, ItemStack itemStack, Level level, LivingEntity livingEntity, int k, Minecraft minecraft, int initialX, int initialY, int initialZ) {
        AnimTemplate anim = ItemInteractionsConfig.getAnimationSetting();
        if (anim == null) return;

        if (prevItem.isEmpty() && !itemStack.isEmpty()) {
            anim.reset();
        }
        prevItem = itemStack;

        PoseStack newPose = anim.makePose(initialX, initialY,0, speedX, speedY, isCurrentItem3d);
        newPose.pushPose();

        guiGraphics.pose().mulPose(newPose.last().pose());

    }
}
