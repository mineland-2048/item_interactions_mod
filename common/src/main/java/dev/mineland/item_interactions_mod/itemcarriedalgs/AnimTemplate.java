package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

public class AnimTemplate {

    public String id;
    public HashMap<String, Object> settings = new HashMap<>();
    public PoseStack makePose(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d, GuiGraphics guiGraphics) {
        return new PoseStack();
    }

    public PoseStack makePoseNoUpdate(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d, GuiGraphics guiGraphics) {
        return makePose(x,y,z,doubleSpeedX,doubleSpeedY,is3d,guiGraphics);
    }

    public AnimTemplate(String id) {
        this.id = id;
    }

    public AnimTemplate defaults() {
        return new AnimTemplate();
    }

    public AnimTemplate() {}

    public void reset(int initialX, int initialY, int initialZ) {}

    public HashMap<String, Object> getSettingsList() {
        return settings;
    }

    public Object get(String setting) {
        return ItemInteractionsConfig.getSetting(setting);
    }

    public void addSetting(String id, Object value) {
        settings.put(id, value);
    }

}
