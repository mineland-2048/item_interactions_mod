package dev.mineland.item_interactions_mod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.mineland.item_interactions_mod.CustomGuiComponents.ConfigInventoryPreview;
import dev.mineland.item_interactions_mod.CustomGuiComponents.SteppedSliderButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Environment(EnvType.CLIENT)
public class ItemInteractionsSettingsScreen extends Screen {



    private final Screen parent;


    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 33, 36);

    public Button button1;
    public Button button2;

    public Button doneButton;

    private Button debugButton;
    private CycleButton<ItemInteractionsConfig.animation> animationCycleButton;
    private SteppedSliderButton scaleSpeed;
    private SteppedSliderButton scaleAmount;
    private SteppedSliderButton mouseSpeedMult;
    private SteppedSliderButton mouseDeceleration;
    private Button guiParticlesButton;


    private Button resetButton;


    LinearLayout linearLayout = this.layout.addToContents(LinearLayout.vertical().spacing(8));
    LinearLayout bodyLayout = linearLayout.addChild(LinearLayout.horizontal(), LayoutSettings::alignHorizontallyCenter).spacing(8);
    LinearLayout leftColumnLayout = bodyLayout.addChild(LinearLayout.vertical()).spacing(8);


    LinearLayout speedAnimLayout = LinearLayout.vertical().spacing(4);
    LinearLayout scaleAnimLayout = LinearLayout.vertical().spacing(4);
    LinearLayout rightColumnLayout = bodyLayout.addChild(LinearLayout.vertical()).spacing(8);



    ItemInteractionsConfig.animation oldAnimationConfig = ItemInteractionsConfig.animationConfig;
    double oldScaleSpeed        = ItemInteractionsConfig.scaleSpeed        ;
    double oldScaleAmount       = ItemInteractionsConfig.scaleAmount       ;
    double oldMouseDeceleration = ItemInteractionsConfig.mouseDeceleration ;
    double oldMouseSpeedMult    = ItemInteractionsConfig.mouseSpeedMult    ;
    boolean oldParticleEnabled  = ItemInteractionsConfig.enableGuiParticles;
