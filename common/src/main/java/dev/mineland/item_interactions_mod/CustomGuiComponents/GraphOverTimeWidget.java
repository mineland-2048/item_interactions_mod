package dev.mineland.item_interactions_mod.CustomGuiComponents;

import dev.mineland.item_interactions_mod.GuiRendererHelper;
import dev.mineland.item_interactions_mod.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.HashMap;

public class GraphOverTimeWidget extends AbstractWidget {

    double maxGraphY, minGraphY;

    int graphDataLength;
    double graphDataHeight;

    int timeIndex = 0;

    double[] points;


    boolean showYAxis = true;

    int yAxisLabelWidth = 0;

    int padding = 2;

    int graphVisualWidth;
    int graphVisualHeight;

    int graphDivisions = 0;

    String[] yAxisMarkers;

    private int colorLineNew = 0xFFFFFFFF;
    private int colorLineOld = 0xFFFFFFFF;

    private int colorBackground = 0xc0000000;


    int fontHeight = Minecraft.getInstance().font.lineHeight;

    HashMap<String, Marker> markers = new HashMap();

    public GraphOverTimeWidget(double graphHeight, int graphWidth) {
        this(0, 0, 0, 0, 0, graphHeight, graphWidth, true, 1, Component.empty());
    }

    public GraphOverTimeWidget(double graphHeight, int graphWidth, boolean showYAxis) {
        this(0, 0, 0, 0, 0, graphHeight, graphWidth, showYAxis, 1, Component.empty());
    }

    public GraphOverTimeWidget(double graphHeight, int graphWidth, int graphDivisions) {
        this(0, 0, 0, 0, 0, graphHeight, graphWidth, true, graphDivisions, Component.empty());
    }

    public GraphOverTimeWidget(double graphHeight, int graphWidth, int graphDivisions, boolean showYAxis) {
        this(0, 0, 0, 0, 0, graphHeight, graphWidth, showYAxis, graphDivisions, Component.empty());
    }

    public GraphOverTimeWidget(double minGraphY, double maxGraphY, int graphWidth) {
        this(0, 0, 0, 0, minGraphY, maxGraphY, graphWidth, true, 1, Component.empty());
    }

    public GraphOverTimeWidget(double minGraphY, double maxGraphY, int graphWidth, int graphDivisions) {
        this(0, 0, 0, 0, minGraphY, maxGraphY, graphWidth, true, graphDivisions, Component.empty());
    }

    public GraphOverTimeWidget(double minGraphY, double maxGraphY, int graphWidth, Component message) {
        this(0, 0, 0, 0, minGraphY, maxGraphY, graphWidth, true, 1, message);
    }
    public GraphOverTimeWidget(double minGraphY, double maxGraphY, int graphWidth, boolean showYAxis, Component message) {
        this(0, 0, 0, 0, minGraphY, maxGraphY, graphWidth, showYAxis, 1, message);
    }
    public GraphOverTimeWidget(double minGraphY, double maxGraphY, int graphWidth, boolean showYAxis, int graphDivisions, Component message) {
        this(0, 0, 0, 0, minGraphY, maxGraphY, graphWidth, showYAxis, graphDivisions, message);
    }

    public GraphOverTimeWidget(int x, int y, int width, int height, double minGraphY, double maxGraphY, int graphWidth, boolean showYAxis, int graphDivisions, Component message) {
        super(x,y,width,height,message);

        this.minGraphY = minGraphY;
        this.maxGraphY = maxGraphY;


        this.graphDataHeight = maxGraphY - minGraphY;
        this.graphDataLength = graphWidth;

        this.points = new double[graphWidth];

        this.showYAxis = showYAxis;
        
        this.graphDivisions = graphDivisions + 1;

        this.setSize(graphWidth, (int) (maxGraphY - minGraphY));

        if (showYAxis) {
            this.yAxisLabelWidth = Math.max(MiscUtils.numberMaxDecimal(minGraphY, 3).length(), MiscUtils.numberMaxDecimal(maxGraphY, 3).length());
            for (int i = 0; i <= graphDivisions; i++) {
                double num = MiscUtils.lerp((double) i /graphDivisions, minGraphY, maxGraphY);
                this.yAxisLabelWidth = Math.max(Minecraft.getInstance().font.width(MiscUtils.numberMaxDecimal(num, 3) + " "), yAxisLabelWidth);
            }

            this.setSize(graphWidth + yAxisLabelWidth + padding, (int) (maxGraphY - minGraphY) + padding + fontHeight);

        }

        dead = false;
    }

    public void setBackgroundColor(int color) {
        colorBackground = color;
    }

    public void setColor(int color) {
        setColor(color, color);
    }

    public void setColor(int colStart, int colEnd) {
        setColorFresh(colStart);
        setColorOld(colEnd);
    }

    public void setColorFresh(int col) {
        this.colorLineNew = col;
    }

    public void setColorOld(int col) {
        this.colorLineOld = col;
    }

