package dev.mineland.item_interactions_mod.CarriedInteractions.Particles;

import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import net.minecraft.client.gui.GuiGraphics;

public class BaseParticle {
    GuiGraphics guiGraphics;

    double x;
    double y;

    double speedX;
    double speedY;

    double accelerationX;
    double accelerationY;

    double frictionX;
    double frictionY;

//    Define in spawner:
//    double rX;
//    double rY;
//
//    double rSpeedX;
//    double rSpeedY;
//
//    double rLifeDuration;
    
    double lifeDuration;


    int id;
    double lifeTime;


    public BaseParticle(GuiGraphics guiGraphics,
                        double x, double y,
                        double speedX, double speedY,
                        double accelerationX, double accelerationY,
                        double lifeDuration) {

        this(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, 0, 0, lifeDuration);

    }

//    public BaseParticle(GuiGraphics guiGraphics,
//                        double x, double y,
//                        double speedX, double speedY,
//                        double accelerationX, double accelerationY,
//                        double frictionX, double frictionY,
//                        double lifeDuration) {
//
//        this(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, lifeDuration);
//
//    }

    public BaseParticle (GuiGraphics guiGraphics,
                         double x, double y,
                         double speedX, double speedY,
                         double accelerationX, double accelerationY,
                         double frictionX, double frictionY,
                         double lifeDuration
//                         double rLifeDuration
//                         double rX, double rY,
//                         double rSpeedX, double rSpeedY
)
    {

        this.guiGraphics = guiGraphics;
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.frictionX = frictionX;
        this.frictionY = frictionY;

        this.lifeDuration = lifeDuration;

//        this.rLifeDuration = rLifeDuration;
//        this.rX = rX;
//        this.rY = rY;
//        this.rSpeedX = rSpeedX;
//        this.rSpeedY = rSpeedY;



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

        if (lifeTime > lifeDuration) {
            shouldDelete = true;
        }
    }


    public int getId() { return this.id; }
}
