package dev.mineland.item_interactions_mod;

import dev.mineland.item_interactions_mod.CustomGuiComponents.ConfigInventoryPreview;
import dev.mineland.item_interactions_mod.CustomGuiComponents.GraphOverTimeWidget;
import dev.mineland.item_interactions_mod.CustomGuiComponents.SteppedSliderButton;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimTemplate;
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
import org.joml.Vector3f;

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
    private Button ropePixelated;
    private SteppedSliderButton ropeElasticity;
    private SteppedSliderButton ropeLength;
    private SteppedSliderButton ropeGravity;
    private SteppedSliderButton ropeInertia;
    private Button guiParticlesButton;
    private Button smoothParticlesButton;

    private Button resetButton;

    LinearLayout linearLayout = this.layout.addToContents(LinearLayout.vertical().spacing(8));
    LinearLayout bodyLayout = linearLayout.addChild(LinearLayout.horizontal(), LayoutSettings::alignHorizontallyCenter).spacing(8);
    LinearLayout leftColumnLayout = bodyLayout.addChild(LinearLayout.vertical(), LayoutSettings::alignVerticallyTop ).spacing(8);


    LinearLayout speedAnimLayout = LinearLayout.vertical().spacing(4);
    LinearLayout scaleAnimLayout = LinearLayout.vertical().spacing(4);
    LinearLayout ropeAnimLayout = LinearLayout.vertical().spacing(4);
    LinearLayout rightColumnLayout = bodyLayout.addChild(LinearLayout.vertical()).spacing(2);

    GraphOverTimeWidget mouseXPosGraph = GraphOverTimeWidget.builder(
            "TickProgress",
            (graph) -> GlobalDirt.tickProgress,
            true
    )
            .showTitle()
            .showYAxis()
            .setZLayer(1000)
            .showCurrentValue()
            .size_fromInnerGraph(50, 0, 1, 50).pos(0, 28).pixelatedGraph().graphDivisions(5)
//            .size(300, 100)
//            .addMarker("shake threshold", ItemInteractionsConfig.getAnimationSetting().itemSpeed.y())
            .allowOverdraw()
            .decimalPrecision(2)
            .build();

    GraphOverTimeWidget mouseYPosGraph = GraphOverTimeWidget.builder(
            "speed y",
            (graph) -> ItemInteractionsConfig.getAnimationSetting().itemSpeed.y(),
            true
    )
            .showTitle()
            .showYAxis()
            .showCurrentValue()
            .size_fromInnerGraph(50, -200, 200, 50f/400).pos(100, 8).pixelatedGraph().graphDivisions(5)
//            .addMarker("shake threshold", ItemInteractionsConfig.getAnimationSetting().itemSpeed.y())
            .allowOverdraw()
            .decimalPrecision(5)
            .build();



//    GraphOverTimeWidget rotationRawGraph = new GraphOverTimeWidget(Math.PI*2, 60, 3);

    static HashMap<String, Object> previousSettingsMap = new HashMap<>(ItemInteractionsConfig.settingsMap);


//    String oldAnimationConfig   = ItemInteractionsConfig.getAnimationSetting().id;
//    double oldScaleSpeed        = (double) ItemInteractionsConfig.getSetting("scale_speed");
//    double oldScaleAmount       = (double) ItemInteractionsConfig.getSetting("scale_amount");
//    double oldMouseDeceleration = (double) ItemInteractionsConfig.getSetting("mouse_deceleration");
//    double oldMouseSpeedMult    = (double) ItemInteractionsConfig.getSetting("mouse_speed_multiplier");
    boolean oldParticleEnabled  = (boolean) ItemInteractionsConfig.getSetting("gui_particles");
    boolean oldSmoothParticles = (boolean) ItemInteractionsConfig.getSetting("gui_smooth_particles");
