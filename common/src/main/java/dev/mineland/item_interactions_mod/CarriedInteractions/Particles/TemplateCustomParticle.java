package dev.mineland.item_interactions_mod.CarriedInteractions.Particles;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class TemplateCustomParticle extends TexturedParticle {

    public TemplateCustomParticle(GuiGraphics guiGraphics,
                                  double x, double y,
                                  double speedX, double speedY,
                                  double accelerationX, double accelerationY,
                                  double frictionX, double frictionY,
                                  double lifeDuration,
                                  ResourceLocation textureLocation)
    {
        super(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, lifeDuration, textureLocation);
    }

    public TemplateCustomParticle(GuiGraphics guiGraphics,
                                  double x, double y,
                                  double speedX, double speedY,
                                  double accelerationX, double accelerationY,
                                  double frictionX, double frictionY,
                                  double lifeDuration,
                                  ResourceLocation textureLocation,
                                  int tint)
    {
        super(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, lifeDuration, textureLocation, tint);
    }

    public TemplateCustomParticle(GuiGraphics guiGraphics,
                                  double x, double y,
                                  double speedX, double speedY,
                                  double accelerationX, double accelerationY,
                                  double frictionX, double frictionY,
                                  double lifeDuration,
                                  ResourceLocation textureLocation, TextureType textureType)
    {
        super(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, lifeDuration,  textureLocation, textureType);
    }

    public TemplateCustomParticle(GuiGraphics guiGraphics,
                                  double x, double y,
                                  double speedX, double speedY,
                                  double accelerationX, double accelerationY,
                                  double frictionX, double frictionY,
                                  double lifeDuration,
                                  ResourceLocation textureLocation, TextureType textureType,
                                  int tint)
    {
        super(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, lifeDuration,  textureLocation, textureType, tint);
    }

    @Override
    public void tick() {
        this.x = this.x + (this.speedX);
        this.y = this.y + (this.speedY);

        this.speedX = (this.speedX + this.accelerationX) * this.frictionX;
        this.speedY = (this.speedY + this.accelerationY) * this.frictionY;

        super.tick();
    }
}
