package dev.mineland.item_interactions_mod.neoforge;

import dev.mineland.item_interactions_mod.GuiParticlesReloadListener;
import dev.mineland.item_interactions_mod.ItemInteractionsMod;
import dev.mineland.item_interactions_mod.renderState.GuiFloatingItemRenderState;
import dev.mineland.item_interactions_mod.renderState.GuiFloatingItemRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import java.nio.file.Path;

@Mod(ItemInteractionsMod.MOD_ID)
public final class Item_interactions_modNeoForge {
    private static Path modFile;
    public Item_interactions_modNeoForge(ModContainer container) {
        // Run our common setup.
        container.registerExtensionPoint(IConfigScreenFactory.class, new ItemInteractionsConfigNeoforge() {});
        ItemInteractionsMod.init();
        ItemInteractionsMod.LOADER = ItemInteractionsMod.LOADER_ENUM.NEOFORGE;

        ReloadListenerHelperImpl.registerReloadListener(new GuiParticlesReloadListener());

        modFile = container.getModInfo().getOwningFile().getFile().getFilePath();

    }


}

@EventBusSubscriber(modid = ItemInteractionsMod.MOD_ID)
class registerPipEventThingy {
    @SubscribeEvent
    public static void registerPip(RegisterPictureInPictureRenderersEvent event) {
        event.register(
                GuiFloatingItemRenderState.class,
                GuiFloatingItemRenderer::new
        );

        ItemInteractionsMod.infoMessage("Neo forge pip renderer registered!");
    }

}
