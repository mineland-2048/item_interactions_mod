package dev.mineland.item_interactions_mod.backport;

import dev.mineland.item_interactions_mod.ItemInteractionsSettingsScreen;
import dev.mineland.item_interactions_mod.mixin.ScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class VideoSettingsMixinAddButton {
    public static void addButton(Screen screen) {
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
                .sprite(new ResourceLocation("item_interactions_mod", "textures/gui/sprites/icon/settings_icon.png"), 15, 15)
                .size(buttonSize, buttonSize)
                .build();

        iconButton.setTooltip(Tooltip.create(Component.literal("Item interactions mod settings")));
        iconButton.setPosition(x, y);

        ((ScreenAccessor) (Object) screen).invokeAddRenderableWidget(
                iconButton
        );

    }
}
