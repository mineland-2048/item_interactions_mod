//package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;
//
//import com.nimbusds.jose.util.Resource;
//import dev.mineland.item_interactions_mod.CarriedInteractions.Particles.BaseParticle;
//import dev.mineland.item_interactions_mod.CarriedInteractions.Particles.LeafParticle;
//import dev.mineland.item_interactions_mod.GlobalDirt;
//import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
//import dev.mineland.item_interactions_mod.Item_interactions_mod;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.resources.ResourceLocation;
//import org.joml.Vector2d;
//import org.joml.Vector2f;
//
//import java.util.List;
//
//public class FallingLeaves extends GuiParticleSpawner {
//
//    protected int color;
//    ResourceLocation particleTexture = ResourceLocation.fromNamespaceAndPath("iteminteractions", "textures/particle/gui/falling_leaf.png");
//
//    public FallingLeaves(String name, int color ) {
//        super(name);
//        this.color = color;
//    }
//
//    public FallingLeaves(int id, String name, int color) {
//        super(id, name);
//        this.color = color;
//    }
//
//    public void spawn(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY) {
//        LeafParticle newParticle = new LeafParticle(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, 100, particleTexture, this.color);
//    }
//
//    public void onCarried(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY) {
//
//
////        for (int i = 0; i < 4; i++) {
//            new LeafParticle(guiGraphics, x-8+((Math.random() - 0.5) * 8), y-8+(((Math.random() - 0.5)*4)), (speedX * 0.5)-(Math.random() * 0.75), (speedY*0.5)-0.1, accelerationX, accelerationY+0.01, 50, particleTexture, this.color);
//            new LeafParticle(guiGraphics, x-8+((Math.random() - 0.5) * 8), y-8+(((Math.random() - 0.5)*4)), (speedX * 0.5)+(Math.random() * 0.75), (speedY*0.5)+0.1, accelerationX, accelerationY+0.01, 50, particleTexture, this.color);
//            new LeafParticle(guiGraphics, x-8+((Math.random() - 0.5) * 4), y-8+(((Math.random() - 0.5)*4)), (speedX * 0.5)-(Math.random() * 0.75), (speedY*0.5)-0.1, accelerationX, accelerationY+0.01, 50, particleTexture, this.color);
//            new LeafParticle(guiGraphics, x-8+((Math.random() - 0.5) * 4), y-8+(((Math.random() - 0.5)*4)), (speedX * 0.5)+(Math.random() * 0.75), (speedY*0.5)+0.1, accelerationX, accelerationY+0.01, 50, particleTexture, this.color);
////        }
//
//
//
//
//
//
//
//    }
//
//    public FallingLeaves newInstance(int id) {
//        return new FallingLeaves(id, this.name, this.color);
//    }
//
//
//    double oldSpeedX = 0;
//    double oldSpeedY = 0;
//
//    int nextSpawn = 0;
//    public void tick(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY) {
//        super.tick(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY);
//        nextSpawn--;
//        if (nextSpawn <= 0) {
////            Item_interactions_mod.infoMessage("Spawned " + GlobalDirt.particleList.size() + "! " + GlobalDirt.msCounter);
//            nextSpawn = (int) (Math.random() * 100);
//            double  rx = Math.random() - 1,
//                    ry = Math.random(),
//                    rsx = (Math.random() - 0.5) * 0.5,
//                    rsy = Math.abs(Math.random()*0.3);
//            spawn(guiGraphics, x +rx, y+ry, speedX+rsx, speedY+rsy, 0, 0);
//
//        }
//
//
//        if (this == GlobalDirt.carriedGuiParticleSpawner && ItemInteractionsConfig.debugDraws) {
//            guiGraphics.drawString(Minecraft.getInstance().font, "oX: " + oldSpeedX, 0, 0, 0xFFFFFFFF);
//            guiGraphics.drawString(Minecraft.getInstance().font, "oY: " + oldSpeedY, 0, 10, 0xFFFFFFFF);
//            guiGraphics.drawString(Minecraft.getInstance().font, "sX: " + speedX, 0, 20, 0xFFFFFFFF);
//            guiGraphics.drawString(Minecraft.getInstance().font, "sY: " + speedY, 0, 30, 0xFFFFFFFF);
//        }
//        this.oldSpeedX = (this.oldSpeedX + speedX) *0.75;
//        this.oldSpeedY = (this.oldSpeedY + speedY) *0.75;
//
//
//
//
//
//        if ((Math.abs(this.oldSpeedX) > 5 && Math.abs(speedX) < 2) || (Math.abs(this.oldSpeedY) > 5 && Math.abs(speedY) < 2) ) {
//
//            Vector2d normal = new Vector2d(oldSpeedX, oldSpeedY).normalize();
//            this.onCarried(guiGraphics, x, y, normal.x * 2, normal.y * 2, accelerationX, accelerationY);
//            this.oldSpeedX = 0;
//            this.oldSpeedY = 0;
//        }
//
//
//
//
//
//    }
//
//
//}
//
//
//
