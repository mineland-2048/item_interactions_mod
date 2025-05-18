package dev.mineland.item_interactions_mod.mixin;

import dev.mineland.item_interactions_mod.ItemInteractionsSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VideoSettingsScreen.class)
public abstract class VideoSettingsScreenMixin {



    @Inject(method = "addOptions", at = @At("TAIL"))
    protected void addSettingsButton(CallbackInfo callbackInfo) {
        VideoSettingsScreen screen = (VideoSettingsScreen) (Object) this;
//        VideoSettingsScreenHelper.modify();


        int buttonSize = Button.DEFAULT_HEIGHT;
        int x = buttonSize - Button.DEFAULT_SPACING;
        int y = buttonSize - Button.DEFAULT_SPACING;

        SpriteIconButton iconButton = new SpriteIconButton.Builder(
                Component.literal("Item interactions mod settings"),
                btn -> {
                    Minecraft.getInstance().setScreen(new ItemInteractionsSettingsScreen(screen));
                },
                true
            )
            .sprite(ResourceLocation.fromNamespaceAndPath("item_interactions_mod", "icon/settings_icon"), 15, 15)
            .size(buttonSize, buttonSize)
            .build();

//        Button button = SpriteIconButton.builder(Component.literal(""), btn -> {
//            Minecraft.getInstance().setScreen(new ItemInteractionsSettingsScreen(screen));
//        }).bounds(x, y, buttonSize, buttonSize).build();
//        button = screen.layout.addToFooter(button, layoutSettings -> {
//            layoutSettings.alignHorizontallyRight();
//            layoutSettings.paddingRight(Button.DEFAULT_SPACING);
//        });

        iconButton = screen.layout.addToFooter(iconButton, layoutSettings -> {
            layoutSettings.alignHorizontallyRight();
            layoutSettings.paddingRight(Button.DEFAULT_SPACING);
        });


        iconButton.setTooltip(Tooltip.create(Component.literal("Item interactions mod settings")));



    }



}
