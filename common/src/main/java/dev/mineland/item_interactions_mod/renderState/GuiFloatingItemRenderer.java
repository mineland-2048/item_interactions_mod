package dev.mineland.item_interactions_mod.renderState;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import dev.mineland.item_interactions_mod.ItemInteractionsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GuiFloatingItemRenderer extends PictureInPictureRenderer<GuiFloatingItemRenderState> {

    public GuiFloatingItemRenderer(BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    public Class<GuiFloatingItemRenderState> getRenderStateClass() {
        return GuiFloatingItemRenderState.class;
    }

    @Override
    protected void renderToTexture(GuiFloatingItemRenderState pictureInPictureRenderState, PoseStack poseStack) {
        Quaternionf quaternionf = pictureInPictureRenderState.overrideCameraAngle();

        if (quaternionf != null) {
            ItemInteractionsMod.infoMessage("Override camera provided");
        }



        PoseStack finalPose = new PoseStack();
//        double trans = 1 * Math.cos(GlobalDirt.msCounter);
//
//        double transX = 0.5 * Math.clamp (2 * Math.cos(GlobalDirt.msCounter), -1, 1);
//        double transY = 0.5 * Math.clamp (2 * Math.sin(GlobalDirt.msCounter), -1, 1);

//        Position according to the gui
        finalPose.pushPose();
        finalPose.mulPose(poseStack.last().pose());

//        Center the item
//        finalPose.translate(transX, transY, 0);
        finalPose.translate(0, -2, 0);


//        Apply the animated transform
        finalPose.pushPose();
        finalPose.scale(1, 1, -1);
        finalPose.mulPose(pictureInPictureRenderState.poseStack().last().pose());

//        Flip since its upside down
        finalPose.scale(1, -1, 1);


        finalPose.pushPose();

//        re center the item again(?)
//        finalPose.translate(0, 1, 0);

//        Finally, render the thing

        Vector3f pos = new Vector3f(ItemInteractionsConfig.getAnimationSetting().itemPos);


        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
        if (!pictureInPictureRenderState.renderState().usesBlockLight()) {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        }
        pictureInPictureRenderState.renderState().render(finalPose, this.bufferSource, 15728880, OverlayTexture.NO_OVERLAY);



    }

    @Override
    protected String getTextureLabel() {
        return "item";
    }
}
