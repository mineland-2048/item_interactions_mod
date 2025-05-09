package dev.mineland.item_interactions_mod.CarriedInteractions.Particles;

import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class BaseParticle {
    GuiGraphics guiGraphics;
    double x, y;
    double speedX, speedY;
    double accelerationX, accelerationY;
    double frictionX, frictionY;

    int r, g, b;
    float a;

    int rEnd, gEnd, bEnd;
    float aEnd;

    double lifeTime;

    double maxTick;


    int id;


    public BaseParticle(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY, double frictionX, double frictionY, double lifeTime) {
        this.guiGraphics = guiGraphics;
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.frictionX = frictionX;
        this.frictionY = frictionY;
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

            String debugString = String.format("""
                    %d: x: %.2f, y: %.2f, %.5f
                    """, this.id, this.x, this.y, this.lifeTime);
            this.guiGraphics.drawString(Minecraft.getInstance().font, debugString, 0, 10 * GlobalDirt.particleCount, 0xFFFF0000);

            GlobalDirt.particleCount++;
        }
    }

    public boolean shouldDelete = false;
    public void tick() {

        lifeTime++;

        if (lifeTime > maxTick) {
            shouldDelete = true;
        }
    }


    public int getId() { return this.id; }
}
