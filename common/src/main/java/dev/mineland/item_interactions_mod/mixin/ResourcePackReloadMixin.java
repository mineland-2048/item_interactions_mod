package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.ItemInteractionsResources;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(Minecraft.class)
public class ResourcePackReloadMixin {
//    @Inject(at = @At("TAIL"), method = "reloadResourcePacks(ZLnet/minecraft/client/Minecraft$GameLoadCookie;)Ljava/util/concurrent/CompletableFuture;")
//    void resourceReload(boolean bl, @Nullable Minecraft.GameLoadCookie gameLoadCookie, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
//        ItemInteractionsResources.onReload();
//
//    }
}
