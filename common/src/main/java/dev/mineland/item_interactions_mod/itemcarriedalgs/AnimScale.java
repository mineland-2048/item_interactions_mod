package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

import static dev.mineland.item_interactions_mod.GlobalDirt.*;

public class AnimScale extends AnimTemplate {

    int itemOffset = 8;
    float scale = 0;

    double scaleSpeed = 1;
    double scaleAmount = 0.1;

    @Override
    public void refreshSettings() {
        scaleSpeed = (double) getSetting("scale_speed");
        scaleAmount = (double) getSetting("scale_amount");
//        scaleAmount = 10;
    }

    public AnimScale() {
        super("scale");
        addSetting("scale_speed", 1.0);
        addSetting("scale_amount", 0.1);
    }


    public PoseStack makePose(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d, GuiGraphics guiGraphics) {

        scale = (float) ( Math.abs(Math.cos(Math.PI * (msCounter / ((tickScale) / scaleSpeed)))) * scaleAmount);

        int posX = 8;
        int posY = 8;
        PoseStack newPose = new PoseStack();
        newPose.translate(-itemOffset -(posX * scale),-itemOffset -(posY * scale), 0);
        newPose.scale(1 + scale, 1 + scale, 1 + scale);
        newPose.translate(itemOffset, itemOffset, 0);

        return newPose;
    }

}
