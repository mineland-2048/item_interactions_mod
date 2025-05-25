package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.GlobalDirt;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
//import static dev.mineland.item_interactions_mod.GlobalDirt.drag;


public class AnimSpeed extends AnimTemplate {


    public static Quaternionf rollback;

    public static void modifyPose(PoseStack pose, int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d) {
        pose.pushPose();
        pose.mulPoseMatrix(makePose(x,y,z,doubleSpeedX,doubleSpeedY,is3d).last().pose());
    }
    public static PoseStack makePose(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d) {
//        setVariables();
        PoseStack newPose = new PoseStack();

        float speedY = (float) (doubleSpeedY);
        float speedX = (float) (doubleSpeedX);
        float zPlane = (232.0f + 150f);
        if (is3d) {


            Quaternionf quatPointTo = new Quaternionf()
                    .rotateTo(0, 0, zPlane,
                            (speedX*4), (speedY*4), zPlane).normalize();

            newPose.rotateAround(quatPointTo, x , y , z);

            rollback = new Quaternionf()
                    .rotateTo(0, 0, zPlane,
                            (-speedX*4), (-speedY*4), zPlane).normalize();
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



    public static PoseStack makeRollback(int x, int y, int z, double speedX, double speedY, boolean is3d) {
        PoseStack newPose = new PoseStack();
        newPose.rotateAround(rollback, x, y, z);
        return newPose;
    }


    public static PoseStack makePose(int x, int y, int z, double speedX, double speedY) {
        return makePose(x, y, z, speedX, speedY, GlobalDirt.isCurrentItem3d);
    }

    public static PoseStack makeRollback(int x, int y, int z, double speedX, double speedY) {
        return makeRollback(x, y, z, speedX, speedY, GlobalDirt.isCurrentItem3d);
    }


}
