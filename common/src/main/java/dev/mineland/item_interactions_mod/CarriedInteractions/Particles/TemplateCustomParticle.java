package dev.mineland.item_interactions_mod.CarriedInteractions.Particles;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class TemplateCustomParticle extends TexturedParticle {

    public TemplateCustomParticle(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY, double lifeTime, ResourceLocation textureLocation, int tint) {
        super(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, lifeTime, textureLocation, tint);
    }

    public TemplateCustomParticle(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY, double lifeTime, ResourceLocation textureLocation, TextureType textureType, int tint) {
        super(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, lifeTime, textureLocation, textureType, tint);
    }
}
