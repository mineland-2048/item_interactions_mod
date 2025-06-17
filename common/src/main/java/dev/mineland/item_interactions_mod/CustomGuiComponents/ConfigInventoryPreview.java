package dev.mineland.item_interactions_mod.CustomGuiComponents;

import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.ItemInteractionsMod;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ConfigInventoryPreview extends AbstractContainerWidget {

    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private final ClientFakeContainer container;

    private static int invX, invY;
    private static final int previewWidth = (3)*18 + 7 + 7;
    private static final int previewHeight = (3)*18 + 7 + 17;

    private static int mouseX, mouseY;

    public ConfigInventoryPreview(int i, int j, int k, int l, Component label) {
        super(i,j,k,l,label);

        int containerRows = 3;
        int imageHeight = 114 + containerRows * 18;
        int inventoryLabelY = imageHeight - 94;
        int imageWidth = 176;

        int previewWidth = containerRows*18 + 7 + 7;
        int previewHeight = 3*18 + 7 + 17;

        invX = getX() + (width / 2) - previewWidth/2;
        invY = getY() + (height/2) - previewHeight/2;

        this.container = new ClientFakeContainer(3, 3, invX + 7, invY + 17);


        mouseX = 0; mouseY = 0;

//        container.printItemStacks();
//        Slot newSlot1 = new Slot();
    }

    @Override
    protected int contentHeight() {
        return 0;
    }

    @Override
    protected double scrollRate() {
        return 0;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
//        guiGraphics.blit();

        Font font = Minecraft.getInstance().font;

        GlobalDirt.updateTimer();

        if (ItemInteractionsConfig.debugDraws) {
//            guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xFF0000FF);
//            guiGraphics.drawString(font, String.format("""
//                x: %d
//                y: %d
//                width: %d
//                height: %d
//                f: %f""", getX(), getY(), getWidth(), getHeight(), f),
//                    0, 0,0xFFFFFFFF);


        }


        int containerRows = 3;
//        int imageHeight = 114 + containerRows * 18;
//        int inventoryLabelY = imageHeight - 94;
//        int imageWidth = 176;
//
//        int previewWidth = containerRows*18 + 7 + 7;
//        int previewHeight = 3*18 + 7 + 17;



//        guiGraphics.blit(RenderType::guiTextured, CONTAINER_BACKGROUND, getX(), getY(), 0.0F, 0.0F, imageWidth, containerRows * 18 + 17, 256, 256);
//        guiGraphics.blit(RenderType::guiTextured, CONTAINER_BACKGROUND, getX(), getY() + (containerRows * 18 + 17), 0.0F, 215, imageWidth, 7, 256, 256);


//        TOPLEFT corner, TOP and LEFT edges
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND,
                invX, invY,
                0.0F, 0.0F,
                (18*3 + 7), (containerRows * 18 + 17),
                256, 256);

//        BOTTOMLEFT corner and BOTTOM Edge
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND,
                invX, invY + (containerRows * 18 + 17),
                0.0F, 215,
                (18*3 + 7), 7,
                256, 256);


//        TOPRIGHT corner and RIGHT edge
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND,
                invX + (18*3 + 7), invY,
                169, 0,
                7, (containerRows * 18 + 17),
                256, 256);

//        BOTTOMRIGHT corner
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND,
                invX + (18*3 + 7), invY + (containerRows * 18 + 17),
                169, 215,
                7, (7),
                256, 256);


        guiGraphics.pose().pushMatrix();
//        guiGraphics.pose().translate(0, 0, -20);
        container.render(guiGraphics, mouseX, mouseY);
        guiGraphics.pose().popMatrix();

        GlobalDirt.updateMousePositions();
        GlobalDirt.tailUpdateTimer();



//        if (ItemInteractionsConfig.debugDraws) {
//            long currentMilis = GlobalDirt.currentMilis;
//            float tickRate = GlobalDirt.tickRate;
//            float tickScale = GlobalDirt.tickScale;
//            long frameTime = GlobalDirt.frameTime;
//            float tickDelta = GlobalDirt.tickDelta;
//            guiGraphics.drawString(Minecraft.getInstance().font, "currentMilis: " + currentMilis, 100, 100, 0xFFFFFFFF);
//            guiGraphics.drawString(Minecraft.getInstance().font, "tickRate: " + tickRate, 100, 100 + 10, 0xFFFFFFFF);
//            guiGraphics.drawString(Minecraft.getInstance().font, "tickScale: " + tickScale, 100, 100 + 20, 0xFFFFFFFF);
//            guiGraphics.drawString(Minecraft.getInstance().font, "currentMilis: " + currentMilis, 100, 100 + 30, 0xFFFFFFFF);
//            guiGraphics.drawString(Minecraft.getInstance().font, "currentMilis: " + currentMilis, 100, 100 + 40, 0xFFFFFFFF);
//        }

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void setItem(int id, ItemStack item) {
        if (id < 0 || id >= container.getContainerSize()) {
            ItemInteractionsMod.warnMessage("Tried setting a slot that doesnt exist!");
            return;
        }
        container.setItem(id, item);
    }

    @Override
    public void setX(int left) {
        super.setX(left);
        invX = left + (width/2) - previewWidth/2;

        this.container.setX(invX + 7);
    }

    @Override
    public void setY(int top) {
        super.setY(top);
        invY = top + (height/2) - previewHeight/2;

        this.container.setY(invY + 17);
    }

    @Override
    public void setPosition(int left, int top) {
        super.setPosition(left, top);
        invX = left + (width/2) - previewWidth/2;
        invY = top + (height/2) - previewHeight/2;

        this.container.setPos(invX + 7, invY + 17);
    }


    public void setInvX(int x) {
        this.container.setX(x);
    }

    public void setInvY(int y) {
        this.container.setY(y);
    }

    public void setInvPos(int x, int y) {
        this.container.setPos(x ,y);
    }


    @Override
    public boolean mouseClicked(double d, double e, int i) {
//        ItemInteractionsMod.infoMessage("d: " + d + ", e" + e + ", i: " + i);

        this.container.mouseClicked(d, e, i);

        return super.mouseClicked(d, e, i);

    };

    @Override
    public void mouseMoved(double d, double e) {
        mouseX = (int) d;
        mouseY = (int) e;

        super.mouseMoved(d, e);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of();
    }
}
