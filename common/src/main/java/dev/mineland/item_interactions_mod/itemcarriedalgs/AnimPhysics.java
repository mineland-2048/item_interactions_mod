package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class AnimPhysics extends AnimTemplate {


    static double itemSpeedX = 0;
    static double itemSpeedY = 0;
    static double itemSpeedZ = 0;

    static Quaternionf rotation = new Quaternionf();

    public AnimPhysics() {
        super("physics");
    }


    public PoseStack makePose(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d) {
        PoseStack pose = new PoseStack();

        itemSpeedX = (itemSpeedX + (doubleSpeedX * 0.05)) * 0.99;
        itemSpeedY = (itemSpeedY + (doubleSpeedY * 0.05)) * 0.99;
//        rotation.rotateXYZ((float) itemSpeedY * Mth.DEG_TO_RAD, 0f, (float) itemSpeedX * Mth.DEG_TO_RAD);

        rotation.rotateLocalX((float) -itemSpeedY * Mth.DEG_TO_RAD);
        rotation.rotateLocalY((float) itemSpeedX * Mth.DEG_TO_RAD);


//        System.out.println(itemSpeedX + " " + itemSpeedY);
        pose.rotateAround(rotation, x+8, y+8, z+150);

        return pose;
    }

    public void reset() {
        rotation = new Quaternionf();
        itemSpeedX = 0;
        itemSpeedY = 0;
        itemSpeedZ = 0;
    }

}
