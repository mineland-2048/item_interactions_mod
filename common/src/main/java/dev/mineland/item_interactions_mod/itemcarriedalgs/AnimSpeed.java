package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;


public class AnimSpeed extends AnimTemplate {
    public AnimSpeed() {
        super("speed");
        addSetting("mouse_speed_multiplier", 1.0);
        addSetting("mouse_deceleration", 1.0);

    }

    public PoseStack makePose(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d, GuiGraphics guiGraphics) {

        PoseStack newPose = new PoseStack();

        int posX = x + 8;
        int posY = y + 8;
        int posZ = 150;
        float speedY = (float) (doubleSpeedY);
        float speedX = (float) (doubleSpeedX);
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
