package com.example.stockify.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stockify.R;
import com.example.stockify.model.CryptoData;
import com.example.stockify.model.CryptoGraphListData;
import com.example.stockify.model.CryptoGraphSingleData;
import com.example.stockify.model.MovingAverage;
import com.example.stockify.model.PriceBar;
import com.example.stockify.model.PriceSeries;
import com.example.stockify.model.TimeSeriesStocks15min;
import com.example.stockify.model.TimeSeriesStocks1min;
import com.example.stockify.model.TimeSeriesStocks60min;
import com.example.stockify.model.TimeSeriesStocks5min;
import com.example.stockify.model.Type;
import com.example.stockify.model.WatchItem;
import com.example.stockify.retrofit.CryptoRetrofitService;
import com.example.stockify.retrofit.CryptoService;
import com.example.stockify.retrofit.StockRetrofitService;
import com.example.stockify.retrofit.StockService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.scichart.charting.ClipMode;
import com.scichart.charting.Direction2D;
import com.scichart.charting.model.AnnotationCollection;
import com.scichart.charting.model.RenderableSeriesCollection;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.modifiers.AxisDragModifierBase;
import com.scichart.charting.modifiers.Placement;
import com.scichart.charting.modifiers.SourceMode;
import com.scichart.charting.modifiers.TooltipModifier;
import com.scichart.charting.modifiers.TooltipPosition;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.CategoryDateAxis;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.renderableSeries.BaseRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.charting.visuals.synchronization.SciChartVerticalGroup;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DateRange;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.opengl.GLTextureView;
import com.scichart.extensions.builders.SciChartBuilder;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraphActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private static final String PRICES = "Prices";

    private StockRetrofitService stockRetrofitService;
    private CryptoRetrofitService cryptoRetrofitService;
    private BottomNavigationView bottomNavigationView;
    private CryptoService cryptoService;
    private StockService stockService;

    private final SciChartVerticalGroup verticalGroup = new SciChartVerticalGroup();
    private final DoubleRange sharedXRange = new DoubleRange();
    protected SciChartBuilder sciChartBuilder;
    private CryptoData tableData;
    private CryptoGraphListData graphData;
    private TimeSeriesStocks1min tableDataStockHour;
    private TimeSeriesStocks5min tableDataStockDay;
    private TimeSeriesStocks15min tableDataStockWeek;
    private TimeSeriesStocks60min tableDataStockMonth;
    private PricePaneModel pricePaneModel;
    private PriceSeries priceData;
    private SciChartSurface surface;
    private String range;
    IXyDataSeries<Date, Double> dataSeries;
    FastMountainRenderableSeries rSeries;
    private WatchItem watchItem;
    private OhlcDataSeries<Date, Double> stockPrices;
    private XyDataSeries<Date, Double> maLow;
    private XyDataSeries<Date, Double> maHigh;
    private TextView nameTV;
    private TextView symbolTV;
    private TextView previousCloseTv;
    private TextView openTv;
    private TextView highTv;
    private TextView lowTv;
    private TextView volumeTv;
    private TextView previousCloseLabelTv;
    private TextView openLabelTv;
    private TextView highLabelTv;
    private TextView lowLabelTv;
    private TextView percentSignTv;
    private DecimalFormat fmt = new DecimalFormat("#,##0.00");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpSciChartLicense();
        setContentView(R.layout.activity_graph);
        Intent intent = getIntent();
        watchItem = (WatchItem) intent.getParcelableExtra("item");

        nameTV = findViewById(R.id.name);
        symbolTV = findViewById(R.id.symbolTv);
        previousCloseTv = findViewById(R.id.previousCloseTv);
        openTv = findViewById(R.id.openTv);
        highTv = findViewById(R.id.highTv);
        lowTv = findViewById(R.id.lowTv);
        volumeTv = findViewById(R.id.volumeTv);

        nameTV.setText(watchItem.getName());
        symbolTV.setText(watchItem.getSymbol());

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
        if (watchItem.getType() == Type.CRYPTO) {
            initExampleCrypto(surface);
        } else {
            initChartStocks(surface, new PricePaneModel(sciChartBuilder, priceData), true);
        }

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnItemSelectedListener(this);
        if (savedInstanceState != null) {
            bottomNavigationView.setSelectedItemId(savedInstanceState.getInt("selectedRange"));
        } else {
            bottomNavigationView.setSelectedItemId(R.id.day);
        }
    }

    protected void initExampleCrypto(SciChartSurface surface) {
        final IAxis xBottomAxis = sciChartBuilder.newDateAxis().withVisibleRange(new DateRange()).build();
        final IAxis yRightAxis = sciChartBuilder.newNumericAxis().withAutoRangeMode(AutoRange.Always).withGrowBy(0.2d, 0.2d).build();


        dataSeries = sciChartBuilder.newXyDataSeries(Date.class, Double.class).build();
        dataSeries.setAcceptsUnsortedData(true);
        dataSeries.append(priceData.getDateData(), priceData.getCloseData());

        rSeries = sciChartBuilder.newMountainSeries()
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

        TooltipModifier tooltipModifier = new TooltipModifier();
        tooltipModifier.setMarkerPlacement(Placement.Top);
        tooltipModifier.setTooltipPosition(TooltipPosition.TopRight);
        tooltipModifier.setSourceMode(SourceMode.AllVisibleSeries);

        // Add the modifier to the surface
        surface.getChartModifiers().add(tooltipModifier);
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

    private class PricePaneModel extends BasePaneModel {

        public PricePaneModel(SciChartBuilder builder, PriceSeries prices) {
            super(builder, PRICES, "$0.0", true);

            // Add the main OHLC chart
            stockPrices = builder.newOhlcDataSeries(Date.class, Double.class).withSeriesName("EUR/USD").build();
            stockPrices.setAcceptsUnsortedData(true);
            stockPrices.append(prices.getDateData(), prices.getOpenData(), prices.getHighData(), prices.getLowData(), prices.getCloseData());
            addRenderableSeries(builder.newCandlestickSeries().withDataSeries(stockPrices).withYAxisId(PRICES).build());

            maLow = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("Low Line").build();
            maLow.setAcceptsUnsortedData(true);
            maLow.append(prices.getDateData(), MovingAverage.movingAverage(prices.getCloseData(), 50));
            addRenderableSeries(builder.newLineSeries().withDataSeries(maLow).withStrokeStyle(0xFFFF3333, 1f).withYAxisId(PRICES).build());

            maHigh = builder.newXyDataSeries(Date.class, Double.class).withSeriesName("High Line").build();
            maHigh.setAcceptsUnsortedData(true);
            maHigh.append(prices.getDateData(), MovingAverage.movingAverage(prices.getCloseData(), 200));
            addRenderableSeries(builder.newLineSeries().withDataSeries(maHigh).withStrokeStyle(0xFF33DD33, 1f).withYAxisId(PRICES).build());

            if (stockPrices.getCount() != 0) {
                Collections.addAll(annotations,
                        builder.newAxisMarkerAnnotation().withY1(stockPrices.getYValues().get(stockPrices.getCount() - 1)).withBackgroundColor(0xFFFF3333).withYAxisId(PRICES).build(),
                        builder.newAxisMarkerAnnotation().withY1(maLow.getYValues().get(maLow.getCount() - 1)).withBackgroundColor(0xFFFF3333).withYAxisId(PRICES).build(),
                        builder.newAxisMarkerAnnotation().withY1(maHigh.getYValues().get(maHigh.getCount() - 1)).withBackgroundColor(0xFF33DD33).withYAxisId(PRICES).build());
            }
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
        Call<CryptoData> call = cryptoService.getOneById(watchItem.getName().toLowerCase().replace(" ", "-"));
        call.enqueue(new Callback<CryptoData>() {
            @Override
            public void onResponse(Call<CryptoData> call, Response<CryptoData> response) {
                if (response.code() == 200) {
                    tableData = response.body();
                    getGraphData();

                    previousCloseLabelTv = findViewById(R.id.previous_close_label);
                    openLabelTv = findViewById(R.id.open_label);
                    highLabelTv = findViewById(R.id.high_label);
                    lowLabelTv = findViewById(R.id.low_label);
                    percentSignTv = findViewById(R.id.percent_sign);

                    previousCloseLabelTv.setText("Price");
                    openLabelTv.setText("Market Cap");
                    highLabelTv.setText("Change Percent in 24Hr");
                    lowLabelTv.setText("Volume Average");
                    percentSignTv.setText("%");

                    previousCloseTv.setText(fmt.format(Double.parseDouble(tableData.getData().getPriceUsd())));
                    openTv.setText(fmt.format(Double.parseDouble(tableData.getData().getMarketCapUsd())));
                    highTv.setText(fmt.format(Double.parseDouble(tableData.getData().getChangePercent24Hr())));
                    lowTv.setText(fmt.format(Double.parseDouble(tableData.getData().getVwap24Hr())));
                    volumeTv.setText(fmt.format(Double.parseDouble(tableData.getData().getVolumeUsd24Hr())));
                } else if (response.code() == 404) {
                    Toast.makeText(getApplicationContext(), "Ooops! Data not found!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CryptoData> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Ooops! Server not accessible!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTableDataStocksDay() {
        if (range.equals("day")) {
            stockCallDay();
        } else if (range.equals("hour")){
            stockCallHour();
        } else if (range.equals("week")){
            stockCallWeek();
        } else if (range.equals("month")){
            stockCallMonth();
        }
    }

    private void getGraphData() {
        Call<CryptoGraphListData> call = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime ldt = LocalDateTime.now();
            long start = 0;
            long end = ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            if (range.equals("day")) {
                start = ldt.minusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                call = cryptoService.getHistory(watchItem.getName().toLowerCase(Locale.ROOT).replace(" ", "-"), "m5", start, end);
            } else if (range.equals("hour")) {
                start = ldt.minusHours(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                call = cryptoService.getHistory(watchItem.getName().toLowerCase(Locale.ROOT).replace(" ", "-"), "m1", start, end);
            } else if (range.equals("week")) {
                start = ldt.minusWeeks(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                call = cryptoService.getHistory(watchItem.getName().toLowerCase(Locale.ROOT).replace(" ", "-"), "m15", start, end);
            } else if (range.equals("month")) {
                start = ldt.minusMonths(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                call = cryptoService.getHistory(watchItem.getName().toLowerCase(Locale.ROOT).replace(" ", "-"), "h2", start, end);
            } else {
                start = ldt.minusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                call = cryptoService.getHistory(watchItem.getName().toLowerCase(Locale.ROOT).replace(" ", "-"), "m5", start, end);
            }
        }

        call.enqueue(new Callback<CryptoGraphListData>() {
            @Override
            public void onResponse(Call<CryptoGraphListData> call, Response<CryptoGraphListData> response) {
                if (response.code() == 200) {
                    graphData = response.body();
                    dataSeries.clear();
                    priceData.clear();
                    PriceBar priceBar = null;
                    for (CryptoGraphSingleData data : graphData.getData()) {
                        priceBar = new PriceBar(new Date(data.getTime()),
                                Double.parseDouble(data.getPriceUsd()),
                                Double.parseDouble(data.getPriceUsd()),
                                Double.parseDouble(data.getPriceUsd()),
                                Double.parseDouble(data.getPriceUsd()),
                                (long) Double.parseDouble(tableData.getData().getVolumeUsd24Hr()));
                        priceData.add(priceBar);
                    }
                    dataSeries.append(priceData.getDateData(), priceData.getCloseData());
                    rSeries = sciChartBuilder.newMountainSeries()
                            .withDataSeries(dataSeries)
                            .withStrokeStyle(Color.parseColor("#73508d"), 2f, true)
                            .withAreaFillLinearGradientColors(Color.parseColor("#73508d"), 0x0083D2F5)
                            .build();
                    UpdateSuspender.using(surface, () -> {
                        surface.getRenderableSeries().clear();
                        Collections.addAll(surface.getRenderableSeries(), rSeries);
                        surface.zoomExtentsX();
                    });
                }  else if (response.code() == 404) {
                    Toast.makeText(getApplicationContext(), "Ooops! Data not found!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CryptoGraphListData> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Ooops! Server not accessible!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hour:
                range = "hour";
                break;
            case R.id.day:
                range = "day";
                break;
            case R.id.week:
                range = "week";
                break;
            case R.id.month:
                range = "month";
                break;
        }
        if (watchItem.getType() == Type.CRYPTO) {
            getTableDataCrypto();
        } else {
            getTableDataStocksDay();
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedRange", bottomNavigationView.getSelectedItemId());
    }

    private void stockCallHour() {
        Call<TimeSeriesStocks1min> call = stockService.getHistoryHour(watchItem.getSymbol(), "LBUPNH3RBRISX2RV");
        call.enqueue(new Callback<TimeSeriesStocks1min>() {
            @Override
            public void onResponse(Call<TimeSeriesStocks1min> call, Response<TimeSeriesStocks1min> response) {
                if (response.code() == 200){
                    tableDataStockHour = response.body();

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                    try {
                        for (String date : tableDataStockHour.getDaily().keySet()) {
                            PriceBar priceBar = null;
                            priceBar = new PriceBar(formatter.parse(date),
                                    Double.parseDouble(tableDataStockHour.getDaily().get(date).getOpeningPrice()),
                                    Double.parseDouble(tableDataStockHour.getDaily().get(date).getHighPrice()),
                                    Double.parseDouble(tableDataStockHour.getDaily().get(date).getLowPrice()),
                                    Double.parseDouble(tableDataStockHour.getDaily().get(date).getClosingPrice()),
                                    (long) Double.parseDouble(tableDataStockHour.getDaily().get(date).getVolume()));

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (formatter.parse(date).after(Date.from(LocalDateTime.now().minusHours(1).atZone(ZoneId.systemDefault()).toInstant()))) {
                                    priceData.add(0, priceBar);
                                }
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    pricePaneModel = new PricePaneModel(sciChartBuilder, priceData);
                    surface.getRenderableSeries().clear();
                    surface.getRenderableSeries().addAll(pricePaneModel.renderableSeries);

                    if (priceData.size() == 0)
                        Toast.makeText(getApplicationContext(), "There is no trading data for this given time range", Toast.LENGTH_SHORT).show();
                }else if (response.code() == 404) {
                    Toast.makeText(getApplicationContext(), "Ooops! Data not found!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<TimeSeriesStocks1min> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Ooops! Server not accessible!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stockCallDay() {
        Call<TimeSeriesStocks5min> call = stockService.getHistoryDay(watchItem.getSymbol(), "LBUPNH3RBRISX2RV");
        call.enqueue(new Callback<TimeSeriesStocks5min>() {
            @Override
            public void onResponse(Call<TimeSeriesStocks5min> call, Response<TimeSeriesStocks5min> response) {
                if (response.code() == 200){
                    tableDataStockDay = response.body();

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                    try {
                        PriceBar priceBar = null;
                        for (String date : tableDataStockDay.getDaily().keySet()) {
                            priceBar = new PriceBar(formatter.parse(date),
                                    Double.parseDouble(tableDataStockDay.getDaily().get(date).getOpeningPrice()),
                                    Double.parseDouble(tableDataStockDay.getDaily().get(date).getHighPrice()),
                                    Double.parseDouble(tableDataStockDay.getDaily().get(date).getLowPrice()),
                                    Double.parseDouble(tableDataStockDay.getDaily().get(date).getClosingPrice()),
                                    (long) Double.parseDouble(tableDataStockDay.getDaily().get(date).getVolume()));

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (formatter.parse(date).after(Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()))) {
                                    priceData.add(0, priceBar);
                                }
                            }
                        }
                        if (priceBar != null) {
                            previousCloseTv.setText(fmt.format(priceBar.getClose()));
                            openTv.setText(fmt.format(priceBar.getOpen()));
                            highTv.setText(fmt.format(priceBar.getHigh()));
                            lowTv.setText(fmt.format(priceBar.getLow()));
                            volumeTv.setText(fmt.format(priceBar.getVolume()));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    pricePaneModel = new PricePaneModel(sciChartBuilder, priceData);
                    surface.getRenderableSeries().clear();
                    surface.getRenderableSeries().addAll(pricePaneModel.renderableSeries);
                if (priceData.size() == 0)  Toast.makeText(getApplicationContext(), "There is no trading data for this given time range", Toast.LENGTH_SHORT).show();
                }  else if (response.code() == 404) {
                    Toast.makeText(getApplicationContext(), "Ooops! Data not found!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<TimeSeriesStocks5min> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Ooops! Server not accessible!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stockCallWeek() {
        Call<TimeSeriesStocks15min> call = stockService.getHistoryWeek(watchItem.getSymbol(), "LBUPNH3RBRISX2RV");
        call.enqueue(new Callback<TimeSeriesStocks15min>() {
            @Override
            public void onResponse(Call<TimeSeriesStocks15min> call, Response<TimeSeriesStocks15min> response) {
                if (response.code() == 200){
                    tableDataStockWeek = response.body();

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                    try {
                        PriceBar priceBar = null;
                        for (String date : tableDataStockWeek.getDaily().keySet()) {
                            priceBar = new PriceBar(formatter.parse(date),
                                    Double.parseDouble(tableDataStockWeek.getDaily().get(date).getOpeningPrice()),
                                    Double.parseDouble(tableDataStockWeek.getDaily().get(date).getHighPrice()),
                                    Double.parseDouble(tableDataStockWeek.getDaily().get(date).getLowPrice()),
                                    Double.parseDouble(tableDataStockWeek.getDaily().get(date).getClosingPrice()),
                                    (long) Double.parseDouble(tableDataStockWeek.getDaily().get(date).getVolume()));

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (formatter.parse(date).after(Date.from(LocalDateTime.now().minusWeeks(1).atZone(ZoneId.systemDefault()).toInstant()))) {
                                    priceData.add(0, priceBar);
                                }
                            }
                        }
                        if (priceBar != null) {
                            previousCloseTv.setText(fmt.format(priceBar.getClose()));
                            openTv.setText(fmt.format(priceBar.getOpen()));
                            highTv.setText(fmt.format(priceBar.getHigh()));
                            lowTv.setText(fmt.format(priceBar.getLow()));
                            volumeTv.setText(fmt.format(priceBar.getVolume()));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    pricePaneModel = new PricePaneModel(sciChartBuilder, priceData);
                    surface.getRenderableSeries().clear();
                    surface.getRenderableSeries().addAll(pricePaneModel.renderableSeries);
                }  else if (response.code() == 404) {
                    Toast.makeText(getApplicationContext(), "Ooops! Data not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TimeSeriesStocks15min> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Ooops! Server not accessible!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stockCallMonth() {
        Call<TimeSeriesStocks60min> call = stockService.getHistoryMonth(watchItem.getSymbol(), "LBUPNH3RBRISX2RV");
        call.enqueue(new Callback<TimeSeriesStocks60min>() {
            @Override
            public void onResponse(Call<TimeSeriesStocks60min> call, Response<TimeSeriesStocks60min> response) {
                if (response.code() == 200){
                    tableDataStockMonth = response.body();

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                    try {
                        PriceBar priceBar = null;
                        for (String date : tableDataStockMonth.getDaily().keySet()) {
                            priceBar = new PriceBar(formatter.parse(date),
                                    Double.parseDouble(tableDataStockMonth.getDaily().get(date).getOpeningPrice()),
                                    Double.parseDouble(tableDataStockMonth.getDaily().get(date).getHighPrice()),
                                    Double.parseDouble(tableDataStockMonth.getDaily().get(date).getLowPrice()),
                                    Double.parseDouble(tableDataStockMonth.getDaily().get(date).getClosingPrice()),
                                    (long) Double.parseDouble(tableDataStockMonth.getDaily().get(date).getVolume()));

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (formatter.parse(date).after(Date.from(LocalDateTime.now().minusMonths(1).atZone(ZoneId.systemDefault()).toInstant()))) {
                                    priceData.add(0, priceBar);
                                }
                            }
                        }
                        if (priceBar != null) {
                            previousCloseTv.setText(fmt.format(priceBar.getClose()));
                            openTv.setText(fmt.format(priceBar.getOpen()));
                            highTv.setText(fmt.format(priceBar.getHigh()));
                            lowTv.setText(fmt.format(priceBar.getLow()));
                            volumeTv.setText(fmt.format(priceBar.getVolume()));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    pricePaneModel = new PricePaneModel(sciChartBuilder, priceData);
                    surface.getRenderableSeries().clear();
                    surface.getRenderableSeries().addAll(pricePaneModel.renderableSeries);
                } else if (response.code() == 404) {
                    Toast.makeText(getApplicationContext(), "Ooops! Data not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TimeSeriesStocks60min> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Ooops! Server not accessible!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}