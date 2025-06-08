package dev.mineland.item_interactions_mod;

import dev.mineland.item_interactions_mod.CustomGuiComponents.ConfigInventoryPreview;
import dev.mineland.item_interactions_mod.CustomGuiComponents.GraphOverTimeWidget;
import dev.mineland.item_interactions_mod.CustomGuiComponents.SteppedSliderButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ItemInteractionsSettingsScreen extends Screen {


    private final Screen parent;


    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 33, 36);

    public Button button1;
    public Button button2;

    public Button doneButton;

    private Button debugButton;
    private CycleButton<String> animationCycleButton;
    private SteppedSliderButton scaleSpeed;
    private SteppedSliderButton scaleAmount;
    private SteppedSliderButton mouseSpeedMult;
    private SteppedSliderButton mouseDeceleration;
    private Button isRope;
    private Button guiParticlesButton;


    private Button resetButton;


    LinearLayout linearLayout = this.layout.addToContents(LinearLayout.vertical().spacing(8));
    LinearLayout bodyLayout = linearLayout.addChild(LinearLayout.horizontal(), LayoutSettings::alignHorizontallyCenter).spacing(8);
    LinearLayout leftColumnLayout = bodyLayout.addChild(LinearLayout.vertical()).spacing(8);


    LinearLayout speedAnimLayout = LinearLayout.vertical().spacing(4);
    LinearLayout scaleAnimLayout = LinearLayout.vertical().spacing(4);
    LinearLayout physAnimLayout = LinearLayout.vertical().spacing(4);
    LinearLayout rightColumnLayout = bodyLayout.addChild(LinearLayout.vertical()).spacing(8);

    GraphOverTimeWidget speedXGraph = new GraphOverTimeWidget(GlobalDirt.shakeThreshold + 20, 60, true);

    static HashMap<String, Object> previousSettingsMap = new HashMap<>(ItemInteractionsConfig.settingsMap);


//    String oldAnimationConfig   = ItemInteractionsConfig.getAnimationSetting().id;
//    double oldScaleSpeed        = (double) ItemInteractionsConfig.getSetting("scale_speed");
//    double oldScaleAmount       = (double) ItemInteractionsConfig.getSetting("scale_amount");
    double oldMouseDeceleration = (double) ItemInteractionsConfig.getSetting("mouse_deceleration");
    double oldMouseSpeedMult    = (double) ItemInteractionsConfig.getSetting("mouse_speed_multiplier");
    boolean oldParticleEnabled  = (boolean) ItemInteractionsConfig.getSetting("gui_particles");
