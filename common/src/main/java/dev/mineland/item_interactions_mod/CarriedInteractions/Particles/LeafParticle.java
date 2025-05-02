//package dev.mineland.item_interactions_mod.CarriedInteractions.Particles;
//
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.resources.ResourceLocation;
//
//public class LeafParticle extends TexturedParticle{
//
//
//
//    public LeafParticle(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY, double lifeTime, ResourceLocation textureLocation, int color) {
//        super(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, lifeTime, textureLocation, TextureType.LIFETIME, color);
//    }
//
//    @Override
//    public void tick() {
//        this.speedX = Math.clamp(speedX + accelerationX, -5, 5) * 0.95;
//        this.speedY = Math.clamp(speedY + accelerationY, -5, 5);
//        this.x += speedX;
//        this.y += speedY;
//        super.tick();
//
//    }
//}
