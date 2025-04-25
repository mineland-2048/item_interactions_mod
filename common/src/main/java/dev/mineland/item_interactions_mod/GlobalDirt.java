package dev.mineland.item_interactions_mod;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.CarriedInteractions.Particles.BaseParticle;
import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.Spawner;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;


public class GlobalDirt {
    public static boolean isCurrentItem3d;
    public static ItemStack carriedItem;

    public static boolean devenv = true;
    public static List<BaseParticle> particleList = new ArrayList<>();

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

    public static List<Spawner> slotSpawners = new ArrayList<>(90);

    public static int slotCount = 0;

    public static Spawner carriedSpawner = null;

    public static boolean shouldTickParticles;


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

        particleList.clear();
        slotSpawners.clear();

        carriedSpawner = null;

    }

    public static void updateMousePositions() {
        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        GlobalDirt.lastMouseX = Minecraft.getInstance().mouseHandler.xpos() / guiScale;
        GlobalDirt.lastMouseY = Minecraft.getInstance().mouseHandler.ypos() / guiScale;
//        System.out.println("Updating Mouse Position");
    }

    public static int tickCounter;

    public static void updateTimer() {
//        System.out.println("Updating Timer");
        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();

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

        mouseDeltaX = (Minecraft.getInstance().mouseHandler.xpos() / guiScale) - lastMouseX;
        mouseDeltaY = (Minecraft.getInstance().mouseHandler.ypos() / guiScale) - lastMouseY;
        speedX = Math.clamp((speedX + (mouseDeltaX * ItemInteractionsConfig.mouseSpeedMult)) * drag,-40f,  40f);
        speedY = Math.clamp((speedY + (mouseDeltaY * ItemInteractionsConfig.mouseSpeedMult)) * drag,-40f,  40f);



        double tickTime = Math.floor((msCounter * tickScale * 30));
        if (tickTime != tickCounter) {
            tickCounter = (int) tickTime;
            shouldTickParticles = true;
        } else shouldTickParticles = false;







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


