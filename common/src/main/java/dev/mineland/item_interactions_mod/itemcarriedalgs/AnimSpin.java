package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.GuiRendererHelper;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import dev.mineland.item_interactions_mod.MiscUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class AnimSpin extends AnimTemplate {

    public AnimSpin() {
        super("spin");
    }

    private Quaternionf rotation = new Quaternionf();

    @Override
    public void reset(int initialX, int initialY, int initialZ) {
        super.reset(initialX, initialY, initialZ);
        rotation = new Quaternionf();
    }

    @Override
    public PoseStack makePose(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d, GuiGraphics guiGraphics) {

        PoseStack pose = new PoseStack();

        if (!GlobalDirt.skipCalcs) {
            itemSpeed.set(
                    (float) ((itemSpeed.x() + (doubleSpeedX * 0.05)) * 0.95) * (GlobalDirt.tickScale),
                    (float) ((itemSpeed.y() + (doubleSpeedY * 0.05)) * 0.95) * (GlobalDirt.tickScale),
                    itemSpeed.z()
            );


            rotation.rotateLocalX(-itemSpeed.y() * Mth.DEG_TO_RAD);
            rotation.rotateLocalY( itemSpeed.x() * Mth.DEG_TO_RAD);
        }

        pose.rotateAround(rotation, x+8, y+8, z+150);

        if (ItemInteractionsConfig.debugDraws)  GuiRendererHelper.renderLine(guiGraphics, x + 8, y + 8, x+8 + itemSpeed.x(), y+8 + itemSpeed.y(), 0xFFFF0000, ItemInteractionsConfig.enableGuiParticles);


        return pose;
    }
}
