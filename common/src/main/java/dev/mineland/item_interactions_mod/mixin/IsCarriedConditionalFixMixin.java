package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.GlobalDirt;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.renderer.item.properties.conditional.IsCarried.class)
public class IsCarriedConditionalFixMixin {

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public void get(ItemStack itemStack, ClientLevel clientLevel, LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext, CallbackInfoReturnable<Boolean> cir) {

        if (GlobalDirt.carriedItem == itemStack) cir.setReturnValue(true);;
    }
}
