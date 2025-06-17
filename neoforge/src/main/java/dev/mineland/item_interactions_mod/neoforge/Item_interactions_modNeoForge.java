package dev.mineland.item_interactions_mod.neoforge;

import dev.mineland.item_interactions_mod.ItemInteractionsMod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.nio.file.Path;

@Mod(ItemInteractionsMod.MOD_ID)
public final class Item_interactions_modNeoForge {
    private static Path modFile;
    public Item_interactions_modNeoForge(ModContainer container) {
        // Run our common setup.
        container.registerExtensionPoint(IConfigScreenFactory.class, new ItemInteractionsConfigNeoforge() {});
        ItemInteractionsMod.init();

        ItemInteractionsMod.LOADER = ItemInteractionsMod.LOADER_ENUM.NEOFORGE;
        


        modFile = container.getModInfo().getOwningFile().getFile().getFilePath();

    }
}
