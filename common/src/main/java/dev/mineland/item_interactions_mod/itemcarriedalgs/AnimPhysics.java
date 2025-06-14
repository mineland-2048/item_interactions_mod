package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AnimPhysics extends AnimTemplate {

    public double length = 16;
    public double elasticity = 1;
    public Vector3f gravity = new Vector3f(0, -0.1f, 0);

    public boolean isRope = false;
    public double stress;

    public double currentStress = 0;

    public float rotationAngle;

    static Quaternionf rotation = new Quaternionf();

    @Override
    public void refreshSettings() {
        this.length = (double) getSetting("rope_length");
        this.elasticity = (double) getSetting("rope_elasticity");
        this.stress = (double) getSetting("rope_stress");
        this.isRope = (boolean) getSetting("rope_is_rope");
        this.gravity = (Vector3f) ItemInteractionsConfig.getSetting("rope_gravity");

    }

    public AnimPhysics() {
        super("physics");
        addSetting("rope_length", 16.0);
        addSetting("rope_elasticity", 1.0);
        addSetting("rope_gravity", new Vector3f(0, -1, 0));
        addSetting("rope_stress", 1.0);
        addSetting("rope_is_rope", false);
    }

    public PoseStack makePose(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d, GuiGraphics guiGraphics) {

        PoseStack pose = new PoseStack();

        if (!(boolean) ItemInteractionsConfig.getSetting("rope_is_rope")) {

            itemSpeed.set(
                    (float) ((itemSpeed.x() + (doubleSpeedX * 0.05)) * 0.95),
                    (float) ((itemSpeed.y() + (doubleSpeedY * 0.05)) * 0.95),
                    itemSpeed.z()
            );

            rotation.rotateLocalX(-itemSpeed.y() * Mth.DEG_TO_RAD);
            rotation.rotateLocalY( itemSpeed.x() * Mth.DEG_TO_RAD);

            pose.rotateAround(rotation, x+8, y+8, z+150);

            if (ItemInteractionsConfig.debugDraws)  GuiRendererHelper.renderLine(guiGraphics, x + 8, y + 8, x+8 + itemSpeed.x(), y+8 + itemSpeed.y(), 0xFFFF0000, ItemInteractionsConfig.enableGuiParticles);

        } else { ropeSim(guiGraphics, pose, x, y, z); }

        return pose;
    }

    @Override
    public void reset(int initialX, int initialY, int initialZ) {
        isDead = false;
        resetValues(initialX,initialY,initialZ);
    }

    private void resetValues(int initialX, int initialY, int initialZ) {
        super.reset(initialX,initialY,initialZ);
        rotation = new Quaternionf();
        rotationAngle = 0;
        oldAngle = 0;
        oldMousePos.set(0,0,0);
    }


    public float angle = 0;

    private boolean isStill = false;
    private float actualX = 0, actualY = 0;
    private float oldAngle = 0;



    Vector3f newPos = new Vector3f();
    Vector3f mousePos = new Vector3f();
    private final Vector3f oldMousePos = new Vector3f();

    private void ropeSim(GuiGraphics guiGraphics, PoseStack pose, int x, int y, int z) {

        try {

            newPos.set(itemPos);
            mousePos.set(x, y, itemPos.z());
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
                        newPos.z() - itemPos.z());


                currentStress = Math.max(0, (newPos.distance(mousePos) - length) / length);

                if (distance > length) {
                    Vector3f vel = new Vector3f(
                            disVec.x(),
                            disVec.y(),
                            0f
                    );

                    itemSpeed.sub(vel.mul((float) ((1 - (elasticity*elasticity)) * 0.3)));

//                    float cappedX = (float) (Math.cos(angle) * length*2);
//                    float cappedY = (float) (Math.sin(angle) * length*2);
//
//                    float unkX = cappedX - itemPos.x;
//                    float unkY = cappedY - itemPos.y;
//
//                    newPos.add(unkX, unkY, 0);

                }


                var stressFactor = stress*stress;
                itemSpeed.mul((float) ((0.9 + (stressFactor*0.1))));

                itemSpeed.add(new Vector3f(gravity).mul((float) (1.1 - (stressFactor*0.1))));


//            pose.translate(new Vec3(newPos));
                newPos.add(itemSpeed);
                distance = newPos.distance(mousePos);

                double elasticLength = length * (1 + elasticity);
                if (distance > elasticLength) {
                    newPos.set(
                            mousePos.x() + itemSpeed.x()+ (float) Math.cos(angle) * ((elasticLength) ),
                            mousePos.y() + itemSpeed.y()+ (float) Math.sin(angle) * ((elasticLength) ),
                            mousePos.z());
                }


                itemPos.set(newPos);
            }

//            GuiRendererHelper.renderLine(guiGraphics,
//                    x + 8, y + 8,
//                    x + newPos.x() - actualX + 8,
//                    y + newPos.y() - actualY + 8, MiscUtils.colorLerp((float) currentStress, 0xFFFFFFFF, 0xFFFF0000), true);
//

            int[] colors = new int[]{0xFFFFFFFF, 0xFFc0c0c0};
            GuiRendererHelper.renderLine_ColorPattern(guiGraphics,
                    x + 8,
                    y + 8,
                    x + newPos.x() - actualX + 8,
                    y + newPos.y() - actualY + 8, colors,
                    (int) (length/8), ItemInteractionsConfig.enableGuiParticles
            );

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
            pose.translate(newPos.x() - actualX, newPos.y - actualY, 0);
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
                Item_interactions_mod.errorMessage(error);
                MiscUtils.displayErrorInUi(error);
            }
        }



    }

    boolean isDead = false;





}
