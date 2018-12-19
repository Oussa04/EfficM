package tn.meteor.efficaisse.ui.analysis_ingredient;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.AnalysisIngredientAdapter;
import tn.meteor.efficaisse.data.db.DatabaseHandler;
import tn.meteor.efficaisse.data.repository.ProductIngredientRepository;
import tn.meteor.efficaisse.model.Analysis;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.ui.base.BaseFragment;


public class AnalysisIngredientFragment extends BaseFragment implements AnalysisIngredientContract.View, AnalysisIngredientAdapter.AnalysisAdapterListener {

    private PieChart pieChart;
    private DatabaseHandler db;
    private RecyclerView recyclerView;
    private ProductIngredientRepository productIngredientRepository;
    private List<Ingredient> ingredients;
    private AnalysisIngredientAdapter analysisIngredientAdapter;
    private List<Analysis> analyses = new ArrayList<>();
    private TextView indication,totaleT,coast;
    private LinearLayout linearLayout;
    private List<Analysis> analysisList = new ArrayList<>();
    private float totale,depense = 0;
    private String prix;

    public static AnalysisIngredientFragment newInstance() {
        return new AnalysisIngredientFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_analysis_ingredient, container, false);
        bindComponent(v);

        productIngredientRepository = new ProductIngredientRepository();
        ingredients = new ArrayList<>();
        db = new DatabaseHandler(getActivity());
        Bundle bundle = getArguments();

        String productName= bundle.getString("libelle");
        prix= bundle.getString("prix");
        Product ProductTarget = productIngredientRepository.
                findByName(productName);

        if(ProductTarget.getIngredients().isEmpty()){
            linearLayout.removeAllViews();
            TextView text = new TextView(getActivity());
            text.setTextSize(40);
            text.setText("AUCUNE DONNEÉ");
            text.setTextColor(Color.BLACK);
            text.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

            linearLayout.setHorizontalGravity(50);
            linearLayout.setVerticalGravity(50);
            linearLayout.addView(text);

        }else{
            for (int i = 0; i < ProductTarget.getIngredients().size(); i++) {
                for (int j = 0; j < ProductTarget.getIngredients()
                        .get(i).getIngredient().getIngredient().size(); j++) {
                    for (int k = 0; k < ProductTarget.getIngredients()
                            .get(i).getIngredient().getIngredient().get(j).getIngredient().getIngredient().size(); k++) {

                        analyses.add(new Analysis(ProductTarget.getIngredients()
                                .get(i).getIngredient().getName(),ProductTarget.getIngredients()
                                .get(i).getIngredient().getIngredient().get(j).getIngredient()
                                .getIngredient().get(k).getQuantity()*bundle.getInt("quantity"),
                                ProductTarget.getIngredients()
                                        .get(i).getIngredient().getUnit().name(),String.valueOf(ProductTarget.getIngredients()
                                .get(i).getIngredient().getPrice()*ProductTarget.getIngredients()
                                .get(i).getIngredient().getIngredient().get(j).getIngredient()
                                .getIngredient().get(k).getQuantity()*bundle.getInt("quantity")+" DT")));

                        totale+=ProductTarget.getIngredients()
                                .get(i).getIngredient().getPrice()*ProductTarget.getIngredients()
                                .get(i).getIngredient().getIngredient().get(j).getIngredient()
                                .getIngredient().get(k).getQuantity()*bundle.getInt("quantity");

                        Log.e("totle9adeh tkalef",totale+"");
                        Log.e("9adech be3et",prix+"");
                        depense = totale - Float.parseFloat(prix.replace(",",".")); //bech na7ou el 0.9999999995555

                    }
                }
            }

            analysisList = Stream.of(analyses).sorted((o1, o2) -> (int)(Math.round(o2.getQuantity()) - Math.round(o1.getQuantity()))).toList();
            totaleT.setText("Vous avez un Totale : "+totale);
            coast.setText("Vous avez gangé : "+Float.parseFloat(prix.replace(",",".")));
            indication.setText("Vous avez Dépensé : "+depense);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            analysisIngredientAdapter = new AnalysisIngredientAdapter(getActivity(), this, analysisList);
            recyclerView.setAdapter(analysisIngredientAdapter);
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Entry> pieEntries = new ArrayList<>();

        for (int i = 1; i < 8; i++) {
            pieEntries.add(new Entry(i, 1));
        }

        PieDataSet pieDataset = new PieDataSet(pieEntries, "");
        pieDataset.setDrawValues(true);
        pieDataset.setValueTextColor(Color.WHITE);
        pieDataset.setValueTextSize(1);


        List<String> pieX = new ArrayList<>();
        pieX.add("Lundi");
        pieX.add("Mardi");
        pieX.add("Mercredi");
        pieX.add("Jeudi");
        pieX.add("Vendredi");
        pieX.add("Samedi");
        pieX.add("Dimanche");


        PieData pieData = new PieData(pieX, pieDataset);
        pieDataset.setColors(new int[]{
                Color.parseColor("#FFC312"),
                Color.parseColor("#A3CB38"),
                Color.parseColor("#1289A7"),
                Color.parseColor("#9980FA"),
                Color.parseColor("#1B1464"),
                Color.parseColor("#ED4C67"),
                Color.parseColor("#f368e0")
        });

        pieDataset.setValueTextSize(22);
        pieChart.setData(pieData);
        pieChart.setDrawSliceText(false);
        pieChart.setUsePercentValues(false);
        pieChart.setBackgroundColor(Color.WHITE);
        pieChart.setCenterText("NOMBRE DE PRODUIT");
        pieChart.setCenterTextSize(16);
        pieChart.setCenterTextColor(Color.parseColor("#006266"));
        pieChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        pieChart.getLegend().setTextSize(15);
        pieChart.getLegend().setXEntrySpace(23);


        pieChart.animateY(5000);
        pieChart.setDescription("");


    }


    @Override
    public void onAnalysisSelected(Analysis analysis) {

        Toast.makeText(getActivity(), analysis.getIngredientName(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAnalysisLongClickSelected(Analysis analysis) {

    }

    @Override
    public void bindComponent(View v) {
        pieChart = v.findViewById(R.id.piechart);
        recyclerView = v.findViewById(R.id.recycleView);
        indication = v.findViewById(R.id.indication);
        totaleT = v.findViewById(R.id.totale);
        coast = v.findViewById(R.id.coast);
        linearLayout = v.findViewById(R.id.layout);
    }
}
