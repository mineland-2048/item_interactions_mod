package dev.mineland.item_interactions_mod.neoforge;

import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Item_interactions_mod.MOD_ID)
public final class Item_interactions_modNeoForge {
    public Item_interactions_modNeoForge(ModContainer container) {
        // Run our common setup.
        container.registerExtensionPoint(IConfigScreenFactory.class, new ItemInteractionsConfigNeoforge() {
        });
        Item_interactions_mod.init();
    }
}
