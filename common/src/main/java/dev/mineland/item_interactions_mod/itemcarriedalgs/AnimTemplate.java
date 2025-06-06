package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;

public class AnimTemplate {

    public String id;
    public PoseStack makePose(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d) {
        return new PoseStack();
    }

    public AnimTemplate(String id) {
        this.id = id;
    }

    public void reset() {
        System.out.println("r");
    }

}