//    public static double scaleSpeed;
//    public static float scaleAmount;
//    public static double mouseSpeedMult = 1;



    public ConfigInventoryPreview inventoryPreview = new ConfigInventoryPreview(
            width - 100 - Button.DEFAULT_SPACING, height/2 - 50, Button.DEFAULT_WIDTH, 100, Component.literal("Inventory preview"));




    private final static String animTooltipString = """
                Type of animation that will play when carrying an item
                Speed: tilts based off the mouse speed
                Scale: scales the item up on cycles
                Rope: ties the item in a rope
                Spin: spins the item around
                None: no animation""";


    public ItemInteractionsSettingsScreen(Screen parent){
        super(Component.literal("Item interactions mod settings"));
        this.parent = parent;
//        ItemInteractionsMod.infoMessage("Created screen");
        ItemInteractionsConfig.refreshConfig();

        GlobalDirt.restore();

        createLayout();

        animationCycleButton.setTooltip(Tooltip.create(Component.literal(animTooltipString)));

        scaleSpeed.setTooltip(Tooltip.create(Component.literal("Speed of the scaling animation measured in seconds/cycle.")));
        scaleAmount.setTooltip(Tooltip.create(Component.literal("How much the item will scale up. \n0.1 = +1/10 \n1 = +1 (Double the item size)")));

        mouseSpeedMult.setTooltip(Tooltip.create(Component.literal("Multiplier for the speed gained while moving the mouse")));
        mouseDeceleration.setTooltip(Tooltip.create(Component.literal("The deceleration factor for the items. \n1 = normal deceleration\n0 = no deceleration")));

        guiParticlesButton.setTooltip(Tooltip.create(Component.literal("Enable or disable particles in the inventory from resource packs.")));
        smoothParticlesButton.setTooltip(Tooltip.create(Component.literal("Toggles weather the particles are locked to the texture grid or not.")));


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
//        rotationAngleGraph.putMarker(GlobalDirt.shakeThreshold, 0xFFFF0000);
//        mouseXPosGraph.setColor(0xFFFFFFFF, 0xFFFF0000);
//        rotationRawGraph.setColor(0x80FF0000);
//        rotationRawGraph.setBackgroundColor(0);


    }




    void updateVisible() {
        scaleAnimLayout.visitWidgets(widget -> widget.visible = false);
        speedAnimLayout.visitWidgets(widget -> widget.visible = false);
        ropeAnimLayout.visitWidgets(widget -> widget.visible = false);

        animationCycleButton.setTooltip(Tooltip.create(Component.literal(animTooltipString)));


        switch (animationCycleButton.getValue()) {
            case "scale" -> {
                scaleAnimLayout.visitWidgets(widget -> widget.visible = true);
            }

            case "speed" -> {
                speedAnimLayout.visitWidgets(widget -> widget.visible = true);
            }

            case "rope" -> {
                ropeAnimLayout.visitWidgets(widget -> widget.visible = true);
            }

            default -> {
                scaleAnimLayout.visitWidgets(widget -> widget.visible = false);
                speedAnimLayout.visitWidgets(widget -> widget.visible = false);
            }
        }
    }

    void createLayout() {

        this.layout.addTitleHeader(this.title, Minecraft.getInstance().font);


        List<String> anims = new ArrayList<>();
        for (AnimTemplate a : ItemInteractionsConfig.animationList) {
            anims.add(a.getId());
        }

        anims.add("none");

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
                                    ItemInteractionsConfig.setAnimationSetting(string);
                                    ItemInteractionsConfig.getAnimationSetting().reset((int) GlobalDirt.lastMouseX, (int) GlobalDirt.lastMouseY, 0);
                                    updateVisible();
                                }
                        )

        );


        addScaleAnimSettings();

        addSpeedAnimSettings();

        addPhysAnimSettings();


