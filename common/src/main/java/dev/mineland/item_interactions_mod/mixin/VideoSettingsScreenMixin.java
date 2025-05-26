package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.ItemInteractionsSettingsScreen;
import dev.mineland.item_interactions_mod.backport.SpriteIconButton;
import dev.mineland.item_interactions_mod.backport.VideoSettingsMixinAddButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VideoSettingsScreen.class)
public abstract class VideoSettingsScreenMixin {




    @Inject(method = "init", at = @At("TAIL"))
    protected void addSettingsButton(CallbackInfo callbackInfo) {

        VideoSettingsScreen screen = (VideoSettingsScreen) (Object) this;
//        VideoSettingsScreenHelper.modify();


        VideoSettingsMixinAddButton.addButton(screen);



    }



}
