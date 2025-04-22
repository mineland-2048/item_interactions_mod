package dev.mineland.item_interactions_mod.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.mineland.item_interactions_mod.ItemInteractionsSettingsScreen;

public class ItemInteractionsModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ItemInteractionsSettingsScreen::new;
    }
}
