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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static dev.mineland.item_interactions_mod.GlobalDirt.*;
import static dev.mineland.item_interactions_mod.MiscUtils.outOfBoundsPoint;
import static dev.mineland.item_interactions_mod.MiscUtils.samePoint;

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
        int x = initialX;
        int y = initialY;
        AnimTemplate anim = ItemInteractionsConfig.getAnimationSetting();
        if (anim == null) return;

        if (prevItem.isEmpty() && !itemStack.isEmpty()) {
            anim.reset(initialX, initialY, initialZ);
        }
        prevItem = itemStack;

        PoseStack newPose = anim.makePose(initialX, initialY,0, speedX, speedY, isCurrentItem3d, new GuiGraphics(Minecraft.getInstance(), guiRenderState));
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


        Vector2f p0 = new Vector2f(x0, y0);
        Vector2f p1 = new Vector2f(x1, y1);

        float angle = (float) Math.atan2(y1 - y0, x1 - x0);


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

        if (pixelated) renderPixelatedLines(guiGraphics, points, colors);
        else renderSmoothLines(guiGraphics, points, colors);
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

    private static void renderSmoothLines(GuiGraphics guiGraphics, float[][] points, int[] colors) {
        VertexConsumer vertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.gui());
        Matrix4f matrix4f = guiGraphics.pose().last().pose();

        for (int i=0; i < points.length-1; i++) {
            float x0 = points[i][0],    y0 = points[i][1];
            float x1 = points[i+1][0],  y1 = points[i+1][1];


            Vector2f p0 = new Vector2f(x0, y0);
            Vector2f p1 = new Vector2f(x1, y1);

            float angle = (float) Math.atan2(y1 - y0, x1 - x0);


            Vector2f[] quadPoints = new Vector2f[] {new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f()};

            quadPoints[0] = MiscUtils.pointAtFrom(new Vector2f((float) (+ (Math.PI*0.5)) + angle, 0.5f), p0);
            quadPoints[1] = MiscUtils.pointAtFrom(new Vector2f((float) (+ (Math.PI*0.5)) + angle, 0.5f), p1);
            quadPoints[2] = MiscUtils.pointAtFrom(new Vector2f((float) (- (Math.PI*0.5)) + angle, 0.5f), p1);
            quadPoints[3] = MiscUtils.pointAtFrom(new Vector2f((float) (- (Math.PI*0.5)) + angle, 0.5f), p0);

            float brX = quadPoints[0].x();
            float brY = quadPoints[0].y();

            float trX = quadPoints[1].x();
            float trY = quadPoints[1].y();

            float tlX  = quadPoints[2].x();
            float tlY  = quadPoints[2].y();

            float blX  = quadPoints[3].x();
            float blY  = quadPoints[3].y();


            vertexConsumer.addVertex(matrix4f, brX, brY, (float) 0).setColor(colors[i]);
            vertexConsumer.addVertex(matrix4f, trX, trY, (float) 0).setColor(colors[i]);
            vertexConsumer.addVertex(matrix4f, tlX, tlY, (float) 0).setColor(colors[i]);
            vertexConsumer.addVertex(matrix4f, blX, blY, (float) 0).setColor(colors[i]);

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
