package dev.mineland.item_interactions_mod.renderState;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.ItemInteractionsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class GuiFloatingItemRenderer extends PictureInPictureRenderer<GuiFloatingItemRenderState> {

    public GuiFloatingItemRenderer(BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    public @NotNull Class<GuiFloatingItemRenderState> getRenderStateClass() {
        return GuiFloatingItemRenderState.class;
    }

    @Override
    protected void renderToTexture(GuiFloatingItemRenderState pictureInPictureRenderState, PoseStack poseStack) {
        Quaternionf quaternionf = pictureInPictureRenderState.overrideCameraAngle();

        if (quaternionf != null) {
            ItemInteractionsMod.infoMessage("Override camera provided");
        }

        PoseStack finalPose = new PoseStack();
        finalPose.last().set(poseStack.last());

        // Apply inverted scaling because needs to
        // and center the render
        finalPose.pushPose();
        finalPose.scale(1, -1, -1);
        finalPose.translate(0, 2, 0);



//        Apply the animated transform
        finalPose.pushPose();
        finalPose.mulPose(pictureInPictureRenderState.poseStack().last().pose());

        boolean blockLight = pictureInPictureRenderState.renderState().usesBlockLight();
        if (blockLight) {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
        } else {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        }


        int magicNumber = 0xF000F0; // 15728880
        int lightLevelMaybe = 0;

        var node = new SubmitNodeStorage();

        pictureInPictureRenderState.renderState().submit(finalPose, node, magicNumber, OverlayTexture.NO_OVERLAY, lightLevelMaybe);

        var featureRendererDispatcher = new FeatureRenderDispatcher(
                node,
                //~ if >= 26.1 '.getBlockRenderer()' -> '.getModelManager()'
                Minecraft.getInstance().getBlockRenderer(),
                bufferSource,
                Minecraft.getInstance().getAtlasManager(),
                Minecraft.getInstance().renderBuffers().outlineBufferSource(),
                Minecraft.getInstance().renderBuffers().crumblingBufferSource(),
                Minecraft.getInstance().font
                //?if >= 26.1
                //,Minecraft.getInstance().gameRenderer.getGameRenderState()
        );

        featureRendererDispatcher.renderAllFeatures();
    }

    @Override
    protected String getTextureLabel() {
        return "item_interactions_mod: renderFloatingItem";
    }
}
