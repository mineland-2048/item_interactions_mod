package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.ItemInteractionsSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
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




        int buttonSize = Button.DEFAULT_HEIGHT;
        int x = screen.width - 8 - buttonSize;
        int y = screen.height - 27;


        SpriteIconButton iconButton = new SpriteIconButton.Builder(
                Component.literal("Item interactions mod settings"),
                btn -> {
                    Minecraft.getInstance().setScreen(new ItemInteractionsSettingsScreen(screen));
                },
                true
            )
            .sprite(new ResourceLocation("item_interactions_mod", "icon/settings_icon"), 15, 15)
            .size(buttonSize, buttonSize)
            .build();

        iconButton.setTooltip(Tooltip.create(Component.literal("Item interactions mod settings")));
        iconButton.setPosition(x, y);

        ((ScreenAccessor) (Object) this).invokeAddRenderableWidget(
                iconButton
        );
    }



}