    boolean dead;
    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) return;
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), colorBackground);
        guiGraphics.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xFF808080);

        try {
            for (int i = 0; i <= graphDivisions; i++ ) {
                int y = i == 0 ? getGraphY() + getGraphHeight() - 1 : Math.round(getGraphY() + getGraphHeight() - ((float) i*(getGraphHeight()) / graphDivisions) - 0.5f);
                if (y!= 0 && y != graphDivisions) GuiRendererHelper.renderLine(guiGraphics, getGraphX(), y, getGraphX() + getGraphWidth(), y, 0x80808080);

                if (!showYAxis) continue;

                String string = MiscUtils.numberMaxDecimal(MiscUtils.lerp((float) i/graphDivisions, minGraphY, maxGraphY), 3) + " ";
                var font = Minecraft.getInstance().font;
                guiGraphics.drawString(font, string,
                        getGraphX() - font.width(string),
                        y - font.lineHeight/2 + 1,
                        0xFFFFFFFF);
            }

            markers.forEach((label, marker) -> {
                int y = Math.round(getGraphY() + getGraphHeight() - (float) ( marker.val*(getGraphHeight()) / graphDataHeight) - 0.5f);
                GuiRendererHelper.renderLine(guiGraphics, getGraphX(), y, getGraphX() + getGraphWidth(), y, marker.color);

                if (showYAxis) {
                    var font = Minecraft.getInstance().font;
                    String string = MiscUtils.numberMaxDecimal(marker.val, 3) + " ";
                    int left = getGraphX() - (font.width(string));
                    int right = getGraphX();
                    int top = y - font.lineHeight/2;
                    int bottom = y + font.lineHeight/2 + 1;

                    guiGraphics.drawString(font, string,
                            left,
                            y - font.lineHeight/2 + 1,
                            marker.color);

                    guiGraphics.fill(left, top, right, bottom, MiscUtils.applyBrightness(marker.color, 0.25));
                }
            });

            guiGraphics.renderOutline(this.getGraphX(), this.getGraphY(), this.getGraphWidth(), this.getGraphHeight(), 0xFFFFFFFF);

            int prevX = 0, prevY = 0;
            for (int i = 0; i < points.length; i++) {
                int entry = (timeIndex + i + 1) % graphDataLength;
                if (Double.isNaN(points[entry])) continue;

                int posX = (int) Math.floor((float) i * getGraphWidth() / graphDataLength);
                int posY = (int) (((points[entry] - minGraphY) * getGraphHeight() / graphDataHeight)) + 1;
                int color = MiscUtils.colorLerp( (float) (i/points.length), colorLineNew, colorLineOld);


//                color = i == 0 ? 0xFFFF0000 : color;
//                color = i == points.length - 1 ? 0xFF00FF00 : color;

                if (i != 0 ) {
                    GuiRendererHelper.renderLine(
                            guiGraphics,
                            this.getGraphX() + prevX,
                            this.getGraphY() + this.getGraphHeight() - prevY,
                            this.getGraphX() + posX,
                            this.getGraphY() + this.getGraphHeight() - posY,
                            color
                    );
                }

                prevX = posX;
                prevY = posY;
            }
        } catch (Exception e) {
            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(e.toString()), this.getX(), this.getY(), 0xFFFF0000);
        }
    }

    public void plotPoint(double y) {
        timeIndex++;
        points[timeIndex % graphDataLength] = y;
    }


    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    private int getGraphX() {
        return showYAxis ? this.getX() + yAxisLabelWidth + padding : this.getX();
    }

    private int getGraphY() {
        return showYAxis ? this.getY() + padding + fontHeight/2 : this.getY();
    }

    private int getGraphWidth() {
        return showYAxis ? getWidth() - padding - (yAxisLabelWidth) : getWidth();
    }

    private int getGraphHeight() {
        return showYAxis ? getHeight() - padding - (showYAxis ? fontHeight : 0) : getHeight();
    }

    public void putMarker(double yPos, int color, String label) {
        markers.put(label, new Marker(yPos, color, label));
        this.yAxisLabelWidth = Math.max(yAxisLabelWidth, Minecraft.getInstance().font.width(MiscUtils.numberMaxDecimal(yPos, 3)));
    }

    public void putMarker(double yPos) {
        putMarker(yPos, 0xFFFFFFFF, "m" + markers.size());
    }

    public void putMarker(double yPos, String label) {
        putMarker(yPos, 0xFFFFFFFF, label);
    }

    public void putMarker(double yPos, int color) {
        putMarker(yPos, color, "m" + markers.size());
    }

    public int getGraphDataLength() {
        return graphDataLength;
    }

    public double getGraphDataHeight() {
        return graphDataHeight;
    }


    private static class Marker {
        public double val;
        public int color;
        public String label;
        public Marker(double val, int color, String label) {
            this.val = val;
            this.color = color;
            this.label = label;
        }
    }


}
