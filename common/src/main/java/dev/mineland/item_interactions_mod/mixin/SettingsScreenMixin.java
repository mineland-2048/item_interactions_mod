package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VideoSettingsScreen.class)
public class SettingsScreenMixin {
//    @Inject(at = @At("TAIL"), method = "onClose")
//    public void onCloseVideoMixin(CallbackInfo ci) {
//        Item_interactions_mod.refreshConfig();
//
//    }

}
