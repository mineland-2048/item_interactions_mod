package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.*;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AnimRope extends AnimTemplate {

    public double elasticity = 0.4;
    public double length = 16;
    public Vector3f gravity = new Vector3f(0, 6f, 0);
    public double inertia = 0.8;
    public boolean pixelated = true;

    int[] colors = new int[]{0xFFAD7249, 0xFF844416};

    public double currentStress = 0;

    public float rotationAngle;

    Quaternionf rotation = new Quaternionf();

    public Vector3f globalItemPos = new Vector3f();

    public float angle = 0;

    private boolean isStill = false;
    private float actualX = 0, actualY = 0;
    private float oldAngle = 0;

    Vector3f prevPos = new Vector3f();
    Vector3f newPos = new Vector3f();
    Vector3f mousePos = new Vector3f();
    private final Vector3f oldMousePos = new Vector3f();
    boolean isDead = false;


    private final Vector3f smoothedMousePos = new Vector3f();
    private boolean firstTick = true;


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
        addSetting("rope_elasticity", 0.4);
        addSetting("rope_length", 16.0);
        addSetting("rope_gravity", new Vector3f(0, 6f, 0));
        addSetting("rope_inertia", 0.8);
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
        prevPos.set(globalItemPos);
        rotation = new Quaternionf();
        rotationAngle = 0;
        oldAngle = 0;
        oldMousePos.set(0,0,0);
        smoothedMousePos.set(initialX, initialY, initialZ);
        firstTick = true;
    }



    private void ropeSim(GuiGraphics guiGraphics, PoseStack pose, int x, int y, int z) {

        try {
            newPos.set(globalItemPos);

            if (!GlobalDirt.skipCalcs){
                mousePos.set(x, y, globalItemPos.z());
                actualX = x; actualY = y;
                update(guiGraphics, x, y, z, GlobalDirt.msTickDelta);
            }

            Vector3f renderPos = new Vector3f(prevPos).lerp(newPos, accumulator*30);

            guiGraphics.pose().translate(0, 0, 500);

            GuiRendererHelper.renderLine_ColorPattern(guiGraphics,
                    x + 8,
                    y + 8,
                    x + renderPos.x() - actualX + 8,
                    y + renderPos.y() - actualY + 8, this.colors,
                    (int) (length/8), pixelated
            );

            if (ItemInteractionsConfig.debugDraws) {
                GuiRendererHelper.renderLine_ColorPattern(guiGraphics,
                        x + 8,
                        y + 8,
                        x + newPos.x() - actualX + 8,
                        y + newPos.y() - actualY + 8, new int[]{0x40FF0000},
                        (int) (length/8), pixelated
                );

            }

            guiGraphics.pose().translate(0, 0, -500);

            rotationAngle = (float) MiscUtils.lerpRotation(Math.clamp(currentStress, 0, 1), rotationAngle, angle);
            float rotationDelta = rotationAngle - oldAngle;



            if (rotationDelta == 0 && !isStill) {
                isStill = true;
                rotationAngle = (float) (rotationAngle%Math.PI);

            } else {
                isStill = false;
            }

            oldAngle = rotationAngle;


            rotation.rotateZ(rotationDelta);
            globalItemPos.set(newPos);
            itemPos.set(newPos.x() - actualX, newPos.y - actualY, globalItemPos.z());
            pose.translate(renderPos.x - actualX, renderPos.y - actualY, 0);
            pose.pushPose();
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

    float accumulator = 0f;
    final float FIXED_STEP = 1f / 30f; // 60 FPS baseline.
    final float MAX_ACCUMULATED_TIME = 0.25f; // prevent spiral of death


    public void update(GuiGraphics guiGraphics, int x, int y, int z, float msTickDelta) {

        // cap delta to avoid huge jumps on lag spikes
        msTickDelta = Math.min(msTickDelta, MAX_ACCUMULATED_TIME);
        accumulator += msTickDelta;


        // physics steps in fixed time intervals because fuck me

        while (accumulator >= FIXED_STEP) {
            simulate(x, y, z, FIXED_STEP * 10 * GlobalDirt.tickScale);
            accumulator -= FIXED_STEP;
        }

    }



    public void simulate(int x, int y, int z, float delta) {
        prevPos.set(globalItemPos);
        actualX = x;
        actualY = y;

        // mouse lerping. Eh
        if (firstTick) {
            smoothedMousePos.set(mousePos);
            firstTick = false;
        } else {
            smoothedMousePos.lerp(mousePos, 1f - (float) Math.pow(0.1, delta * 60));
            // Smooths over ~6 frames
        }

//        float distance = newPos.distance(smoothedMousePos);
        angle = (float) Math.atan2(newPos.y() - smoothedMousePos.y(), newPos.x() - smoothedMousePos.x());

        Vector3f ropeDir = new Vector3f(
                newPos.x() - smoothedMousePos.x(),
                newPos.y() - smoothedMousePos.y(),
                0f
        );

        float ropeLen = ropeDir.length();
        currentStress = Math.max(0, (ropeLen - length) / length);

        if (ropeLen != 0) ropeDir.div(ropeLen); // normals are annoying

        // spring force (soft constraint)
        float stretch = (float) (ropeLen - length);
        if (stretch > 0) {
            float springStrength = 10f;
            float springForceMag = Math.min(stretch * springStrength, 300f); // cap for stability
            Vector3f springForce = new Vector3f(ropeDir).mul(springForceMag * delta);
            itemSpeed.sub(springForce);
        }

        float gravityFactor = (float) (0.5 + (inertia * inertia * 0.5));
        itemSpeed.add(new Vector3f(gravity).mul(gravityFactor * delta));

        // tangential preserving dampening
        float outwardSpeed = itemSpeed.dot(ropeDir);
        Vector3f radialVel = new Vector3f(ropeDir).mul(outwardSpeed);
        Vector3f tangentialVel = new Vector3f(itemSpeed).sub(radialVel);

        radialVel.mul(0.92f);
        tangentialVel.mul(0.995f);

        itemSpeed.set(radialVel.add(tangentialVel));

        newPos.add(new Vector3f(itemSpeed).mul(delta));

        // elastic soft cap
        float maxRopeLength = (float) (length * (1f + elasticity * 1.25f));
        float newDistance = newPos.distance(smoothedMousePos);

        if (newDistance > maxRopeLength) {
            Vector3f dir = new Vector3f(newPos).sub(smoothedMousePos).normalize();
            newPos.set(smoothedMousePos.x() + dir.x * maxRopeLength,
                    smoothedMousePos.y() + dir.y * maxRopeLength,
                    newPos.z());

            float overshootSpeed = itemSpeed.dot(dir);
            if (overshootSpeed > 0) {
                Vector3f outwardVel = new Vector3f(dir).mul(overshootSpeed);
                itemSpeed.sub(outwardVel).sub(outwardVel.mul(0.05f));
            }
        }

        globalItemPos.set(newPos);
    }








}


