package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.GuiRendererHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class AnimPhysics extends AnimTemplate {


    static double itemSpeedX = 0;
    static double itemSpeedY = 0;
    static double itemSpeedZ = 0;

//    public double length = 16;
//    public double elasticity = 1;
//    public Vector3f gravity = new Vector3f(0, -1, 0);

    public boolean isRope = false;



    static Quaternionf rotation = new Quaternionf();

    public AnimPhysics() {
        super("physics");
        addSetting("rope_length", 16);
        addSetting("rope_elasticity", 1f);
        addSetting("rope_gravity", new Vector3f(0, -1, 0));
        addSetting("rope_is_rope", false);
    }

    static Vector3f rope = new Vector3f(0,0,0);

    public PoseStack makePose(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d, GuiGraphics guiGraphics) {

        PoseStack pose = new PoseStack();

        if (!isRope) {
            GuiRendererHelper.renderLine(guiGraphics, x + 8, y + 8, x+8 + (int) itemSpeedX, y+8 + (int) itemSpeedY, 0xFFFF0000);
            itemSpeedX = (itemSpeedX + (doubleSpeedX * 0.05)) * 0.99;
            itemSpeedY = (itemSpeedY + (doubleSpeedY * 0.05)) * 0.99;
    //        rotation.rotateXYZ((float) itemSpeedY * Mth.DEG_TO_RAD, 0f, (float) itemSpeedX * Mth.DEG_TO_RAD);

            rotation.rotateLocalX((float) -itemSpeedY * Mth.DEG_TO_RAD);
            rotation.rotateLocalY((float) itemSpeedX * Mth.DEG_TO_RAD);


    //        System.out.println(itemSpeedX + " " + itemSpeedY);
            pose.rotateAround(rotation, x+8, y+8, z+150);

        } else {

        }

        return pose;
    }

    public void reset() {
        rotation = new Quaternionf();
        itemSpeedX = 0;
        itemSpeedY = 0;
        itemSpeedZ = 0;
    }

}
