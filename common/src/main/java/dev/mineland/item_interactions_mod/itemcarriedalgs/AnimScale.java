package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;

import static dev.mineland.item_interactions_mod.GlobalDirt.*;

public class AnimScale extends AnimTemplate {

    static int itemOffset = 8;
    static float scale = 0;


    public static void modifyPose(PoseStack pose, int x, int y, int z) {
        pose.pushPose();
        pose.mulPose(makePose(x, y, z).last().pose());
    }
    public static PoseStack makePose(int x, int y, int z) {

//        System.out.println("makePose(" + x + ", " + y + ", " + z + ")");
//        setVariables();
        scale = (float) ( Math.abs(Math.cos(Math.PI * (msCounter / ((tickScale) / ItemInteractionsConfig.scaleSpeed)))) * ItemInteractionsConfig.scaleAmount);

        PoseStack newPose = new PoseStack();
//        newPose.translate(-itemOffset -(x * scale),-itemOffset -(y * scale), 0);
        newPose.scale(1 + scale, 1 + scale, 1 + scale);
//        newPose.translate(itemOffset, itemOffset, 0);


//        setVariables();
//        GlobalDirt.rollbackPose = rollback(x, y, z);
        return newPose;
    }

    public static PoseStack rollback(PoseStack pose, int x, int y, int z) {
//        setVariables();
        float localScale = scale;

//        this.scale = (float) Math.abs(Math.cos(Math.PI * (msCounter / ((tickScale) / ItemInteractionsConfig.scaleSpeed)))) * ItemInteractionsConfig.scaleAmount;

        PoseStack newPose = new PoseStack();

        newPose.translate(-itemOffset, -itemOffset, 0);
        newPose.scale(1 / (1 + localScale), 1 / (1 + localScale), 1);
        newPose.translate(itemOffset + (x * localScale), itemOffset + (y * localScale), 0);

//        pose.translate(-itemOffset -(x * scale),-itemOffset -(y * scale), 0);
//        pose.scale(1 + scale, 1 + scale, 1);
//        pose.translate(itemOffset, itemOffset, 0);

//        setLastVariables();

        return newPose;

    }
}
