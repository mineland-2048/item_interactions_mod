package dev.mineland.item_interactions_mod.CarriedInteractions.Particles;

import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import net.minecraft.client.Minecraft;
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

    double lifeTime;

    double maxTick;


    int id;


    public BaseParticle(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY, double lifeTime) {
        this.guiGraphics = guiGraphics;
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.maxTick = lifeTime;

        this.id = GlobalDirt.particleList.size();

        GlobalDirt.particleList.add(this);

    }

    public void render() {

        if (ItemInteractionsConfig.debugDraws) {
            int i = (int) this.x - 2;
            int j = (int) this.y - 2;
            int k = (int) this.x + 2;
            int l = (int) this.y + 2;
            this.guiGraphics.fill (i,j,k,l, 0xFFFF0000);

//            this.guiGraphics.drawString(Minecraft.getInstance().font, "#" + this.id + ": " + "x: " + x + ". y: " + y + ". sX: " + speedX + ". sY: " + speedY, 0, 10 * this.id, 0xFFFF0000);

        }
    }

    public boolean shouldDelete = false;
    public void tick() {

        lifeTime ++;

        if (lifeTime > maxTick) {
            shouldDelete = true;
        }
    }


    public int getId() { return this.id; }
}
