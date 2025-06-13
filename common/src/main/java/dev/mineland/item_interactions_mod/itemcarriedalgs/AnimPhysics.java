package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Vector;

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
    private double rotationSpeed = 0;


    public double currentStress = 0;

    public float rotationAngle;

    static Quaternionf rotation = new Quaternionf();

    public AnimPhysics() {
        super("physics");
        addSetting("rope_length", 16.0);
        addSetting("rope_elasticity", 1.0);
        addSetting("rope_gravity", new Vector3f(0, -1, 0));
        addSetting("rope_stress", 1.0);
        addSetting("rope_is_rope", false);
    }

    static Vector3f rope = new Vector3f(0,0,0);

    public PoseStack makePose(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d, GuiGraphics guiGraphics) {

        PoseStack pose = new PoseStack();

        if (!(boolean) ItemInteractionsConfig.getSetting("rope_is_rope")) {
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
        rotation = new Quaternionf();
        rotationAngle = 0;
        oldAngle = 0;
        rotationSpeed = 0;
    }

    boolean isStill = false;
    private float actualX = 0, actualY = 0;
    public float angle = 0;
    private float oldAngle = 0;
    private void ropeSim(GuiGraphics guiGraphics, PoseStack pose, int x, int y, int z) {

        try {
            Vector3f newPos = new Vector3f(itemPos);
            Vector3f mousePos = new Vector3f(x, y, itemPos.z());
            if (!GlobalDirt.skipCalcs){
                actualX = x; actualY = y;
//                guiGraphics.drawString(Minecraft.getInstance().font, newPos.toString(), 0, 0, 0xFFFFFFFF);
//                guiGraphics.drawString(Minecraft.getInstance().font, itemVel.toString(), 0, +9, 0xFFFFFFFF);
//                guiGraphics.drawString(Minecraft.getInstance().font, "" + newPos.distance(mousePos), 0, +18, 0xFFFFFFFF);


                Vector3f grav = (Vector3f) ItemInteractionsConfig.getSetting("rope_gravity");
                length = (double) ItemInteractionsConfig.getSetting("rope_length");
                float distance = newPos.distance(mousePos);
                angle = (float) Math.atan2(newPos.y() - mousePos.y(), newPos.x() - mousePos.x());
                elasticity = (double) ItemInteractionsConfig.getSetting("rope_elasticity");
                double stress = (double) ItemInteractionsConfig.getSetting("rope_stress");


                Vector3f disVec = new Vector3f(
                        newPos.x() - (float) (mousePos.x +  Math.cos(angle) * (length)),
                        newPos.y() - (float) (mousePos.y +  Math.sin(angle) * (length)),
                        newPos.z() - itemPos.z());


                currentStress = Math.max(0, (newPos.distance(mousePos) - length) / length);

                if (distance > length) {
                    Vector3f vel = new Vector3f(
                            disVec.x(),
                            disVec.y(),
                            0f
                    );

                    itemVel.sub(vel.mul((float) ((1 - (elasticity*elasticity)) * 0.3)));

//                    float cappedX = (float) (Math.cos(angle) * length*2);
//                    float cappedY = (float) (Math.sin(angle) * length*2);
//
//                    float unkX = cappedX - itemPos.x;
//                    float unkY = cappedY - itemPos.y;
//
//                    newPos.add(unkX, unkY, 0);

                }


                var stressFactor = stress*stress;
                itemVel.mul((float) ((0.9 + (stressFactor*0.1))));

                itemVel.add(new Vector3f(grav).mul((float) (1.1 - (stressFactor*0.1))));


//            pose.translate(new Vec3(newPos));
                newPos.add(itemVel).sub(new Vector3f().sub(itemVel).mul(0) );
                distance = newPos.distance(mousePos);

                double elasticLength = length * (1 + elasticity);
                if (distance > elasticLength) {
                    newPos.set(
                            mousePos.x() + itemVel.x()+ (float) Math.cos(angle) * ((elasticLength) ),
                            mousePos.y() + itemVel.y()+ (float) Math.sin(angle) * ((elasticLength) ),
                            mousePos.z());
                }


                itemPos = newPos;
            }

//            GuiRendererHelper.renderLine(guiGraphics,
//                    x + 8, y + 8,
//                    x + newPos.x() - actualX + 8,
//                    y + newPos.y() - actualY + 8, MiscUtils.colorLerp((float) currentStress, 0xFFFFFFFF, 0xFFFF0000), true);
//

            int[] colors = new int[]{0xFFFFFFFF, 0xFFa0a0a0};
            GuiRendererHelper.renderLine_ColorPattern(guiGraphics,
                    x + 8,
                    y + 8,
                    x + newPos.x() - actualX + 8,
                    y + newPos.y() - actualY + 8,colors,
                    (int) (length/8), ItemInteractionsConfig.enableGuiParticles
            );
//            guiGraphics.drawString(Minecraft.getInstance().font, ""+currentStress, 0, 0, 0xFFFFFFFF);

            rotationAngle = (float) MiscUtils.lerpRotation(Math.clamp(currentStress, 0, 1), rotationAngle, angle);
            float h = rotationAngle - oldAngle;

//            rotationSpeed = (rotationSpeed + h) * 0.9;


            if (h == 0 && !isStill) {
                isStill = true;
                rotationAngle = (float) (rotationAngle%Math.PI);

            } else {
                isStill = false;
            }

            oldAngle = rotationAngle;


            rotation.rotateZ((float) (h));



//            rotation.rotateTo(newPos, rotPoint);

//            rotation.rotateTo(newPos.x(), newPos.y(), z + 150, (float) actualX + 8, (float) actualY + 8, z + 150);
            pose.translate(newPos.x() - actualX, newPos.y - actualY, 0);
            pose.pushPose();
//            pose.mulPose(new Quaternionf().rotateZ(angle));
            pose.rotateAround(rotation, x+8, y+8, z+150);


//  175 -> -175
//
        } catch (Exception e) {
            guiGraphics.drawString(Minecraft.getInstance().font, e.toString(), 0, 0, 0xFFFF0000);
        }

    }



}
