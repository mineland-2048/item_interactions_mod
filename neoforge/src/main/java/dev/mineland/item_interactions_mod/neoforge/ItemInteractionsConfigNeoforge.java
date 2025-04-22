package dev.mineland.item_interactions_mod.neoforge;

import dev.mineland.item_interactions_mod.ItemInteractionsSettingsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;

public class ItemInteractionsConfigNeoforge implements IConfigScreenFactory {

    public ItemInteractionsConfigNeoforge() {}

    @Override
    public @NotNull Screen createScreen(@NotNull ModContainer modContainer, Screen arg) {
        return new ItemInteractionsSettingsScreen(arg);
    }
}
