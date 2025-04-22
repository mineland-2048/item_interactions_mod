package dev.mineland.item_interactions_mod.CarriedInteractions.Particles;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class BaseParticle {
    GuiGraphics guiGraphics;
    double x;
    double y;
    double speedX;
    double speedY;
    double accelerationX;
    double accelerationY;


    public BaseParticle(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY) {
        this.guiGraphics = guiGraphics;
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.accelerationX=accelerationX;
        this.accelerationY = accelerationY;
    }

    public void render() {
        int i = (int) this.x - 2;
        int j = (int) this.y - 2;
        int k = (int) this.x + 2;
        int l = (int) this.y + 2;
        this.guiGraphics.fill (i,j,k,l, 0xFFFF0000);
    }

    public void tick() {

    }

}
