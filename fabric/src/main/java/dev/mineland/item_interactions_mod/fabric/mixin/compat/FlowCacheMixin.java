package dev.mineland.item_interactions_mod.fabric.mixin.compat;

import dev.imb11.flow.render.RenderHelper;
import dev.mineland.item_interactions_mod.GlobalDirt;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(RenderHelper.class)
public class FlowCacheMixin {

    @Inject(method = "cacheScreen", at = @At("HEAD"))
    private static void cacheScreenMixinHead(AbstractContainerScreen<?> screenToCache, GuiGraphics ctx, float tickDelta, int mouseX, int mouseY, CallbackInfo ci) {
//        GlobalDirt.dontUpdateTimer = true;
//        GlobalDirt.shouldTickParticles = false;
    }
    @Inject(method = "cacheScreen", at = @At("TAIL"))
    private static void cacheScreenMixinTail(AbstractContainerScreen<?> screenToCache, GuiGraphics ctx, float tickDelta, int mouseX, int mouseY, CallbackInfo ci) {
//        GlobalDirt.dontUpdateTimer = false;
    }

}
