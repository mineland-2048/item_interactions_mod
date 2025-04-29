package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import dev.mineland.item_interactions_mod.GlobalDirt;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Map;

public class Spawner {
    protected int id;
    protected String name;


    protected double x;
    protected double y;

    protected double speedX;
    protected double speedY;

    protected double accelerationX;
    protected double accelerationY;

    protected double frictionX;
    protected double frictionY;

    protected double rX;
    protected double rY;

    protected double rSpeedX;
    protected double rSpeedY;

    protected double lifeDuration;
    protected double rLifeDuration;

    protected int ticks;

    public Spawner(String name) {
        this.id = -1;
        this.name = name;

        this.ticks = 0;
    }
    public Spawner(int id, String name) {
        this.id = id;
        this.name = name;
        this.ticks = 0;


    }

    public void tick(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY) {
        this.ticks++;
        
    }

    public void init(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY) {

    }



    public void spawn(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY) {

    }

    public void onCarried(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY) {
    }

    public void setAll(
//            double x, double y,
            double speedX, double speedY,
            double accelerationX, double accelerationY,
            double frictionX, double frictionY,
            double rX, double rY,
            double rSpeedX, double rSpeedY,
            double lifeDuration, double rLifeDuration
    ) {
//        this.x = x;
//        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;

        this.frictionX = frictionX;
        this.frictionY = frictionY;
        this.rX = rX;
        this.rY = rY;
        this.rSpeedX = rSpeedX;
        this.rSpeedY = rSpeedY;
        this.lifeDuration = lifeDuration;
        this.rLifeDuration = rLifeDuration;
    }

    public Spawner newInstance(int id) {
        Spawner a = new Spawner(id, this.name);
        a.setAll(
            this.speedX,
            this.speedY,
            this.accelerationX,
            this.accelerationY,
            this.frictionX,
            this.frictionY,
            this.rX,
            this.rY,
            this.rSpeedX,
            this.rSpeedY,
            this.lifeDuration,
            this.rLifeDuration
        );
        return a;
    }

    public String getName() {
        return this.name;
    }

}
