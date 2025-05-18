package dev.mineland.item_interactions_mod;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimScale;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimSpeed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static dev.mineland.item_interactions_mod.GlobalDirt.*;

public class GuiRendererHelper {

    public static ItemStackRenderState currentItemStackRenderer;
    public static PoseStack currentPose;
    public static void renderItem(GuiGraphics guiGraphics, ItemStack itemStack, Level level, LivingEntity livingEntity, int k, Minecraft minecraft, int initialX, int initialY, int initialZ) {
        PoseStack newPose = new PoseStack();

        int x = initialX;
        int y = initialY;

        switch (ItemInteractionsConfig.getAnimationSetting()) {
            case ItemInteractionsConfig.animation.ANIM_SCALE -> {
                newPose = AnimScale.makePose(x+16, y+16, 0);
            }

            case ItemInteractionsConfig.animation.ANIM_SPEED -> {
                newPose = AnimSpeed.makePose(x+8, y+8, 150, speedX, speedY, isCurrentItem3d);
            }
        }
        newPose.pushPose();

        guiGraphics.pose().mulPose(newPose.last().pose());





    }
}
