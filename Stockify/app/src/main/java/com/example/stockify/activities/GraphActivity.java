package com.example.stockify.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.stockify.R;
import com.example.stockify.model.CryptoData;
import com.example.stockify.model.CryptoGraphListData;
import com.example.stockify.model.CryptoGraphSingleData;
import com.example.stockify.model.MovingAverage;
import com.example.stockify.model.PriceBar;
import com.example.stockify.model.PriceSeries;
import com.example.stockify.model.TimeSeriesStocks;
import com.example.stockify.model.WatchItem;
import com.example.stockify.retrofit.CryptoRetrofitService;
import com.example.stockify.retrofit.CryptoService;
import com.example.stockify.retrofit.StockRetrofitService;
import com.example.stockify.retrofit.StockService;
import com.google.gson.JsonObject;
import com.scichart.charting.ClipMode;
import com.scichart.charting.Direction2D;
import com.scichart.charting.model.AnnotationCollection;
import com.scichart.charting.model.RenderableSeriesCollection;
import com.scichart.charting.model.dataSeries.DataSeries;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.AxisDragModifierBase;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.CategoryDateAxis;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.BaseRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.charting.visuals.synchronization.SciChartVerticalGroup;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.opengl.GLTextureView;
import com.scichart.extensions.builders.SciChartBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraphActivity extends AppCompatActivity {
    private static final String PRICES = "Prices";

    private StockRetrofitService stockRetrofitService;
    private CryptoRetrofitService cryptoRetrofitService;
    private CryptoService cryptoService;
    private StockService stockService;

    private final SciChartVerticalGroup verticalGroup = new SciChartVerticalGroup();
    private final DoubleRange sharedXRange = new DoubleRange();
    Boolean stock = false;
    protected SciChartBuilder sciChartBuilder;
    private CryptoData tableData;
    private CryptoGraphListData graphData;
    private TimeSeriesStocks tableDataStock;
    PriceSeries priceData;
    SciChartSurface surface;
    private WatchItem item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpSciChartLicense();
        setContentView(R.layout.activity_graph);

        Intent intent = getIntent();
        item = (WatchItem) intent.getParcelableExtra("item");


        SciChartBuilder.init(getApplicationContext());
        sciChartBuilder = SciChartBuilder.instance();

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
        }

        surface = findViewById(R.id.surface);
        surface.setRenderSurface(new GLTextureView(getApplicationContext()));
        surface.setBackgroundColor(getResources().getColor(R.color.background));

        cryptoRetrofitService = new CryptoRetrofitService();
        cryptoService = cryptoRetrofitService.getRetrofit().create(CryptoService.class);
        stockRetrofitService = new StockRetrofitService();
        stockService = stockRetrofitService.getRetrofit().create(StockService.class);
        priceData = new PriceSeries();

        if (stock) {
            getTableDataCrypto();
        } else {
            getTableDataStocks();
        }
    }

    protected void initExampleCrypto(SciChartSurface surface) {
        final IAxis xBottomAxis = sciChartBuilder.newDateAxis().withGrowBy(0.1d, 0.1d).build();
        final IAxis yRightAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();

        final IXyDataSeries<Date, Double> dataSeries = sciChartBuilder.newXyDataSeries(Date.class, Double.class).build();
        dataSeries.append(priceData.getDateData(), priceData.getCloseData());

        final FastMountainRenderableSeries rSeries = sciChartBuilder.newMountainSeries()
                .withDataSeries(dataSeries)
                .withStrokeStyle(Color.parseColor("#73508d"), 2f, true)
                .withAreaFillLinearGradientColors(Color.parseColor("#73508d"), 0x0083D2F5)
                .build();

        UpdateSuspender.using(surface, () -> {
            Collections.addAll(surface.getXAxes(), xBottomAxis);
            Collections.addAll(surface.getYAxes(), yRightAxis);
            Collections.addAll(surface.getRenderableSeries(), rSeries);
            Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
        });
    }

    private void initChartStocks(SciChartSurface surface, BasePaneModel model, boolean isMainPane) {
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
            stockPrices.setAcceptsUnsortedData(true);
            stockPrices.append(prices.getDateData(), prices.getOpenData(), prices.getHighData(), prices.getLowData(), prices.getCloseData());
            addRenderableSeries(builder.newCandlestickSeries().withDataSeries(stockPrices).withYAxisId(PRICES).build());

            final XyDataSeries<Date, Double> maLow = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("Low Line").build();
            maLow.setAcceptsUnsortedData(true);
            maLow.append(prices.getDateData(), MovingAverage.movingAverage(prices.getCloseData(), 50));
            addRenderableSeries(builder.newLineSeries().withDataSeries(maLow).withStrokeStyle(0xFFFF3333, 1f).withYAxisId(PRICES).build());

            final XyDataSeries<Date, Double> maHigh = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("High Line").build();
            maHigh.setAcceptsUnsortedData(true);
            maHigh.append(prices.getDateData(), MovingAverage.movingAverage(prices.getCloseData(), 200));
            addRenderableSeries(builder.newLineSeries().withDataSeries(maHigh).withStrokeStyle(0xFF33DD33, 1f).withYAxisId(PRICES).build());

            Collections.addAll(annotations,
                    builder.newAxisMarkerAnnotation().withY1(stockPrices.getYValues().get(stockPrices.getCount() - 1)).withBackgroundColor(0xFFFF3333).withYAxisId(PRICES).build(),
                    builder.newAxisMarkerAnnotation().withY1(maLow.getYValues().get(maLow.getCount() - 1)).withBackgroundColor(0xFFFF3333).withYAxisId(PRICES).build(),
                    builder.newAxisMarkerAnnotation().withY1(maHigh.getYValues().get(maHigh.getCount() - 1)).withBackgroundColor(0xFF33DD33).withYAxisId(PRICES).build());
        }
    }

    private void setUpSciChartLicense() {
        try {
            SciChartSurface.setRuntimeLicenseKey("UhUDqx6TcvunlrRj4ug7MRoZ99PJxHD1d4MKMXmNZFINgkg35puTv351I6HYq08eRdsokKqnZhfmKy0OJ42xfoctt9WX/9K/hrVHPT+OaITr3q2q4uxIEizn9QX8GK57F5k3vilGIouuy5IGyiON8IT6WymTRNCkDb81EP/Cshn/GNSqCW5V+lhU0mL4pdfxVL4XCuLeYWEjMurIZPjtCDukksrhAaZo0Cf0GN/K2L326PYfCbr8fBk1g6QMtlD7EkJ3q4B1pU+G6Vui7py9TU5iDcvKriWMtpLj0N9P3WN9yprR4Mjuj+EXzZ3/RkndZqE2Of3EbgHkz6hhXfiZBF5+3kS5XR8ubHuUSmO+AURdMbMixBgXF+djz5HsCKvzm3H/UruegavlEfuwyRTiKK7mQnOEr4SmEZekGbShOiHpzvTn4kEX+5r86eJnIQL7/Bq9Ew0hClR4D8j6RLSPfgqcUJ53Gn5EkXDHygr/3fUjFWCIRb3znVqSCeJLa0+6+0PSOSDKYA5NY4qxZAmvRCF17UYR");
        } catch (Exception e) {
            Log.e("SciChart", "Error when setting the license", e);
        }
    }

    private void getTableDataCrypto() {
        Call<CryptoData> call = cryptoService.getOneById("ethereum");
        call.enqueue(new Callback<CryptoData>() {
            @Override
            public void onResponse(Call<CryptoData> call, Response<CryptoData> response) {
                tableData = response.body();
                Log.d("TABLE", tableData.getData().getPriceUsd().toString());
                getGraphData();
            }

            @Override
            public void onFailure(Call<CryptoData> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Ooops! Server not accessible!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTableDataStocks() {
        Call<TimeSeriesStocks> call = stockService.getHistory("IBM", "5min", "LBUPNH3RBRISX2RV");
        call.enqueue(new Callback<TimeSeriesStocks>() {
            @Override
            public void onResponse(Call<TimeSeriesStocks> call, Response<TimeSeriesStocks> response) {
                tableDataStock = response.body();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                for (String date : tableDataStock.getDaily().keySet()) {
                    PriceBar priceBar = null;
                    try {
                        priceBar = new PriceBar(formatter.parse(date),
                                (long) Double.parseDouble(tableDataStock.getDaily().get(date).getOpeningPrice()),
                                (long) Double.parseDouble(tableDataStock.getDaily().get(date).getHighPrice()),
                                (long) Double.parseDouble(tableDataStock.getDaily().get(date).getLowPrice()),
                                (long) Double.parseDouble(tableDataStock.getDaily().get(date).getClosingPrice()),
                                (long) Double.parseDouble(tableDataStock.getDaily().get(date).getVolume()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    priceData.add(0, priceBar);
                }
                Log.i("ispis", priceData.getDateData().toString());
                final PricePaneModel pricePaneModel = new PricePaneModel(sciChartBuilder, priceData);
                initChartStocks(surface,pricePaneModel, true);

            }

            @Override
            public void onFailure(@NonNull Call<TimeSeriesStocks> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Ooops! Server not accessible!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getGraphData() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Call<CryptoGraphListData> call = null;
        try {
            call = cryptoService.getHistory("ethereum", "h1", formatter.parse("02-04-2023").getTime(), formatter.parse("09-04-2023").getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        call.enqueue(new Callback<CryptoGraphListData>() {
            @Override
            public void onResponse(Call<CryptoGraphListData> call, Response<CryptoGraphListData> response) {
                graphData = response.body();

                for (CryptoGraphSingleData data : graphData.getData()) {
                    PriceBar priceBar = new PriceBar(new Date(data.getTime()), (long) Double.parseDouble(data.getPriceUsd()), (long) Double.parseDouble(data.getPriceUsd()), (long) Double.parseDouble(data.getPriceUsd()),
                            (long) Double.parseDouble(data.getPriceUsd()), (long) Double.parseDouble(tableData.getData().getVolumeUsd24Hr()));
                    priceData.add(priceBar);
                }

                final PricePaneModel pricePaneModel = new PricePaneModel(sciChartBuilder, priceData);


                initExampleCrypto(surface);

            }

            @Override
            public void onFailure(Call<CryptoGraphListData> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Ooops! Server not accessible!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}