package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.GlobalDirt;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(AbstractRecipeBookScreen.class)
public abstract class AbstractRecipeBookScreenMixin {

    @Inject(method = "render", at = @At("HEAD"))
    public void renderMixinHead(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        GlobalDirt.renderHead(guiGraphics);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderMixinTail(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        GlobalDirt.renderTail(guiGraphics);
    }

}