//    public static double scaleSpeed;
//    public static float scaleAmount;
//    public static double mouseSpeedMult = 1;



    public ConfigInventoryPreview inventoryPreview = new ConfigInventoryPreview(
            width - 100 - Button.DEFAULT_SPACING, height/2 - 50, Button.DEFAULT_WIDTH, 100, Component.literal("Inventory preview"));




    private final static String animTooltipString = """
                Type of animation that will play when carrying an item
                Speed: tilts based off the mouse speed
                Scale: scales the item up on cycles
                Physics: speen
                None: no animation""";

    public ItemInteractionsSettingsScreen(Screen parent){
        super(Component.literal("Item interactions mod settings"));
        this.parent = parent;
//        Item_interactions_mod.infoMessage("Created screen");
        ItemInteractionsConfig.refreshConfig();

        GlobalDirt.restore();

        createLayout();

        animationCycleButton.setTooltip(Tooltip.create(Component.literal(animTooltipString)));

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
        speedXGraph.putMarker(GlobalDirt.shakeThreshold, 0xFFFF0000);


    }




    void updateVisible() {
        scaleAnimLayout.visitWidgets(widget -> widget.visible = false);
        speedAnimLayout.visitWidgets(widget -> widget.visible = false);
        physAnimLayout.visitWidgets(widget -> widget.visible = false);

        animationCycleButton.setTooltip(Tooltip.create(Component.literal(animTooltipString)));


        switch (animationCycleButton.getValue()) {
            case "scale" -> {
                scaleAnimLayout.visitWidgets(widget -> widget.visible = true);
            }

            case "speed" -> {
                speedAnimLayout.visitWidgets(widget -> widget.visible = true);
            }

            case "physics" -> {
                physAnimLayout.visitWidgets(widget -> widget.visible = true);
            }

            default -> {
                scaleAnimLayout.visitWidgets(widget -> widget.visible = false);
                speedAnimLayout.visitWidgets(widget -> widget.visible = false);
            }
        }
    }

    void createLayout() {

        this.layout.addTitleHeader(this.title, Minecraft.getInstance().font);


        List<String> anims = new ArrayList<>(ItemInteractionsConfig.animations.keySet());
        animationCycleButton = leftColumnLayout.addChild(
                CycleButton.<String>builder(animationSetting ->
                                Component.literal(animationSetting).withStyle(
                                        animationSetting.equals("none") ?
                                                ChatFormatting.RED : ChatFormatting.YELLOW)
                        )
                        .withValues(anims)
                        .withInitialValue((String) ItemInteractionsConfig.getSetting("animation"))
                        .create(0,0,Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.literal("Animation"),
                                (button, string) -> {
                                    ItemInteractionsConfig.setSetting("animation",  string);
                                    updateVisible();
                                }
                        )

        );



        scaleSpeed = scaleAnimLayout.addChild(new SteppedSliderButton(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, CommonComponents.EMPTY, (double) ItemInteractionsConfig.getSetting("scale_speed"), 0, 4, 40, false) {
            {
                this.value = (double) ItemInteractionsConfig.getSetting("scale_speed");
                this.applyValue();
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(Component.literal("Scale speed: " + ItemInteractionsConfig.getSetting("scale_speed")));
            }

            @Override
            protected void applyValue() {
                ItemInteractionsConfig.setSetting("scale_speed", value);

            }

        });

        scaleAmount = scaleAnimLayout.addChild(new SteppedSliderButton(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, CommonComponents.EMPTY, (double) ItemInteractionsConfig.getSetting("scale_amount"), 0, 2, 20, false) {
            {
                this.value = (double) ItemInteractionsConfig.getSetting("scale_amount");
                this.applyValue();
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(Component.literal("Scale amount: " + ItemInteractionsConfig.getSetting("scale_amount")));
            }

            @Override
            protected void applyValue() {
                ItemInteractionsConfig.setSetting("scale_amount", Math.clamp(this.value, this.minValue, this.maxValue));

            }

        });

        mouseSpeedMult = speedAnimLayout.addChild(new SteppedSliderButton(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, CommonComponents.EMPTY, (double) ItemInteractionsConfig.getSetting("mouse_speed_multiplier"), -2, 2, 40) {
            {
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {


                Component message = Component.literal("Mouse speed: " + ItemInteractionsConfig.getSetting("mouse_speed_multiplier") + "x");


                this.setMessage(message);

            }

            @Override
            protected void applyValue() {
                ItemInteractionsConfig.setSetting("mouse_speed_multiplier", value);
                ItemInteractionsConfig.mouseSpeedMult = value;

            }

        });

        mouseDeceleration = speedAnimLayout.addChild(new SteppedSliderButton(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, CommonComponents.EMPTY, (double) ItemInteractionsConfig.getSetting("mouse_deceleration"), 0, 1, 10) {
            {
                this.value = (double) ItemInteractionsConfig.getSetting("mouse_deceleration");
                this.applyValue();
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(Component.literal("Mouse deceleration: " + ItemInteractionsConfig.getSetting("mouse_deceleration")));
            }

            @Override
            protected void applyValue() {
                ItemInteractionsConfig.setSetting("mouse_deceleration", value);
                ItemInteractionsConfig.mouseDeceleration = value;
            }
        });

        isRope = physAnimLayout.addChild(Button.builder(Component.literal("is rope"), (self) -> {
                boolean value = (boolean) ItemInteractionsConfig.getSetting("rope_is_rope");
                ItemInteractionsConfig.setSetting("rope_is_rope", !value);
                self.setMessage(Component.literal ("is rope: ").append(Component.literal(""+ value).withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED)) );
            }).build()
        );

        leftColumnLayout.addChild(speedAnimLayout);
        leftColumnLayout.addChild(scaleAnimLayout);
        leftColumnLayout.addChild(physAnimLayout);


        if (ItemInteractionsConfig.debugDraws || GlobalDirt.devenv) {
            Component debugButtonInitialText = Component.literal("debug: ")
                    .append(Component.literal(""+ItemInteractionsConfig.debugDraws)
                            .withStyle(ItemInteractionsConfig.debugDraws ? ChatFormatting.GREEN : ChatFormatting.RED)
                    );

            debugButton = leftColumnLayout.addChild(
                    Button.builder(debugButtonInitialText, (self) -> {
                        ItemInteractionsConfig.setSetting("debug", ! (boolean) ItemInteractionsConfig.getSetting("debug"));

                        ChatFormatting color = (boolean) ItemInteractionsConfig.getSetting("debug") ?
                                ChatFormatting.GREEN : ChatFormatting.RED;


                        self.setMessage(
                                Component.literal("debug: ")
                                        .append(Component.literal(""+ItemInteractionsConfig.getSetting("debug"))
                                                .withStyle(color)
                                        )
                        );
                    }).build()
            );
        }




        inventoryPreview = rightColumnLayout.addChild(inventoryPreview, LayoutSettings::alignVerticallyMiddle);
        rightColumnLayout.addChild(Button.builder(Component.literal("Restore defaults"), (self) -> resetToDefaults()).width(100).build(), LayoutSettings::alignHorizontallyCenter);


        Component debugButtonInitialText = Component.literal("Inventory particles: ")
                .append(Component.literal(""+ItemInteractionsConfig.getSetting("gui_particles"))
                        .withStyle((boolean) ItemInteractionsConfig.getSetting("gui_particles") ? ChatFormatting.GREEN : ChatFormatting.RED)
                );

        guiParticlesButton = rightColumnLayout.addChild(
                Button.builder(debugButtonInitialText, (self) -> {
                    ItemInteractionsConfig.enableGuiParticles = !ItemInteractionsConfig.enableGuiParticles;
                    ItemInteractionsConfig.setSetting("gui_particles", ItemInteractionsConfig.enableGuiParticles);

                    ChatFormatting color = (boolean) ItemInteractionsConfig.getSetting("gui_particles") ?
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
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        if (speedXGraph.visible) {
            speedXGraph.plotPoint(GlobalDirt.shakeSpeed);
        }
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

        this.addRenderableWidget(speedXGraph);
        this.speedXGraph.setPosition(8, 8+speedXGraph.getHeight());

    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
//        Item_interactions_mod.infoMessage("Exiting item screen");
        ItemInteractionsConfig.createConfig();
    }


    public void onCancel() {
//        ItemInteractionsConfig.animationConfig = oldAnimationConfig;
//        ItemInteractionsConfig.scaleSpeed = oldScaleSpeed;
//        ItemInteractionsConfig.scaleAmount = oldScaleAmount;
        ItemInteractionsConfig.mouseDeceleration = oldMouseDeceleration;
        ItemInteractionsConfig.mouseSpeedMult = oldMouseSpeedMult;
        ItemInteractionsConfig.enableGuiParticles = oldParticleEnabled;

        ItemInteractionsConfig.settingsMap = previousSettingsMap;
        this.minecraft.setScreen(parent);
    }

    public void resetToDefaults() {
        animationCycleButton.setValue((String) ItemInteractionsConfig.getDefaultSetting("animation"));
        scaleSpeed.setValue((double) ItemInteractionsConfig.getDefaultSetting("scale_speed"));
        scaleAmount.setValue((double) ItemInteractionsConfig.getDefaultSetting("scale_amount"));
        mouseSpeedMult.setValue((double) ItemInteractionsConfig.getDefaultSetting("mouse_speed_multiplier"));
        mouseDeceleration.setValue((double) ItemInteractionsConfig.getDefaultSetting("mouse_deceleration"));

        guiParticlesButton.setMessage(
                Component.literal("Inventory particles: ").append(
                Component.literal("" + ItemInteractionsConfig.getDefaultSetting("gui_particles")).withStyle(ChatFormatting.GREEN)
        ));

        ItemInteractionsConfig.init();

        this.updateVisible();
//        scaleSpeed.setV

    }
}
