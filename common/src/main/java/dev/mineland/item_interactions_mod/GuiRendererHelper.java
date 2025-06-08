package dev.mineland.item_interactions_mod;

import com.mojang.blaze3d.vertex.*;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimTemplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static dev.mineland.item_interactions_mod.GlobalDirt.*;
import static dev.mineland.item_interactions_mod.GuiRendererHelper.setPixel;

public class GuiRendererHelper {

    public static ItemStack prevItem = ItemStack.EMPTY;
    public static void renderItem(GuiGraphics guiGraphics, ItemStack itemStack, Level level, LivingEntity livingEntity, int k, Minecraft minecraft, int initialX, int initialY, int initialZ) {
        AnimTemplate anim = ItemInteractionsConfig.getAnimationSetting();
        if (anim == null) return;

        if (prevItem.isEmpty() && !itemStack.isEmpty()) {
            anim.reset(initialX, initialY, initialZ);
        }
        prevItem = itemStack;

        PoseStack newPose = anim.makePose(initialX, initialY,0, speedX, speedY, isCurrentItem3d, guiGraphics);
        newPose.pushPose();

        guiGraphics.pose().mulPose(newPose.last().pose());

    }


    public static void setPixel(GuiGraphics guiGraphics, int x, int y, int color) {
        guiGraphics.fill(x, y, x+1, y+1, color);
    }

    public static void renderLine(GuiGraphics guiGraphics, int x0, int y0, int x1, int y1, int color) {
        renderLine(guiGraphics, x0, y0, x1, y1, color, true);
    }

    public static void renderLine(GuiGraphics guiGraphics, float x0, float y0, float x1, float y1, int color, boolean pixelated) {
        if (pixelated) {
            if (x0 == x1 || y0 == y1) {
                int px = 0, py = 0;
                if (y0 != y1) {
                    px = 1;
                }

                if (x0 != x1) {
                    py = 1;
                }

                guiGraphics.fill((int) x0, (int) y0, (int) x1 + px, (int) y1+py, color);
                return;

            }

            LineAlgs.plotLine(guiGraphics, (int) x0, (int) y0, (int) x1, (int) y1, color);
            return;
        }

        VertexConsumer vertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.gui());
        Matrix4f matrix4f = guiGraphics.pose().last().pose();

//        if (x0 > x1) {
//            float o = x0;
//            x0 = x1;
//            x1 = o;
//        }
//
//        if (y0 < y1) {
//            float o = y0;
//            y0 = y1;
//            y1 = o;
//        }

        Vector2f p0 = new Vector2f(x0, y0);
        Vector2f p1 = new Vector2f(x1, y1);

        float angle = (float) Math.atan2(y1 - y0, x1 - x0);
        float mag = p0.distance(p1);

        Vector2f line = new Vector2f(angle, mag);








        Vector2f[] points = new Vector2f[] {new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f()};

        points[0] = MiscUtils.pointAtFrom(new Vector2f((float) (+ (Math.PI*0.5)) + angle, 0.5f), p0);
        points[1] = MiscUtils.pointAtFrom(new Vector2f((float) (+ (Math.PI*0.5)) + angle, 0.5f), p1);
        points[2] = MiscUtils.pointAtFrom(new Vector2f((float) (- (Math.PI*0.5)) + angle, 0.5f), p1);
        points[3] = MiscUtils.pointAtFrom(new Vector2f((float) (- (Math.PI*0.5)) + angle, 0.5f), p0);

        float brX = points[0].x();
        float brY = points[0].y();

        float trX = points[1].x();
        float trY = points[1].y();

        float tlX  = points[2].x();
        float tlY  = points[2].y();

        float blX  = points[3].x();
        float blY  = points[3].y();


        vertexConsumer.addVertex(matrix4f, brX, brY, (float) 0).setColor(color);
        vertexConsumer.addVertex(matrix4f, trX, trY, (float) 0).setColor(color);
        vertexConsumer.addVertex(matrix4f, tlX, tlY, (float) 0).setColor(color);
        vertexConsumer.addVertex(matrix4f, blX, blY, (float) 0).setColor(color);



//        guiGraphics.drawString(Minecraft.getInstance().font, ""+line, 0, 0, 0xFFFFFFFF);
//        System.out.println(line);



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
