package dev.mineland.item_interactions_mod;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimScale;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimSpeed;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimTemplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static dev.mineland.item_interactions_mod.GlobalDirt.*;

public class GuiRendererHelper {

    public static ItemStackRenderState currentItemStackRenderer;
    public static PoseStack currentPose;

    private final static CachedOrthoProjectionMatrixBuffer itemsProjectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("items", -1000.0F, 1000.0F, true);
    public static void clearItem() {
        currentItemStackRenderer = new ItemStackRenderState();
        currentPose = new PoseStack();
    }

    public static ItemStack prevItem = ItemStack.EMPTY;
    public static void renderItem(GuiRenderState guiRenderState, ItemStack itemStack, Level level, LivingEntity livingEntity, int k, Minecraft minecraft, int initialX, int initialY, int initialZ) {
        ItemStackRenderState scratchItemStackRenderState = new ItemStackRenderState();
        PoseStack newPose = new PoseStack();

        int x = initialX;
        int y = initialY;
        AnimTemplate anim = ItemInteractionsConfig.getAnimationSetting();
        if (anim == null) return;

        if (prevItem.isEmpty() && !itemStack.isEmpty()) {
            anim.reset();
        }
        prevItem = itemStack;

        PoseStack newPose = anim.makePose(initialX, initialY,0, speedX, speedY, isCurrentItem3d);
        newPose.pushPose();
        try {
            minecraft.getItemModelResolver().updateForTopItem(scratchItemStackRenderState, itemStack, ItemDisplayContext.GUI, level, livingEntity, k);
            guiRenderState.submitPicturesInPictureState(
                    new GuiFloatingItemRenderState(
                            scratchItemStackRenderState,
                            new Vector3f(),
                            new Quaternionf(),
                            null,
                            x-8, y-8,
                            x+24, y+24,
                            16,
                            new ScreenRectangle(x-8,y-8,32,32),
                            newPose
                    )
            );

        }
        catch (Exception e) {
            Item_interactions_mod.errorMessage("Crashed. " + e);
        }


    }
}
