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
import static dev.mineland.item_interactions_mod.GuiRendererHelper.setPixel;

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

        PoseStack newPose = anim.makePose(initialX, initialY,0, speedX, speedY, isCurrentItem3d, guiGraphics);
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


    public static void setPixel(GuiGraphics guiGraphics, int x, int y, int color) {
        guiGraphics.fill(x, y, x+1, y+1, color);
    }

    public static void renderLine(GuiGraphics guiGraphics, int x0, int y0, int x1, int y1, int color) {
        LineAlgs.plotLine(guiGraphics, x0,y0,x1,y1, color);
    }

// Source: https://zingl.github.io/Bresenham.pdf
    void plotLine3D (GuiGraphics guiGraphics, int x0, int y0, int z0, int x1, int y1, int z1, int color) {
        int dx = Math.abs(x1-x0), sx = x0<x1 ? 1:-1;
        int dy = Math.abs(y1-y0), sy = y0<y1 ? 1:-1;
        int dz = Math.abs(z1-z0), sz = z0<z1 ? 1:-1;
        int dm = Math.max(Math.max(dx,dy) ,dz), i = dm; /* maximum difference */
        for (x1 = y1 = z1 = i/2; i-- >= 0; ) { /* loop */
            setPixel(guiGraphics, x0,y0,color);
            x1 -= dx; if (x1 < 0) { x1 += dm; x0 += sx; }
            y1 -= dy; if (y1 < 0) { y1 += dm; y0 += sy; }
            z1 -= dz; if (z1 < 0) { z1 += dm; z0 += sz; }
        }
    }
}


class LineAlgs {
    public static void plotLineLow(GuiGraphics guiGraphics, int x0, int y0, int x1, int y1, int color) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int yi = 1;

        if (dy < 0) {
            yi = -1;
            dy = -dy;
        }

        int D = (2 * dy) - dx;
        int y = y0;

        for (int x = x0; x < x1; x++) {
            setPixel(guiGraphics, x, y, color);
            if (D > 0) {
                y = y + yi;
                D = D + (2 * (dy - dx));
            } else D = D + 2 * dy;

        }

    }

    public static void plotLineHigh(GuiGraphics guiGraphics, int x0, int y0, int x1, int y1, int color) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int xi = 1;

        if (dx < 0) {
            xi = -1;
            dx = -dx;
        }

        int D = (2 * dx) - dy;
        int x = x0;

        for (int y = y0; y < y1; y++) {
            setPixel(guiGraphics, x, y, color);
            if (D > 0) {
                x = x + xi;
                D = D + (2 * (dx - dy));
            } else D = D + 2 * dx;

        }
    }

    public static void plotLine(GuiGraphics guiGraphics, int x0, int y0, int x1, int y1, int color) {
        if (Math.abs(y1 - y0) < Math.abs(x1-x0)) {
            if (x0 > x1) plotLineLow(guiGraphics, x1,y1,x0,y0, color);
            else plotLineLow(guiGraphics, x0, y0, x1, y1, color);
            return;
        }

        if (y0 > y1) plotLineHigh(guiGraphics, x1, y1, x0, y0, color);
        else plotLineHigh(guiGraphics, x0, y0, x1, y1, color);
    }

}
