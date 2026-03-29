package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.GlobalDirt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(AbstractRecipeBookScreen.class)
public abstract class AbstractRecipeBookScreenMixin {

    @Inject(
            at = @At("HEAD"),
            //~ if >= 26.1 'render' -> 'extractRenderState'
            method = "extractRenderState"
    )
    public void renderMixinHead(GuiGraphicsExtractor guiGraphics, int i, int j, float f, CallbackInfo ci) {
        GlobalDirt.renderHead(guiGraphics);
    }

    @Inject(
            at = @At("TAIL"),
            //~ if >= 26.1 'render' -> 'extractRenderState'
            method = "extractRenderState"
    )
    public void renderMixinTail(GuiGraphicsExtractor guiGraphics, int i, int j, float f, CallbackInfo ci) {
        GlobalDirt.renderTail(guiGraphics);
    }

}



