package dev.mineland.item_interactions_mod.neoforge;

import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

@Mod(Item_interactions_mod.MOD_ID)
public final class Item_interactions_modNeoForge {
    private static Path modFile;
    public Item_interactions_modNeoForge(ModContainer container) {
        // Run our common setup.
        container.registerExtensionPoint(IConfigScreenFactory.class, new ItemInteractionsConfigNeoforge() {});
        Item_interactions_mod.init();


        modFile = container.getModInfo().getOwningFile().getFile().getFilePath();

    }
}
