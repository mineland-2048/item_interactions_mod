package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import dev.mineland.item_interactions_mod.GlobalDirt;
import net.minecraft.client.gui.GuiGraphics;

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

    public void setAll(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
    }

    public Spawner newInstance(int id) {
        return new Spawner(id, this.name);
    }

    public String getName() {
        return this.name;
    }

}
