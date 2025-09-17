package dev.mineland.item_interactions_mod.forge;

import dev.mineland.item_interactions_mod.ItemInteractionsSettingsScreen;
import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod(Item_interactions_mod.MOD_ID)
public final class Item_interactions_modNeoForge {
    private static Path modFile;
    public Item_interactions_modNeoForge() {
        // Run our common setup.

        DistExecutor.unsafeRunForDist(() -> () ->{
            ModContainer container = net.minecraftforge.fml.ModList.get().getModContainerById(Item_interactions_mod.MOD_ID).orElseThrow(() -> new IllegalArgumentException("Mod not found: " + Item_interactions_mod.MOD_ID));
            container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                    new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ItemInteractionsSettingsScreen(screen))
            );
            Item_interactions_mod.init();
            Item_interactions_mod.LOADER = Item_interactions_mod.LOADER_ENUM.NEOFORGE;



            modFile = container.getModInfo().getOwningFile().getFile().getFilePath();
            return null;
        }, () -> () -> null);

    }
}
