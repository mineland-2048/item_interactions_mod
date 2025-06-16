package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.*;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AnimRope extends AnimTemplate {

    public double length = 16;
    public double elasticity = 1;
    public Vector3f gravity = new Vector3f(0, -0.1f, 0);
    public double inertia;
    public boolean pixelated;

    int[] colors = new int[]{0xFFAD7249, 0xFF844416};

    public double currentStress = 0;

    public float rotationAngle;

    Quaternionf rotation = new Quaternionf();

    public Vector3f globalItemPos = new Vector3f();

    public float angle = 0;

    private boolean isStill = false;
    private float actualX = 0, actualY = 0;
    private float oldAngle = 0;

    Vector3f newPos = new Vector3f();
    Vector3f mousePos = new Vector3f();
    private final Vector3f oldMousePos = new Vector3f();
    boolean isDead = false;

    @Override
    public void refreshSettings() {
        this.length = (double) getSetting("rope_length");
        this.elasticity = (double) getSetting("rope_elasticity");
        this.inertia = (double) getSetting("rope_inertia");
        this.gravity = (Vector3f) ItemInteractionsConfig.getSetting("rope_gravity");
        this.pixelated = (boolean) getSetting("rope_pixelated");

    }

    public AnimRope() {
        super("rope");
        addSetting("rope_elasticity", 0.26);
        addSetting("rope_length", 16.0);
        addSetting("rope_gravity", new Vector3f(0, 0.2f, 0));
        addSetting("rope_inertia", 0.75);
        addSetting("rope_pixelated", true);
    }

    public PoseStack makePose(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d, GuiGraphics guiGraphics) {

        PoseStack pose = new PoseStack();

        ropeSim(guiGraphics, pose, x, y, z);

        return pose;
    }

    @Override
    public void reset(int initialX, int initialY, int initialZ) {
        isDead = false;
        resetValues(initialX,initialY,initialZ);
    }

    private void resetValues(int initialX, int initialY, int initialZ) {
        super.reset(initialX,initialY,initialZ);
        globalItemPos.set(initialX, initialY, initialZ);
        rotation = new Quaternionf();
        rotationAngle = 0;
        oldAngle = 0;
        oldMousePos.set(0,0,0);
    }



    private void ropeSim(GuiGraphics guiGraphics, PoseStack pose, int x, int y, int z) {

        try {
            newPos.set(globalItemPos);
            mousePos.set(x, y, globalItemPos.z());
            if (!GlobalDirt.skipCalcs){
                actualX = x; actualY = y;
//                guiGraphics.drawString(Minecraft.getInstance().font, newPos.toString(), 0, 0, 0xFFFFFFFF);
//                guiGraphics.drawString(Minecraft.getInstance().font, itemVel.toString(), 0, +9, 0xFFFFFFFF);
//                guiGraphics.drawString(Minecraft.getInstance().font, "" + newPos.distance(mousePos), 0, +18, 0xFFFFFFFF);


                float distance = newPos.distance(mousePos);
                angle = (float) Math.atan2(newPos.y() - mousePos.y(), newPos.x() - mousePos.x());


                Vector3f disVec = new Vector3f(
                        newPos.x() - (float) (mousePos.x +  Math.cos(angle) * (length)),
                        newPos.y() - (float) (mousePos.y +  Math.sin(angle) * (length)),
                        newPos.z() - globalItemPos.z());


                currentStress = Math.max(0, ( newPos.distance(mousePos) - length) / length);

                if (distance > length) {
                    Vector3f vel = new Vector3f(
                            disVec.x(),
                            disVec.y(),
                            0f
                    );

                    itemSpeed.sub(vel);
                }


                var stressFactor = inertia * inertia;
                itemSpeed.mul((float) ((0.9 + (stressFactor*0.1))));

                itemSpeed.add(new Vector3f(gravity).mul((float) (1.1 - (stressFactor*0.1))));


//                TODO: make tick rate better
                itemSpeed.mul(GlobalDirt.tickScale);
                newPos.add(itemSpeed);
                distance = newPos.distance(mousePos);

                double elasticLength = length * (1 + (elasticity));
                if (distance > elasticLength) {
                    newPos.set(
                            mousePos.x() + itemSpeed.x()+ (float) Math.cos(angle) * ((elasticLength) ),
                            mousePos.y() + itemSpeed.y()+ (float) Math.sin(angle) * ((elasticLength) ),
                            mousePos.z());
                }




                globalItemPos.set(newPos);
            }

//            GuiRendererHelper.renderLine(guiGraphics,
//                    x + 8, y + 8,
//                    x + newPos.x() - actualX + 8,
//                    y + newPos.y() - actualY + 8, MiscUtils.colorLerp((float) currentStress, 0xFFFFFFFF, 0xFFFF0000), true);
//

            guiGraphics.pose().translate(0, 0, 500);

            GuiRendererHelper.renderLine_ColorPattern(guiGraphics,
                    x + 8,
                    y + 8,
                    x + newPos.x() - actualX + 8,
                    y + newPos.y() - actualY + 8, this.colors,
                    (int) (length/8), pixelated
            );

            guiGraphics.pose().translate(0, 0, -500);

//            guiGraphics.drawString(Minecraft.getInstance().font, ""+currentStress, 0, 0, 0xFFFFFFFF);

            rotationAngle = (float) MiscUtils.lerpRotation(Math.clamp(currentStress, 0, 1), rotationAngle, angle);
            float rotationDelta = rotationAngle - oldAngle;

//            rotationSpeed = (rotationSpeed + h) * 0.9;


            if (rotationDelta == 0 && !isStill) {
                isStill = true;
                rotationAngle = (float) (rotationAngle%Math.PI);

            } else {
                isStill = false;
            }

            oldAngle = rotationAngle;


            rotation.rotateZ(rotationDelta);



//            rotation.rotateTo(newPos, rotPoint);

//            rotation.rotateTo(newPos.x(), newPos.y(), z + 150, (float) actualX + 8, (float) actualY + 8, z + 150);
            itemPos.set(newPos.x() - actualX, newPos.y - actualY, 0);
            pose.translate(itemPos.x, itemPos.y, 0);
            pose.pushPose();
//            pose.mulPose(new Quaternionf().rotateZ(angle));
            pose.rotateAround(rotation, x+8, y+8, z+150);



            isDead = true;
        } catch (Exception e) {
//            System.out.println();
            resetValues(x, y, z);
            if (ItemInteractionsConfig.debugDraws && isDead) {
                String error = "(" + this.getClass().getName() + ") \n" + e;
                isDead = false;
                MiscUtils.displayErrorInUi(error);
            }
        }



    }






}
