package dev.mineland.item_interactions_mod.CustomGuiComponents;

import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.Item_interactions_mod;
import dev.mineland.item_interactions_mod.ItemInteractionsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClientFakeContainer implements Container {
    private final List<Slot> slots = new ArrayList<Slot>();
    private final List<ItemStack> itemStacks = new ArrayList<ItemStack>();

    private ItemStack mouseItem;

    private int x;
    private int y;
    private int slotSize = 18;
    private int columns, rows;
    public ClientFakeContainer(int columns, int rows, int left, int top) {
        int id = 0;
        this.x = left;
        this.y = top;
        this.columns = columns;
        this.rows = rows;
        int slotSize = this.slotSize;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Slot newSlot = new Slot(this, id, left + (j*slotSize), top + (i*slotSize));
                newSlot.index = id;
                ItemStack newEmptyItem = new ItemStack(ItemStack.EMPTY.getItem());
                slots.add(newSlot);
                itemStacks.add(newEmptyItem);
                id++;
            }
        }

        this.mouseItem = ItemStack.EMPTY;

    }


    public void setX(int x) {
        this.x = x;
        updateSlots();
    }

    public void setY(int y) {
        this.y = y;
        updateSlots();
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
        updateSlots();
    }

    public void updateSlots() {
        int id = 0;
        for (int i = 0; i < this.columns; i++) {
            for (int j = 0; j < this.rows; j++) {
                Slot newSlot = new Slot(this, id, this.x + (j*slotSize), this.y + (i*slotSize));
                newSlot.index = id;

                ItemStack oldItem = slots.get(id).getItem();
                newSlot.set(oldItem);
                slots.set(id, newSlot);

                id++;
            }
        }

    }

    @Override
    public int getContainerSize() {
        return slots.size();
    }

    public void renderSlots(GuiGraphics guiGraphics) {

        for (Slot slot : slots) {


            if (ItemInteractionsConfig.debugDraws) {
                int slotX = (int) ((slot.x - this.x) * 2);
                int slotY = (int) 40 + ((slot.y - this.y) * 2);


                guiGraphics.drawString(Minecraft.getInstance().font, "i: " + slot.index, (slotX), (slotY),  0xFFFFFFFF);
                guiGraphics.drawString(Minecraft.getInstance().font, "x: " + slot.x, (slotX), (slotY + 10), 0xFFFFFFFF);
                guiGraphics.drawString(Minecraft.getInstance().font, "y: " + slot.y, (slotX), (slotY + 20), 0xFFFFFFFF);

                guiGraphics.fill(slot.x, slot.y, slot.x + 18, slot.y + 18, 0x20FF0000);
                guiGraphics.renderOutline(slot.x, slot.y, 18, 18, 0x80FFFF00);

                guiGraphics.renderOutline(this.x, this.y, 18*3, 18*3, 0xFF00FFFF);


            }


            if (slot.getItem().isEmpty()) continue;
//            guiGraphics.drawString(Minecraft.getInstance().font, "x: " + slot.x)
            int itemX = slot.x + 1;
            int itemY = slot.y + 1;

            guiGraphics.renderItem(slot.getItem(), itemX, itemY);
            guiGraphics.renderItemDecorations(Minecraft.getInstance().font, slot.getItem(), itemX, itemY);


        }




        if (ItemInteractionsConfig.debugDraws) {
            guiGraphics.drawString(Minecraft.getInstance().font, "x: " + this.x + ", y: " + this.y, 0, 200, 0xFFFFFFFF);
        }



    }

    public void renderMouseItem(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.renderItem(mouseItem, x - 8, y - 8);
        guiGraphics.renderItemDecorations(Minecraft.getInstance().font, mouseItem, x - 8, y - 8);
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {

        renderSlots(guiGraphics);
        renderMouseItem(guiGraphics, mouseX, mouseY);

        GlobalDirt.skipCalcs = true;
        guiGraphics.renderItem(mouseItem, x + 18, y - 36);
        GlobalDirt.skipCalcs = false;
    }

    public void printItemStacks() {
        Item_interactions_mod.infoMessage(itemStacks.toString());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int i) {
        if (i > slots.size()) { return ItemStack.EMPTY; }
        if (i < 0) { return mouseItem; }

        return itemStacks.get(i);
    }

    @Override
    public ItemStack removeItem(int id, int count) {
//        Item_interactions_mod.infoMessage("removeItem(" + i + ", " + j + ")");
//        ItemStack slotItem, takenItem;
//        slotItem = slots.get(id).getItem();
//

        return slots.get(id).getItem().split(count);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int id) {
//        Item_interactions_mod.infoMessage("removeItemNoUpdate(" + i + ")");
        if (id >= slots.size() || id < 0 ) return ItemStack.EMPTY;
        ItemStack oldItem = slots.get(id).getItem();
        slots.get(id).set(ItemStack.EMPTY);
        return oldItem;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
//        Item_interactions_mod.infoMessage("setItem(" + i + ", " + itemStack.toString() + ")");
        itemStacks.set(i, itemStack);

    }



    @Override
    public void setChanged() {
//        GlobalDirt.carriedItem = mouseItem;
//        Item_interactions_mod.infoMessage("setChanged()");
    }

    @Override
    public boolean stillValid(Player player) {
//        Item_interactions_mod.infoMessage("stillValid() called oh no");

        return false;
    }

    @Override
    public void clearContent() {
//        Item_interactions_mod.infoMessage("clearContent()");

    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
//        if (!this.slots.get(i).hasItem()) {

//        this.slots.get(i).safeInsert(this.mouseItem);
        return Container.super.canPlaceItem(i, itemStack);


//        }
    }


    @Override
    public boolean canTakeItem(Container container, int i, ItemStack itemStack) {
        return Container.super.canTakeItem(container, i, itemStack);
    }

    public ItemStack getMouseItem() {
        return mouseItem;
    }

    public void mouseClicked(double x, double y, int mouseButton) {
        int slotX = (int) Math.floor((x - this.x) / slotSize);
        int slotY = (int) Math.floor((y - this.y) / slotSize);
        int slotId = (columns * slotY) + slotX;

        if (    (slotX < 0 || slotY < 0)
                || slotX >= columns || slotY >= rows
                || slotId >= slots.size()
        ) return;


//        Item_interactions_mod.infoMessage("slotX: " + slotX + ", slotY: " + slotY + ", id: " + slotId);

        mouseClicked(slotId, mouseButton);
//        Item_interactions_mod.infoMessage(");
//        int id =
    }
    public void mouseClicked(int slotId, int mouseButton) {
        if (slotId < 0 || slotId >= slots.size()) return;

        Slot targetSlot = slots.get(slotId);
        switch (mouseButton) {
            case 0:
//                noMouse yesSlot
//                take
                if (mouseItem.isEmpty() && targetSlot.hasItem()) {
//                    Item_interactions_mod.infoMessage("taking!");
                    mouseItem = targetSlot.getItem();
                    targetSlot.set(ItemStack.EMPTY);
                    break;
                }


                if (!mouseItem.isEmpty() && targetSlot.hasItem()){
                        ItemStack oldMouse = mouseItem.copy();
                        mouseItem = targetSlot.getItem();
                        targetSlot.set(oldMouse);

                }
//                yesMouse
//
//                Item_interactions_mod.infoMessage("Safe inserting!");
                mouseItem = targetSlot.safeInsert(mouseItem);




//                yesMouse yesSlot
//                swap or insert
//                if (!mouseItem.isEmpty() && targetSlot.hasItem()){
//                }



                break;
            case 1:

                if (mouseItem.isEmpty() && targetSlot.hasItem()) {
                    int takeCount = targetSlot.getItem().getCount();
                    mouseItem = targetSlot.getItem().split(takeCount/2);
                    break;
                }

                if (mouseItem.isEmpty()) break;

                mouseItem = targetSlot.safeInsert(mouseItem, 1);
        }

        GlobalDirt.carriedItem = mouseItem;
    }

}
