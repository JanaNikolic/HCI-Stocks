package com.example.stockify.activities;

import static com.scichart.charting3d.interop.eTSRPlatform.Android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.stockify.R;
import com.example.stockify.model.MovingAverage;
import com.example.stockify.model.PriceBar;
import com.example.stockify.model.PriceSeries;
import com.scichart.charting.ClipMode;
import com.scichart.charting.Direction2D;
import com.scichart.charting.model.AnnotationCollection;
import com.scichart.charting.model.RenderableSeriesCollection;
import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.AxisDragModifierBase;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.CategoryDateAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.BaseRenderableSeries;
import com.scichart.charting.visuals.synchronization.SciChartVerticalGroup;
import com.scichart.core.utility.ListUtil;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.opengl.GLTextureView;
import com.scichart.extensions.builders.SciChartBuilder;
import com.scichart.extensions3d.builders.SciChart3DBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String VOLUME = "Volume";
    private static final String PRICES = "Prices";
    private static final String RSI = "RSI";
    private static final String MACD = "MACD";

    private final SciChartVerticalGroup verticalGroup = new SciChartVerticalGroup();
    private final DoubleRange sharedXRange = new DoubleRange();

    protected SciChartBuilder sciChartBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpSciChartLicense();
        setContentView(R.layout.activity_main);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SciChartBuilder.init(getApplicationContext());
        sciChartBuilder = SciChartBuilder.instance();

        PriceSeries priceData = new PriceSeries();
        try {
            priceData.add(new PriceBar(sdf.parse("30/3/2023"),132.0,132.0,130.0,132.0,178));
            priceData.add(new PriceBar(sdf.parse("31/3/2023"),132.0,152.0,132.0,132.0,178));
            priceData.add(new PriceBar(sdf.parse("1/4/2023"),132.0,132.0,132.0,132.0,178));
            priceData.add(new PriceBar(sdf.parse("2/4/2023"),132.0,152.0,132.0,132.0,178));
            priceData.add(new PriceBar(sdf.parse("3/4/2023"),132.0,132.0,132.0,132.0,178));
            priceData.add(new PriceBar(sdf.parse("4/4/2023"),132.0,132.0,132.0,132.0,178));
            priceData.add(new PriceBar(sdf.parse("5/4/2023"),132.0,132.0,132.0,132.0,178));
            priceData.add(new PriceBar(sdf.parse("6/4/2023"),132.0,162.0,152.0,132.0,178));
            priceData.add(new PriceBar(sdf.parse("7/4/2023"),132.0,132.0,132.0,132.0,178));
            priceData.add(new PriceBar(sdf.parse("8/4/2023"),132.0,132.0,112.0,132.0,178));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        final PricePaneModel pricePaneModel = new PricePaneModel(sciChartBuilder, priceData);
        SciChartSurface surface = findViewById(R.id.surface);
        surface.setRenderSurface(new GLTextureView(getApplicationContext()));
        surface.setBackgroundColor(getResources().getColor(R.color.background));

        initChart(surface, pricePaneModel, true);
    }

    private void setUpSciChartLicense() {
        try {
            SciChartSurface.setRuntimeLicenseKey("UhUDqx6TcvunlrRj4ug7MRoZ99PJxHD1d4MKMXmNZFINgkg35puTv351I6HYq08eRdsokKqnZhfmKy0OJ42xfoctt9WX/9K/hrVHPT+OaITr3q2q4uxIEizn9QX8GK57F5k3vilGIouuy5IGyiON8IT6WymTRNCkDb81EP/Cshn/GNSqCW5V+lhU0mL4pdfxVL4XCuLeYWEjMurIZPjtCDukksrhAaZo0Cf0GN/K2L326PYfCbr8fBk1g6QMtlD7EkJ3q4B1pU+G6Vui7py9TU5iDcvKriWMtpLj0N9P3WN9yprR4Mjuj+EXzZ3/RkndZqE2Of3EbgHkz6hhXfiZBF5+3kS5XR8ubHuUSmO+AURdMbMixBgXF+djz5HsCKvzm3H/UruegavlEfuwyRTiKK7mQnOEr4SmEZekGbShOiHpzvTn4kEX+5r86eJnIQL7/Bq9Ew0hClR4D8j6RLSPfgqcUJ53Gn5EkXDHygr/3fUjFWCIRb3znVqSCeJLa0+6+0PSOSDKYA5NY4qxZAmvRCF17UYR");
        } catch (Exception e) {
            Log.e("SciChart", "Error when setting the license", e);
        }
    }

    private void initChart(SciChartSurface surface, BasePaneModel model, boolean isMainPane) {
        final CategoryDateAxis xAxis = sciChartBuilder.newCategoryDateAxis()
                .withVisibility(isMainPane ? View.VISIBLE : View.GONE)
                .withVisibleRange(sharedXRange)
                .withGrowBy(0, 0.05)
                .build();

        surface.getXAxes().add(xAxis);
        surface.getYAxes().add(model.yAxis);

        surface.getRenderableSeries().addAll(model.renderableSeries);

        surface.getChartModifiers().add(sciChartBuilder
                .newModifierGroup()
                .withXAxisDragModifier().withReceiveHandledEvents(true).withDragMode(AxisDragModifierBase.AxisDragMode.Pan).withClipModeX(ClipMode.StretchAtExtents).build()
                .withPinchZoomModifier().withReceiveHandledEvents(true).withXyDirection(Direction2D.XDirection).build()
                .withZoomPanModifier().withReceiveHandledEvents(true).build()
                .withZoomExtentsModifier().withReceiveHandledEvents(true).build()
                .withLegendModifier().withShowCheckBoxes(false).build()
                .build());

        surface.setAnnotations(model.annotations);

        verticalGroup.addSurfaceToGroup(surface);
    }

    private abstract static class BasePaneModel {
        public final RenderableSeriesCollection renderableSeries = new RenderableSeriesCollection();
        public final AnnotationCollection annotations = new AnnotationCollection();
        public final NumericAxis yAxis;
        public final String title;

        protected BasePaneModel(SciChartBuilder builder, String title, String yAxisTextFormatting, boolean isMainPane) {
            this.title = title;

            this.yAxis = builder.newNumericAxis()
                    .withAxisId(title)
                    .withTextFormatting(yAxisTextFormatting)
                    .withAutoRangeMode(AutoRange.Always)
                    .withMinorsPerMajor(isMainPane ? 4 : 2)
                    .withMaxAutoTicks(isMainPane ? 8 : 4)
                    .withGrowBy(isMainPane ? new DoubleRange(0.05d, 0.05d) : new DoubleRange(0d, 0d))
                    .build();
        }

        final void addRenderableSeries(BaseRenderableSeries renderableSeries) {
            renderableSeries.setClipToBounds(true);
            this.renderableSeries.add(renderableSeries);
        }
    }

    private static class PricePaneModel extends BasePaneModel {

        public PricePaneModel(SciChartBuilder builder, PriceSeries prices) {
            super(builder, PRICES, "$0.0", true);

            // Add the main OHLC chart
            final OhlcDataSeries<Date, Double> stockPrices = builder.newOhlcDataSeries(Date.class, Double.class).withSeriesName("EUR/USD").build();
            stockPrices.append(prices.getDateData(), prices.getOpenData(), prices.getHighData(), prices.getLowData(), prices.getCloseData());
            addRenderableSeries(builder.newCandlestickSeries().withDataSeries(stockPrices).withYAxisId(PRICES).build());

            final XyDataSeries<Date, Double> maLow = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("Low Line").build();
            maLow.append(prices.getDateData(), MovingAverage.movingAverage(prices.getCloseData(), 50));
            addRenderableSeries(builder.newLineSeries().withDataSeries(maLow).withStrokeStyle(0xFFFF3333, 3f).withYAxisId(PRICES).build());

            final XyDataSeries<Date, Double> maHigh = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("High Line").build();
            maHigh.append(prices.getDateData(), MovingAverage.movingAverage(prices.getCloseData(), 200));
            addRenderableSeries(builder.newLineSeries().withDataSeries(maHigh).withStrokeStyle(0xFF33DD33, 3f).withYAxisId(PRICES).build());

            Collections.addAll(annotations,
                    builder.newAxisMarkerAnnotation().withY1(stockPrices.getYValues().get(stockPrices.getCount() - 1)).withBackgroundColor(0xFFFF3333).withYAxisId(PRICES).build(),
                    builder.newAxisMarkerAnnotation().withY1(maLow.getYValues().get(maLow.getCount() - 1)).withBackgroundColor(0xFFFF3333).withYAxisId(PRICES).build(),
                    builder.newAxisMarkerAnnotation().withY1(maHigh.getYValues().get(maHigh.getCount() - 1)).withBackgroundColor(0xFF33DD33).withYAxisId(PRICES).build());
        }
    }

}