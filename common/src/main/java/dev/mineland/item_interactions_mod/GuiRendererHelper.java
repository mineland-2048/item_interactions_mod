package dev.mineland.item_interactions_mod;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimTemplate;
import dev.mineland.item_interactions_mod.renderState.GuiFloatingItemRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static dev.mineland.item_interactions_mod.GlobalDirt.*;
import static dev.mineland.item_interactions_mod.MiscUtils.outOfBoundsPoint;
import static dev.mineland.item_interactions_mod.MiscUtils.samePoint;

public class GuiRendererHelper {

    public static ItemStackRenderState currentItemStackRenderer;
    public static PoseStack currentPose;

    public final static CachedOrthoProjectionMatrixBuffer itemsProjectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("items", -1000.0F, 1000.0F, true);

    private static float zNear = -1000f;
    private static float zFar = 1000f;
    private static boolean invertY = true;
    private static float f = 0;
    private static float g = 0;

    private static Matrix4f orthoMatrix = (new Matrix4f()).setOrtho(0.0F, f, invertY ? g : 0.0F, invertY ? 0.0F : g, zNear, zFar);



    public static void clearItem() {
        currentItemStackRenderer = new ItemStackRenderState();
        currentPose = new PoseStack();
    }

    public static ItemStack prevItem = ItemStack.EMPTY;
    public static void renderItem(GuiRenderState guiRenderState, ItemStack itemStack, Level level, LivingEntity livingEntity, int k, Minecraft minecraft, int initialX, int initialY, int initialZ) {
        ItemStackRenderState scratchItemStackRenderState = new ItemStackRenderState();
        int x = initialX;
        int y = initialY;
        AnimTemplate anim = ItemInteractionsConfig.getAnimationSetting();
        if (anim == null) return;

        if (prevItem.isEmpty() && !itemStack.isEmpty()) {
            anim.reset(initialX, initialY, 1000);
        }
        prevItem = itemStack;

        GuiGraphics guiGraphics = new GuiGraphics(Minecraft.getInstance(), guiRenderState);

        PoseStack newPose = anim.makePose(initialX, initialY ,0, speedX, speedY, isCurrentItem3d, guiGraphics);
//        newPose.pushPose();
        try {
            minecraft.getItemModelResolver().updateForTopItem(scratchItemStackRenderState, itemStack, ItemDisplayContext.GUI, level, livingEntity, k);

            AnimTemplate animationSetting = ItemInteractionsConfig.getAnimationSetting();
            float ivX = animationSetting.itemPos.x;
            float ivY = animationSetting.itemPos.y;

            int size = 64;

            int correction = (size / 2) - 8;
            int x0 = (int) (ivX * 16) + x - (correction);
            int y0 = (int) (ivY * 16) + y - (correction);
            int x1 = (int) (ivX * 16) + x - (correction) + size;
            int y1 = (int) (ivY * 16) + y - (correction) + size;

//            x0 = 200; x1 = 260;
//            y0 = 100; y1 = 120;
            int scX = x + (int) (ivX * 16) - correction;
            int scY = y + (int) (ivY * 16) - correction;



            if (ItemInteractionsConfig.debugDraws) guiGraphics.renderOutline(scX, scY, size, size, 0xFFFFFFFF);


            guiRenderState.submitPicturesInPictureState(
                    new GuiFloatingItemRenderState(
                            scratchItemStackRenderState,
                            new Vector3f(),
                            new Quaternionf(),
                            null,
                            x0, y0,
                            x1, y1,
                            16,
                            null,
                            newPose
                    )
            );

        }
        catch (Exception e) {
            ItemInteractionsMod.errorMessage("Crashed. " + e);
        }


    }


    public static void setPixel(GuiGraphics guiGraphics, int x, int y, int color) {
        guiGraphics.fill(x, y, x+1, y+1, color);
    }

    public static void renderLine(GuiGraphics guiGraphics, int x0, int y0, int x1, int y1, int color) {
        renderLine(guiGraphics, x0, y0, x1, y1, color, true);
    }

