package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

import static dev.mineland.item_interactions_mod.GlobalDirt.msTickDelta;


public class AnimSpeed extends AnimTemplate {
    double mouseDeceleration;
    double mouseSpeedMult;

    @Override
    public void refreshSettings() {
        mouseSpeedMult = (double) getSetting("mouse_speed_multiplier");
        mouseDeceleration = (double) getSetting("mouse_deceleration");

    }

    public AnimSpeed() {
        super("speed");
        addSetting("mouse_speed_multiplier", 1.0);
        addSetting("mouse_deceleration", 1.0);
    }

    float speedX = 0, speedY = 0;
    public PoseStack makePose(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d, GuiGraphics guiGraphics) {

        PoseStack newPose = new PoseStack();


        int posX = 0;
        int posY = 0;
        int posZ = 0;

        double drag = Math.exp(-(16 * mouseDeceleration * mouseDeceleration) * msTickDelta);
        double mouseSpeedMultiplier = mouseSpeedMult;

        speedX = (float) Math.clamp((speedX + (doubleSpeedX * mouseSpeedMultiplier)) * drag, -100, 100);
        speedY = (float) Math.clamp((speedY + (doubleSpeedY * mouseSpeedMultiplier)) * drag, -100, 100);




        float zPlane = (232.0f + 150f);
        if (is3d) {


            Quaternionf quatPointTo = new Quaternionf()
                    .rotateTo(0, 0, zPlane,
                            (speedX*4), (speedY*4), zPlane).normalize();

            newPose.rotateAround(quatPointTo, posX , posY , posZ);

        } else {

            float angleVertical = (Mth.DEG_TO_RAD * 22.5f * Math.clamp((-speedY*0.1f), -1.5f ,1.5f));
            float angleHorizontal = Mth.DEG_TO_RAD * speedX*0.4f;
            Quaternionf quatRotateVertical = new Quaternionf()
                    .rotateX(angleVertical)
                    .rotateZ(angleHorizontal)
                    .normalize();

            newPose.rotateAround(quatRotateVertical, posX, posY, posZ);

        }

        return newPose;

    }



}
