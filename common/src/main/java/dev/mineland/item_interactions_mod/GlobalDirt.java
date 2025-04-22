package dev.mineland.item_interactions_mod;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;


public class GlobalDirt {
    public static boolean isCurrentItem3d;
    public static ItemStack carriedItem;

    public static long lastMilis = 0;

//    public static int animationSetting = Iteminteractions.getAnimationSetting();
    public static float msCounter = 0;

    public static boolean debugStuck = false;

    public static double     lastMouseX = 0, lastMouseY = 0,
                            speedX = 0, speedY = 0;

    public static int topPos = 0, leftPos = 0;

//    public static double mouseDdeceleration = 0.8f;

    public static float tickScale = 1;

    public static boolean skipCalcs = false;

    public static double mouseDeltaX = 0, mouseDeltaY = 0;


    public static long currentMilis = 0;
    public static float tickRate = 0;
    public static long frameTime = 0;
    public static float tickDelta = 0;
//    public static float shortFPS = 0;

    public static float drag = 0.8f;

    public static Quaternionf rollback;
    public static PoseStack.Pose rollbackPose;

    public static void restore() {
//        System.out.println("Restoring global dirt");
        lastMouseX = 0;
        lastMouseY = 0;
        lastMilis = 0;

        msCounter = 0;


        speedX = 0;
        speedY = 0;

        topPos = 0;
        leftPos = 0;

    }

    public static void updateMousePositions() {
        GlobalDirt.lastMouseX = Minecraft.getInstance().mouseHandler.xpos();
        GlobalDirt.lastMouseY = Minecraft.getInstance().mouseHandler.ypos();
//        System.out.println("Updating Mouse Position");
    }


    public static void updateTimer() {
//        System.out.println("Updating Timer");

        currentMilis = Util.getMillis();

        tickRate = Minecraft.getInstance().level != null ?
                Minecraft.getInstance().level.tickRateManager().tickrate() : 20;

        tickScale = tickRate / 20;

        frameTime = currentMilis - lastMilis;
        tickDelta = ((frameTime) / 1000f);

        drag = (float) Math.pow(
                    ( (2*ItemInteractionsConfig.mouseDeceleration) * tickScale/1000),
                    tickDelta * tickScale * ItemInteractionsConfig.mouseDeceleration * 2
        );

        mouseDeltaX = Minecraft.getInstance().mouseHandler.xpos() - lastMouseX;
        mouseDeltaY = Minecraft.getInstance().mouseHandler.ypos() - lastMouseY;
        speedX = Math.clamp((speedX + (mouseDeltaX * ItemInteractionsConfig.mouseSpeedMult)) * drag,-40f,  40f);
        speedY = Math.clamp((speedY + (mouseDeltaY * ItemInteractionsConfig.mouseSpeedMult)) * drag,-40f,  40f);






    }

    public static void tailUpdateTimer() {
        msCounter += tickDelta;
        msCounter %= 1000;
        lastMilis = currentMilis;
//        System.out.println("Finished timer");
    }

//    public static List<TransitionItem> transItems;


//    public static void addTransItem(ItemStack itemStack, Slot slot, int lastMouseX, int lastMouseY) {
//        transItems.add(new TransitionItem(itemStack, slot, lastMouseX, lastMouseY));
//    }
}


