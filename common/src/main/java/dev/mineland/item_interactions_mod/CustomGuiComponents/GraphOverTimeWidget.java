package dev.mineland.item_interactions_mod.CustomGuiComponents;

import dev.mineland.item_interactions_mod.GuiRendererHelper;
import dev.mineland.item_interactions_mod.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class GraphOverTimeWidget extends AbstractWidget {

    private final boolean showGraphTitle;
    private int titleWidth;
    private int valueWidth = 0;
    double maxGraphY;
    double minGraphY;

    int graphDataLength;
    double graphDataHeight;
    boolean showYAxis;
    int graphDivisions;
    boolean showCurrentValue;

    int graphVisualWidth;
    int graphVisualHeight;

    String[] yAxisMarkers;

    private int colorLineNew = 0xFFFFFFFF;
    private int colorLineOld = 0xFFFFFFFF;
    private int colorBackground = 0xc0000000;
    private int colorOutline = 0xFFFFFFFF;

    private boolean pixelatedGraph;
    int timeIndex = 0;

    double[] points;



    int yAxisLabelWidth = 0;

    static int padding = 2;

    static int lineHeight = Minecraft.getInstance().font.lineHeight;


    List<Marker> markers;

    protected final GraphOverTimeWidget.PointGetter pointGetter;
    private boolean updateGraphEveryFrame = false;
    private boolean overdraw = false;



    public static GraphOverTimeWidget.Builder builder(Component graphTitle, PointGetter pointGetter, boolean updateGraphEveryFrame) {
        return new Builder(graphTitle, pointGetter, updateGraphEveryFrame);
    }

    public static GraphOverTimeWidget.Builder builder(String graphTitle, PointGetter pointGetter, boolean updateGraphEveryFrame) {
        return new Builder(Component.literal(graphTitle), pointGetter, updateGraphEveryFrame);
    }



    public GraphOverTimeWidget(int x, int y, int width, int height, int graphWidth, int graphHeight, boolean overdraw, double minGraphY, double maxGraphY, int graphDataLength, boolean showYAxis, int graphDivisions, int newerColor, int olderColor, int backgroundColor, int outlineColor, boolean showCurrentValue, boolean pixelatedGraph, PointGetter pointGetter, boolean updateGraphEveryFrame, List<Marker> markers, boolean showGraphTitle, Component message) {
        super(x,y,width,height,message);


        this.minGraphY = minGraphY;
        this.maxGraphY = maxGraphY;
        this.overdraw = overdraw;
        this.graphDataLength = graphDataLength;
        this.showYAxis = showYAxis;
        this.graphDivisions = graphDivisions + 1;
        this.colorLineNew = newerColor;
        this.colorLineOld = olderColor;
        this.colorBackground = backgroundColor;
        this.colorOutline = outlineColor;
        this.pixelatedGraph = pixelatedGraph;
        this.pointGetter = pointGetter;
        this.updateGraphEveryFrame = updateGraphEveryFrame;
        this.showGraphTitle = showGraphTitle;
        this.showCurrentValue = showCurrentValue;

        this.graphDataHeight = maxGraphY - minGraphY;


        this.graphVisualWidth = graphWidth;
        this.graphVisualHeight = graphHeight;

        Font FONT = Minecraft.getInstance().font;
        if (showYAxis) {
            this.yAxisLabelWidth = Math.max(MiscUtils.numberMaxDecimal(minGraphY, 3).length(), MiscUtils.numberMaxDecimal(maxGraphY, 3).length());
            for (int i = 0; i <= graphDivisions; i++) {
                double num = MiscUtils.lerp((double) i / graphDivisions, minGraphY, maxGraphY);
                this.yAxisLabelWidth = Math.max(FONT.width(MiscUtils.numberMaxDecimal(num, 3) + " "), yAxisLabelWidth);
            }

//            this.setSize(graphWidth + yAxisLabelWidth + padding, (int) (maxGraphY - minGraphY) + padding + fontHeight);
        }

        if (showGraphTitle) this.titleWidth = FONT.width(message.getString());
        if (showCurrentValue) this.valueWidth = FONT.width("000000.000");


        this.points = new double[graphDataLength];

        this.markers = markers;
    }


    public void setPixelatedGraph(boolean v) {
        this.pixelatedGraph = v;
    }

    public void displayCurrentValue(boolean v) {
        this.showCurrentValue = v;
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
        Font FONT = Minecraft.getInstance().font;

        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), colorBackground);
        guiGraphics.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), colorOutline);


        GuiRendererHelper.renderLine_ColorPattern(guiGraphics, getX() + (float) getWidth() /2, getY(), getX() + (float) getWidth() /2, getY() + getHeight(), new int[]{0x20FFFFFF, 0}, 8, true);
        GuiRendererHelper.renderLine_ColorPattern(guiGraphics, getX(), getY() + (float) getHeight()/2, getX() + getWidth(), getY() + (float) getHeight()/2, new int[]{0x20FFFFFF, 0}, 8, true);
        if (showGraphTitle) guiGraphics.drawCenteredString(FONT, this.getMessage(), getX() + getWidth()/2, getY() + padding + 1, 0xFFFFFFFF);

        if (showCurrentValue) {
            guiGraphics.drawCenteredString(FONT, MiscUtils.numberMaxDecimal(getCurrentValue(), 3), this.getX() + getWidth()/2, this.getY() + this.getHeight() - lineHeight - 1, 0xFFFFFFFF );
        }

        try {
            if (updateGraphEveryFrame) plotPoint();

            for (int i = 0; i <= graphDivisions; i++ ) {
                int y = i == 0 ? getGraphY() + getGraphHeight() - 1 : Math.round(getGraphY() + getGraphHeight() - ((float) i*(getGraphHeight()) / graphDivisions) - 0.5f);

                if (y!= 0 && y != graphDivisions) GuiRendererHelper.renderLine(guiGraphics, getGraphX(), y, getGraphX() + getGraphWidth(), y, 0x80808080, pixelatedGraph);

                if (!showYAxis) continue;

                String string = MiscUtils.numberMaxDecimal(MiscUtils.lerp((float) i/graphDivisions, minGraphY, maxGraphY), 3) + " ";
                guiGraphics.drawString(FONT, string,
                        getGraphX() - FONT.width(string),
                        y - FONT.lineHeight/2 + 1,
                        0xFFFFFFFF);
            }

            markers.forEach((marker) -> {
                int y = Math.round(getGraphY() + getGraphHeight() - (float) ( marker.val*(getGraphHeight()) / graphDataHeight) - 0.5f);
                GuiRendererHelper.renderLine(guiGraphics, getGraphX(), y, getGraphX() + getGraphWidth(), y, marker.color);

                if (showYAxis) {
                    String string = MiscUtils.numberMaxDecimal(marker.val, 3) + " ";
                    int left = getGraphX() - (FONT.width(string));
                    int right = getGraphX();
                    int top = y - FONT.lineHeight/2;
                    int bottom = y + FONT.lineHeight/2 + 1;

                    guiGraphics.drawString(FONT, string,
                            left,
                            y - FONT.lineHeight/2 + 1,
                            marker.color);

                    guiGraphics.fill(left, top, right, bottom, MiscUtils.applyBrightness(marker.color, 0.25));
                }
            });

            guiGraphics.renderOutline(this.getGraphX(), this.getGraphY(), this.getGraphWidth(), this.getGraphHeight(), 0xFFFFFFFF);

            if (!overdraw) guiGraphics.enableScissor(getGraphX(), getGraphY(), getGraphX() + getGraphWidth(), getGraphY() + getGraphHeight());

            float prevX = 0, prevY = 0;
            for (int i = 0; i < points.length; i++) {
                int entry = (timeIndex + i + 1) % graphDataLength;
                if (Double.isNaN(points[entry])) continue;

                float posX = (float) Math.floor((double) i * getGraphWidth() / graphDataLength);
                float posY = (float) (((points[entry] - minGraphY) * getGraphHeight() / graphDataHeight)) + 1;
                int color = MiscUtils.colorLerp(  ((float) i/points.length), colorLineNew, colorLineOld);


//                color = i == 0 ? 0xFFFF0000 : color;
//                color = i == points.length - 1 ? 0xFF00FF00 : color;

                if (i != 0 ) {
                    GuiRendererHelper.renderLine(
                            guiGraphics,
                            this.getGraphX() + prevX,
                            this.getGraphY() + this.getGraphHeight() - prevY,
                            this.getGraphX() + posX,
                            this.getGraphY() + this.getGraphHeight() - posY,
                            color,
                            pixelatedGraph
                    );
                }

                prevX = posX;
                prevY = posY;
            }

            if (!overdraw) guiGraphics.disableScissor();

        } catch (Exception e) {
            MiscUtils.displayErrorInUi(e.toString());
        }
    }

    public void plotPoint() {
        plotPoint(pointGetter.pointGetter(this));
    }

    public void plotPoint(double y) {
        timeIndex++;
        points[timeIndex % graphDataLength] = y;
    }

    public void plotPoint(float y) {
        plotPoint((double) y);
    }

    public void plotPoint(int y) {
        plotPoint((double) y);
    }

    public double getCurrentValue() {
        return points[timeIndex % graphDataLength];
    }


    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    private boolean notPureGraph() {return this.showYAxis || this.showGraphTitle || this.showCurrentValue; }

    private int getGraphX() {
        var res = this.getX();
        if (this.showYAxis && !(this.showCurrentValue || this.showGraphTitle)) res += yAxisLabelWidth + padding + 1;
        else if (notPureGraph()) res += (this.getWidth()/2) - (this.getGraphWidth()/2) + 1  ;
        return res;
    }

    private int getGraphY() {
        var res = this.getY();
        if (showYAxis || showGraphTitle || showCurrentValue) res += 1;
        if (showYAxis) res += padding + lineHeight/2;
        if (showGraphTitle) res += padding + lineHeight;
//        if (showCurrentValue) res -= Minecraft.getInstance().font.lineHeight;
        return res;
    }

    private int getGraphWidth() {
        var res = graphVisualWidth;
//        if (showYAxis || showGraphTitle || showCurrentValue) res -= (Math.max(Math.max(yAxisLabelWidth*2, titleWidth), valueWidth) + padding + 2);
//        if (showYAxis) res-= (padding + (yAxisLabelWidth));
        return res;
    }

    private int getGraphHeight() {
        var res = this.graphVisualHeight;
//        if (showYAxis || showGraphTitle || showCurrentValue) res -= 2;
//
//        if (showYAxis) res -= (padding + lineHeight + padding);
//        if (showGraphTitle) res -= (lineHeight + padding);
//        if (showCurrentValue) res -= (lineHeight + padding);
//
        return res;
    }

    public void addMarker(double yPos, int color, String label) {
        markers.add(new Marker(yPos, color, label));
        this.yAxisLabelWidth = Math.max(yAxisLabelWidth, Minecraft.getInstance().font.width(MiscUtils.numberMaxDecimal(yPos, 3)));
    }

    public void addMarker(double yPos) {
        addMarker(yPos, 0xFFFFFFFF, "m" + markers.size());
    }

    public void addMarker(double yPos, String label) {
        addMarker(yPos, 0xFFFFFFFF, label);
    }

    public void addMarker(double yPos, int color) {
        addMarker(yPos, color, "m" + markers.size());
    }

    public int getGraphDataLength() {
        return graphDataLength;
    }

    public double getGraphDataHeight() {
        return graphDataHeight;
    }


    public static class Marker {
        public double val;
        public int color;
        public String label;
        public Marker(double val, int color, String label) {
            this.val = val;
            this.color = color;
            this.label = label;
        }
    }



    public static class Builder {
        private final Component graphTitle;
        private final PointGetter pointGetter;

        int x;
        int y;
        int width;
        int height;
        private int graphWidth;
        private int graphHeight;

        private double maxGraphY = 0;
        private double minGraphY = 0;



        private int graphDataLength = 60;

        private int graphDivisions = 0;

        private boolean showYAxis = true;
        private boolean showCurrentValue = false;
        private boolean titleVisible = true;

        private final List<Marker> yAxisMarkers = new ArrayList<>();

        private int newDataColor = 0xFFFFFFFF;
        private int oldDataColor = 0xFFFFFFFF;
        private int colorBackground = 0xc0000000;
        private int colorOutline = 0xFFFFFFFF;

        private boolean pixelatedGraph = true;
        private boolean overdraw = false;

        private final boolean updateGraphEveryFrame;


        private int yAxisLabelWidth = 0;

        private boolean sizedFromInnerGraph = false;
        private float sizedScale = 1f;

        public Builder(Component graphTitle, PointGetter pointGetter) {
            this(graphTitle, pointGetter, true);
        }

        public Builder(Component graphTitle, PointGetter pointGetter, boolean updateGraphEveryFrame) {
            this.graphTitle = graphTitle;
            this.pointGetter = pointGetter;
            this.updateGraphEveryFrame = updateGraphEveryFrame;

        }

        private void refreshYAxisLabelLength() {
            this.yAxisLabelWidth = Math.max(MiscUtils.numberMaxDecimal(minGraphY, 3).length(), MiscUtils.numberMaxDecimal(maxGraphY, 3).length());
            for (Marker m : yAxisMarkers) {
                double num = m.val;
                this.yAxisLabelWidth = Math.max(Minecraft.getInstance().font.width(MiscUtils.numberMaxDecimal(num, 3) + " "), yAxisLabelWidth);
            }

            for (int i = 0; i <= graphDivisions; i++) {
                double num = MiscUtils.lerp((double) i /graphDivisions, minGraphY, maxGraphY);
                this.yAxisLabelWidth = Math.max(Minecraft.getInstance().font.width(MiscUtils.numberMaxDecimal(num, 3) + " "), yAxisLabelWidth);
            }
        }


        public GraphOverTimeWidget build() {

            if (sizedFromInnerGraph) size_fromInnerGraph(graphDataLength, minGraphY, maxGraphY, sizedScale);

            return new GraphOverTimeWidget(
                    this.x,
                    this.y,
                    this.width,
                    this.height,
                    this.graphWidth,
                    this.graphHeight,
                    this.overdraw,
                    this.minGraphY,
                    this.maxGraphY,
                    this.graphDataLength,
                    this.showYAxis,
                    this.graphDivisions,
                    this.newDataColor,
                    this.oldDataColor,
                    this.colorBackground,
                    this.colorOutline,
                    this.showCurrentValue,
                    this.pixelatedGraph,
                    this.pointGetter,
                    this.updateGraphEveryFrame,
                    this.yAxisMarkers,
                    this.titleVisible,
                    this.graphTitle
            );
        }

        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;

            if (!(this.showCurrentValue || this.showYAxis || this.titleVisible)) {
                 this.graphWidth = width;
                 this.graphHeight = height;
            } else {
                int subY = 0;
                int subX = padding * 2;

                if (showYAxis) {
                    subY += lineHeight + padding + padding;
                    subX += yAxisLabelWidth;
                    if (titleVisible || showCurrentValue) {
                        subX += yAxisLabelWidth;
                    }
                }

                if (this.titleVisible) {
                    subY += lineHeight + padding;
                }

                if (showCurrentValue) {
                    subY += lineHeight + padding;
                }

                this.graphWidth = width - subX;
                this.graphHeight = height - subY;

            }

            sizedFromInnerGraph = false;
            return this;
        }

        public Builder size_fromInnerGraph(int dataSize, double height) {
            return size_fromInnerGraph(dataSize, 0, height, 1);
        }

        public Builder size_fromInnerGraph(int dataSize, double height, float scale) {
            return size_fromInnerGraph(dataSize, 0, height, scale);
        }

        public Builder size_fromInnerGraph(int dataSize, double start, double end) {
            return size_fromInnerGraph(dataSize,start,end, 1);
        }

        public Builder size_fromInnerGraph(int dataSize, double start, double end, float scale) {
            sizedFromInnerGraph = true;
            sizedScale = scale;

            int widthScaled = (int) (Math.abs(end - start) * scale);

            var height = widthScaled;
            var width = dataSize;

            if (this.showYAxis || this.titleVisible || this.showCurrentValue) {
                height += 2;
                width += 2;
            }


            this.graphWidth = dataSize;
            this.graphHeight = (widthScaled);
//            if (this.showYAxis || this.titleVisible || this.showCurrentValue) {
//                height += padding;
//            }

            Font FONT = Minecraft.getInstance().font;



            if (this.titleVisible || this.showCurrentValue || this.showYAxis) {
                int titleWidth = FONT.width(this.graphTitle) + padding + padding;
                int valueWidth = FONT.width("000000.000") + padding + padding;
                int yAxisWidth = dataSize + yAxisLabelWidth + padding;
                if (this.titleVisible || this.showCurrentValue) yAxisWidth += yAxisLabelWidth + padding;

                if (this.showYAxis) {
                    refreshYAxisLabelLength();
                    height += lineHeight + padding*2;
                }

                if (this.titleVisible) {
                    height += lineHeight + padding;
                }

                if (this.showCurrentValue) {
                    height += lineHeight + padding + (showYAxis ? 0 : padding);
                }

                width = MiscUtils.max(titleWidth, valueWidth, yAxisWidth, width);
            }



            this.width = width;
            this.height = height;

            return graphRange(start, end).dataLength(dataSize);

        }

        public Builder graphRange(double start, double end) {
            this.minGraphY = Math.min(start, end);
            this.maxGraphY = Math.max(start, end);
            return this;
        }

        public Builder dataLength(int size) {
            this.graphDataLength = size;
            return this;
        }

        public Builder addMarker(String name, double value) {
            this.yAxisMarkers.add(new Marker(value, 0xFFFFFFFF, name));
            yAxisLabelWidth = Math.max(Minecraft.getInstance().font.width(MiscUtils.numberMaxDecimal(value, 3) + " "), yAxisLabelWidth);
            return this;
        }

        public Builder lineColor(int color) {
            return lineColor(color, color);
        }
        public Builder lineColor(int newDataColor, int oldDataColor) {
            this.newDataColor = newDataColor;
            this.oldDataColor = oldDataColor;
            return this;
        }

        public Builder background(int color) {
            this.colorBackground = color;
            return this;
        }
        public Builder outline(int color) {
            this.colorOutline = color;
            return this;
        }
        public Builder background(int backgroundColor, int outlineColor) {
            this.colorOutline = outlineColor;
            this.colorBackground = backgroundColor;
            return this;
        }

        public Builder showTitle() {
            return showTitle(true);
        }
        public Builder showTitle(boolean visible) {
            this.titleVisible = visible;
            return this;
        }


        public Builder smoothGraph() {
            return pixelatedGraph(false);
        }
        public Builder pixelatedGraph() {
            return pixelatedGraph(true);
        }
        public Builder pixelatedGraph(boolean value) {
            this.pixelatedGraph = value;
            return this;
        }


        public Builder showYAxis() {
            return showYAxis(true);
        }
        public Builder hideYAxis() {
            return showYAxis(false);
        }
        public Builder showYAxis(boolean value) {
            this.showYAxis = value;
            return this;
        }

        public Builder graphDivisions(int n) {
            this.graphDivisions = n;
            return this;
        }

        public Builder showCurrentValue() {
            return showCurrentValue(true);
        }
        public Builder showCurrentValue(boolean visible) {

            this.showCurrentValue = visible;
            return this;
        }

        public Builder allowOverdraw() {
            this.overdraw = true;
            return this;
        }
        public Builder allowOverdraw(boolean v) {
            this.overdraw = v;
            return this;
        }
    }

    public interface PointGetter {
        double pointGetter(GraphOverTimeWidget graphOverTimeWidget);
    }
}