    public static void renderLine(GuiGraphics guiGraphics, float x0, float y0, float x1, float y1, int color) {
        renderLine(guiGraphics, x0, y0, x1, y1, color, true);
    }


//    TODO: fix non pixelated lines
    public static void renderLine(GuiGraphics guiGraphics, float x0, float y0, float x1, float y1, int color, boolean pixelated) {
//        if (pixelated) {
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
//        }





//        VertexConsumer vertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
////        CachedOrthoProjectionMatrixBuffer itemsProjectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("items", -1000.0F, 1000.0F, true);
//
//
//
//
//        Vector2f p0 = new Vector2f(x0, y0);
//        Vector2f p1 = new Vector2f(x1, y1);
//
//        float angle = (float) Math.atan2(y1 - y0, x1 - x0);
//
//
//        Vector2f[] points = new Vector2f[] {new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f()};
//
//        points[0] = MiscUtils.pointAtFrom(new Vector2f((float) (+ (Math.PI*0.5)) + angle, 0.5f), p0);
//        points[1] = MiscUtils.pointAtFrom(new Vector2f((float) (+ (Math.PI*0.5)) + angle, 0.5f), p1);
//        points[2] = MiscUtils.pointAtFrom(new Vector2f((float) (- (Math.PI*0.5)) + angle, 0.5f), p1);
//        points[3] = MiscUtils.pointAtFrom(new Vector2f((float) (- (Math.PI*0.5)) + angle, 0.5f), p0);
//
//        float brX = points[0].x();
//        float brY = points[0].y();
//
//        float trX = points[1].x();
//        float trY = points[1].y();
//
//        float tlX  = points[2].x();
//        float tlY  = points[2].y();
//
//        float blX  = points[3].x();
//        float blY  = points[3].y();
//
//
//        vertexConsumer.addVertex(orthoMatrix, brX, brY, (float) 0).setColor(color);
//        vertexConsumer.addVertex(orthoMatrix, trX, trY, (float) 0).setColor(color);
//        vertexConsumer.addVertex(orthoMatrix, tlX, tlY, (float) 0).setColor(color);
//        vertexConsumer.addVertex(orthoMatrix, blX, blY, (float) 0).setColor(color);



//        guiGraphics.drawString(Minecraft.getInstance().font, ""+line, 0, 0, 0xFFFFFFFF);
//        System.out.println(line);



    }


//    TODO:
//     - add line colors per length
//     - add line color gradients

    public static void renderLines_RepeatColors(GuiGraphics guiGraphics, float[][] points, int[] colors, boolean pixelated) {
        int pointsLength = points.length;
        int colorsLength = colors.length;

        int length = Math.max(pointsLength, colorsLength);
        int[] newColors = new int[length + 1];

        for (int i = 0; i <= length; i++) {
            newColors[i] = colors[i%colors.length];
        }

        renderLines(guiGraphics, points, newColors, pixelated);
    }

    public static void renderLine_ColorPattern(GuiGraphics guiGraphics, float x0, float y0, float x1, float y1, int[] colors, int repeats, boolean pixelated) {
        if (repeats < 1) repeats = 1;
        if (samePoint(x0,y0,x1,y1)) return;
        if (colors.length == 0) return;

        int length = repeats * colors.length;
        float[][] points = new float[1 + length][2];

        for (int i = 0; i <= length; i++) {
            float x, y;
            float progress = (float) i / length;

            x = MiscUtils.lerp(progress, x0, x1);
            y = MiscUtils.lerp(progress, y0, y1);

            points[i] = new float[]{x, y};
        }

        renderLines_RepeatColors(guiGraphics, points, colors, pixelated);



    }

//    TODO: maybe add lerped colored lines.
//    public static void renderLines_LerpColors(GuiGraphics guiGraphics, float[][] points, int[] colors, boolean pixelated) {
//        int[] newColors = new int[points.length];
//
//        for (int i = 0; i < newColors.length; i++) {
//            newColors[i] =
//        }
//
//    }

    private static void renderLines(GuiGraphics guiGraphics, float[][] points, int[] colors, boolean pixelated) {
        if (points.length == 0) return;

        renderPixelatedLines(guiGraphics, points, colors);

//        if (pixelated) renderPixelatedLines(guiGraphics, points, colors);
//        else renderSmoothLines(guiGraphics, points, colors);
    }

