package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.ItemInteractionsMod;
import dev.mineland.item_interactions_mod.renderState.GuiFloatingItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;
import java.util.List;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @ModifyArgs(method = "<init>", at = @At(
            value = "INVOKE",
//          target = "Lnet/minecraft/client/gui/render/GuiRenderer;<init>(Lnet/minecraft/client/gui/render/state/GuiRenderState;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Ljava/util/List;)V"))

            //? if >= 26.1 {
            /*target = "Lnet/minecraft/client/gui/render/GuiRenderer;<init>(Lnet/minecraft/client/renderer/state/gui/GuiRenderState;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;Ljava/util/List;)V"
            *///?} else
            target = "Lnet/minecraft/client/gui/render/GuiRenderer;<init>(Lnet/minecraft/client/gui/render/state/GuiRenderState;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;Ljava/util/List;)V"
    ))
    public void renderGuiFloatingItemRenderer(Args args) {
        if (ItemInteractionsMod.isNeo()) {
            return;
        }

        List<PictureInPictureRenderer<?>> original = args.get(4);
        List<PictureInPictureRenderer<?>> modified = new ArrayList<>(original);
        modified.add(new GuiFloatingItemRenderer(Minecraft.getInstance().renderBuffers().bufferSource()));
        args.set(4, modified);
//            ItemInteractionsMod.debugInfoMessage("Renderer rendered in the mixin");

    }
}
