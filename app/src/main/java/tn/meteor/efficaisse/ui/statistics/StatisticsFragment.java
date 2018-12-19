package tn.meteor.efficaisse.ui.statistics;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.StatisticsAdapter;
import tn.meteor.efficaisse.data.db.DatabaseHandler;
import tn.meteor.efficaisse.data.network.Statistics;
import tn.meteor.efficaisse.ui.base.BaseFragment;


public class StatisticsFragment extends BaseFragment implements StatisticsContract.View {


    private LineChart lineChart;
    private BarChart barChart;
    private DatabaseHandler db;
    private StatisticsAdapter statisticsAdapter;
    private RecyclerView recycleView;
    private FloatingSearchView search;
    private List<Statistics> InitStatistics;
    private CardView cardView;
    private List<Statistics> statisticsList;
    private TextView ticketNubmer,commandeNotPayed;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statistics, container, false);
        bindComponent(v);
        db = new DatabaseHandler(getActivity());
        db.ticketNumber();

        ticketNubmer.setText(String.valueOf(db.ticketNumber()));
        commandeNotPayed.setText(String.valueOf(db.commandeNotPayed()));
        InitStatistics = Stream.of(db.tableProductQunatity()).sorted((o1, o2) -> o2.getQuantity() - o1.getQuantity()).toList();
        statisticsList = InitStatistics;
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> barX = new ArrayList<>();

        for (int i = 0; i < db.pieChartCategory().size(); i++) {
            barX.add(db.pieChartCategory().get(i).getCategory());
            barEntries.add(new BarEntry(db.pieChartCategory().get(i).getQuantity(), i));
        }

        BarDataSet barDataset = new BarDataSet(barEntries, "");
        barDataset.setColors(ColorTemplate.VORDIPLOM_COLORS);
        BarData barData = new BarData(barX, barDataset);
        barChart.setData(barData);
        barChart.setDescription("");
        barDataset.setBarSpacePercent(40f);
        barDataset.setDrawValues(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.setViewPortOffsets(100f, 130f, 100f, 100f);
        barChart.setBackgroundColor(Color.WHITE);
        barChart.setDrawGridBackground(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.animateY(3000);
        barChart.setDescriptionPosition(800, 70f);
        barChart.setDescription("TOP VENTE PAR CATEGORIE DU MOIS COURANT");
        barChart.setDescriptionTextSize(25);
        load();
        search.setOnQueryChangeListener((oldQuery, newQuery) -> {
            List<Statistics> listToShow = Stream.of(InitStatistics).filter(i -> i.getProductName().toLowerCase().contains(newQuery.toLowerCase())).toList();
            InitStatistics.clear();
            InitStatistics.addAll(listToShow);
            statisticsAdapter.notifyDataSetChanged();

            if (newQuery.isEmpty()) {
                listToShow.clear();
                statisticsAdapter.notifyDataSetChanged();
            }

        });


        ArrayList<Entry> lineEntries = new ArrayList<>();
        ArrayList<String> lineX = new ArrayList<>();

        for (int i = 0; i < db.lineChartRecette().size(); i++) {
            lineEntries.add(new Entry(Double.valueOf(db.lineChartRecette().get(i).getMontant()).floatValue(), i));
            DateFormat df2 = new SimpleDateFormat("dd");
            String reportDate = df2.format(db.lineChartRecette().get(i).getDate());
            lineX.add(reportDate);

        }
        LineDataSet lineDataset = new LineDataSet(lineEntries, "");
        LineData lineData = new LineData(lineX, lineDataset);
        lineDataset.setColors(ColorTemplate.COLORFUL_COLORS);
        lineDataset.setDrawCubic(false);
        lineDataset.setColor(Color.BLACK);
        lineDataset.setDrawFilled(true);
        lineDataset.setFillColor(Color.parseColor("#273c75"));
        lineDataset.setDrawValues(false);
        lineDataset.setDrawCircles(false);
        lineDataset.setHighlightEnabled(false);
        lineData.setValueTextSize(15);
        lineChart.setData(lineData);
        lineChart.setNoDataText("Pas de données");
        lineChart.setDrawGridBackground(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setTextColor(Color.BLACK);
        lineChart.getXAxis().setTextSize(15);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setSpaceBetweenLabels(0);
        lineChart.setBackgroundColor(Color.WHITE);
        lineDataset.setDrawCubic(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setViewPortOffsets(100f, 130f, 100f, 100f);
        lineChart.setExtraBottomOffset(15f);
        lineChart.animateY(1000);
        lineChart.setDescriptionPosition(735, 70f);
        lineChart.setDescription("VARIATION DU RECETTE DU MOIS COURANT");
        lineChart.setDescriptionTextSize(25);


    }

    @Override
    public void bindComponent(View v) {
        lineChart = v.findViewById(R.id.linechart);
        barChart = v.findViewById(R.id.barchart);
        recycleView = v.findViewById(R.id.recycleView);
        search = v.findViewById(R.id.search);
        cardView = v.findViewById(R.id.nb);
        ticketNubmer = v.findViewById(R.id.ticketNubmer);
        commandeNotPayed = v.findViewById(R.id.commandeNotPayed);
    }

    @Override
    public void load() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        statisticsAdapter = new StatisticsAdapter(getActivity(), InitStatistics);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(statisticsAdapter);

    }
}
