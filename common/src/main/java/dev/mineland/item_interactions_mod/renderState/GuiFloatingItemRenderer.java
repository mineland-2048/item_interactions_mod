package dev.mineland.item_interactions_mod.renderState;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.GuiRendererHelper;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import dev.mineland.item_interactions_mod.ItemInteractionsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
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

//        Apply the animated transform
        finalPose.pushPose();
        finalPose.mulPose(pictureInPictureRenderState.poseStack().last().pose());

//        Center the pivot
        finalPose.translate(-0.5f, -0.5f, -0.5f);
//        finalPose.scale(1, -1, 1);

//        Finally, render the thing

//        Vector3f pos = new Vector3f(ItemInteractionsConfig.getAnimationSetting().itemPos);

        boolean blockLight = pictureInPictureRenderState.renderState().usesBlockLight();
        if (blockLight) {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
        } else {
            Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        }

        int magicNumber = 0xF000F0; // 15728880
        int lightLevelMaybe = 0;

        // Is the itme renderbuffer cutout?
        var consumer = this.bufferSource.getBuffer(RenderType.cutout());

//        pictureInPictureRenderState.renderState().submit(tempPose, nodeCollector, magicNumber, OverlayTexture.NO_OVERLAY, lightLevelMaybe);
        var node = new SubmitNodeStorage();

        pictureInPictureRenderState.renderState().submit(poseStack, node, magicNumber, OverlayTexture.NO_OVERLAY, lightLevelMaybe);


//        Dummy node to get the quads for the item
        var h = node.getSubmitsPerOrder();
        h.forEach((integer, submitNodeCollection) -> {
             submitNodeCollection.getItemSubmits().forEach(itemSubmit -> {
                var itemPose = new PoseStack();

                float rotationAmount = (float) (Math.sin(GlobalDirt.msCounter* 0.1) * Math.PI);
                itemPose.rotateAround(new Quaternionf().rotateLocalX(rotationAmount), 0, 0, 0);
                itemPose.pushPose();

//                Gets the pose from the item and sets it like in OversizedItemRenderer
                itemPose.pushPose();
                itemPose.last().set(itemSubmit.pose());
                itemPose.scale(1.0f, -1.0f, -1.0f);

//                the 0.5 is from the pivot centering
                itemPose.translate(0.0f + 0.5f, 2.0f - 0.5f + (blockLight ? 2f : 0), 0.0f - 0.5f);



                itemPose.pushPose();
//                Finally, apply the tranformation from the animation
                itemPose.last().mulPose(finalPose.last().pose());




                ItemRenderer.renderItem(
                        itemSubmit.displayContext(),
                        itemPose,
                        bufferSource,
                        itemSubmit.lightCoords(),
                        itemSubmit.overlayCoords(),
                        itemSubmit.tintLayers(),
                        itemSubmit.quads(),
                        itemSubmit.renderType(),
                        itemSubmit.foilType()

                );

//                 for (var bakedQuad : itemSubmit.quads()) {
//                     float alpha;
//                     float r;
//                     float g;
//                     float b;
//
//                     if (bakedQuad.isTinted()) {
//                         int layerColor = getLayerColorSafe(tintLayers, bakedQuad.tintIndex());
//                         alpha = ARGB.alpha(layerColor) / 255.0F;
//                         r = ARGB.red(layerColor) / 255.0F;
//                         g = ARGB.green(layerColor) / 255.0F;
//                         b = ARGB.blue(layerColor) / 255.0F;
//                     } else {
//                         alpha = 1.0F;
//                         r = 1.0F;
//                         g = 1.0F;
//                         b = 1.0F;
//                     }
//
//                     consumer.putBulkData(finalPose.last(), bakedQuad, r, g, b, alpha, itemSubmit.lightCoords(), itemSubmit.overlayCoords());
//
//                 }
             });
            }
        );

//        System.out.println(node.toString());

//        consumer.putBulkData(new PoseStack.Pose());




//        This code is being reached.
//        TODO: find why tf this is not rendering.
//        ItemInteractionsMod.infoMessage("rendering??");


    }

//    Taken from ItemRenderer
    private static int getLayerColorSafe(int[] is, int i) {
        return i >= 0 && i < is.length ? is[i] : -1;
    }


    @Override
    protected String getTextureLabel() {
        return "item_interactions_mod: renderFloatingItem";
    }
}
