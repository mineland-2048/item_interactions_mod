package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.GuiRendererHelper;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class AnimPhysics extends AnimTemplate {


    static double itemSpeedX = 0;
    static double itemSpeedY = 0;
    static double itemSpeedZ = 0;

    public double length = 16;
    public double elasticity = 1;
    public Vector3f gravity = new Vector3f(0, -1, 0);

    public int ropeSegmentLength = 16;

    static Vector3f itemPos = new Vector3f(0, 0, 0);
    static Vector3f itemVel = new Vector3f(0,0,0);
    private Vector2f[] ropeSegments = new Vector2f[ropeSegmentLength];

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

        if ((boolean) ItemInteractionsConfig.getSetting("rope_is_rope")) {
            GuiRendererHelper.renderLine(guiGraphics, x + 8, y + 8, x+8 + (float) itemSpeedX, y+8 + (float) itemSpeedY, 0xFFFF0000, ItemInteractionsConfig.enableGuiParticles);
            itemSpeedX = (itemSpeedX + (doubleSpeedX * 0.05)) * 0.95;
            itemSpeedY = (itemSpeedY + (doubleSpeedY * 0.05)) * 0.95;

            rotation.rotateLocalX((float) -itemSpeedY * Mth.DEG_TO_RAD);
            rotation.rotateLocalY((float) itemSpeedX * Mth.DEG_TO_RAD);


            pose.rotateAround(rotation, x+8, y+8, z+150);

        } else {

            ropeSim(guiGraphics, pose, x, y, z);

        }

        return pose;
    }

    public void reset(int initialX, int initialY, int initialZ) {
        rotation = new Quaternionf();
        itemSpeedX = 0;
        itemSpeedY = 0;
        itemSpeedZ = 0;

        itemPos = new Vector3f(initialX, initialY, initialZ);
        itemVel = new Vector3f(0,0,0);
    }

    private float actualX = 0, actualY = 0;
    private float angle = 0;
    private void ropeSim(GuiGraphics guiGraphics, PoseStack pose, int x, int y, int z) {

        try {
            Vector3f newPos = new Vector3f(itemPos);
            Vector3f mousePos = new Vector3f(x, y, itemPos.z());
            if (!GlobalDirt.skipCalcs){
                actualX = x; actualY = y;
                guiGraphics.drawString(Minecraft.getInstance().font, newPos.toString(), 0, 0, 0xFFFFFFFF);
                guiGraphics.drawString(Minecraft.getInstance().font, itemVel.toString(), 0, +9, 0xFFFFFFFF);
                guiGraphics.drawString(Minecraft.getInstance().font, "" + newPos.distance(mousePos), 0, +18, 0xFFFFFFFF);


                newPos.add(itemVel);
                Vector3f grav = new Vector3f(0, 0.1f, 0);
                itemVel.add(grav);
                float distance = newPos.distance(mousePos);
                angle = (float) Math.atan2(newPos.y() - mousePos.y(), newPos.x() - mousePos.x());
                if (distance > length) {

                    elasticity = 1;
                    double stress = 0.5;
                    Vector3f disVec = new Vector3f(
                            newPos.x() - (float) (mousePos.x +  Math.cos(angle) * (length)),
                            newPos.y() - (float) (mousePos.y +  Math.sin(angle) * (length)),
                            newPos.z() - itemPos.z());

                    itemVel.sub(
                            (float) (disVec.x() * elasticity),
                            (float) (disVec.y() * elasticity),
                            (float) (0) * Math.signum(itemVel.z())
                    );
                    itemVel.mul((float) stress);
                }

//            pose.translate(new Vec3(newPos));

                itemPos = newPos;
            }

            GuiRendererHelper.renderLine(guiGraphics,
                    x + 8, y + 8,
                    x + newPos.x() - actualX + 8,
                    y + newPos.y() - actualY + 8, 0xFFFFFFFF, false);

            rotation = new Quaternionf();
            rotation.rotateTo(newPos.x(), newPos.y(), z + 150, (float) actualX + 8, (float) actualY + 8, z + 150);
            pose.rotateAround(rotation, x+8, y+8, z+150);

            pose.pushPose();
//            pose.mulPose(new Quaternionf().rotateZ(angle));

            pose.translate(newPos.x() - actualX, newPos.y - actualY, 0);

        } catch (Exception e) {
            guiGraphics.drawString(Minecraft.getInstance().font, e.toString(), 0, 0, 0xFFFF0000);
        }

    }



}
