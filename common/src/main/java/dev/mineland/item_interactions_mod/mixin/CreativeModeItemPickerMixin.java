package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.GlobalDirt;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.ItemPickerMenu.class)
public class CreativeModeItemPickerMixin {

    @Inject(at = @At("HEAD"), method = "scrollTo")
    public void scrollToMixin(float f, CallbackInfo callbackInfo) {
        GlobalDirt.isInventoryScrolling = true;
    }

}
