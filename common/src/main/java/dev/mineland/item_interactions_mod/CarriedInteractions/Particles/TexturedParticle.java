package dev.mineland.item_interactions_mod.CarriedInteractions.Particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class TexturedParticle extends BaseParticle {

    ResourceLocation textureLocation;
    public TexturedParticle(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY, ResourceLocation textureLocation) {
        super(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY);

        this.textureLocation = textureLocation;
    }


    public void render() {
        super.render();
        int textureHeight = Minecraft.getInstance().getTextureManager().getTexture(this.textureLocation).getTexture().getHeight(0);
        int textureWidth = Minecraft.getInstance().getTextureManager().getTexture(this.textureLocation).getTexture().getWidth(0);
//        this.guiGraphics.blit();
    }
}
