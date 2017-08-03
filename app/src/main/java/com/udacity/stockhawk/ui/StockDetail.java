package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Ahmed on 3/6/17.
 */
public class StockDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    @BindView(R.id.chart)LineChart mChart;
    private static final int STOCK_LOADER = 0;


    private boolean getDataOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ButterKnife.bind(this);

        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS,
                Contract.Quote.COLUMN_SYMBOL + " = ?",
                new String[]{getIntent().getStringExtra("symbol")},
                Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        String history = data.getString(
                data.getColumnIndexOrThrow(Contract.Quote.COLUMN_HISTORY)
        );

        CSVReader csvReader = new CSVReader(new StringReader(history));

        List<PriceData> PriceDataList = new ArrayList<>();

        String[] line = null;

        try {
            while ((line = csvReader.readNext()) != null) {
                PriceData PriceData = new PriceData();
                PriceData.setPrice(Float.valueOf(line[1]));
                PriceDataList.add(PriceData);
            }
            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!getDataOnce)
            initChart(PriceDataList);

        getDataOnce = true;

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public class PriceData {
        private float price;

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

    }


    private void initChart(List<PriceData> data) {
        mChart.setViewPortOffsets(0, 20, 0, 0);
        mChart.setBackgroundColor(Color.rgb(255, 255, 255));

        mChart.getDescription().setEnabled(true);
        Description desc =new Description();
        desc.setText("Price of Stock over time");
        mChart.setDescription(desc);

        mChart.setTouchEnabled(true);

        mChart.setPinchZoom(true);

        mChart.setDrawGridBackground(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(false);


        YAxis y = mChart.getAxisLeft();
        y.setLabelCount(10, false);
        y.setTextColor(Color.BLACK);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.BLACK);

        mChart.getAxisRight().setEnabled(true);

        // add data
        setData(data);

        mChart.getLegend().setEnabled(true);

        mChart.animateXY(2000, 2000);

        mChart.invalidate();

    }

    private void setData(List<PriceData> dataItems) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        int total = 0;

        for (PriceData data : dataItems) {
            yVals.add(new Entry(total,data.getPrice()));
            total+=1;
        }

        LineDataSet set1 = new LineDataSet(yVals, "DataSet");
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setDrawCircles(true);
            set1.setLineWidth(1f);
            set1.setColor(Color.BLACK);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.RED);
            set1.setFillAlpha(110);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setDrawCircleHole(false);
            set1.setDrawFilled(true);

            LineData data = new LineData(set1);
            data.setValueTextSize(9f);
            data.setDrawValues(true);
            mChart.setData(data);
        }

}