//        leftColumnLayout.addChild(speedAnimLayout);
//        leftColumnLayout.addChild(scaleAnimLayout);
//        leftColumnLayout.addChild(physAnimLayout);

        this.layout.addToContents(speedAnimLayout);
        this.layout.addToContents(scaleAnimLayout);
        this.layout.addToContents(ropeAnimLayout);

        inventoryPreview = rightColumnLayout.addChild(inventoryPreview, LayoutSettings::alignVerticallyMiddle);
        rightColumnLayout.addChild(Button.builder(Component.literal("Restore defaults"), (self) -> resetToDefaults()).width(100).build(), LayoutSettings::alignHorizontallyCenter);


        Component guiButtonInitialText
                = Component.literal("Inventory particles: ")
                .append(Component.literal(""+ItemInteractionsConfig.getSetting("gui_particles"))
                        .withStyle((boolean) ItemInteractionsConfig.getSetting("gui_particles") ? ChatFormatting.GREEN : ChatFormatting.RED)
                );

        Component guiSmoothInitialText
                = Component.literal("Smooth particles: ")
                .append(Component.literal(""+ItemInteractionsConfig.getSetting("gui_smooth_particles"))
                        .withStyle((boolean) ItemInteractionsConfig.getSetting("gui_smooth_particles") ? ChatFormatting.GREEN : ChatFormatting.RED)
                );

        guiParticlesButton = rightColumnLayout.addChild(
                Button.builder(guiButtonInitialText, (self) -> {
                    ItemInteractionsConfig.enableGuiParticles = !ItemInteractionsConfig.enableGuiParticles;
                    ItemInteractionsConfig.setSetting("gui_particles", ItemInteractionsConfig.enableGuiParticles);

                    ChatFormatting color = (boolean) ItemInteractionsConfig.enableGuiParticles ?
                            ChatFormatting.GREEN : ChatFormatting.RED;


                    self.setMessage(
                            Component.literal("Inventory particles: ")
                                    .append(Component.literal(""+ItemInteractionsConfig.enableGuiParticles)
                                            .withStyle(color)
                                    )
                    );

                    smoothParticlesButton.active = ItemInteractionsConfig.enableGuiParticles;
                }).build(), LayoutSettings::alignHorizontallyCenter
        );

        smoothParticlesButton = rightColumnLayout.addChild(
                Button.builder(guiSmoothInitialText, (self) -> {
                    ItemInteractionsConfig.smoothGuiParticles = !ItemInteractionsConfig.smoothGuiParticles;
                    ItemInteractionsConfig.setSetting("gui_smooth_particles", ItemInteractionsConfig.smoothGuiParticles);

                    ChatFormatting color = (boolean) ItemInteractionsConfig.getSetting("gui_smooth_particles") ?
                            ChatFormatting.GREEN : ChatFormatting.RED;


                    self.setMessage(
                            Component.literal("Smooth particles: ")
                                    .append(Component.literal(""+ItemInteractionsConfig.smoothGuiParticles)
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

        Component debugButtonInitialText = Component.literal("debug: ")
                .append(Component.literal(""+ItemInteractionsConfig.debugDraws)
                        .withStyle(ItemInteractionsConfig.debugDraws ? ChatFormatting.GREEN : ChatFormatting.RED)
                );


        if (ItemInteractionsConfig.debugDraws || GlobalDirt.devenv) {
            debugButton = Button.builder(debugButtonInitialText, (self) -> {
                boolean d = ItemInteractionsConfig.debugDraws;
                ItemInteractionsConfig.setSetting("debug", ! d);
                ItemInteractionsConfig.debugDraws = !d;

                ChatFormatting color = (boolean) ItemInteractionsConfig.getSetting("debug") ?
                        ChatFormatting.GREEN : ChatFormatting.RED;


                self.setMessage(
                        Component.literal("debug: ")
                                .append(Component.literal(""+ItemInteractionsConfig.getSetting("debug"))
                                        .withStyle(color)
                                )
                );
            }).build();
            debugButton.setPosition(8, 8);
            debugButton.setSize(Button.SMALL_WIDTH, Button.DEFAULT_HEIGHT);
            this.addRenderableWidget(debugButton);
        }

    }

    private void addPhysAnimSettings() {

        double elasticity;
        double length;
        Vector3f gravity;
        double inertia;
        boolean pixelated;

        try {
            elasticity = (double) ItemInteractionsConfig.getSetting("rope_elasticity");
            length = (double) ItemInteractionsConfig.getSetting("rope_length");
            gravity = (Vector3f) ItemInteractionsConfig.getSetting("rope_gravity");
            inertia = (double) ItemInteractionsConfig.getSetting("rope_inertia");
            pixelated = (boolean) ItemInteractionsConfig.getSetting("rope_pixelated");
        } catch (Exception e) {
            MiscUtils.displayErrorInUi("Phys error: \n" + e);
            elasticity = (double) ItemInteractionsConfig.getDefaultSetting("rope_elasticity");
            length = (double) ItemInteractionsConfig.getDefaultSetting("rope_length");
            gravity = (Vector3f) ItemInteractionsConfig.getDefaultSetting("rope_gravity");
            inertia = (double) ItemInteractionsConfig.getDefaultSetting("rope_inertia");
            pixelated = (boolean) ItemInteractionsConfig.getDefaultSetting("rope_pixelated");

            ItemInteractionsConfig.setSetting("rope_elasticity", ItemInteractionsConfig.getDefaultSetting("rope_elasticity"));
            ItemInteractionsConfig.setSetting("rope_length", ItemInteractionsConfig.getDefaultSetting("rope_length"));
            ItemInteractionsConfig.setSetting("rope_gravity", ItemInteractionsConfig.getDefaultSetting("rope_gravity"));
            ItemInteractionsConfig.setSetting("rope_inertia", ItemInteractionsConfig.getDefaultSetting("rope_inertia"));
            ItemInteractionsConfig.setSetting("rope_pixelated", ItemInteractionsConfig.getDefaultSetting("rope_pixelated"));

        }


        ropeElasticity = ropeAnimLayout.addChild(new SteppedSliderButton(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, CommonComponents.EMPTY, elasticity, 0, 1, 20) {
            {
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                Component message = Component.literal("Elasticity: " + value);
                this.setMessage(message);
            }

            @Override
            protected void applyValue() {
                ItemInteractionsConfig.setSetting("rope_elasticity", value);
            }
        });

        ropeLength = ropeAnimLayout.addChild(new SteppedSliderButton(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, CommonComponents.EMPTY, length, 1, 64, 63, false) {
            {
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                Component message = Component.literal("Length: " + (int) value);
                this.setMessage(message);
            }

            @Override
            protected void applyValue() {
                ItemInteractionsConfig.setSetting("rope_length", value);
            }
        });

        ropeGravity = ropeAnimLayout.addChild(new SteppedSliderButton(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, CommonComponents.EMPTY, gravity.y(), -32, 32, 64*2) {
            {
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                Component message = Component.literal("Gravity: " + value);
                this.setMessage(message);
            }

            @Override
            protected void applyValue() {
                Vector3f gravity = (Vector3f) ItemInteractionsConfig.getSetting("rope_gravity");
                ItemInteractionsConfig.setSetting("rope_gravity", new Vector3f(gravity.x, (float) value, gravity.z));
            }
        });

        ropeInertia = ropeAnimLayout.addChild(new SteppedSliderButton(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, CommonComponents.EMPTY, inertia, 0, 1, 20) {
            {
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                Component message = Component.literal("Inertia: " + value);
                this.setMessage(message);
            }

            @Override
            protected void applyValue() {
                ItemInteractionsConfig.setSetting("rope_inertia", value);
            }
        });

        ropePixelated = ropeAnimLayout.addChild(Button.builder(Component.literal("Pixel consistent: ").append(Component.literal( ""+ pixelated).withStyle(pixelated ? ChatFormatting.GREEN : ChatFormatting.RED)), (self) -> {
                    final boolean rope = !(boolean) ItemInteractionsConfig.getSetting("rope_pixelated");
                    ItemInteractionsConfig.setSetting("rope_pixelated", rope);
                    self.setMessage(Component.literal ("Pixel consistent: ").append(Component.literal(""+ rope).withStyle(rope ? ChatFormatting.GREEN : ChatFormatting.RED)) );
                }).build());


    }
    private void addSpeedAnimSettings() {
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
            }
        });
    }
    private void addScaleAnimSettings() {

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
    }


    int timer = 10;
    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        GlobalDirt.setGlobalGuiGraphics(guiGraphics);
        AnimTemplate currentAnimation = ItemInteractionsConfig.getAnimationSetting();
        if (currentAnimation != null) currentAnimation.refreshSettings();
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

        leftColumnLayout.setY(rightColumnLayout.getY());
        int firstY = leftColumnLayout.getY() + Button.DEFAULT_HEIGHT + Button.DEFAULT_SPACING;

        speedAnimLayout.setPosition(leftColumnLayout.getX(), firstY);
        scaleAnimLayout.setPosition(leftColumnLayout.getX(), firstY);
        ropeAnimLayout.setPosition(leftColumnLayout.getX(), firstY);



        this.addRenderableWidget(mouseXPosGraph);
        this.addRenderableWidget(mouseYPosGraph);
        if (!ItemInteractionsConfig.debugDraws) {
//            rotationRawGraph.visible = false;
            mouseXPosGraph.visible = false;
        }
        mouseYPosGraph.visible = false;


    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
//        ItemInteractionsMod.infoMessage("Exiting item screen");
        ItemInteractionsConfig.createConfig();
    }


    public void onCancel() {
//        ItemInteractionsConfig.animationConfig = oldAnimationConfig;
//        ItemInteractionsConfig.scaleSpeed = oldScaleSpeed;
//        ItemInteractionsConfig.scaleAmount = oldScaleAmount;

        ItemInteractionsConfig.enableGuiParticles = oldParticleEnabled;
        ItemInteractionsConfig.smoothGuiParticles = oldSmoothParticles;

        ItemInteractionsConfig.settingsMap = previousSettingsMap;
        this.minecraft.setScreen(parent);
    }

    public void resetToDefaults() {
        animationCycleButton.setValue((String) ItemInteractionsConfig.getDefaultSetting("animation"));
        scaleSpeed.setValue((double) ItemInteractionsConfig.getDefaultSetting("scale_speed"));
        scaleAmount.setValue((double) ItemInteractionsConfig.getDefaultSetting("scale_amount"));
        mouseSpeedMult.setValue((double) ItemInteractionsConfig.getDefaultSetting("mouse_speed_multiplier"));
        mouseDeceleration.setValue((double) ItemInteractionsConfig.getDefaultSetting("mouse_deceleration"));

        ropePixelated.setMessage(Component.literal("Pixel consistent: ").append(
                Component.literal("" + ItemInteractionsConfig.getDefaultSetting("rope_pixelated")).withStyle(ChatFormatting.GREEN)
        ));

        ropeElasticity.setValue((double) ItemInteractionsConfig.getDefaultSetting("rope_elasticity"));
        ropeLength.setValue((double) ItemInteractionsConfig.getDefaultSetting("rope_length"));
        ropeGravity.setValue(((Vector3f) ItemInteractionsConfig.getDefaultSetting("rope_gravity")).y());
        ropeInertia.setValue((double) ItemInteractionsConfig.getDefaultSetting("rope_inertia"));


        guiParticlesButton.setMessage(
                Component.literal("Inventory particles: ").append(
                Component.literal("" + ItemInteractionsConfig.getDefaultSetting("gui_particles")).withStyle(ChatFormatting.GREEN)
        ));

        smoothParticlesButton.setMessage(
                Component.literal("Smooth particles: ").append(
                        Component.literal("" + ItemInteractionsConfig.getDefaultSetting("gui_smooth_particles")).withStyle(ChatFormatting.GREEN)
                ));

        ItemInteractionsConfig.init();

        this.updateVisible();
//        scaleSpeed.setV

    }
}
