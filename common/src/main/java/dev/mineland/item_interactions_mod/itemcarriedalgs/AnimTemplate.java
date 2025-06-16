package dev.mineland.item_interactions_mod.itemcarriedalgs;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector3f;

import java.util.HashMap;

public class AnimTemplate {

    public String getId() {
        return id;
    }

    private String id;
    public HashMap<String, Object> settings = new HashMap<>();

    public Vector3f itemPos = new Vector3f();
    public Vector3f itemSpeed = new Vector3f();

    public PoseStack makePose(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d, GuiGraphics guiGraphics) {
        return new PoseStack();
    }

    public PoseStack makePoseNoUpdate(int x, int y, int z, double doubleSpeedX, double doubleSpeedY, boolean is3d, GuiGraphics guiGraphics) {
        return makePose(x,y,z,doubleSpeedX,doubleSpeedY,is3d,guiGraphics);
    }

    public AnimTemplate(String id) {
        this.id = id;
    }
    
    public AnimTemplate() {}

    public void reset(int initialX, int initialY, int initialZ) {
        itemPos.set(0, 0, 0);
        itemSpeed.set(0,0,0);
    }

    public HashMap<String, Object> getSettingsList() {
        return settings;
    }

    public Object getSetting(String setting) {
        return ItemInteractionsConfig.getSetting(setting);
    }

    public void addSetting(String id, Object value) {
        settings.put(id, value);
    }

    public void refreshSettings() {}

}
