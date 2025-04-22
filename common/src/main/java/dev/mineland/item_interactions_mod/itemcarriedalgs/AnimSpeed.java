package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.GlobalDirt;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
//import static dev.mineland.item_interactions_mod.GlobalDirt.drag;


public class AnimSpeed extends AnimTemplate {


    public static Quaternionf rollback;

    public static PoseStack makePose(PoseStack pose, int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d) {
//        setVariables();
        PoseStack newPose = new PoseStack();

        float speedY = (float) (doubleSpeedY);
        float speedX = (float) (doubleSpeedX);


//        if (!GlobalDirt.skipCalcs) {
//            mouseDeltaX = i - lastMouseX;
//            mouseDeltaY = j - lastMouseY;
//            speedX = Math.clamp((speedX + mouseDeltaX) * drag,-40f,  40f);
//            speedY = Math.clamp((speedY + mouseDeltaY) * drag,-40f,  40f);
//        }

        float zPlane = (232.0f + 150f);


//
//                        this.drawString(this.minecraft.font, "§fspeedX: " + speedX,      -GlobalDirt.leftPos,  -GlobalDirt.topPos, 0xFFFFFFFF);
//                        this.drawString(this.minecraft.font, "§fspeedY: " + speedY,      -GlobalDirt.leftPos,  -GlobalDirt.topPos + this.minecraft.font.lineHeight, 0xFFFFFFFF);
//                        this.drawString(this.minecraft.font, "§ftickDelta: " + tickDelta,      -GlobalDirt.leftPos,  -GlobalDirt.topPos + this.minecraft.font.lineHeight * 2, 0xFFFFFFFF);
//                        this.drawString(this.minecraft.font, "§drag: " + drag,      -GlobalDirt.leftPos,  -GlobalDirt.topPos + this.minecraft.font.lineHeight * 3, 0xFFFFFFFF);


//                    guiGraphics.pose().translate(-this.leftPos, -this.topPos, 0);

        if (is3d) {


            Quaternionf quatPointTo = new Quaternionf()
                    .rotateTo(0, 0, zPlane,
                            (speedX*4), (speedY*4), zPlane).normalize();

//                            int hThing = 128+16+8;
            newPose.rotateAround(quatPointTo, x , y , z);

            rollback = new Quaternionf()
                    .rotateTo(0, 0, zPlane,
                            (-speedX*4), (-speedY*4), zPlane).normalize();




//                        this.pose.translate(-i, -j, 0);
        } else {

//                            speedY = Math.clamp (speedY, -20, 20);
            float angleVertical = Mth.DEG_TO_RAD * 22.5f * Math.clamp((-speedY*0.1f), -1.5f ,1.5f);
            float angleHorizontal = Mth.DEG_TO_RAD * speedX*0.4f;
            Quaternionf quatRotateVertical = new Quaternionf()
                    .rotateX(angleVertical)
                    .rotateZ(angleHorizontal)
                    .normalize();

            newPose.rotateAround(quatRotateVertical, x, y, z);

            rollback = new Quaternionf()
                    .rotateZ(-angleHorizontal)
                    .rotateX(-angleVertical)
                    .normalize();





        }

//        setLastVariables();
        return newPose;




    }



    public static PoseStack makeRollback(PoseStack pose, int x, int y, int z, double speedX, double speedY, boolean is3d) {
        PoseStack newPose = new PoseStack();
        newPose.rotateAround(rollback, x, y, z);
        return newPose;
    }


    public static PoseStack makePose(PoseStack pose, int x, int y, int z, double speedX, double speedY) {
        return makePose(pose, x, y, z, speedX, speedY, GlobalDirt.isCurrentItem3d);
    }

    public static PoseStack makeRollback(PoseStack pose, int x, int y, int z, double speedX, double speedY) {
        return makeRollback(pose, x, y, z, speedX, speedY, GlobalDirt.isCurrentItem3d);
    }


}
