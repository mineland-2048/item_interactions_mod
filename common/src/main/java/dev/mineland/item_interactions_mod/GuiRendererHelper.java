package dev.mineland.item_interactions_mod;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimScale;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimSpeed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static dev.mineland.item_interactions_mod.GlobalDirt.*;

public class GuiRendererHelper {


// copied this because i cant use this for some reason

    private static final HashMap<ResourceLocation, int[]> particleSizesCache = new HashMap<>();

    public static void clearCache() { particleSizesCache.clear();}
    public static void blit(PoseStack pose, ResourceLocation resourceLocation, int i, int j, float f, float g, int k, int l, int m, int n, int color) {
        blit(pose, resourceLocation, i, j, k, l, f, g, k, l, m, n, color);
    }

    static void blit(PoseStack pose, ResourceLocation resourceLocation, int i, int j, int k, int l, float f, float g, int m, int n, int o, int p, int color) {
        blit(pose, resourceLocation, i, i + k, j, j + l, 0, m, n, f, g, o, p, color);
    }
    static void blit(PoseStack pose, ResourceLocation resourceLocation, int i, int j, int k, int l, int m, int n, int o, float f, float g, int p, int q, int color) {
        blit(pose, resourceLocation, i, j, k, l, m, (f + 0.0F) / (float)p, (f + (float)n) / (float)p, (g + 0.0F) / (float)q, (g + (float)o) / (float)q, color);
    }
    static void blit(PoseStack pose, ResourceLocation resourceLocation, int i, int j, int k, int l, int m, float f, float g, float h, float n, int color) {
        float alpha,red,green,blue;


        alpha = (float) ((color >> 24) & 0xff) / 255;
        red = (float) ((color >> 16) & 0xff) / 255;
        green = (float) ((color >> 8) & 0xff) / 255;
        blue = (float) (color & 0xff) / 255;
        innerBlitTinted(pose, resourceLocation, i, j, k, l, m, f, g, h, n, red, green, blue, alpha);

    }


    static void innerBlitTinted(PoseStack pose, ResourceLocation resourceLocation, int i, int j, int k, int l, int m, float f, float g, float h, float n, float o, float p, float q, float r) {
        RenderSystem.setShaderTexture(0, resourceLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = pose.last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.addVertex(matrix4f, (float)i, (float)k, (float)m).setUv(f, h).setColor(o, p, q, r);
        bufferBuilder.addVertex(matrix4f, (float)i, (float)l, (float)m).setUv(f, n).setColor(o, p, q, r);
        bufferBuilder.addVertex(matrix4f, (float)j, (float)l, (float)m).setUv(g, n).setColor(o, p, q, r);
        bufferBuilder.addVertex(matrix4f, (float)j, (float)k, (float)m).setUv(g, h).setColor(o, p, q, r);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

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

    public static int[] getSize(ResourceLocation particleLocation) {
        if (particleSizesCache.containsKey(particleLocation)) return particleSizesCache.get(particleLocation);

        return particleSizesCache.getOrDefault(particleLocation, new int[]{0, 0});

    }

    public static boolean setParticleSizeCache(ResourceLocation particleLocation) {
        try {
            InputStream stream = Minecraft.getInstance().getResourceManager().open(particleLocation);
            NativeImage image = NativeImage.read(stream);
            int[] sizes = new int[]{ image.getWidth(), image.getHeight() };
            particleSizesCache.put(particleLocation, sizes);
            return true;


        } catch (IOException e) {
            particleSizesCache.put(particleLocation, new int[]{0, 0});
            return false;
        }
    }
}