//    public static double scaleSpeed;
//    public static float scaleAmount;
//    public static double mouseSpeedMult = 1;


    private static final int DEFAULT_SPACING = 8;

    public ConfigInventoryPreview inventoryPreview = new ConfigInventoryPreview(
            width - 100 - DEFAULT_SPACING, height/2 - 50, Button.DEFAULT_WIDTH, 100, Component.literal("Inventory preview"));





    public ItemInteractionsSettingsScreen(Screen parent){
        super(Component.literal("Item interactions mod settings"));
        this.parent = parent;
//        Item_interactions_mod.infoMessage("Created screen");
        ItemInteractionsConfig.refreshConfig();

        GlobalDirt.restore();

        createLayout();

        animationCycleButton.setTooltip(Tooltip.create(Component.literal("""
                Type of animation that will play when carrying an item
                Speed: tilts based off the mouse speed
                Scale: scales the item up on cycles
                None: no animation""")));

        scaleSpeed.setTooltip(Tooltip.create(Component.literal("Speed of the scaling animation measured in seconds/cycle.")));
        scaleAmount.setTooltip(Tooltip.create(Component.literal("How much the item will scale up. \n0.1 = +1/10 \n1 = +1 (Double the item size)")));

        mouseSpeedMult.setTooltip(Tooltip.create(Component.literal("Multiplier for the speed gained while moving the mouse")));
        mouseDeceleration.setTooltip(Tooltip.create(Component.literal("The deceleration factor for the items. \n1 = normal deceleration\n0 = no deceleration")));

        guiParticlesButton.setTooltip(Tooltip.create(Component.literal("Enable or disable particles in the inventory from resource packs.")));


        boolean hadItems = false;
        if (Minecraft.getInstance().level != null  && Minecraft.getInstance().player != null) {
                for (int hotbar = 0; hotbar < 9; hotbar++) {
                    ItemStack item = Minecraft.getInstance().player.getInventory().getItem(hotbar).copy();
                    inventoryPreview.setItem(hotbar, item);

                    if (!item.isEmpty()) hadItems = true;


            }
        }

        if (!hadItems) {
            inventoryPreview.setItem(0,  new ItemStack(Items.CRAFTING_TABLE));
            inventoryPreview.setItem(1,  new ItemStack(Items.OAK_LEAVES));
            inventoryPreview.setItem(2,  new ItemStack(Items.DIAMOND_PICKAXE));
            inventoryPreview.setItem(4,  new ItemStack(Items.REDSTONE_LAMP));
            inventoryPreview.setItem(6,  new ItemStack(Items.FLINT_AND_STEEL));
            inventoryPreview.setItem(7,  new ItemStack(Items.ZOMBIE_HEAD));
            inventoryPreview.setItem(8,  new ItemStack(Items.EGG));


        }


        updateVisible();


    }




    void updateVisible() {
        scaleAnimLayout.visitWidgets(widget -> widget.visible = false);
        speedAnimLayout.visitWidgets(widget -> widget.visible = false);

        animationCycleButton.setTooltip(Tooltip.create(Component.literal("""
                Type of animation that will play when carrying an item
                -speed: tilts based off the mouse speed
                -scale: scales the item up on cycles
                -none: no animation""")));


        switch (animationCycleButton.getValue()) {
            case ANIM_SCALE -> {
                scaleAnimLayout.visitWidgets(widget -> widget.visible = true);

            }

            case ANIM_SPEED -> {
                speedAnimLayout.visitWidgets(widget -> widget.visible = true);
            }

            case NONE -> {
                scaleAnimLayout.visitWidgets(widget -> widget.visible = false);
                speedAnimLayout.visitWidgets(widget -> widget.visible = false);
            }
        }
    }

    void createLayout() {


        this.layout.addToHeader(new StringWidget(this.title, Minecraft.getInstance().font));


        animationCycleButton = leftColumnLayout.addChild(
                CycleButton.<ItemInteractionsConfig.animation>builder(animationSetting ->
                                animationSetting.component.copy().withStyle(
                                        animationSetting == ItemInteractionsConfig.animation.NONE ?
                                                ChatFormatting.RED : ChatFormatting.YELLOW)
                        )
                        .withValues(
                                ItemInteractionsConfig.animation.ANIM_SPEED,
                                ItemInteractionsConfig.animation.ANIM_SCALE,
                                ItemInteractionsConfig.animation.NONE)
                        .withInitialValue(ItemInteractionsConfig.animationConfig)
                        .create(0,0,Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.literal("Animation"),
                                (arg, arg2) -> {
                                    ItemInteractionsConfig.animationConfig = arg2;
                                    updateVisible();
                                }
                        )
        );



        scaleSpeed = scaleAnimLayout.addChild(new SteppedSliderButton(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, CommonComponents.EMPTY, ItemInteractionsConfig.scaleSpeed, 0, 4, 40, false) {
            {
                this.value = ItemInteractionsConfig.scaleSpeed;
                this.applyValue();
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(Component.literal("Scale speed: " + ItemInteractionsConfig.scaleSpeed));
            }

            @Override
            protected void applyValue() {
                ItemInteractionsConfig.scaleSpeed = value;

            }

        });

        scaleAmount = scaleAnimLayout.addChild(new SteppedSliderButton(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, CommonComponents.EMPTY, ItemInteractionsConfig.scaleAmount, 0, 2, 20, false) {
            {
                this.value = ItemInteractionsConfig.scaleAmount;
                this.applyValue();
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(Component.literal("Scale amount: " + ItemInteractionsConfig.scaleAmount));
            }

            @Override
            protected void applyValue() {
                ItemInteractionsConfig.scaleAmount = MiscUtils.clamp((double) this.value, this.minValue, this.maxValue);

            }

        });

        mouseSpeedMult = speedAnimLayout.addChild(new SteppedSliderButton(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, CommonComponents.EMPTY, ItemInteractionsConfig.mouseSpeedMult, -2, 2, 40) {
            {
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {


                Component message = Component.literal("Mouse speed: " + ItemInteractionsConfig.mouseSpeedMult + "x");


                this.setMessage(message);

            }

            @Override
            protected void applyValue() {
                ItemInteractionsConfig.mouseSpeedMult = value;

            }

        });

        mouseDeceleration = speedAnimLayout.addChild(new SteppedSliderButton(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, CommonComponents.EMPTY, ItemInteractionsConfig.mouseDeceleration, 0, 1, 10) {
            {
                this.value = ItemInteractionsConfig.mouseDeceleration;
                this.applyValue();
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(Component.literal("Mouse deceleration: " + ItemInteractionsConfig.mouseDeceleration));
            }

            @Override
            protected void applyValue() {
                ItemInteractionsConfig.mouseDeceleration = value;

            }
        });


        leftColumnLayout.addChild(speedAnimLayout);
        leftColumnLayout.addChild(scaleAnimLayout);


        if (ItemInteractionsConfig.debugDraws || GlobalDirt.devenv) {
            Component debugButtonInitialText = Component.literal("debug: ")
                    .append(Component.literal(""+ItemInteractionsConfig.debugDraws)
                            .withStyle(ItemInteractionsConfig.debugDraws ? ChatFormatting.GREEN : ChatFormatting.RED)
                    );

            debugButton = leftColumnLayout.addChild(
                    Button.builder(debugButtonInitialText, (self) -> {
                        ItemInteractionsConfig.debugDraws = !ItemInteractionsConfig.debugDraws;

                        ChatFormatting color = ItemInteractionsConfig.debugDraws ?
                                ChatFormatting.GREEN : ChatFormatting.RED;


                        self.setMessage(
                                Component.literal("debug: ")
                                        .append(Component.literal(""+ItemInteractionsConfig.debugDraws)
                                                .withStyle(color)
                                        )
                        );
                    }).build()
            );
        }




        inventoryPreview = rightColumnLayout.addChild(inventoryPreview, LayoutSettings::alignVerticallyMiddle);
        rightColumnLayout.addChild(Button.builder(Component.literal("Restore defaults"), (self) -> resetToDefaults()).width(100).build(), LayoutSettings::alignHorizontallyCenter);


        Component debugButtonInitialText = Component.literal("Inventory particles: ")
                .append(Component.literal(""+ItemInteractionsConfig.enableGuiParticles)
                        .withStyle(ItemInteractionsConfig.enableGuiParticles ? ChatFormatting.GREEN : ChatFormatting.RED)
                );

        guiParticlesButton = rightColumnLayout.addChild(
                Button.builder(debugButtonInitialText, (self) -> {
                    ItemInteractionsConfig.enableGuiParticles = !ItemInteractionsConfig.enableGuiParticles;

                    ChatFormatting color = ItemInteractionsConfig.enableGuiParticles ?
                            ChatFormatting.GREEN : ChatFormatting.RED;


                    self.setMessage(
                            Component.literal("Inventory particles: ")
                                    .append(Component.literal(""+ItemInteractionsConfig.enableGuiParticles)
                                            .withStyle(color)
                                    )
                    );
                }).build(), LayoutSettings::alignHorizontallyCenter
        );


        LinearLayout footerLayout = LinearLayout.horizontal().spacing(8);
        footerLayout.addChild(Button.builder(CommonComponents.GUI_CANCEL, arg -> this.onCancel()).width(Button.DEFAULT_WIDTH).build());
        footerLayout.addChild(Button.builder(CommonComponents.GUI_DONE, arg -> this.onClose()).width(Button.DEFAULT_WIDTH).build());

        this.layout.addToFooter(footerLayout);

        updateVisible();


    }


    @Override
    public void mouseMoved(double d, double e) {
        inventoryPreview.mouseMoved(d, e);
        super.mouseMoved(d, e);

    }

    @Override
    protected void init() {

        this.layout.visitWidgets(arg2 -> {
            AbstractWidget uhh = this.addRenderableWidget(arg2);
        });

        this.layout.arrangeElements();

        int firstY = speedAnimLayout.getY();
        scaleAnimLayout.setY(firstY);


    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
//        Item_interactions_mod.infoMessage("Exiting item screen");
        ItemInteractionsConfig.createConfig();
    }

    public void onCancel() {
        ItemInteractionsConfig.animationConfig = oldAnimationConfig;
        ItemInteractionsConfig.scaleSpeed = oldScaleSpeed;
        ItemInteractionsConfig.scaleAmount = oldScaleAmount;
        ItemInteractionsConfig.mouseDeceleration = oldMouseDeceleration;
        ItemInteractionsConfig.mouseSpeedMult = oldMouseSpeedMult;
        ItemInteractionsConfig.enableGuiParticles = oldParticleEnabled;
        this.minecraft.setScreen(parent);
    }

    public void resetToDefaults() {
        animationCycleButton.setValue(ItemInteractionsConfig.DefaultValues.animationConfig);
        scaleSpeed.setValue(ItemInteractionsConfig.DefaultValues.scaleSpeed);
        scaleAmount.setValue(ItemInteractionsConfig.DefaultValues.scaleAmount);
        mouseSpeedMult.setValue(ItemInteractionsConfig.DefaultValues.mouseSpeedMult);
        mouseDeceleration.setValue(ItemInteractionsConfig.DefaultValues.mouseDeceleration);
        guiParticlesButton.setMessage(
                Component.literal("Inventory particles: ").append(
                Component.literal("" + ItemInteractionsConfig.DefaultValues.enableGuiParticles).withStyle(ChatFormatting.GREEN)
        ));

        ItemInteractionsConfig.init();

        this.updateVisible();
//        scaleSpeed.setV

    }
}