    private static void renderPixelatedLines(GuiGraphics guiGraphics, float[][] points, int[] colors) {

        if (points.length == 1) setPixel(guiGraphics, (int) points[0][0], (int) points[0][1], colors[0]);

        for (int i = 0; i < points.length - 1; i++) {
            float[] currentPoint = points[i];
            float[] nextPoint = points[i+1];
            if (samePoint( (int) currentPoint[0], (int) currentPoint[1],
                           (int) nextPoint[0], (int) nextPoint[1])
            ) {
                continue;
            }

            if (outOfBoundsPoint(currentPoint[0], currentPoint[1])) break;

            renderLine(guiGraphics, currentPoint[0], currentPoint[1], nextPoint[0], nextPoint[1], colors[i], true);
        }


    }

//    private static void renderSmoothLines(GuiGraphics guiGraphics, float[][] points, int[] colors) {
//        VertexConsumer vertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
//
//        for (int i=0; i < points.length-1; i++) {
//            float x0 = points[i][0],    y0 = points[i][1];
//            float x1 = points[i+1][0],  y1 = points[i+1][1];
//
//
//
//            Vector2f p0 = new Vector2f(x0, y0);
//            Vector2f p1 = new Vector2f(x1, y1);
//
//            float angle = (float) Math.atan2(y1 - y0, x1 - x0);
//
//
//            Vector2f[] quadPoints = new Vector2f[] {new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f()};
//
//            quadPoints[0] = MiscUtils.pointAtFrom(new Vector2f((float) (+ (Math.PI*0.5)) + angle, 0.5f), p0);
//            quadPoints[1] = MiscUtils.pointAtFrom(new Vector2f((float) (+ (Math.PI*0.5)) + angle, 0.5f), p1);
//            quadPoints[2] = MiscUtils.pointAtFrom(new Vector2f((float) (- (Math.PI*0.5)) + angle, 0.5f), p1);
//            quadPoints[3] = MiscUtils.pointAtFrom(new Vector2f((float) (- (Math.PI*0.5)) + angle, 0.5f), p0);
//
//            float brX = quadPoints[0].x();
//            float brY = quadPoints[0].y();
//
//            float trX = quadPoints[1].x();
//            float trY = quadPoints[1].y();
//
//            float tlX  = quadPoints[2].x();
//            float tlY  = quadPoints[2].y();
//
//            float blX  = quadPoints[3].x();
//            float blY  = quadPoints[3].y();
//
//
//            vertexConsumer.addVertex(orthoMatrix, brX, brY, (float) 0).setColor(colors[i]);
//            vertexConsumer.addVertex(orthoMatrix, trX, trY, (float) 0).setColor(colors[i]);
//            vertexConsumer.addVertex(orthoMatrix, tlX, tlY, (float) 0).setColor(colors[i]);
//            vertexConsumer.addVertex(orthoMatrix, blX, blY, (float) 0).setColor(colors[i]);
//
//        }
//
//
//
//    }

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

        int l = x0, r = x0;


        for (int x = x0; x < x1; x++) {
//            setPixel(guiGraphics, x, y, color);
            if (D > 0) {

                r = x;
                guiGraphics.fill(l, y, r, y+1, color);
                l = x;

                y = y + yi;
                D = D + (2 * (dy - dx));
            } else D = D + 2 * dy;

            if (outOfBoundsPoint(x, y)) {
                guiGraphics.fill(l, y, x, y+1, color);
                break;
            }

        }

        guiGraphics.fill(l, y, x1, y+1, color);



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
        int t = y0, b = y0;
        for (int y = y0; y < y1; y++) {
//            setPixel(guiGraphics, x, y, color);
            if (D > 0) {
                t = y;
                guiGraphics.fill(x, t, x+1, b, color);
                b = y;
                x = x + xi;
                D = D + (2 * (dx - dy));
            } else D = D + 2 * dx;

            if (outOfBoundsPoint(x, y)) {
                guiGraphics.fill(x, t, x+1, b, color);
                break;
            }
        }
        guiGraphics.fill(x, t, x+1, y1, color);

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
