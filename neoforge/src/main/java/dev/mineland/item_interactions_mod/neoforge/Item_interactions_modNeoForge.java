package dev.mineland.item_interactions_mod.neoforge;

import dev.mineland.item_interactions_mod.ItemInteractionsSettingsScreen;
import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ConfigScreenHandler;

import java.nio.file.Path;

@Mod(Item_interactions_mod.MOD_ID)
public final class Item_interactions_modNeoForge {
    private static Path modFile;
    public Item_interactions_modNeoForge(ModContainer container) {
        // Run our common setup.
        container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
            new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ItemInteractionsSettingsScreen(screen))
        );
        Item_interactions_mod.init();
        Item_interactions_mod.LOADER = Item_interactions_mod.LOADER_ENUM.FABRIC;
        


        modFile = container.getModInfo().getOwningFile().getFile().getFilePath();

    }
}
