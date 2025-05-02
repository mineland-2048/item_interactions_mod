package dev.mineland.item_interactions_mod;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.CarriedInteractions.Particles.BaseParticle;
import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.GuiParticleSpawner;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class GlobalDirt {
    public static class slotSpawners{
        private static final List<List<GuiParticleSpawner>> SPAWNERS = new ArrayList<>(90);

        public static void setState(int id, String state) {
            if (SPAWNERS.get(id) == null || SPAWNERS.get(id).isEmpty()) return;
            for (GuiParticleSpawner s : SPAWNERS.get(id)) {
                s.setState(state);
            }
        }
        public static List<GuiParticleSpawner> get(int id) {
            return SPAWNERS.get(id);
        }

        public static void set(int id, GuiParticleSpawner guiParticleSpawner){
            SPAWNERS.get(id).clear();
            SPAWNERS.get(id).add(guiParticleSpawner);
        }

        public static void set(int id, List<GuiParticleSpawner> guiParticleSpawners, String state) {
            for (GuiParticleSpawner s : guiParticleSpawners) s.setState(state);
            SPAWNERS.set(id, guiParticleSpawners);
        }

        public static void set(int id, List<GuiParticleSpawner> guiParticleSpawners) {
            set(id, guiParticleSpawners, "onIdle");
        }

        public static int size() {
            return SPAWNERS.size();
        }

        public static void clear() {
            SPAWNERS.clear();
        }

        public static void add(List<GuiParticleSpawner> guiParticleSpawner) {
            SPAWNERS.add(guiParticleSpawner);
        }

        public static void add(int id, GuiParticleSpawner guiParticleSpawner) {
            SPAWNERS.get(id).add(guiParticleSpawner);
        }

        public static List<ResourceLocation> getIdList(int id) {
            List<ResourceLocation> result = new ArrayList<>();
            if (SPAWNERS.get(id) == null) return result;
            for(GuiParticleSpawner s : get(id)) {
                result.add(s.getName());
            }
            return result;
        }


        public static void tick(int id, float time, GuiGraphics guiGraphics, float globalX, float globalY, float speedX, float speedY) {
            tickSpawners(SPAWNERS.get(id), time, guiGraphics, globalX, globalY, speedX, speedY);
        }
        public static void tickSpawners(List<GuiParticleSpawner> guiParticleSpawners, float time, GuiGraphics guiGraphics, float globalX, float globalY, float speedX, float speedY) {
            for (GuiParticleSpawner guiParticleSpawner : guiParticleSpawners) {
                guiParticleSpawner.tick(time, guiGraphics, globalX, globalY, speedX, speedY);
            }

        }


    }
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

    public static int particleCount = 0;

    public static Quaternionf rollback;
    public static PoseStack.Pose rollbackPose;


    public static int slotCount = 0;

    public static List<GuiParticleSpawner> carriedGuiParticleSpawner = new ArrayList<>();

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

        carriedGuiParticleSpawner.clear();

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


        particleCount = 0;





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


